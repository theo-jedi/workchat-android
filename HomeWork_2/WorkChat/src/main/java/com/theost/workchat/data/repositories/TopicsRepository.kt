package com.theost.workchat.data.repositories

import com.theost.workchat.data.models.core.Topic

object TopicsRepository {

    private val topics = mutableListOf(
        Topic(0, 0,"#main", 100),
        Topic(1, 0,"#testing", 32),
        Topic(2, 1,"#main", 31),
        Topic(3, 1,"#testing", 23),
        Topic(4, 1,"#dev", 60),
        Topic(5, 1,"#offtop", 3),
        Topic(6, 1,"#bruh", 5),
        Topic(7, 1,"#jokes", 25),
        Topic(8, 2,"#hello", 54),
        Topic(9, 2,"#world", 23)
    )

    fun getTopics(): List<Topic> {
        return topics
    }

}