package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Topic

object TopicsRepository {

    private val topics = mutableListOf(
        Topic(0, 0,"Topic 1", 100),
        Topic(1, 0,"Topic 2", 32),
        Topic(2, 1,"Topic 3", 50),
        Topic(3, 1,"Topic 4", 51),
        Topic(4, 1,"Topic 5", 51),
        Topic(5, 1,"Topic 6", 51),
        Topic(6, 1,"Topic 7", 51),
        Topic(7, 1,"Topic 8", 51),
        Topic(8, 2,"Topic 9", 54),
        Topic(9, 2,"Topic 10", 23)
    )

    fun getTopics(): List<Topic> {
        return topics
    }

}