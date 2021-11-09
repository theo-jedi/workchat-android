package com.theost.workchat.network

import com.theost.workchat.data.models.dto.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface Api {

    @GET("users/{user_id}")
    fun getUser(
        @Path(value = "user_id", encoded = true)
        userId: Int
    ): Single<GetUserResponse>

    @GET("users/me")
    fun getCurrentUser(): Single<CurrentUserDto>

    @GET("users")
    fun getUsers(): Single<GetUsersResponse>

    @GET("streams")
    fun getChannels(): Single<GetChannelsResponse>

    @GET("users/me/subscriptions")
    fun getSubscribedChannels(): Single<GetSubscribedChannelsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getChannelTopics(
        @Path(value = "stream_id", encoded = true)
        channelId: Int
    ): Single<GetTopicsResponse>

    @GET("messages?anchor=newest")
    fun getMessages(
        @Query(value = "num_before", encoded = true)
        numBefore: Int,
        @Query(value = "num_after", encoded = true)
        numAfter: Int,
        @Query(value = "narrow", encoded = true)
        narrow: String
    ): Single<GetMessagesResponse>

    @FormUrlEncoded
    @POST("messages")
    fun sendMessage(
        @Field("to")
        stream: String,
        @Field("topic")
        topic: String,
        @Field("content")
        content: String,
        @Field("type")
        type: String
    ): Completable

}