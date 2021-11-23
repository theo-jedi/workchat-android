package com.theost.workchat.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.theost.workchat.database.entities.TopicEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TopicsDao {

    @Query("SELECT * FROM topics")
    fun getAll(): Single<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE channel_id = :channelId")
    fun getChannelTopics(channelId: Int): Single<List<TopicEntity>>

    @Insert(onConflict = REPLACE)
    fun insertAll(topics: List<TopicEntity>): Completable

    @Delete
    fun delete(topic: TopicEntity): Completable

    @Query("DELETE FROM topics")
    fun deleteAll(): Completable

}