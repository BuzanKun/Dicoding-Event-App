package com.dicoding.dicodingeventapp.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingeventapp.data.Result
import com.dicoding.dicodingeventapp.databinding.ActivitySearchableBinding
import com.dicoding.dicodingeventapp.ui.EventViewModel
import com.dicoding.dicodingeventapp.ui.ListEventAdapter
import com.dicoding.dicodingeventapp.ui.ViewModelFactory
import com.dicoding.dicodingeventapp.ui.utils.GoBackActivity

class SearchableActivity : GoBackActivity() {
    private lateinit var binding: ActivitySearchableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: EventViewModel by viewModels {
            factory
        }

        val query = intent.getStringExtra("query")
        Log.e("query", query.toString())

        supportActionBar?.title = "Search result for: $query"

        val eventAdapter = ListEventAdapter()
        binding.rvEventList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = eventAdapter
        }

        viewModel.searchEvents(query.toString()).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val eventData = result.data
                        eventAdapter.submitList(eventData)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Log.e("ERROR", result.error)
                    }
                }
            }
        }
    }

}