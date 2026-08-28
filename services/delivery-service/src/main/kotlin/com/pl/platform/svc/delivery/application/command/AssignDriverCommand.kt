package com.pl.platform.svc.delivery.application.command

import com.pl.platform.svc.delivery.domain.DeliveryId
import java.util.UUID

data class AssignDriverCommand(val deliveryId: DeliveryId, val driverId: UUID?)