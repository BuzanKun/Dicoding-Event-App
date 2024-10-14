package com.dicoding.dicodingeventapp.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingeventapp.data.response.ListEventsItem
import com.dicoding.dicodingeventapp.databinding.ActivitySearchableBinding
import com.dicoding.dicodingeventapp.ui.ListEventAdapter
import com.dicoding.dicodingeventapp.ui.ListViewModel
import com.dicoding.eldenringwiki.GoBackActivity
import com.google.android.material.snackbar.Snackbar

class SearchableActivity : GoBackActivity() {
    private lateinit var binding: ActivitySearchableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchableViewModel = ViewModelProvider(this)[ListViewModel::class.java]

        val query = intent.getStringExtra("query")
        Log.e("query", query.toString())
        searchableViewModel.searchEvents(query)

        supportActionBar?.title = "Search result for: $query"

        val layoutManager = LinearLayoutManager(this)
        binding.rvEventList.layoutManager = layoutManager

        searchableViewModel.listEvent.observe(this) { eventList ->
            setEventList(eventList)
        }

        searchableViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        searchableViewModel.errorMessage.observe(this) { it ->
            it?.getContentIfNotHandled()?.let { errorMessage ->
                errorMessage.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun setEventList(eventList: List<ListEventsItem>) {
        val adapter = ListEventAdapter()
        adapter.submitList(eventList)
        binding.rvEventList.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}