package io.radiosphere.inifispantest.exactlyonce

import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent

@Listener(observation = Listener.Observation.POST)
class CreateDeleteEventListener(val startupFunction: (String) -> Unit, val stopFunction:(String) -> Unit) {

    val logger = org.jboss.logging.Logger.getLogger(this.javaClass)

    @CacheEntryCreated
    fun onEntryCreated(ev: CacheEntryCreatedEvent<String, String>) {
        logger.info("Entry created: ${ev}")
        this.startupFunction(ev.key)
    }

    @CacheEntryRemoved
    fun onEntryRemoved(ev: CacheEntryRemovedEvent<String, String>) {
        logger.info("Entry removed: ${ev}")
        this.stopFunction(ev.key)
    }
}
