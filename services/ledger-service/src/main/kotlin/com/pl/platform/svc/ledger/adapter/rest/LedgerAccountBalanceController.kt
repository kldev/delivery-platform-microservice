package com.pl.platform.svc.ledger.adapter.rest

import com.pl.platform.svc.ledger.application.port.LedgerAccountRepository
import com.pl.platform.svc.ledger.domain.LedgerAccount
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("/api/ledger-accounts")
@Tag(name = "Ledger account")
class LedgerAccountBalanceController(private val repository: LedgerAccountRepository) {

    @GetMapping
    fun getAll(): List<LedgerAccount> = repository.getAll()

    @GetMapping("{accountId}")
    fun getOne(@PathVariable accountId: UUID) = repository.getById(accountId);
}