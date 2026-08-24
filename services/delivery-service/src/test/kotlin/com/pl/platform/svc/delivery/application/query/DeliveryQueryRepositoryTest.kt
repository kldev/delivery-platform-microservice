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
import kotlin.test.Test

class DeliveryQueryRepositoryTest : BaseIntegrationTest() {

    @Autowired
    lateinit var repository: DeliveryQueryRepository;

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
    fun seedTest(){
        deliveryDatabaseFixture.create()
        deliveryDatabaseFixture.create()
        val assigned = deliveryDatabaseFixture.create();
        val driver = driverDatabaseFixture.create();

        deliveryDatabaseFixture.changeStatus(assigned,
            DeliveryStatusJpa.ASSIGNED, driverId = driver)
    }

    @Transactional
    @Test
    fun shouldReturnDeliveries() {

        val query = GetDeliveryQuery.empty()

        val result = repository.search(query)

        assertThat(result).isNotNull()
        assertThat(result.content).hasSize(3)
    }

    @Transactional
    @Test
    fun shouldReturnDeliveriesWithStatusAssigned() {

        val query = GetDeliveryQuery(status = DeliveryStatus.ASSIGNED, deliveryId = null)

        val result = repository.search(query)

        assertThat(result).isNotNull()
        assertThat(result.content).hasSize(1)
    }

    @Transactional
    @Test
    fun shouldReturnSelectedDelivery() {

        val deliveryId = deliveryDatabaseFixture.create();
        val query = GetDeliveryQuery(status = null, deliveryId = deliveryId)

        val result = repository.search(query)

        assertThat(result).isNotNull()
        assertThat(result.content).hasSize(1)
    }
}

