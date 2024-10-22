package com.dicoding.dicodingeventapp.ui.settings

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.dicoding.dicodingeventapp.R
import com.dicoding.dicodingeventapp.databinding.FragmentSettingsBinding
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private lateinit var workManager: WorkManager
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    requireActivity(),
                    "Notifications permission rejected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = view.findViewById<SwitchMaterial>(R.id.switch_theme)
        val switchNotification = view.findViewById<SwitchMaterial>(R.id.switch_notification)

        val preferences = SettingsPreferences.getInstance(requireContext().dataStore)
        val factory = SettingViewModelFactory(preferences)
        val viewModel: SettingViewModel by viewModels {
            factory
        }

        viewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }


        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }

        workManager = WorkManager.getInstance(requireContext())

        viewModel.getNotificationSetting()
            .observe(viewLifecycleOwner) { isNotificationActive: Boolean ->
                if (isNotificationActive) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                    startPeriodicTask()
                    switchNotification.isChecked = true
                } else {
                    cancelPeriodicTask()
                    switchNotification.isChecked = false
                }
            }

        switchNotification.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveNotificationSetting(isChecked)
        }
    }

    private fun startPeriodicTask() {
        val data = Data.Builder().build()

        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(MyWorker::class.java, 1, TimeUnit.DAYS)
                .setInputData(data)
                .addTag("periodicTask")
                .build()

        workManager.enqueueUniquePeriodicWork(
            "periodicTask",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    private fun cancelPeriodicTask() {
        workManager.cancelUniqueWork("periodicTask")
    }
}