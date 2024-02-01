package com.example.mc_homework

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.Upsert

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: Int = 0,
    @ColumnInfo(name = "username") val userName: String?
)

@Dao
interface UserDao{
    @Query("SELECT * FROM users")
    fun getAll(): List<User>

    @Insert
    fun insertAll(vararg users: User)

    @Upsert
    fun upsertUser(users: User)

    @Update
    fun update(user: User)

    @Query("SELECT * FROM users WHERE uid = :uid")
    fun findEmployeeById(uid: Int): User
}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
