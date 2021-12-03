package io.radiosphere.inifispantest.exactlyonce

import io.quarkus.runtime.StartupEvent
import org.infinispan.commons.api.CacheContainerAdmin
import org.infinispan.configuration.cache.CacheMode
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.partitionhandling.PartitionHandling
import org.jboss.logging.Logger
import java.util.concurrent.TimeUnit
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject

@ApplicationScoped
class CacheBootstrapper {
    val logger = Logger.getLogger(this.javaClass)

    @Inject
    lateinit var cacheManager: EmbeddedCacheManager

    fun boostrapCache(@Observes e: StartupEvent) {
        logger.info("Bootstrapping caches")
        prepareSubscription()

        logger.info("Starting Cache Manager")
        cacheManager.start()
        cacheManager.cacheNames.forEach {
            cacheManager.getCache<Any, Any>(it).start()
        }
    }

    fun prepareSubscription() {
        val builder = ConfigurationBuilder()
        builder.clustering().cacheMode(CacheMode.DIST_SYNC)
            .stateTransfer().awaitInitialTransfer(true)
            .partitionHandling()
            .whenSplit(PartitionHandling.ALLOW_READ_WRITES)
            .expiration().maxIdle(20, TimeUnit.MINUTES).enableReaper()
            .statistics().enabled(true)

        val cache = cacheManager.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE)
            .getOrCreateCache<String, String>("start-session", builder.build())
        val createDeleteListener = CreateDeleteEventListener(
            {logger.infof("Session started cache %s", it) },
            {logger.infof("Session stopped cache %s", it) }
        )
        val topologyChangeListener = RebalanceEventListener(
            {logger.infof("Session started rebalance %s", it) },
            {logger.infof("Session stopped rebalance %s", it) }
        )
        cache.addListener(createDeleteListener)
        cache.addListener(topologyChangeListener)
    }
}