package com.pl.platform.svc.reconciliation.adapter.rest

import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.reconciliation.application.query.GetReconciliationQuery
import com.pl.platform.svc.reconciliation.application.query.ReconciliationQueryRepository
import com.pl.platform.svc.reconciliation.domain.Reconciliation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/reconciliations")
@Tag(name = "Reconciliations")
class ReconciliationController(
    private val reconciliationQueryRepository: ReconciliationQueryRepository
) {

    @GetMapping
    fun getAll(
        @RequestParam(required = false) externalTransactionId: String?,
        @RequestParam(required = false) deliveryId: UUID?,
        @RequestParam(required = false) settlementId: UUID?,
        @RequestParam(required = false) paymentId: UUID?,
        @RequestParam(required = false, defaultValue = "100")
        @Max(500)
        size: Int,
        @RequestParam(required = false, defaultValue = "0")
        page: Int
    ): SliceResponse<Reconciliation> {

        return reconciliationQueryRepository.search(
            GetReconciliationQuery(
                externalTransactionId = externalTransactionId,
                deliveryId = deliveryId,
                settlementId = settlementId,
                paymentId = paymentId
            ),
            PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
            )
        )
    }
}