package com.theost.workchat.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.theost.workchat.database.entities.ReactionEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ReactionsDao {

    @Query("SELECT * FROM reactions")
    fun getAll(): Single<List<ReactionEntity>>

    @Insert(onConflict = REPLACE)
    fun insertAll(reactions: List<ReactionEntity>): Completable

    @Delete
    fun delete(reaction: ReactionEntity): Completable

    @Query("DELETE FROM reactions")
    fun deleteAll(): Completable

}