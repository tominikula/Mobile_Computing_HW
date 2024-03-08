package com.example.mc_homework

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mc_homework.ui.theme.MC_homeworkTheme
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!hasRequiredPermissions()){
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS,0
            )
        }
        installSplashScreen().apply{}
        setContent {
            MC_homeworkTheme{
                val context = LocalContext.current
                val userDao = AppDatabase.getDatabase(context).userDao()
                val userRepository = UserRepository(userDao)
                val viewModel = SettingsViewModel(userRepository, context)
                viewModel.setDefaultUser(User(0, "default", null))

                val sensor = SensorActivity(applicationContext)
                sensor.init()

                Navigation(viewModel)
            }
        }
    }
    fun hasRequiredPermissions(): Boolean{
        return CAMERAX_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}

@Composable
fun Navigation(viewModel: SettingsViewModel){
    val navController = rememberNavController()
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
        composable(route = "CameraScreen"){
            CameraScreen(navController, viewModel)
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: SettingsViewModel){
    Column{
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            IconButton(
                onClick = { navController.navigate("CameraScreen") },
            ){
                Icon(
                    Icons.Rounded.Face, contentDescription = "",
                )
            }
            IconButton(
                onClick = { navController.navigate("Settings") },
            ){
                Icon(
                    Icons.Rounded.Settings, contentDescription = "",
                )
            }
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
    val context = LocalContext.current

    var currentUserName by remember {
        mutableStateOf(viewModel.getUserByID(0).userName ?: "default")
    }

    var chosenImg by remember {
        mutableStateOf(viewModel.getUserByName(currentUserName).image)
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri ->
            if (uri != null) {
                viewModel.saveUser(currentUserName, viewModel.copyImg(uri).toString())
            }
            chosenImg = uri.toString() //galleria uri nopeaan käyttöön
            //chosenImg = viewModel.getUserByID(0).image
        }
    )

    var hasNotifPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotifPermission = isGranted
            if(isGranted){
                val app = MyApp()
                app.showNotification(context, "Notifications enabled")
            }
        }
    )

    Column{
        IconButton(
            onClick = {
                navController.navigate("HomeScreen"){
                    popUpTo("HomeScreen"){
                        inclusive = true
                        viewModel.saveUser(currentUserName, viewModel.getUserByID(0).image)
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
        if(chosenImg != null){
            AsyncImage(
                model = chosenImg,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RectangleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, RectangleShape)
                    .clickable {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        }else{
            Image(
                painter = painterResource(R.drawable.bnh),
                contentDescription = "Contact profile picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RectangleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, RectangleShape)
                    .clickable {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        }
        TextField(value = currentUserName, onValueChange = {
            currentUserName = it
        })
        Button(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }) {
            Text("Enable notifications")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController, viewModel: SettingsViewModel) {
    val context = LocalContext.current;
    Column {
        IconButton(
            onClick = {
                navController.navigate("HomeScreen") {
                    popUpTo("HomeScreen") {
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
    }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
        }
    ) {padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ){
            CameraPreview(controller = controller,
                modifier = Modifier.fillMaxSize()
            )
        }

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