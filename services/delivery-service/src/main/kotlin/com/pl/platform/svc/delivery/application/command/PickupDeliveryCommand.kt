package com.pl.platform.svc.delivery.application.command

import com.pl.platform.svc.delivery.domain.DeliveryId

data class PickupDeliveryCommand(var deliveryId: DeliveryId)