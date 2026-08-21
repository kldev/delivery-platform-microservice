package com.pl.platform.svc.delivery.application.handler

import com.pl.platform.svc.delivery.adapter.rest.response.DeliveryItemResponse
import com.pl.platform.svc.delivery.port.DeliveryRepository
import org.springframework.stereotype.Service

@Service
class GetAllDeliveryHandler( private val deliveryRepository: DeliveryRepository) {
    fun handle(): List<DeliveryItemResponse> {
        return deliveryRepository.getAll().map ( DeliveryItemResponse::from)
    }
}