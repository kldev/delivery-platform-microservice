package com.pl.platform.svc.ledger.adapter.rest
import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.ledger.application.query.GetLedgerEntryListQuery
import com.pl.platform.svc.ledger.application.query.LedgerEntryQueryRepository
import com.pl.platform.svc.ledger.domain.LedgerEntry
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import java.util.UUID

@Validated
@RestController
@RequestMapping("/api/ledger-entries")
@Tag(name = "Ledger entry")
class LedgerEntriesController(
    private val repository: LedgerEntryQueryRepository
) {


    @GetMapping
    fun search(@RequestParam(required = false) accountId: UUID?,
               @RequestParam(required = false, defaultValue = "100")@Max(500) size: Int,
               @RequestParam(required = false, defaultValue = "0") page: Int,
               @RequestParam(required = false) from: LocalDate?,
               @RequestParam(required = false) to: LocalDate?): SliceResponse<LedgerEntry>
    {
        val query = GetLedgerEntryListQuery(
            accountId = accountId,
            from = from,
            to = to
        )
        val pageable = PageRequest.of(page, size,
            Sort.by(Sort.Direction.DESC, "createdAt"))


        return repository.search(query, pageable)
    }
}