package com.pl.platform.svc.delivery.application.event

import com.pl.platform.common.messaging.event.Event
import java.time.Instant
import java.util.UUID

abstract class DeliveryEvent(
                         override val eventId: UUID = UUID.randomUUID(),
                         override val occurredAt: Instant = Instant.now(),
                         override val module: String = "delivery",
                         override val aggregateId: UUID,
                         override val eventType: String
) : Event

