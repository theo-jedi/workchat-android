package com.theost.workchat.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.theost.workchat.database.entities.MessageEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MessagesDao {

    @Query("SELECT * FROM messages")
    fun getAll(): Single<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE channel_name = :channelName AND topic_name = :topicName")
    fun getDialogMessages(channelName: String, topicName: String): Single<List<MessageEntity>>

    @Insert(onConflict = REPLACE)
    fun insertAll(messages: List<MessageEntity>): Completable

    @Delete
    fun delete(message: MessageEntity): Completable

    @Query("DELETE FROM messages WHERE id = :messageId")
    fun delete(messageId: Int): Completable

    @Query("DELETE FROM messages WHERE channel_name = :channelName AND topic_name = :topicName")
    fun deleteTopicMessages(channelName: String, topicName: String): Completable

    @Query("DELETE FROM messages")
    fun deleteAll(): Completable

}