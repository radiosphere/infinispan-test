package io.radiosphere.inifispantest

import org.infinispan.client.hotrod.RemoteCache
import org.infinispan.client.hotrod.RemoteCacheManager
import javax.inject.Inject
import javax.ws.rs.*

@Path("/values")
class ValueController {

//    @Inject
//    lateinit var cache: RemoteCache<String, String>

    @Inject
    lateinit var cacheManager: RemoteCacheManager


    @POST
    fun saveValue(@QueryParam("key") key: String, @QueryParam("value") value: String) {
        val cache  = cacheManager.getCache<String, String>("default")
        cache.put(key, value)
    }

    @GET
    @Path("/{key}")
    fun saveValue(@PathParam(value = "key") key: String): String? {
        val cache  = cacheManager.getCache<String, String>("default")
        return cache[key]
    }
}