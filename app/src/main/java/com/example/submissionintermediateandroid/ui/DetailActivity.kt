package com.example.submissionintermediateandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.submissionintermediateandroid.data.remote.response.ListStoryItem
import com.example.submissionintermediateandroid.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getStoryData()
    }

    private fun getStoryData() {
        val story = intent.getParcelableExtra("story") as? ListStoryItem

        Glide.with(this)
            .load(story?.photoUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = story?.name
        binding.tvDetailDescription.text = story?.description
    }

}