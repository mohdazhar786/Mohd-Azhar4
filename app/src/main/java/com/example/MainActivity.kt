package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.screens.EndlessScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.LevelEditorScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TutorialScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameScreen as ScreenType
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: GameViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val darkColors = darkColorScheme(
        primary = Color(0xFF38BDF8),
        secondary = Color(0xFFC084FC),
        tertiary = Color(0xFFF59E0B),
        background = Color(0xFF0F172A),
        surface = Color(0xFF1E293B)
      )

      MaterialTheme(colorScheme = darkColors) {
        Surface(
          modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
          color = MaterialTheme.colorScheme.background
        ) {
          // Camera Transitions between scenes and levels
          AnimatedContent(
            targetState = viewModel.currentScreen,
            transitionSpec = {
              when (targetState) {
                ScreenType.PLAYING -> {
                  // Forward slide & zoom into game scene
                  (slideInHorizontally { width -> width / 3 } + fadeIn())
                    .togetherWith(slideOutHorizontally { width -> -width / 3 } + fadeOut())
                }
                ScreenType.MENU -> {
                  // Pull back camera transition into menu
                  (slideInHorizontally { width -> -width / 3 } + fadeIn())
                    .togetherWith(slideOutHorizontally { width -> width / 3 } + fadeOut())
                }
                else -> {
                  (slideInHorizontally { width -> width / 4 } + fadeIn())
                    .togetherWith(slideOutHorizontally { width -> -width / 4 } + fadeOut())
                }
              }
            },
            label = "CameraSceneTransition"
          ) { screen ->
            when (screen) {
              ScreenType.MENU -> {
                MenuScreen(
                  totalStars = viewModel.totalStars.value ?: 0,
                  completedCount = viewModel.completedCount.value ?: 0,
                  endlessHighest = viewModel.endlessHighest,
                  onNavigate = { viewModel.navigateTo(it) }
                )
              }
              ScreenType.CAMPAIGN_SELECT -> {
                LevelSelectScreen(viewModel = viewModel)
              }
              ScreenType.PLAYING -> {
                GameScreen(viewModel = viewModel)
              }
              ScreenType.ENDLESS -> {
                EndlessScreen(viewModel = viewModel)
              }
              ScreenType.EDITOR -> {
                LevelEditorScreen(viewModel = viewModel)
              }
              ScreenType.TUTORIAL -> {
                TutorialScreen(viewModel = viewModel)
              }
              ScreenType.SETTINGS -> {
                SettingsScreen(viewModel = viewModel)
              }
              ScreenType.STATS -> {
                StatsScreen(viewModel = viewModel)
              }
            }
          }
        }
      }
    }
  }
}
