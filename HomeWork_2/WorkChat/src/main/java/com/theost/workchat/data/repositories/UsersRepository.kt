package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.User

/* Todo RxJava */
object UsersRepository {

    private val users = mutableListOf(
        User(0, "Theo Jedi", "May the force be with you", "https://link", mutableListOf(0)),
        User(1, "Obi Wan", "May the force be with you", "https://link", mutableListOf(0)),
        User(2, "General Grievous", "You Think You Can Defeat Me?", "https://link", mutableListOf(0))
    )

    fun getUsers(): List<User> {
        return users
    }

    fun getUser(id: Int): User? {
        return users.find { it.id == id }
    }

    fun addUser(name: String, status: String, avatar: String, dialogsIds: List<Int>): Boolean {
        return simulateUserCreation(name, status, avatar, dialogsIds)
    }

    fun removeUser(id: Int): Boolean {
        users.removeAll { it.id == id }
        return true
    }

    fun removeUsers(ids: List<Int>): Boolean {
        users.removeAll { it.id in ids }
        return true
    }

    private fun simulateUserCreation(
        name: String,
        status: String,
        avatar: String,
        dialogsIds: List<Int>
    ): Boolean {
        users.add(
            User(
                id = users.size,
                name = name,
                status = status,
                avatar = avatar,
                dialogsIds = dialogsIds
            )
        )
        return true
    }

}