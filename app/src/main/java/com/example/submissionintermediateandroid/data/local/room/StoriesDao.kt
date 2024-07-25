package com.example.submissionintermediateandroid.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.submissionintermediateandroid.data.remote.response.ListStoryItem

@Dao
interface StoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<ListStoryItem>)

    @Query("SELECT * FROM story")
    fun getAllStories(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAll()

}