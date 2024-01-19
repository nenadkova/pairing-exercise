package io.billie.orders.data

import io.billie.common.ValidationException
import io.billie.orders.model.Order
import io.billie.orders.model.OrderCreationRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*

@Repository
class OrdersRepository {

    private val orderQuery = """
       select 
            id, total_items, time_placed, organisation_id 
        from organisations_schema.orders 
        where id = ?
     """.trimIndent()

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Transactional(readOnly = true)
    fun findOrders(orgId: UUID) : List<Order> {
        return jdbcTemplate.query(ordersQuery(), ordersMapper(), orgId)
    }

    @Transactional
    fun findOrder(id: UUID): Order? {
        return jdbcTemplate.queryForObject(orderQuery, ordersMapper(), id)
    }

    @Transactional
    fun deleteOrder(orderId: UUID) {
        val deleted = jdbcTemplate.update("delete from organisations_schema.orders where id = ?", orderId);
        if (deleted == 0)
            throw ValidationException("Order with id ${orderId} not found")
    }

    @Transactional
    fun createOrder(orgId: UUID, orderCreationRequest: OrderCreationRequest): UUID {
        this.validate(orgId, orderCreationRequest)
        return this.doCreate(orgId, orderCreationRequest)
    }

    private fun doCreate(orgId: UUID, orderCreationRequest: OrderCreationRequest): UUID {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        if (orderCreationRequest.timePlaced == null) {
            jdbcTemplate.update(
                    { conn ->
                        val ps = conn.prepareStatement(
                            """
                            insert into organisations_schema.orders(
                               total_items, organisation_id) 
                            values (?, ?)
                            """.trimIndent(),
                                arrayOf("id")
                        )
                        ps.setInt(1, orderCreationRequest.totalItems)
                        ps.setObject(2, orgId)
                        ps
                    }, keyHolder
            )
        } else {
            val orderTime = Timestamp(orderCreationRequest.timePlaced.time)
            jdbcTemplate.update(
                    { conn ->
                        val ps = conn.prepareStatement(
                            """
                            insert into organisations_schema.orders(
                               total_items, time_placed, organisation_id) 
                            values (?, ?, ?)
                            """.trimIndent(),
                                arrayOf("id")
                        )
                        ps.setInt(1, orderCreationRequest.totalItems)
                        ps.setTimestamp(2, orderTime)
                        ps.setObject(3, orgId)
                        ps
                    }, keyHolder
            )
        }
        return keyHolder.getKeyAs(UUID::class.java)!!
    }

    @Throws(ValidationException::class)
    private fun validate(orgId: UUID, orderCreationRequest: OrderCreationRequest) {
        if (orderCreationRequest.totalItems < 1) throw ValidationException("totalItems must be greater than 1")
        //TODO check if orgId is a valid organization
    }

    private fun ordersMapper() = RowMapper<Order> { it: ResultSet, _: Int ->
        Order(
                it.getObject("id", UUID::class.java),
                it.getInt("total_items"),
                Date(it.getTimestamp("time_placed").time),
                it.getObject("organisation_id", UUID::class.java)
        )
    }

    private fun ordersQuery() = """
        select 
            id, total_items, time_placed, organisation_id 
        from organisations_schema.orders 
        where organisation_id = ?
    """.trimIndent()

}