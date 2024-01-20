package io.billie.orders.resource

import io.billie.common.ErrorResponse
import io.billie.orders.model.Shipment
import io.billie.orders.resource.ApiExamples.SHIPMENT_EXAMPLE
import io.billie.orders.service.ShipmentsService
import io.billie.organisations.viewmodel.Entity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("organisations/{orgId}/orders/{orderId}/shipments", produces = [APPLICATION_JSON_VALUE])
class ShipmentResource(val service: ShipmentsService) {

    @GetMapping
    @Operation(
        summary = "Retrieve order shipments",
        description = """
             Retrieve shipment notifications for an order that have been posted to Billie. 
            """,
        parameters = [
            Parameter(
                name = "orgId",
                description = "Billie's merchant identifier"
            ),
            Parameter(
                name = "orderId",
                description = "Billie's order identifier"
            )],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Shipments found for the order",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = Shipment::class)
                    )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "No such order found within the organisation",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                description = "No such order found within exception",
                                value = ApiExamples.ENTITY_NOT_FOUND_EXCEPTION_RESPONSE
                            )]
                    )],
            )
        ],
    )
    fun getOrderShipments(@PathVariable orgId: UUID, @PathVariable orderId: UUID): List<Shipment> =
        service.findOrderShipments(orgId, orderId)

    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Send a shipment notification",
        description = """
             Notify Billie of an order shipment that has been sent. There could be multiple shipments for 
             an order, however total number of shipped items cannot exceed number of items in the order. 
            """,
        parameters = [
            Parameter(
                name = "orgId",
                description = "Billie's merchant identifier"
            ),
            Parameter(
                name = "orderId",
                description = "Billie's order identifier"
            )],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Shipment details",
            content = [Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ShipmentCreateRequest::class),
                examples = [
                    ExampleObject(
                        summary = "Shipment object details",
                        value = SHIPMENT_EXAMPLE
                    )
                ]
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Accepted shipment notification",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = Entity::class)
                    )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "No such order found within the organisation",
                content = [
                    Content(
                        mediaType = APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                description = "Invalid Value",
                                value = ApiExamples.VALIDATION_EXCEPTION_RESPONSE
                            ),
                            ExampleObject(
                                description = "Number of items shipped exceeds order items",
                                value = ApiExamples.ITEMS_SHIPPED_EXCEED_ORDER_EXCEPTION_RESPONSE
                            )]
                    )],
            )
        ],
    )
    fun createShipment(
        @PathVariable orgId: UUID,
        @PathVariable orderId: UUID,
        @RequestBody shipmentRequest: ShipmentCreateRequest
    ): Entity =
        Entity(service.onShipmentSent(orgId, orderId, shipmentRequest))

}