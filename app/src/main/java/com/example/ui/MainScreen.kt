package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SettingsScreen

enum class DailyPulseTab(val title: String, val icon: ImageVector, val tag: String) {
    TODAY("Today", Icons.Default.WbSunny, "tab_today"),
    HABITS("Habits", Icons.Default.Spa, "tab_habits"),
    REFLECT("Reflect", Icons.Default.AutoStories, "tab_reflect"),
    INSIGHTS("Insights", Icons.Default.BarChart, "tab_insights"),
    SETTINGS("Settings", Icons.Default.Settings, "tab_settings")
}

@Composable
fun MainScreen(
    viewModel: DailyPulseViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(DailyPulseTab.TODAY) }
    var isAboutScreenOpen by remember { mutableStateOf(false) }

    // Back handling
    if (isAboutScreenOpen) {
        BackHandler {
            isAboutScreenOpen = false
        }
    } else if (currentTab != DailyPulseTab.TODAY) {
        BackHandler {
            currentTab = DailyPulseTab.TODAY
        }
    }

    if (isAboutScreenOpen) {
        AboutScreen(
            onNavigateBack = { isAboutScreenOpen = false }
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    DailyPulseTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(tab.title) },
                            modifier = Modifier.testTag(tab.tag),
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            val screenModifier = Modifier.padding(innerPadding)

            // Smooth 3D depth transition across screens
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing)) +
                     scaleIn(initialScale = 0.97f, animationSpec = tween(200, easing = FastOutSlowInEasing)))
                    .togetherWith(
                        fadeOut(animationSpec = tween(140, easing = FastOutSlowInEasing)) +
                        scaleOut(targetScale = 1.02f, animationSpec = tween(140, easing = FastOutSlowInEasing))
                    )
                },
                label = "screen_3d_transition"
            ) { targetScreen ->
                when (targetScreen) {
                    DailyPulseTab.TODAY -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToHabits = { currentTab = DailyPulseTab.HABITS },
                            modifier = screenModifier
                        )
                    }
                    DailyPulseTab.HABITS -> {
                        HabitsScreen(
                            viewModel = viewModel,
                            modifier = screenModifier
                        )
                    }
                    DailyPulseTab.REFLECT -> {
                        JournalScreen(
                            viewModel = viewModel,
                            modifier = screenModifier
                        )
                    }
                    DailyPulseTab.INSIGHTS -> {
                        ProgressScreen(
                            viewModel = viewModel,
                            onNavigateToHomeForDate = {
                                currentTab = DailyPulseTab.TODAY
                            },
                            modifier = screenModifier
                        )
                    }
                    DailyPulseTab.SETTINGS -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigateToAbout = { isAboutScreenOpen = true },
                            modifier = screenModifier
                        )
                    }
                }
            }
        }
    }
}
