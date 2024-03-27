package presentation.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.MongoDB
import domain.RequestState
import domain.TaskAction
import domain.ToDoTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

typealias MutableTasks = MutableState<RequestState<List<ToDoTask>>>
typealias Tasks = MutableState<RequestState<List<ToDoTask>>>

class HomeViewModel(private val mongoDB: MongoDB) : ScreenModel {
    private var _activeTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val activeTasks: Tasks = _activeTasks

    private var _completedTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val completedTask: Tasks = _completedTasks

    init {
        _activeTasks.value = RequestState.Loading
        _completedTasks.value = RequestState.Loading
        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            mongoDB.readActiveTasks().collectLatest {
                _activeTasks.value = it

            }
        }
        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            mongoDB.readCompletedTasks().collectLatest {
                _completedTasks.value = it

            }
        }
    }

    fun setCompleted(task: ToDoTask, completed: Boolean) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.setCompleted(task, completed)
        }
    }

    fun setFavorite(task: ToDoTask, isFavorite: Boolean) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.setFavorite(task, isFavorite)
        }
    }

    fun setDelete(task: ToDoTask) {
        screenModelScope.launch(Dispatchers.IO) {
            mongoDB.deleteTask(task)
        }
    }

    fun setAction(action: TaskAction) {
        when (action) {
            is TaskAction.Delete -> setDelete(action.task)
            is TaskAction.SetFavorite -> setFavorite(action.task, action.isFavorite)
            is TaskAction.SetCompleted -> setCompleted(action.task, action.completed)
            else -> {}
        }
    }
}