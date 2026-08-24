package com.pl.platform.svc.reconciliation.application.query

import java.util.UUID

data class GetReconciliationQuery(val externalTransactionId: String?,
                                  val deliveryId: UUID?,
                                  val settlementId: UUID?,
                                  val paymentId: UUID?
                                )