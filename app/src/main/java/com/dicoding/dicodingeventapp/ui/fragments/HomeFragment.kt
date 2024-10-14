package com.dicoding.dicodingeventapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingeventapp.data.response.ListEventsItem
import com.dicoding.dicodingeventapp.databinding.FragmentHomeBinding
import com.dicoding.dicodingeventapp.ui.ListEventAdapter
import com.dicoding.dicodingeventapp.ui.ListViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[ListViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val layoutManagerHorizontal = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvEventListUpcoming.layoutManager = layoutManagerHorizontal

        val layoutManagerVertical = LinearLayoutManager(requireActivity())
        binding.rvEventListFinished.layoutManager = layoutManagerVertical

        homeViewModel.findEvents(1)
        homeViewModel.findEvents(0)

        homeViewModel.listUpcomingEvent.observe(viewLifecycleOwner) { eventList ->
            setEventListUpcoming(eventList)
        }
        homeViewModel.listFinishedEvent.observe(viewLifecycleOwner) { eventList ->
            setEventListFinished(eventList)
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoadingUpcoming(isLoading)
            showLoadingFinished(isLoading)
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { it ->
            it?.getContentIfNotHandled()?.let { errorMessage ->
                errorMessage.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        return root
    }

    private fun setEventListUpcoming(eventList: List<ListEventsItem>) {
        val adapter = ListEventAdapter()
        val limitList = eventList.take(5)
        adapter.submitList(limitList)
        binding.rvEventListUpcoming.adapter = adapter
    }

    private fun setEventListFinished(eventList: List<ListEventsItem>) {
        val adapter = ListEventAdapter()
        val limitList = eventList.take(5)
        adapter.submitList(limitList)
        binding.rvEventListFinished.adapter = adapter
    }

    private fun showLoadingUpcoming(isLoading: Boolean) {
        binding.pbUpcoming.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showLoadingFinished(isLoading: Boolean) {
        binding.pbFinished.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}