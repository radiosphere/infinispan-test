package io.radiosphere.inifispantest

//import org.infinispan.client.hotrod.RemoteCache
//import org.infinispan.client.hotrod.RemoteCacheManager
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.infinispan.lifecycle.ComponentStatus
import org.infinispan.lock.api.ClusteredLockManager
import org.infinispan.manager.EmbeddedCacheManager
import org.jboss.logmanager.LogManager
import org.jboss.logmanager.Logger
import javax.enterprise.event.Observes
import javax.inject.Inject
import javax.ws.rs.*
import kotlin.time.Duration

@Path("/values")
class ValueController {

//    @Inject
//    lateinit var cache: RemoteCache<String, String>

    @Inject
    lateinit var cacheManager: EmbeddedCacheManager

    @Inject
    lateinit var lockManager: ClusteredLockManager

    val logger = Logger.getAnonymousLogger()

    fun onStartup(@Observes startupEvent: StartupEvent) {
        logger.warning("Status of cache manager: ${cacheManager.status}")
        cacheManager.start()
    }

    fun onStop(@Observes shutdownEvent: ShutdownEvent) {
        logger.warning("Shutting down cache manager status: ${cacheManager.status}")
        cacheManager.stop()
        logger.warning("Status of cache manager after shutdown: ${cacheManager.status}")
    }

    @POST
    fun saveValue(@QueryParam("key") key: String, @QueryParam("value") value: String) {
        try {
            lockManager.defineLock(key)


            val lock = lockManager.get(key)

            lock.tryLock().whenComplete { acquired, problem ->
                if(acquired) {
                    logger.warning("Lock acquired! settings value :).")
                    val cache  = cacheManager.getCache<String, String>("default")
                    cache.addListener(MyExpireListener())
                    cache.put(key, value)
                } else {
                    logger.warning("Nope, lock not acquired. ${problem}")
                }
            }
        } catch ( t: Throwable) {
            val cache  = cacheManager.getCache<String, String>("default")
            logger.info("Cache default has availability mode: ${cache.advancedCache.availability}")
            throw t
        }
    }

    @GET
    @Path("/{key}")
    fun saveValue(@PathParam(value = "key") key: String): String? {
        val cache  = cacheManager.getCache<String, String>("default")
        return cache[key]
    }
}