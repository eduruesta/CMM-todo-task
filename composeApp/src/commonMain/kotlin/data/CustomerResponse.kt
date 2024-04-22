package data

import kotlinx.serialization.Serializable

@Serializable
data class CustomerResponse(
    val customer: List<Customer>
)