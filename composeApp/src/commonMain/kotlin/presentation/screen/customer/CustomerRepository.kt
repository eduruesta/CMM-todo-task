package presentation.screen.customer

import apiClient.httpClient
import data.Customer
import data.CustomerResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.flow

class CustomerRepository {
    private suspend fun getCustomerApi(): CustomerResponse {
        val response = httpClient.get("http://10.0.2.2:8080/customer")
        return response.body()
    }

    private suspend fun postCustomerApi(customer: Customer) {
        httpClient.post("http://10.0.2.2:8080/customer") {
            contentType(ContentType.Application.Json)
            setBody(customer)
        }
    }

    fun getCustomers() = flow {
        emit(getCustomerApi())
    }

    fun addCustomer(customer: Customer) = flow {
        emit(postCustomerApi(customer))
    }
}