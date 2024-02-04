package com.example.mc_homework

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mc_homework.ui.theme.MC_homeworkTheme
import android.content.res.Configuration
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MC_homeworkTheme{
                val context = LocalContext.current
                val userDao = AppDatabase.getDatabase(context).userDao()
                val userRepository = UserRepository(userDao)
                val viewModel = SettingsViewModel(userRepository)
                Navigation(viewModel)
            }
        }
    }
}

@Composable
fun Navigation(viewModel: SettingsViewModel){
    val navController = rememberNavController()

    viewModel.saveUser("default")

    NavHost(
        navController = navController,
        startDestination = "HomeScreen"
    ){
        composable(route = "HomeScreen"){
            HomeScreen(navController, viewModel)
        }
        composable(route = "Settings"){
            SettingsScreen(navController, viewModel)
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: SettingsViewModel){
    Column{
        IconButton(
            onClick = { navController.navigate("Settings") },
            modifier = Modifier.align(Alignment.End)
        ){
            Icon(
                Icons.Rounded.Settings, contentDescription = "",
            )
        }
        Conversation(SampleData.conversationSample, viewModel)
    }
}
@Composable
fun Conversation(messages: List<Message>, viewModel: SettingsViewModel){
    LazyColumn{
        items(messages) {
                message -> MessageCard(message, viewModel)
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    var currentUser by remember { mutableStateOf<User?>(null)}

    /*val currentUser by viewModel.currentUser.observeAsState()*/

    var username by remember{
        mutableStateOf(currentUser?.userName ?: "default")
    }
    Column{
        IconButton(
            onClick = {
                viewModel.saveUser(username)
                navController.navigate("HomeScreen"){
                    popUpTo("HomeScreen"){
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                Icons.Rounded.ArrowBack, contentDescription = "",
            )
        }
        Text(text = "User:")
        Image(
            painter = painterResource(R.drawable.bnh),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(60.dp)
                .clip(RectangleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, RectangleShape)
        )
        TextField(value = username, onValueChange = {
            username = it
        })
    }
}



/*
@Preview
@Composable
fun PreviewConversation(){
    MC_homeworkTheme {
        Conversation(SampleData.conversationSample)
    }
}

@Preview(name = "light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun PreviewMessageCard() {
    MC_homeworkTheme{
        Surface{
            MessageCard(
                msg = Message("Tomi", "Moro")
            )
        }
    }
}*/