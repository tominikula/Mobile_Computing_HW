package com.example.mc_homework

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
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
    fun getAll(): List<User>

    @Query("SELECT * FROM User WHERE uid = :uid")
    fun findUserById(uid: Int): User

    @Insert
    fun insertAll(vararg user: User)

    @Upsert
    fun upsertUser(user: User)

    @Update
    fun update(user: User)

}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
/*
    val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()
*/
    companion object {
        @Volatile
        private var Instance: RoomDatabase? = null
        fun getDatabase(applicationContext: Context): RoomDatabase {
            return Instance ?: synchronized(this){
                Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "database-name"
                ).build().also {Instance = it}
            }
        }
    }
}
