package io.billie.orders.resource

import io.billie.common.ValidationException
import io.billie.orders.model.Order
import io.billie.orders.service.OrderCreationRequest
import io.billie.orders.service.OrderService
import io.billie.organisations.viewmodel.Entity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("organisations/{orgId}/orders", produces = [MediaType.APPLICATION_JSON_VALUE])
class OrderResource (val service: OrderService) {

    @ExceptionHandler(ValidationException::class)
    fun handle(ex: ValidationException): ResponseEntity<String> =  ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)


    @GetMapping
    fun allOrders(@PathVariable orgId: UUID): List<Order> =
            service.findOrganisationOrders(orgId)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createOrder(@PathVariable orgId: UUID, @Valid @RequestBody orderRequest: OrderCreationRequest): Entity {
        val id = service.createOrder(orgId, orderRequest)
        return Entity(id)
    }
}