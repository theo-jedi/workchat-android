package com.theost.workchat.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.theost.workchat.database.entities.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface UsersDao {

    @Query("SELECT * FROM users")
    fun getAll(): Single<List<UserEntity>>

    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUser(id: Int): Single<UserEntity>

    @Insert(onConflict = REPLACE)
    fun insertAll(users: List<UserEntity>): Completable

    @Delete
    fun delete(user: UserEntity): Completable

    @Query("DELETE FROM users")
    fun deleteAll(): Completable

}