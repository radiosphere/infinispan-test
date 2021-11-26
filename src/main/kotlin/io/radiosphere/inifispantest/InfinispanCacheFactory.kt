package io.radiosphere.inifispantest

import org.infinispan.Cache
import org.infinispan.commons.api.CacheContainerAdmin
import org.infinispan.configuration.cache.CacheMode
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.configuration.cache.StorageType
import org.infinispan.configuration.global.GlobalConfigurationBuilder
import org.infinispan.conflict.EntryMergePolicy
import org.infinispan.eviction.EvictionStrategy
import org.infinispan.lock.EmbeddedClusteredLockManagerFactory
import org.infinispan.lock.api.ClusteredLockManager
import org.infinispan.lock.configuration.ClusteredLockConfiguration
import org.infinispan.lock.configuration.ClusteredLockConfigurationBuilder
import org.infinispan.lock.configuration.ClusteredLockManagerConfigurationBuilder
import org.infinispan.lock.configuration.Reliability
import org.infinispan.lock.impl.manager.EmbeddedClusteredLockManager
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.partitionhandling.PartitionHandling
import java.util.concurrent.TimeUnit
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces


@ApplicationScoped
class InfinispanCacheFactory {

    @Produces
    fun createCacheManager(): EmbeddedCacheManager {

        val global = GlobalConfigurationBuilder.defaultClusteredBuilder()

        if(System.getenv("POD_NAME") != null) {
            global.transport().addProperty("configurationFile", "default-configs/default-jgroups-kubernetes.xml")
            global.transport().addProperty("jgroups.dns.query", "localhost")
        } else {
            global.transport().addProperty("configurationFile", "default-configs/default-jgroups-tcp.xml")
        }

        val cacheManager = DefaultCacheManager(global.build())
        val builder = ConfigurationBuilder()

        global.addModule(ClusteredLockManagerConfigurationBuilder::class.java)
            .reliability(Reliability.AVAILABLE)
            .numOwner(2)

        builder.clustering().cacheMode(CacheMode.DIST_SYNC)
            .stateTransfer().awaitInitialTransfer(true)
            .partitionHandling()
            .whenSplit(PartitionHandling.ALLOW_READ_WRITES)
//            .mergePolicy { preferredEntry, otherEntries ->
//                preferredEntry
//            }
            .expiration().maxIdle(30, TimeUnit.MINUTES).enableReaper()
            .statistics().enabled(true)

        cacheManager.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE)
            .getOrCreateCache<String, String>("default", builder.build())

        return cacheManager
    }

    @Produces
    fun createLockManager(cacheManager: EmbeddedCacheManager): ClusteredLockManager {
        val lockManager = EmbeddedClusteredLockManagerFactory.from(cacheManager)
        return lockManager
    }
}