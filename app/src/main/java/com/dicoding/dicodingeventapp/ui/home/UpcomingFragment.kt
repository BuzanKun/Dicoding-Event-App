package com.dicoding.dicodingeventapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingeventapp.data.Result
import com.dicoding.dicodingeventapp.databinding.FragmentUpcomingBinding
import com.dicoding.dicodingeventapp.ui.EventViewModel
import com.dicoding.dicodingeventapp.ui.ListEventAdapter
import com.dicoding.dicodingeventapp.ui.ViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null

    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: EventViewModel by viewModels {
            factory
        }

        val eventAdapter = ListEventAdapter()
        binding?.root?.postDelayed({
            binding?.rvEventList?.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = eventAdapter
            }
        }, 100)

        viewModel.getEventByStatus(1).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        Log.d("LOADING", "Loading")
                        binding?.pbUpcoming?.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        Log.d("SUCCESS", "Success")
                        binding?.pbUpcoming?.visibility = View.GONE
                        val eventData = result.data
                        eventAdapter.submitList(eventData)
                    }
                    is Result.Error -> {
                        binding?.pbUpcoming?.visibility = View.GONE
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