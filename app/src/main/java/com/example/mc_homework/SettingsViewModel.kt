package com.example.mc_homework
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

   fun saveUser(userName: String) {
        viewModelScope.launch {
            val user = User(uid = 0, userName = userName)
            userRepository.insertUser(user)
        }
    }

    suspend fun getUserByID(uid: Int): User {
        return userRepository.findUserByID(uid)
    }
}

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
    suspend fun findUserByID(uid: Int): User{
        return userDao.findUserById(uid)
    }
}
/*fun getUserByID(uid: Int): User{
     viewModelScope.launch {
         return userRepository.findUserByID(uid)
     }
 }*/

/*
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