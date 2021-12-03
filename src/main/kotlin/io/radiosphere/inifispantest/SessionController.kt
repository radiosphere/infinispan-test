package io.radiosphere.inifispantest

import org.infinispan.Cache
import org.infinispan.manager.EmbeddedCacheManager
import org.jboss.logmanager.Logger
import javax.inject.Inject
import javax.ws.rs.DELETE
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("/sessions")
class SessionController {

    @Inject
    lateinit var cacheManager: EmbeddedCacheManager

    val logger = Logger.getAnonymousLogger()

    val cache: Cache<String, String> by lazy {
        cacheManager.getCache("start-session")
    }

    @PUT
    @Path("{id}")
    fun startSession(@PathParam("id") sessionId: String) {
        logger.info("Controller: Called startSession $sessionId")
        cache.put(sessionId, sessionId)
    }

    @DELETE
    @Path("{id}")
    fun deleteSession(@PathParam(value = "id") sessionId: String) {
        logger.info("Controller: Called deleteSession $sessionId")
        cache.remove(sessionId)
    }

}