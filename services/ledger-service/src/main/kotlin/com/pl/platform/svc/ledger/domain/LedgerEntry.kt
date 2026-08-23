package com.pl.platform.svc.ledger.domain


import java.math.BigDecimal
import java.util.UUID

class LedgerEntry(
    val id: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
) {

    init {
        require(amount != BigDecimal.ZERO) {
            "Ledger entry amount must not be zero"
        }
    }
}