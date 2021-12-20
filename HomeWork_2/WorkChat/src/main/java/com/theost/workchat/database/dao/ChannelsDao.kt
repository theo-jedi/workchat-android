package com.theost.workchat.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.theost.workchat.database.entities.ChannelEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ChannelsDao {

    @Query("SELECT * FROM channels")
    fun getAll(): Single<List<ChannelEntity>>

    @Insert(onConflict = REPLACE)
    fun insertAll(channels: List<ChannelEntity>): Completable

    @Delete
    fun delete(channel: ChannelEntity): Completable

    @Query("DELETE FROM channels")
    fun deleteAll(): Completable

}