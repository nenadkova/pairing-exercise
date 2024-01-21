package io.billie.orders.resource

internal object ApiExamples {
    const val ENTITY_NOT_FOUND_EXCEPTION_RESPONSE = """
        {
             "errorType": "EntityNotFoundException",
             "message": "Order with specified orderId does not exist in this organisation"
         }
    """

    const val VALIDATION_EXCEPTION_RESPONSE = """
        {
             "errorType": "ValidationException",
             "message": "Shipment with no items"
         }
    """

    const val ORDER_FULFILLED_EXCEPTION_RESPONSE = """
        {
             "errorType": "OrderFulfilledException",
         }
    """

    const val ITEMS_SHIPPED_EXCEED_ORDER_EXCEPTION_RESPONSE = """
        {
             "errorType": "ItemsShippedExceedOrderException",
             "message": "Number of shipped items (7) exceeds order total (5)"
         }
    """

    const val SHIPMENT_EXAMPLE = """
        {
             "shippedItems": 2,
             "timeShipped": "2021-01-18T12:08:56.235-0500"
         }
    """

}