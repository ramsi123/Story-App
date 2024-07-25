package com.example.submissionintermediateandroid

import com.example.submissionintermediateandroid.data.remote.response.ListStoryItem

object DataDummy {

    val token = "initoken"

    fun generateDummyStoriesResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                "photoUrl + $i",
                "createdAt + $i",
                "name + $i",
                "description + $i",
                i.toDouble(),
                i.toString(),
                i.toDouble()
            )
            items.add(quote)
        }
        return items
    }

}