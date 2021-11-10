package com.theost.workchat.data.models.state

enum class UserStatus(val apiName: String) {
    IDLE("idle"), ONLINE("active"), OFFLINE("")
}