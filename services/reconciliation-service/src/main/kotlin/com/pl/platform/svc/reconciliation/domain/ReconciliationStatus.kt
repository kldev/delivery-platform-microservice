package com.pl.platform.svc.reconciliation.domain

enum class ReconciliationStatus {
    PENDING,
    RECONCILED,
    DISCREPANCY,
    MISSING_PAYMENT,
    MISSING_SETTLEMENT
}