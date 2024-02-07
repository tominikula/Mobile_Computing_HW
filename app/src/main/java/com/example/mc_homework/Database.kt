package com.example.mc_homework

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity
data class User(
    @PrimaryKey val uid: Int = 0,
    @ColumnInfo(name = "username") val userName: String?,
    @ColumnInfo(name = "image") val image: String?
)

@Dao
interface UserDao{

    @Query("SELECT * FROM User WHERE uid = :uid LIMIT 1")
    fun findUserById(uid: Int): User

    @Query("SELECT * FROM User WHERE username LIKE :name LIMIT 1 ")
    fun findUserByName(name: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDefaultUser(user: User)

    @Update
    fun updateUser(user: User)
}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    companion object {
        @Volatile
        private var Instance: AppDatabase? = null
        fun getDatabase(applicationContext: Context): AppDatabase {
            return Instance ?: synchronized(this){
                Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "database-name"
                ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build().also {Instance = it}
            }
        }
    }
}
