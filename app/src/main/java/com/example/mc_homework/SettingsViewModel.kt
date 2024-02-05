package com.example.mc_homework
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.ActivityResult
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.URI
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class SettingsViewModel(private val userRepository: UserRepository, context: Context) : ViewModel() {
    var currentUser: LiveData<User> = userRepository.findUserByIdLive(0)
    val appContext = context

   fun saveUser(userName: String) {
        viewModelScope.launch {
            val user = User(uid = 0, userName = userName)
            userRepository.insertUser(user)
        }
    }

    suspend fun getUserByID(uid: Int): User {
        return userRepository.findUserById(uid)
    }

    var selectedImageUri by mutableStateOf<Uri?>(null)

    fun setImageUri(uri: Uri?){
        selectedImageUri = uri
        if (uri != null) {
            writeImageUri(uri)
        }
    }

    private val uriFileName = "uri.txt"
    private fun writeImageUri(uri: Uri){
        val f = File(appContext.getExternalFilesDir("Uris"), uriFileName)
        if(f.exists()){
            f.deleteRecursively()
        }
        f.createNewFile()
        val fileWriter = BufferedWriter(FileOutputStream(f).bufferedWriter())
        fileWriter.write(uri.toString())
        fileWriter.close()
    }

    fun readUriFromFile() {
        val f = File(appContext.getExternalFilesDir("Uris"), uriFileName)
        if(!f.exists()){
            Toast.makeText(appContext, "File not found", Toast.LENGTH_SHORT).show()
        }else{
            val fileReader = BufferedReader(FileInputStream(f).bufferedReader())
            selectedImageUri = Uri.parse(fileReader.readLine())
            fileReader.close()
        }
    }
}

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
    suspend fun findUserById(uid: Int): User{
        return userDao.findUserById(uid)
    }

    fun findUserByIdLive(uid: Int): LiveData<User> {
        return userDao.findUserByIdLive(uid)
    }
}



/*fun getUserByID(uid: Int): User{
     viewModelScope.launch {
         return userRepository.findUserByID(uid)
     }
 }*/

/*
    fun currentUserUpdate(uid: Int) {
        viewModelScope.launch {
            val user = userRepository.findUserByIdLive(uid)
            currentUser = user
        }
    }
   fun saveUser(userName: String) {
        viewModelScope.launch {
            val user = User(uid = 0, userName = userName)
            userRepository.updateUser(user)
        }
    }
 */

/*
val currentUser: LiveData<User?> = _currentUser

  private val _currentUser = MutableLiveData<User?>(null)

    init{
        viewModelScope.launch {
            _currentUser.value = getUserByID(0)
        }
    }
    fun saveUser(userName: String) {
        val user = _currentUser.value ?: return // Exit if currentUser is null
        viewModelScope.launch {
            val updatedUser = user.copy(userName = userName)
            userRepository.updateUser(updatedUser)
            _currentUser.value = updatedUser // Update LiveData with new user data
        }
    }
 */

/*
    fun getUserByID(uid: Int): User{
        viewModelScope.launch {
            return userRepository.findUserByID(uid)
        }
    }
 */

/*
private fun saveImageUri(uri: Uri){
        val outputStream = appContext.openFileOutput(uriFileName, Context.MODE_PRIVATE)
        val writer = OutputStreamWriter(outputStream)
        writer.write(uri.toString())
        writer.close()
        outputStream.close()
    }

    fun readUriFromFile() {
        viewModelScope.launch {
            try{
                val inputStream = appContext.openFileInput(uriFileName)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val uriString = reader.readLine()
                inputStream.close()
                if(uriString != null){
                    selectedImageUri = Uri.parse(uriString)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

  /*
    fun saveImage(){
        val uri = selectedImageUri ?: return

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val outputDir = context.filesDir
                val uriFile = File(outputDir, uriFileName)
                FileOutputStream(uriFile).use {
                    it.write(uri.toString().toByteArray())
                }
            }
        }
    }*/
 */