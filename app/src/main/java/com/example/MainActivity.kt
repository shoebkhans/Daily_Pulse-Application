package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DailyPulseViewModel
import com.example.ui.MainScreen
import com.example.ui.theme.DailyPulseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val pulseViewModel: DailyPulseViewModel = viewModel()
            val themePreference by pulseViewModel.themePreference.collectAsStateWithLifecycle()
            val colorOption by pulseViewModel.colorOption.collectAsStateWithLifecycle()
            val reminderEnabled by pulseViewModel.reminderEnabled.collectAsStateWithLifecycle()

            // Request notification permission if reminders are enabled on Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* granted / denied handled gracefully */ }

            LaunchedEffect(reminderEnabled) {
                if (reminderEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!hasPermission) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            DailyPulseTheme(
                themePreference = themePreference,
                colorOption = colorOption
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(viewModel = pulseViewModel)
                }
            }
        }
    }
}
