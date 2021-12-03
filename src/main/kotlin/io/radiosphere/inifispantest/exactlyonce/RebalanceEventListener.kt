package io.radiosphere.inifispantest.exactlyonce

import org.infinispan.Cache
import org.infinispan.commons.util.IntSets
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation.TopologyChanged
import org.infinispan.notifications.cachelistener.event.TopologyChangedEvent
import org.jboss.logging.Logger
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.function.Consumer

@Listener(sync = false)
class RebalanceEventListener(val startupFunction: (String) -> Unit, val stopFunction:(String) -> Unit) {

    val logger = Logger.getLogger(this.javaClass)

    @TopologyChanged
    fun onTopologyChange(e : TopologyChangedEvent<String, String>) {
        logger.info("Rebalance event listener has been triggered!")
        if (!e.isPre()) {
            val cache = e.cache
            val localAddress = cache.getAdvancedCache().getRpcManager().getAddress();
            val oldPrimarySegments =
                e.getReadConsistentHashAtStart().getPrimarySegmentsForOwner(localAddress);
            val newPrimarySegments =
                e.getReadConsistentHashAtEnd().getPrimarySegmentsForOwner(localAddress);

            val allNewKeys = IntSets.mutableCopyFrom(newPrimarySegments);
            allNewKeys.removeAll(oldPrimarySegments);
            val allRemovedKeys = IntSets.mutableCopyFrom(oldPrimarySegments);
            allNewKeys.removeAll(newPrimarySegments);

            logger.info("Rebalance, new keys: ${allNewKeys.count()}, removed keys: ${allRemovedKeys.count()}")
            cache.entries.stream()
                .filterKeySegments(allRemovedKeys)
                .forEach ( Consumer {
                    logger.info("Rebalance, starting ${it.key}")
                    stopFunction(it.key)
                })

            cache.entries.stream()
                .filterKeySegments(allNewKeys)
                .forEach ( Consumer {
                    logger.info("Rebalance, starting ${it.key}")
                    startupFunction(it.key)
                })

//            cache.entries.localPublisher(allRemovedKeys)
//                .subscribe(object: Subscriber<MutableMap.MutableEntry<String, String>> {
//                    override fun onSubscribe(s: Subscription?) {
//                        logger.info("Rebalance, subscribe")
//                    }
//
//                    override fun onNext(it: MutableMap.MutableEntry<String, String>?) {
//                        logger.info("Rebalance, stopping ${it!!.key}")
//                    }
//
//                    override fun onError(t: Throwable?) {
//                        logger.info("Rebalance, onError")
//                    }
//
//                    override fun onComplete() {
//                        logger.info("Rebalance, onComplete")
//                    }
//
//                })
//
//            cache.entries.localPublisher(allNewKeys)
//                .subscribe(object: Subscriber<MutableMap.MutableEntry<String, String>> {
//                    override fun onSubscribe(s: Subscription?) {
//                    }
//
//                    override fun onNext(it: MutableMap.MutableEntry<String, String>?) {
//                        logger.info("Rebalance, starting ${it!!.key}")
//                        sessionRunnerService.createSession(it!!.key)
//                    }
//
//                    override fun onError(t: Throwable?) {
//                        logger.info("Rebalance, onError")
//                    }
//
//                    override fun onComplete() {
//                    }
//                })
        }
    }
}
