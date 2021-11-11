package com.theost.workchat.network.api

import com.theost.workchat.network.dto.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface Api {

    @GET("users")
    fun getUsers(): Single<GetUsersResponse>

    @GET("users/{user_id}")
    fun getUser(
        @Path(value = "user_id")
        userId: Int
    ): Single<GetUserResponse>

    @GET("users/me")
    fun getCurrentUser(): Single<CurrentUserDto>

    @GET("users/{user_id}/presence")
    fun getUserPresence(
        @Path(value = "user_id")
        userId: Int
    ): Single<GetUserPresenceResponse>

    @GET("streams")
    fun getChannels(): Single<GetChannelsResponse>

    @GET("users/me/subscriptions")
    fun getSubscribedChannels(): Single<GetSubscribedChannelsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getChannelTopics(
        @Path(value = "stream_id")
        channelId: Int
    ): Single<GetTopicsResponse>

    @GET("messages?anchor=newest")
    fun getMessages(
        @Query(value = "num_before")
        numBefore: Int,
        @Query(value = "num_after")
        numAfter: Int,
        @Query(value = "narrow")
        narrow: String
    ): Single<GetMessagesResponse>

    @FormUrlEncoded
    @POST("messages")
    fun addMessage(
        @Field("to")
        stream: String,
        @Field("topic")
        topic: String,
        @Field("content")
        content: String,
        @Field("type")
        type: String = "stream"
    ): Completable

    @GET("/static/generated/emoji/emoji_codes.json")
    fun getReactions(): Single<GetReactionsResponse>

    @FormUrlEncoded
    @POST("messages/{message_id}/reactions")
    fun addReaction(
        @Path("message_id")
        messageId: Int,
        @Field("emoji_name")
        emojiName: String
    ): Completable

    @DELETE("messages/{message_id}/reactions")
    fun removeReaction(
        @Path("message_id")
        messageId: Int,
        @Query("emoji_name")
        emojiName: String,
        @Query("emoji_code")
        emojiCode: String,
        @Query("reaction_type")
        reactionType: String
    ): Completable

}