package presentation.screen.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import data.Customer
import data.CustomerResponse

const val NAME = "Enter the Title"
const val LAST_NAME = "Enter the last name"
const val EMAIL = "Add some email"

class CustomersScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val customerViewModel = getScreenModel<CustomerViewModel>()
        var currentName by remember {
            mutableStateOf(NAME)
        }
        var currentLastName by remember {
            mutableStateOf(LAST_NAME)
        }
        var currentId by remember {
            mutableStateOf(100)
        }
        var currentEmail by remember {
            mutableStateOf(EMAIL)
        }
        val textFieldList: List<String> = listOf(currentLastName, currentEmail)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        BasicTextField(
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            ),
                            singleLine = true,
                            value = currentName,
                            onValueChange = { currentName = it }
                        )
                    },
                )
            },
            floatingActionButton = {

                FloatingActionButton(
                    onClick = {
                        customerViewModel.addCustomer(
                            Customer(
                                id = currentId,
                                firstName = currentName,
                                lastName = currentLastName,
                                email = currentEmail
                            )
                        )
                    },
                    shape = RoundedCornerShape(size = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Checkmark Icon"
                    )
                }

            }
        ) { padding ->
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(textFieldList) { text ->
                    var onChange = text
                    BasicTextField(
                        modifier = Modifier
                            .padding(all = 24.dp)
                            .padding(
                                top = padding.calculateTopPadding(),
                                bottom = padding.calculateBottomPadding()
                            ),
                        textStyle = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        value = onChange,
                        onValueChange = { description -> onChange = description }
                    )
                }
            }
            customerViewModel.customer.collectAsState().value.let { state ->
                when (state) {
                    is CustomerUiState.GetCustomerState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp), // Opcional: añade padding alrededor del elemento
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is CustomerUiState.GetCustomerState.Success -> {
                        ShowCustomerList(state.customer)
                    }

                    is CustomerUiState.GetCustomerState.Error -> {
                    }

                    else -> {}
                }
            }
        }
    }

    @Composable
    fun ShowCustomerList(customers: CustomerResponse) {
        // Show user list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(8.dp)
        ) {
            items(customers.customer) { customer ->
                // Show user item
                Card(
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        // Show user item content
                        val name = customer.firstName + " " + customer.lastName
                        Text(text = name, modifier = Modifier.padding(8.dp))
                        Text(text = customer.email, modifier = Modifier.padding(8.dp))
                        Text(text = customer.id.toString(), modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}