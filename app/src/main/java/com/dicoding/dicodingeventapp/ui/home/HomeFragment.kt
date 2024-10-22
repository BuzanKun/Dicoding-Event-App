package com.dicoding.dicodingeventapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingeventapp.data.Result
import com.dicoding.dicodingeventapp.databinding.FragmentHomeBinding
import com.dicoding.dicodingeventapp.ui.EventViewModel
import com.dicoding.dicodingeventapp.ui.ListEventAdapter
import com.dicoding.dicodingeventapp.ui.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: EventViewModel by viewModels {
            factory
        }

        val upcomingAdapter = ListEventAdapter { event ->
            if (event.isFavorite) {
                viewModel.deleteFavoriteEvent(event)
            } else {
                viewModel.saveFavoriteEvent(event)
            }
        }
        val finishedAdapter = ListEventAdapter { event ->
            if (event.isFavorite) {
                viewModel.deleteFavoriteEvent(event)
            } else {
                viewModel.saveFavoriteEvent(event)
            }
        }

        viewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding?.pbUpcoming?.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding?.pbUpcoming?.visibility = View.GONE
                        val eventData = result.data
                        val limitData = eventData.take(5)
                        upcomingAdapter.submitList(limitData)
                        binding?.rvEventListUpcoming?.apply {
                            layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            setHasFixedSize(true)
                            adapter = upcomingAdapter
                        }
                    }

                    is Result.Error -> {
                        binding?.pbUpcoming?.visibility = View.GONE
                        Snackbar.make(
                            requireView(),
                            "Error Occurred: ${result.error}",
                            Snackbar.LENGTH_SHORT
                        ).setAction("Dismiss") {
                        }.show()
                    }
                }
            }
        }

        viewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding?.pbFinished?.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding?.pbFinished?.visibility = View.GONE
                        val eventData = result.data
                        val limitData = eventData.take(5)
                        finishedAdapter.submitList(limitData)
                        binding?.rvEventListFinished?.apply {
                            layoutManager = LinearLayoutManager(context)
                            setHasFixedSize(true)
                            adapter = finishedAdapter
                        }
                    }

                    is Result.Error -> {
                        binding?.pbFinished?.visibility = View.GONE
                        Snackbar.make(
                            requireView(),
                            "Error Occurred: ${result.error}",
                            Snackbar.LENGTH_SHORT
                        ).setAction("Dismiss") {
                        }.show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}