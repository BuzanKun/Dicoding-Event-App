package com.dicoding.dicodingeventapp.ui.detail

import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.dicodingeventapp.databinding.ActivityDetailBinding
import com.dicoding.eldenringwiki.GoBackActivity
import com.google.android.material.snackbar.Snackbar

class DetailActivity : GoBackActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        val id = intent.getIntExtra("id", 0)
        detailViewModel.findEventDetail(id)

        detailViewModel.eventDetail.observe(this) { eventDetail ->
            if (eventDetail != null) {
                supportActionBar?.title = eventDetail.name
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

        detailViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        detailViewModel.errorMessage.observe(this) { it ->
            it?.getContentIfNotHandled()?.let { errorMessage ->
                errorMessage.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}