package com.pl.platform.svc.delivery.adapter.rest
import com.pl.platform.svc.delivery.application.command.DeliveryAction
import org.springframework.core.convert.converter.Converter

import org.springframework.stereotype.Component

@Component
class DeliveryActionConverter : Converter<String, DeliveryAction> {

    override fun convert(source: String): DeliveryAction =
        DeliveryAction.valueOf(source.uppercase())
}