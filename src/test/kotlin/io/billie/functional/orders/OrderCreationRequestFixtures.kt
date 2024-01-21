package io.billie.functional.orders

object OrderCreationRequestFixtures {
    fun zeroTotalItems() = """
        {
            "totalItems": 0 
        }
    """.trimIndent()

    fun negativeTotalItems() = """
         {
             "totalItems": -5
         }
    """.trimIndent()

    fun invalidTimeFormat() = """
        {
            "totalItems": 2,
            "timePlaced": "2023-12-04"
         }
    """.trimIndent()

    fun invalidWithoutTime() = """
        {
             "totalItems": 3
         }
    """.trimIndent()

    fun validWithTwoItems() = """
        {
            "totalItems": 2,
            "timePlaced": "2023-07-04T12:08:56.235-0500"
        }
        """.trimIndent()

    fun orgCreationRequest() = """
        {
           "name": "Mozzak Inc",
           "date_founded": "18/10/2001",
           "country_code": "CA",
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
}