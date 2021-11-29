package io.radiosphere.inifispantest

import io.quarkus.runtime.Startup
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated
import org.infinispan.notifications.cachelistener.annotation.CacheEntryExpired
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent
import org.infinispan.notifications.cachelistener.event.CacheEntryExpiredEvent
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent
import org.jboss.logmanager.Logger
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@Listener(clustered = true)
class MyExpireListener {
    val logger = Logger.getAnonymousLogger()

    @CacheEntryExpired
    fun onEntryExpired(ev: CacheEntryExpiredEvent<String, String>) {
        logger.info("Entry expired: ${ev}")
    }

    @CacheEntryCreated
    fun onEntryCreated(ev: CacheEntryCreatedEvent<String, String>) {
        logger.info("Entry created: ${ev}")
    }

    @CacheEntryRemoved
    fun onEntryRemoved(ev: CacheEntryRemovedEvent<String, String>) {
        logger.info("Entry removed: ${ev}")
    }
}