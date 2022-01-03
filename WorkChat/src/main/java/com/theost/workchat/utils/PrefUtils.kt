package com.theost.workchat.utils

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object PrefUtils {

    private const val APP_PREFERENCES = "app_preferences"

    private const val PREFERENCE_CURRENT_USER_ID = "current_user_id"
    private const val PREFERENCE_SUBSCRIBED_CHANNELS = "subscribed_channels"

    fun getCurrentUserId(context: Context): Int {
        return getIntData(context, PREFERENCE_CURRENT_USER_ID, -1)
    }

    fun putCurrentUserId(context: Context, userId: Int) {
        putIntData(context, PREFERENCE_CURRENT_USER_ID, userId)
    }

    fun getSubscribedChannels(context: Context): List<Int> {
        val jsonChannels = getStringData(context, PREFERENCE_SUBSCRIBED_CHANNELS)
        return if (jsonChannels.isNotEmpty()) {
            Json.decodeFromString(serializer(), jsonChannels)
        } else {
            listOf()
        }
    }

    fun putSubscribedChannels(context: Context, channels: List<Int>) {
        val jsonChannels = Json.encodeToString(serializer(), channels)
        putStringData(context, PREFERENCE_SUBSCRIBED_CHANNELS, jsonChannels)
    }

    fun putIntData(context: Context, key: String, value: Int) {
        context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).edit()
            .putInt(key, value).apply()
    }

    fun getIntData(context: Context, key: String): Int {
        return getIntData(context, key, 0)
    }

    fun getIntData(context: Context, key: String, defaultValue: Int): Int {
        return context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            .getInt(key, defaultValue)
    }

    fun putStringData(context: Context, key: String, value: String) {
        context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE).edit()
            .putString(key, value).apply()
    }

    fun getStringData(context: Context, key: String): String {
        return getStringData(context, key, "")
    }

    fun getStringData(context: Context, key: String, defaultValue: String): String {
        return context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(key, defaultValue).orEmpty()
    }

}