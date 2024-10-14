package com.dicoding.dicodingeventapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingeventapp.data.response.ListEventsItem
import com.dicoding.dicodingeventapp.databinding.FragmentUpcomingBinding
import com.dicoding.dicodingeventapp.ui.ListEventAdapter
import com.dicoding.dicodingeventapp.ui.ListViewModel
import com.google.android.material.snackbar.Snackbar

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val upcomingViewModel =
            ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListViewModel::class.java]

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEventList.layoutManager = layoutManager

        upcomingViewModel.findEvents(1)

        upcomingViewModel.listUpcomingEvent.observe(viewLifecycleOwner) { eventList ->
            setEventList(eventList)
        }

        upcomingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        upcomingViewModel.errorMessage.observe(viewLifecycleOwner) { it ->
            it?.getContentIfNotHandled()?.let { errorMessage ->
                errorMessage.let {
                    Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        return root
    }

    private fun setEventList(eventList: List<ListEventsItem>) {
        val adapter = ListEventAdapter()
        adapter.submitList(eventList)
        binding.rvEventList.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}