package com.example.mc_homework

import android.content.Context
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
import androidx.room.Upsert

@Entity
data class User(
    @PrimaryKey val uid: Int = 0,
    @ColumnInfo(name = "username") val userName: String?
)

@Dao
interface UserDao{
    @Query("SELECT * FROM User")

    suspend fun getAll(): List<User>
    @Query("SELECT * FROM User WHERE uid = :uid")

    suspend fun findUserById(uid: Int): User
    @Insert
    suspend fun insertAll(vararg user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun update(user: User)
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
                ).build().also {Instance = it}
            }
        }
    }
}
