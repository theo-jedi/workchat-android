package com.theost.workchat.network.api

import com.theost.workchat.network.dto.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
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

    @FormUrlEncoded
    @POST("users/me/subscriptions")
    fun addChannel(
        @Field("subscriptions")
        stream: String
    ): Single<CreateChannelResponse>

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

    @GET("messages")
    fun getMessages(
        @Query(value = "anchor")
        anchor: Int,
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

    @FormUrlEncoded
    @PATCH("messages/{msg_id}")
    fun editMessage(
        @Path("msg_id")
        messageId: Int,
        @Field("content")
        content: String,
    ): Completable

    @DELETE("messages/{msg_id}")
    fun deleteMessage(
        @Path("msg_id")
        messageId: Int
    ): Single<DeleteMessageResponse>

    @Multipart
    @POST("user_uploads")
    fun addPhoto(
        @Part
        photo: MultipartBody.Part
    ): Single<AddPhotoResponse>

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