package com.pl.platform.svc.delivery.adapter.persistence



import com.pl.platform.svc.BaseIntegrationTest
import com.pl.platform.svc.delivery.domain.Delivery
import com.pl.platform.svc.delivery.domain.DeliveryStatus
import com.pl.platform.svc.test.fixture.DriverDatabaseFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
class DeliveryPersistenceAdapterTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var driverFixture: DriverDatabaseFixture;

    @Autowired
    private lateinit var deliveryRepository: DeliveryPersistenceAdapter


    @Autowired
    private lateinit var springDataRepository: SpringDataDeliveryRepository

    @BeforeEach
    fun cleanUp() {
        springDataRepository.deleteAll()
    }

    @Test
    fun `should create and load delivery`() {
        val driverId = driverFixture.create(phoneNumber = "1234567890");

        val delivery = Delivery.create(
            driverId =  driverId,
            pickupAddress = "Opole",
            deliveryAddress = "Wrocław"
        )

        deliveryRepository.create(delivery)

        val loaded =
            deliveryRepository.findById(delivery.id)

        assertNotNull(loaded)
        assertEquals(delivery.id, loaded.id)
        assertEquals(delivery.driverId, loaded.driverId)
        assertEquals(
            delivery.pickupAddress,
            loaded.pickupAddress
        )
        assertEquals(
            delivery.deliveryAddress,
            loaded.deliveryAddress
        )
        assertEquals(
            DeliveryStatus.CREATED,
            loaded.status
        )
    }

    @Test
    fun `should update delivery status`() {
        val driverId = driverFixture.create(phoneNumber = "1234567891");
        val delivery = Delivery.create(
            driverId = driverId,
            pickupAddress = "Opole",
            deliveryAddress = "Wrocław"
        )

        deliveryRepository.create(delivery)

        delivery.assign()
        deliveryRepository.update(delivery)

        val loaded =
            deliveryRepository.findById(delivery.id)

        assertNotNull(loaded)
        assertEquals(
            DeliveryStatus.ASSIGNED,
            loaded.status
        )
    }
}