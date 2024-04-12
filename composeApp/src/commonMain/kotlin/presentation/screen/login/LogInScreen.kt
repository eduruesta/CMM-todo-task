package presentation.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import dev.gitlive.firebase.auth.FirebaseUser
import presentation.screen.home.HomeScreen

class LogInScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var showDialog by remember { mutableStateOf(false) }
        var signedInUserName: String by remember { mutableStateOf("") }
        var firebaseUser: FirebaseUser? by remember { mutableStateOf(null) }
        val onFirebaseResult: (Result<FirebaseUser?>) -> Unit = { result ->
            if (result.isSuccess) {
                firebaseUser = result.getOrNull()
                signedInUserName =
                    firebaseUser?.displayName ?: firebaseUser?.email ?: "Null User"
            } else {
                signedInUserName = "Null User"
                println("Error Result: ${result.exceptionOrNull()?.message}")
            }

        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Insert valid email") },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
        if (firebaseUser != null) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Log In",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                GoogleButtonUiContainerFirebase(onResult = onFirebaseResult) {
                    GoogleSignInButton(modifier = Modifier.fillMaxWidth()) { this.onClick() }
                }

/*                OutlinedTextField(
                    value = userEmail,
                    onValueChange = {
                        userEmail = it
                        isError = !isValidEmail(it)
                    },
                    label = { Text("Email") },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black),
                    placeholder = { Text("Insert email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        errorTextColor = if (isError) Color.Red else MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (isError) Color.Red else MaterialTheme.colorScheme.onSurface.copy()
                    )
                )

                OutlinedTextField(
                    value = userPassword,
                    onValueChange = { userPassword = it },
                    label = { Text("Password") },
                    placeholder = { Text("Insert password") },
                    textStyle = TextStyle(color = Color.Black),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )*/
                Spacer(Modifier.height(12.dp))
/*                Button(
                    onClick = {
                        if (isError) {
                            showDialog = true
                        } else {
                            scope.launch {
                                try {
                                    auth.createUserWithEmailAndPassword(userEmail, userPassword)
                                    navigator.push(HomeScreen(fireBaseUser))
                                } catch (e: Exception) {
                                    auth.signInWithEmailAndPassword(userEmail, userPassword)
                                    println("Error: $e")
                                }
                            }
                        }
                    }
                ) {
                    Text(text = "Log In")
                }*/
            }
        } else {
            navigator.push(HomeScreen(firebaseUser?.email, firebaseUser?.photoURL))
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }
}