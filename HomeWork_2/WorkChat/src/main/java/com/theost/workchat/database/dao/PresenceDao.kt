package com.theost.workchat.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.theost.workchat.database.entities.PresenceEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface PresenceDao {

    @Query("SELECT * FROM presence")
    fun getAll(): Single<List<PresenceEntity>>

    @Query("SELECT * FROM presence WHERE user_id = :userId")
    fun getPresence(userId: Int): Single<PresenceEntity>

    @Insert(onConflict = REPLACE)
    fun insertAll(presence: List<PresenceEntity>): Completable

    @Query("DELETE FROM presence WHERE user_id = :userId")
    fun deletePresence(userId: Int): Completable

    @Delete
    fun delete(presenceEntity: PresenceEntity): Completable

    @Query("DELETE FROM presence")
    fun deleteAll(): Completable

}