package presentation.screen.customer

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Customer
import data.CustomerResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class CustomerUiState {
    sealed class AddCustomerState : CustomerUiState() {
        object Loading : AddCustomerState()
        data class Success(val customer: Customer) : AddCustomerState()
        data class Error(val error: String) : AddCustomerState()
    }
    sealed class GetCustomerState : CustomerUiState() {
        object Loading : GetCustomerState()
        data class Success(val customer: CustomerResponse) : GetCustomerState()
        data class Error(val error: String) : GetCustomerState()
    }
}
class CustomerViewModel : ScreenModel {
    private val _customerState = MutableStateFlow<CustomerUiState.GetCustomerState>(CustomerUiState.GetCustomerState.Loading)
    val customer = _customerState

    private val _addCustomerState = MutableStateFlow<CustomerUiState.AddCustomerState>(CustomerUiState.AddCustomerState.Loading)

    private val customerRepository = CustomerRepository()

    init {
        try {
            screenModelScope.launch {
                _customerState.value = CustomerUiState.GetCustomerState.Loading

                customerRepository.getCustomers().collect { customers ->
                    _customerState.update { CustomerUiState.GetCustomerState.Success(customers) }
                }
            }
        } catch (e: Exception) {
            _customerState.update { CustomerUiState.GetCustomerState.Error(e.message.toString()) }
        }
    }

    fun addCustomer(customer: Customer) {
        try {
            screenModelScope.launch {
                _addCustomerState.value = CustomerUiState.AddCustomerState.Loading

                customerRepository.addCustomer(customer).collect {
                    _addCustomerState.update { CustomerUiState.AddCustomerState.Success(customer) }
                }
            }
        } catch (e: Exception) {
            _addCustomerState.update { CustomerUiState.AddCustomerState.Error(e.message.toString()) }
        }
    }
}