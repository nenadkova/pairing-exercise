package io.billie.functional.orders

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.billie.functional.orders.ShipmentFixtures.invalidWithNegativeItems
import io.billie.functional.orders.ShipmentFixtures.orgCreationRequest
import io.billie.functional.orders.ShipmentFixtures.validShipmentOf2
import io.billie.functional.orders.ShipmentFixtures.validShipmentOf8
import io.billie.orders.data.ShipmentRepository
import io.billie.orders.model.Shipment
import io.billie.orders.resource.ShipmentCreationRequest
import io.billie.organisations.viewmodel.Entity
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ShipmentResourceTest {
    @LocalServerPort
    private val port = 8080

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var shipmentDb: ShipmentRepository

    private lateinit var testOrgId: UUID

    private lateinit var testOrderId: UUID

    private lateinit var testShipmentsUrl: String

    @BeforeAll
    fun createTestOrgAndOrder() {
        var result = mockMvc.perform(
            post("/organisations").contentType(APPLICATION_JSON).content(orgCreationRequest())
        ).andExpect(status().isOk).andReturn()

        var response = mapper.readValue(result.response.contentAsString, Entity::class.java)
        testOrgId = response.id
        val testOrdersUrl = "/organisations/${testOrgId}/orders"

        result = mockMvc.perform(
            post(testOrdersUrl).contentType(APPLICATION_JSON).content(ShipmentFixtures.validOrder())
        ).andExpect(status().isOk).andReturn()
        response = mapper.readValue(result.response.contentAsString, Entity::class.java)
        testOrderId = response.id
        testShipmentsUrl = testOrdersUrl + "/${testOrderId}/shipments"
    }

    @Test
    fun canStoreShipment() {
        val result = mockMvc.perform(
            post(testShipmentsUrl).contentType(APPLICATION_JSON).content(validShipmentOf2())
        ).andExpect(status().isCreated).andReturn()
        val response = mapper.readValue(result.response.contentAsString, Entity::class.java)
        val shipmentFromDb = shipmentDb.findById(response.id).get()

        val request = mapper.readValue(validShipmentOf2(), ShipmentCreationRequest::class.java)
        assertThat(shipmentFromDb.shippedItems, equalTo(request.shippedItems))
        assertThat(shipmentFromDb.timeShipped.time, equalTo(request.timeShipped.time))
        shipmentDb.deleteById(response.id)
    }

    @Test
    fun cannotStoreInvalidRequest() {
        mockMvc.perform(
            post(testShipmentsUrl).contentType(APPLICATION_JSON).content("{ blah }")
        ).andExpect(status().isBadRequest).andExpect(content().contentType(APPLICATION_JSON))
    }

    @Test
    fun cannotStoreWithNegativeItems() {
        mockMvc.perform(
            post(testShipmentsUrl).contentType(APPLICATION_JSON).content(invalidWithNegativeItems())
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errorType").value("ValidationException"))
    }

    @Test
    fun cannotStoreWhenNoOrder() {
        val fakeOrderId = UUID(2132122, 212121)
        mockMvc.perform(
            post("/organisations/${testOrgId}/orders/${fakeOrderId}/shipments").contentType(APPLICATION_JSON)
                .content(validShipmentOf2())
        ).andExpect(status().isNotFound)
            .andExpect(jsonPath("$.errorType").value("EntityNotFoundException"))

    }

    @Test
    fun cannotStoreWhenNoOrg() {
        val fakeOrgId = UUID(2132122, 212121)
        mockMvc.perform(
            post("/organisations/${fakeOrgId}/orders/${testOrderId}/shipments").contentType(APPLICATION_JSON)
                .content(validShipmentOf2())
        ).andExpect(status().isNotFound)
            .andExpect(jsonPath("$.errorType").value("EntityNotFoundException"))
    }

    @Test
    fun cannotStoreWhenExceedsOrderItems() {
        mockMvc.perform(
            post(testShipmentsUrl).contentType(APPLICATION_JSON).content(validShipmentOf8())
        ).andExpect(status().isCreated)

        mockMvc.perform(
            post(testShipmentsUrl).contentType(APPLICATION_JSON).content(validShipmentOf8())
        ).andExpect(status().isBadRequest)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.errorType").value("ItemsShippedExceedOrderException"))
    }

    @Test
    fun cannotStoreWhenOrderFulfilled() {
        mockMvc.perform(
            post(testShipmentsUrl).contentType(APPLICATION_JSON).content(validShipmentOf8())
        ).andExpect(status().isCreated)
        mockMvc.perform(
            post(testShipmentsUrl).contentType(APPLICATION_JSON).content(validShipmentOf2())
        ).andExpect(status().isCreated)

        mockMvc.perform(
            post(testShipmentsUrl).contentType(APPLICATION_JSON).content(validShipmentOf2())
        ).andExpect(status().isBadRequest)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.errorType").value("OrderFulfilledException"))
    }

    @Test
    fun canRetrieveShipments() {
        mockMvc.perform(
            post(testShipmentsUrl).contentType(APPLICATION_JSON).content(validShipmentOf8())
        ).andExpect(status().isCreated)
        mockMvc.perform(
            post(testShipmentsUrl).contentType(APPLICATION_JSON).content(validShipmentOf2())
        ).andExpect(status().isCreated)

        val result = mockMvc.perform(
            get(testShipmentsUrl)
        ).andExpect(status().isOk).andReturn()

        val typeRef: TypeReference<List<Shipment>> = object: TypeReference<List<Shipment>>() {}
        val response: List<Shipment> = mapper.readValue(result.response.contentAsString, typeRef)
        assertThat(response, hasSize(2))
    }

    @AfterEach
    fun cleanUp() {
        shipmentDb.deleteAll()
    }
}