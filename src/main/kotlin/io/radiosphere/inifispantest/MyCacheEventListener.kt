package io.radiosphere.inifispantest

import io.quarkus.runtime.Startup
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation.*
import org.infinispan.notifications.cachelistener.event.*
import org.jboss.logmanager.Logger
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@Listener(primaryOnly = true, observation = Listener.Observation.POST)
class MyCacheEventListener {
    val logger = Logger.getAnonymousLogger()

    @CacheEntryExpired
    fun onEntryExpired(ev: CacheEntryExpiredEvent<String, String>) {
        logger.info("Entry expired: ${ev}")
    }

    @CacheEntryCreated
    fun onEntryCreated(ev: CacheEntryCreatedEvent<String, String>) {
        logger.info("Entry created: ${ev}")
    }

    @CacheEntryModified
    fun onEntryModified(ev: CacheEntryModifiedEvent<String, String>) {
        logger.info("Entry modified: ${ev}")
    }

    @CacheEntryActivated
    fun onEntryActivated(ev: CacheEntryActivatedEvent<String, String>) {
        logger.info("Entry activated: ${ev}")
    }

    @CacheEntryLoaded
    fun onEntryLoaded(ev: CacheEntryLoadedEvent<String, String>) {
        logger.info("Entry loaded: ${ev}")
    }

    @CacheEntryPassivated
    fun onEntryPassivated(ev: CacheEntryPassivatedEvent<String, String>) {
        logger.info("Entry passivated: ${ev}")
    }

    @CacheEntryRemoved
    fun onEntryRemoved(ev: CacheEntryRemovedEvent<String, String>) {
        logger.info("Entry removed: ${ev}")
    }
}