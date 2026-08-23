package com.pl.platform.svc.ledger.adapter.rest
import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.ledger.application.query.GetLedgerTransactionListQuery
import com.pl.platform.svc.ledger.application.query.LedgerTransactionQueryRepository
import com.pl.platform.svc.ledger.domain.LedgerEntry
import com.pl.platform.svc.ledger.domain.LedgerTransaction
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*

@Validated
@RestController
@RequestMapping("/api/ledger-transations")
@Tag(name = "Ledger transactions")

class LedgerTransactionsController(
    private val repository: LedgerTransactionQueryRepository
) {
    @GetMapping
    fun search(@RequestParam(required = false) referenceId: UUID?,
               @RequestParam(required = false) referenceType: String?,
               @RequestParam(required = false, defaultValue = "100")@Max(500) size: Int,
               @RequestParam(required = false, defaultValue = "0") page: Int,
               @RequestParam(required = false) from: LocalDate?,
               @RequestParam(required = false) to: LocalDate?): SliceResponse<LedgerTransaction>
    {
        val query = GetLedgerTransactionListQuery(
            referenceId = referenceId,
            referenceType = referenceType,
            from = from,
            to = to
        )
        val pageable = PageRequest.of(page, size,
            Sort.by(Sort.Direction.DESC, "createdAt"))


        return repository.search(query, pageable);
    }
}