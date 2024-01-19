package io.billie.functional.orders

import com.fasterxml.jackson.databind.ObjectMapper
import io.billie.functional.orders.OrderCreationRequestFixtures.invalidTimeFormat
import io.billie.functional.orders.OrderCreationRequestFixtures.negativeTotalItems
import io.billie.functional.orders.OrderCreationRequestFixtures.orgCreationRequest
import io.billie.functional.orders.OrderCreationRequestFixtures.validWithTime
import io.billie.functional.orders.OrderCreationRequestFixtures.validWithoutTime
import io.billie.functional.orders.OrderCreationRequestFixtures.zeroTotalItems
import io.billie.orders.data.OrdersRepository
import io.billie.orders.model.OrderCreationRequest
import io.billie.organisations.viewmodel.Entity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class OrdersResourceTest {

    @LocalServerPort
    private val port = 8080

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var ordersRepository: OrdersRepository

    private lateinit var testOrgId: UUID

    private lateinit var testOrdersUrl: String

    @BeforeAll
    fun createBbcOrganisation() {
        val result = mockMvc.perform(
                post("/organisations").contentType(APPLICATION_JSON).content(orgCreationRequest())
        )
                .andExpect(status().isOk)
                .andReturn()

        val response = mapper.readValue(result.response.contentAsString, Entity::class.java)
        testOrgId = response.id
        testOrdersUrl = "/organisations/${testOrgId}/orders"
    }

    @Test
    fun geOorders() {
        mockMvc.perform(
                get(testOrdersUrl).contentType(APPLICATION_JSON)
        )
                .andExpect(status().isOk())
    }

    @Test
    fun cannotStoreOrderWhenNoTotalItems() {
        mockMvc.perform(
                post(testOrdersUrl).contentType(APPLICATION_JSON).content(zeroTotalItems())
        )
                .andExpect(status().isBadRequest)

    }

    @Test
    fun cannotStoreOrderWhenNegativeTotalItems() {
        mockMvc.perform(
                post(testOrdersUrl).contentType(APPLICATION_JSON).content(negativeTotalItems()))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun cannotStoreOrderWhenInvalidTimeFormat() {
        mockMvc.perform(
                post(testOrdersUrl).contentType(APPLICATION_JSON).content(invalidTimeFormat()))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun canStoreOrderWithoutTime() {
        val result = mockMvc.perform(
                post(testOrdersUrl).contentType(APPLICATION_JSON).content(validWithoutTime()))
                .andExpect(status().isOk()).andReturn()
        val response = mapper.readValue(result.response.contentAsString, Entity::class.java)
        val readFromDb = ordersRepository.findOrder(response.id)

        val request = mapper.readValue(validWithoutTime(), OrderCreationRequest::class.java)
        assertThat(readFromDb!!.totalItems, equalTo(request.totalItems))
        ordersRepository.deleteOrder(response.id)
    }

    @Test
    fun canStoreOrderWithTime() {
        val result = mockMvc.perform(
                post(testOrdersUrl).contentType(APPLICATION_JSON).content(validWithTime()))
                        .andExpect(status().isOk()).andReturn()
        val response = mapper.readValue(result.response.contentAsString, Entity::class.java)
        val readFromDb = ordersRepository.findOrder(response.id)

        val request = mapper.readValue(validWithTime(), OrderCreationRequest::class.java)
        assertThat(readFromDb!!.totalItems, equalTo(request.totalItems))
        assertThat(readFromDb.timePlaced, equalTo(request.timePlaced))
        ordersRepository.deleteOrder(response.id)
    }

}