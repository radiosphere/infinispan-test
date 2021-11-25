package io.radiosphere.inifispantest

//import org.infinispan.client.hotrod.RemoteCache
//import org.infinispan.client.hotrod.RemoteCacheManager
import org.infinispan.lock.api.ClusteredLockManager
import org.infinispan.manager.EmbeddedCacheManager
import org.jboss.logmanager.LogManager
import org.jboss.logmanager.Logger
import javax.inject.Inject
import javax.ws.rs.*

@Path("/values")
class ValueController {

//    @Inject
//    lateinit var cache: RemoteCache<String, String>

    @Inject
    lateinit var cacheManager: EmbeddedCacheManager

    @Inject
    lateinit var lockManager: ClusteredLockManager

    val logger = Logger.getAnonymousLogger()

    @POST
    fun saveValue(@QueryParam("key") key: String, @QueryParam("value") value: String) {
        lockManager.defineLock(key)

        val lock = lockManager.get(key)

        lock.tryLock().whenComplete { acquired, problem ->
            if(acquired) {
                val cache  = cacheManager.getCache<String, String>("default")
                cache.put(key, value)
            } else {
                logger.warning("Nope, some issue. ${problem}")
            }
        }
    }

    @GET
    @Path("/{key}")
    fun saveValue(@PathParam(value = "key") key: String): String? {
        val cache  = cacheManager.getCache<String, String>("default")
        return cache[key]
    }
}