package com.pl.platform.common.messaging

import com.pl.platform.common.messaging.event.Event

interface EventPublisher {
    fun publish(event: Event)
}