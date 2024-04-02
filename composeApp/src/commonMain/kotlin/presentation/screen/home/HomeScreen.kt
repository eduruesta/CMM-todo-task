package presentation.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.RequestState
import domain.TaskAction
import domain.ToDoTask
import presentation.screen.components.ErrorScreen
import presentation.screen.components.LoadingScreen
import presentation.screen.components.TaskView
import presentation.screen.task.TaskScreen

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HomeViewModel>()
        val activeTask by viewModel.activeTasks
        val completedTask by viewModel.completedTask
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text("Home") })
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(TaskScreen()) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Icon"
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
            ) {

                DisplayTask(
                    modifier = Modifier.weight(1f),
                    task = activeTask,
                    onSelect = { selectedTask -> navigator.push(TaskScreen(selectedTask)) },
                    onFavorite = { task, isFavorite ->
                        viewModel.setAction(
                            action = TaskAction.SetFavorite(
                                task,
                                isFavorite
                            )
                        )
                    },
                    onComplete = { selectedTask, completed ->
                        viewModel.setAction(
                            action = TaskAction.SetCompleted(
                                selectedTask,
                                completed
                            )
                        )
                    },
                )
                Spacer(modifier = Modifier.height(24.dp))
                DisplayTask(
                    modifier = Modifier.weight(1f),
                    task = completedTask,
                    showActive = false,
                    onComplete = { selectedTask, completed -> viewModel.setAction(
                        action = TaskAction.SetCompleted(
                            selectedTask,
                            completed
                        )
                    )},
                    onDelete = { task -> viewModel.setAction(action = TaskAction.Delete(task))})
            }

        }

    }

    @Composable
    fun DisplayTask(
        modifier: Modifier = Modifier,
        task: RequestState<List<ToDoTask>>,
        showActive: Boolean = true,
        onSelect: ((ToDoTask) -> Unit)? = null,
        onFavorite: ((ToDoTask, Boolean) -> Unit)? = null,
        onComplete: (ToDoTask, Boolean) -> Unit,
        onDelete: ((ToDoTask) -> Unit)? = null
    ) {
        var showDialog by remember { mutableStateOf(false) }
        var taskToDelete: ToDoTask? by remember { mutableStateOf(null) }

        if (showDialog) {
            AlertDialog(
                title = {
                    Text(text = "Delete", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                },
                text = {
                    Text(
                        text = "Are you sure you want to remove '${taskToDelete!!.title}' task?",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        onDelete?.invoke(taskToDelete!!)
                        showDialog = false
                        taskToDelete = null
                    }) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            taskToDelete = null
                            showDialog = false
                        }
                    ) {
                        Text(text = "Cancel")
                    }
                },
                onDismissRequest = {
                    taskToDelete = null
                    showDialog = false
                }
            )
        }


        Column(modifier = modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = if (showActive) "Active Tasks" else "Completed Tasks",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            task.DisplayResult(
                onLoading = {
                    LoadingScreen()
                },
                onError = {
                    ErrorScreen(message = it)
                },
                onSuccess = {
                    if (it.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.padding(horizontal = 24.dp)) {
                            items(items = it, key = { task -> task._id.toHexString() }) {
                                TaskView(
                                    task = it,
                                    showActive = showActive,
                                    onSelect = { onSelect?.invoke(it) },
                                    onComplete = { selectedTask, completed ->
                                        onComplete(selectedTask, completed)
                                    },
                                    onFavorite = { selectedTask, favorite ->
                                        onFavorite?.invoke(selectedTask, favorite)
                                    },
                                    onDelete = { selectedTask ->
                                        taskToDelete = selectedTask
                                        showDialog = true
                                    }
                                )
                            }
                        }

                    } else {
                        ErrorScreen()
                    }
                }
            )
        }
    }

}