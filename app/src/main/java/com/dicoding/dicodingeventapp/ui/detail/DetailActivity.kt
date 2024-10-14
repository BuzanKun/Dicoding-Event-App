package com.dicoding.dicodingeventapp.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.dicodingeventapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailViewMode = ViewModelProvider(this)[DetailViewModel::class.java]

        val id = intent.getIntExtra("id", 0)
        detailViewMode.findEventDetail(id)

        detailViewMode.eventDetail.observe(this) { eventDetail ->
            if (eventDetail != null) {
                Glide.with(this)
                    .load(eventDetail.mediaCover)
                    .into(binding.ivEventImage)
                binding.tvEventSummary.text = eventDetail.summary
                binding.tvEventName.text = eventDetail.name
                binding.tvEventDescription.text = HtmlCompat.fromHtml(
                    eventDetail.description.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }

        detailViewMode.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        detailViewMode.errorMessage.observe(this) { it ->
            it?.getContentIfNotHandled()?.let { errorMessage ->
                errorMessage.let {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}