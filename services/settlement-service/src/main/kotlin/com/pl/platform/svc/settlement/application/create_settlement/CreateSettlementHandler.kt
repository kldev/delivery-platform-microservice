package com.pl.platform.svc.settlement.application.create_settlement
import com.pl.platform.common.messaging.port.OutboxRepository
import com.pl.platform.svc.settlement.application.event.DriverSettlementCompletedEvent
import com.pl.platform.svc.settlement.application.mapper.toCreatedEvent
import com.pl.platform.svc.settlement.domain.Settlement
import com.pl.platform.svc.settlement.port.SettlementRateRepository
import com.pl.platform.svc.settlement.port.SettlementRepository
import org.springframework.stereotype.Component

@Component
class CreateSettlementHandler(
    private val settlementRepository: SettlementRepository,
    private val settlementRateRepository: SettlementRateRepository,
    private val outboxRepository: OutboxRepository,
    private val calculator: SettlementCalculator
) {

    fun handle(command: CreateSettlementCommand): Settlement {
        val rates = settlementRateRepository.findActive()

        val settlement = calculator.calculate(
            command = command,
            rates = rates,
        )

        settlementRepository.create(settlement)

        val event = settlement.toCreatedEvent();
        outboxRepository.save(event)

        val driverSettlementEvent = DriverSettlementCompletedEvent(currency = settlement.currency,
            amount = settlement.deliveryAmount,
            settlementId = settlement.id.value,
            driverId = settlement.driverId,)

        outboxRepository.save(driverSettlementEvent)

        return settlement
    }
}