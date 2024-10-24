package com.dicoding.dicodingeventapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.dicodingeventapp.R
import com.dicoding.dicodingeventapp.data.Result
import com.dicoding.dicodingeventapp.databinding.ActivityDetailBinding
import com.dicoding.dicodingeventapp.ui.EventViewModel
import com.dicoding.dicodingeventapp.ui.ViewModelFactory
import com.dicoding.dicodingeventapp.ui.utils.GoBackActivity
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class DetailActivity : GoBackActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: EventViewModel by viewModels {
            factory
        }
        val ivFavorite = binding.fabFavorite

        val id = intent.getIntExtra("id", 0)
        if (id != 0) {
            viewModel.getEventById(id).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val event = result.data // This is of type EventEntity
                        supportActionBar?.title = event.name
                        binding.apply{
                            Glide.with(binding.root)
                                .load(event.mediaCover)
                                .into(binding.ivEventImage)
                            binding.tvEventSummary.text = event.summary
                            binding.tvEventName.text = event.name
                            binding.tvEventOwner.text = event.ownerName
                            binding.tvEventTime.text = event.beginTime
                            binding.tvEventQuota.text = String.format(
                                Locale.getDefault(), "%,d",
                                viewModel.calculateRemainingQuota(event.quota!!, event.registrants!!)
                            )
                            binding.tvEventDescription.text = HtmlCompat.fromHtml(
                                event.description.toString(),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                            binding.btnEventLink.setOnClickListener {
                                val url = event.link
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                startActivity(intent)
                            }
                        }
                        if (event.isFavorite) {
                            ivFavorite.setImageDrawable(
                                ContextCompat.getDrawable(
                                    ivFavorite.context,
                                    R.drawable.ic_favorite_full_24
                                )
                            )
                        } else {
                            ivFavorite.setImageDrawable(
                                ContextCompat.getDrawable(
                                    ivFavorite.context,
                                    R.drawable.ic_favorite_border_24
                                )
                            )
                        }

                        ivFavorite.setOnClickListener {
                            if (event.isFavorite) {
                                viewModel.deleteFavoriteEvent(event)
                            } else {
                                viewModel.saveFavoriteEvent(event)
                            }
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Log.e("DetailActivity", "Error: ${result.error}")
                        Snackbar.make(
                            this,
                            binding.root,
                            "Error Occurred: ${result.error}",
                            Snackbar.LENGTH_SHORT
                        ).setAction("Dismiss") {
                        }.show()
                    }
                }
            }
        }
    }
}
