package io.billie.functional.orders

object ShipmentFixtures {
    fun orgCreationRequest() = """
        {
           "name": "Mozzak Inc",
           "date_founded": "18/10/2001",
           "country_code": "GE",
           "vat_number": "333289454",
           "registration_number": "3686147",
           "legal_entity_type": "NONPROFIT_ORGANIZATION",
           "contact_details": {
             "phone_number": "+443700100222",
             "fax": "",
             "email": "test@gmail.com"
           }
        }        
    """.trimIndent()

    fun validOrder() = """
        {
             "totalItems": 10
         }
    """.trimIndent()

    fun validShipmentOf8() = """
        {
             "shippedItems": 8,
             "timeShipped": "2021-01-13T12:08:56.235-0500"
        }
    """

    fun validShipmentOf2() = """
        {
             "shippedItems": 2,
             "timeShipped": "2021-01-14T12:45:56.235-0500"
        }
    """

    fun validShipmentWithoutTime() = """
        {
             "shippedItems": 2
         }
    """.trimIndent()

    fun validShipmentWithTime() = """
        {
             "shippedItems": 2,
             "timeShipped": "2021-01-18T12:08:56.235-0500"
         }
    """.trimIndent()

}