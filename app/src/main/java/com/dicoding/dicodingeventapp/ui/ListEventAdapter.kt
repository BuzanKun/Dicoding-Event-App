package com.dicoding.dicodingeventapp.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.dicodingeventapp.R
import com.dicoding.dicodingeventapp.data.local.entity.EventEntity
import com.dicoding.dicodingeventapp.databinding.ItemEventRowBinding
import com.dicoding.dicodingeventapp.ui.detail.DetailActivity

class ListEventAdapter(private val onFavoriteClick: (EventEntity) -> Unit) : ListAdapter<EventEntity, ListEventAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding =
            ItemEventRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("id", event.id)
            holder.itemView.context.startActivity(intent)
        }

        val ivFavorite = holder.binding.fabFavorite
        if (event.isFavorite) {
            ivFavorite.setImageDrawable(ContextCompat.getDrawable(ivFavorite.context, R.drawable.ic_favorite_full_24))
        } else {
            ivFavorite.setImageDrawable(ContextCompat.getDrawable(ivFavorite.context, R.drawable.ic_favorite_border_24))
        }

        ivFavorite.setOnClickListener {
            onFavoriteClick(event)
            notifyItemChanged(position)
        }

    }

    class MyViewHolder(internal val binding: ItemEventRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            Glide.with(binding.root.context)
                .load(event.mediaCover)
                .into(binding.ivEventImage)
            binding.tvEventName.text = event.name
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(
                oldItem: EventEntity,
                newItem: EventEntity
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: EventEntity,
                newItem: EventEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}