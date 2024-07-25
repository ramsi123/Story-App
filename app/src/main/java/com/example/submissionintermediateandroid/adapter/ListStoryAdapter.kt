package com.example.submissionintermediateandroid.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionintermediateandroid.data.remote.response.ListStoryItem
import com.example.submissionintermediateandroid.databinding.ItemStoryBinding
import com.example.submissionintermediateandroid.ui.DetailActivity

class ListStoryAdapter : PagingDataAdapter<ListStoryItem, ListStoryAdapter.MyViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.ivItemPhoto)
            binding.tvItemName.text = data.name
            binding.tvItemDescription.text = data.description

            itemView.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivItemPhoto, "photo"),
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.tvItemDescription, "description"),
                    )

                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("story", data)

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}