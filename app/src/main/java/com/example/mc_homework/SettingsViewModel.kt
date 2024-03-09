package com.example.mc_homework
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(private val userRepository: UserRepository, context: Context) : ViewModel() {
    private val appContext = context

    fun saveUser(userName: String, imageUriString: String?) {
        viewModelScope.launch {
            val user = User(uid = 0, userName = userName, image = imageUriString)
            userRepository.insertUser(user)
        }
    }

    fun getUserByID(uid: Int): User {
        return userRepository.findUserById(uid)
    }

    fun getUserByName(username: String): User{
        return userRepository.findUserByName(username)
    }

    fun setDefaultUser(user: User){
        userRepository.insertDefaultUser(user)
    }

    fun copyImg(newUri: Uri): Uri{
        val input = appContext.contentResolver.openInputStream(newUri)
        val outPutFile = appContext.filesDir.resolve("profilepic.jpg")
        input?.copyTo(outPutFile.outputStream())
        input?.close()
        return outPutFile.toUri()
    }//image copying from uri

    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap){
        _bitmaps.value += bitmap
    }
}

class UserRepository(private val userDao: UserDao) {

    fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    fun insertDefaultUser(user: User){
        userDao.insertDefaultUser(user)
    }

    fun findUserById(uid: Int): User{
        return userDao.findUserById(uid)
    }

    fun findUserByName(name: String): User{
        return userDao.findUserByName(name)
    }
}
