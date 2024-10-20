package com.dicoding.dicodingeventapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.dicodingeventapp.data.Result
import com.dicoding.dicodingeventapp.databinding.ActivityDetailBinding
import com.dicoding.dicodingeventapp.ui.EventViewModel
import com.dicoding.dicodingeventapp.ui.ViewModelFactory
import com.dicoding.dicodingeventapp.ui.utils.GoBackActivity

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
                        Glide.with(this)
                            .load(event.mediaCover)
                            .into(binding.ivEventImage)
                        binding.tvEventSummary.text = event.summary
                        binding.tvEventName.text = event.name
                        binding.tvEventOwner.text = event.ownerName
                        binding.tvEventTime.text = event.beginTime
                        binding.tvEventQuota.text = viewModel.calculateRemainingQuota(
                            event.quota!!,
                            event.registrants!!
                        ).toString()
                        binding.tvEventDescription.text = HtmlCompat.fromHtml(
                            event.description.toString(),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        binding.btEventLink.setOnClickListener {
                            val url = event.link
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(url)
                            startActivity(intent)
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Log.e("DetailActivity", "Error: ${result.error}")
                    }
                }
            }
        }
    }
}
