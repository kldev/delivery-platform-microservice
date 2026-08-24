package com.pl.platform.svc.delivery.application.query

import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.delivery.adapter.persistence.DeliveryStatusJpa
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import com.pl.platform.svc.test.fixture.DeliveryDatabaseFixture
import com.pl.platform.svc.test.fixture.DriverDatabaseFixture
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal
import kotlin.test.Test

class DeliveryStatusDurationQueryRepositoryTest : BaseIntegrationTest() {

    @Autowired
    lateinit var repository: DeliveryStatusDurationQueryRepository

    @Autowired
    lateinit var deliveryDatabaseFixture: DeliveryDatabaseFixture

    @Autowired
    lateinit var driverDatabaseFixture: DriverDatabaseFixture

    @Transactional
    @BeforeEach
    fun cleanUp() {
        cleanDatabase()
        seedTest()
    }

    @Transactional
    fun seedTest() {
        deliveryDatabaseFixture.create()
        deliveryDatabaseFixture.create()

        val assigned = deliveryDatabaseFixture.create()
        val driver = driverDatabaseFixture.create()

        deliveryDatabaseFixture.changeStatus(
            assigned,
            DeliveryStatusJpa.ASSIGNED,
            driverId = driver
        )
    }

    @Transactional
    @Test
    fun shouldReturnDeliveryStatusDuration() {

        val query = GetDeliveryStatusDurationQuery(
            deliveryId = null
        )

        val result = repository.search(query)

        assertThat(result).isNotNull()
        assertThat(result.content).isNotEmpty()
    }

    @Transactional
    @Test
    fun shouldReturnDeliveryStatusDurationForSelectedDelivery() {

        val deliveryId = deliveryDatabaseFixture.create()

        val query = GetDeliveryStatusDurationQuery(
            deliveryId = deliveryId
        )

        val result = repository.search(query)

        assertThat(result).isNotNull()
        assertThat(result.content).isNotEmpty()

        assertThat(result.content)
            .allMatch { it.deliveryId == deliveryId }
    }

    @Transactional
    @Test
    fun shouldReturnStatusHistoryForAssignedDelivery() {

        val deliveryId = deliveryDatabaseFixture.create()
        val driver = driverDatabaseFixture.create()

        deliveryDatabaseFixture.changeStatus(
            deliveryId,
            DeliveryStatusJpa.ASSIGNED,
            driverId = driver
        )

        val query = GetDeliveryStatusDurationQuery(
            deliveryId = deliveryId
        )

        val result = repository.search(query)

        assertThat(result.content).hasSize(2)

        assertThat(result.content.map { it.status })
            .containsExactly(
                DeliveryStatus.CREATED,
                DeliveryStatus.ASSIGNED
            )
    }

    @Transactional
    @Test
    fun shouldReturnOnlySelectedDeliveryStatusHistory() {

        val firstDeliveryId = deliveryDatabaseFixture.create()
        val secondDeliveryId = deliveryDatabaseFixture.create()

        val driver = driverDatabaseFixture.create()

        deliveryDatabaseFixture.changeStatus(
            firstDeliveryId,
            DeliveryStatusJpa.ASSIGNED,
            driverId = driver
        )

        val query = GetDeliveryStatusDurationQuery(
            deliveryId = firstDeliveryId
        )

        val result = repository.search(query)

        assertThat(result.content)
            .allMatch { it.deliveryId == firstDeliveryId }

        assertThat(result.content)
            .noneMatch { it.deliveryId == secondDeliveryId }
    }

    @Transactional
    @Test
    fun shouldReturnStatusDurationWithOpenLastStatus() {

        val deliveryId = deliveryDatabaseFixture.create()

        val query = GetDeliveryStatusDurationQuery(
            deliveryId = deliveryId
        )

        val result = repository.search(query)

        val currentStatus = result.content.last()

        assertThat(currentStatus.statusTo)
            .isNull()

        assertThat(currentStatus.durationSeconds)
            .isGreaterThanOrEqualTo(BigDecimal.ZERO)
    }
}