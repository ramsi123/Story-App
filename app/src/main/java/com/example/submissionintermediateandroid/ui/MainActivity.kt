package com.example.submissionintermediateandroid.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionintermediateandroid.R
import com.example.submissionintermediateandroid.adapter.ListStoryAdapter
import com.example.submissionintermediateandroid.adapter.LoadingStateAdapter
import com.example.submissionintermediateandroid.data.remote.response.ListStoryItem
import com.example.submissionintermediateandroid.databinding.ActivityMainBinding
import com.example.submissionintermediateandroid.ui.viewmodels.MainViewModel
import com.example.submissionintermediateandroid.ui.viewmodels.ViewModelFactory
import com.example.submissionintermediateandroid.util.dataStore

class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, application.dataStore)
    }
    private lateinit var adapter: ListStoryAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerList()

        // observing token & stories
        mainViewModel.token.observe(this) { token ->
            if (token.isNullOrEmpty()) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                mainViewModel.getStories().observe(this) {
                    getStoriesData(it)
                }
            }
        }

        // floating action button
        binding.fabUploadStory.setOnClickListener {
            startActivity(Intent(this, UploadStoryActivity::class.java))
        }

        // top app bar menu
        binding.topAppBarChild.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                R.id.menu2 -> {
                    mainViewModel.saveToken("")
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.menu3 -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    // get data list from adapter
    private fun getStoriesData(usersData: PagingData<ListStoryItem>) {
        adapter.submitData(lifecycle, usersData)
    }

    private fun setRecyclerList() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        adapter = ListStoryAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
    }

}