package com.pl.platform.svc.settlement.application.query
import com.pl.platform.common.rest.SliceResponse
import com.pl.platform.svc.settlement.adapter.persistence.SettlementJpaEntity
import com.pl.platform.svc.settlement.adapter.persistence.SettlementSpecifications
import com.pl.platform.svc.settlement.domain.Settlement
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class GetSettlementsListHandler(
    private val repository: SettlementQueryRepository,
) {

    fun handle(
        query: GetSettlementsListQuery,
    ): SliceResponse<Settlement> {

        return repository.search(query,
            PageRequest.of(query.page, query.size,  Sort.by(Sort.Direction.DESC, "createdAt")))

    }
}