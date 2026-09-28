package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.game.Direction
import com.example.ui.components.DpadControls
import com.example.ui.components.GameBoard
import com.example.ui.components.PauseDialog
import com.example.ui.components.TopHudBar
import com.example.ui.components.VictoryDialog
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

@Composable
fun GameScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val level = viewModel.activeLevel
  val engine = viewModel.gameEngine
  val config = viewModel.config

  BackHandler {
    if (viewModel.isPaused) {
      viewModel.isPaused = false
    } else {
      viewModel.navigateTo(if (viewModel.isEndlessActive) GameScreen.ENDLESS else GameScreen.CAMPAIGN_SELECT)
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFF0F172A), Color(0xFF020617))
        )
      )
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 560.dp)
        .align(Alignment.Center)
    ) {
      // Top HUD Bar
      TopHudBar(
        level = level,
        moveCount = viewModel.moveCount,
        canUndo = engine.canUndo(),
        isHintCalculating = viewModel.isCalculatingHint,
        hasActiveHint = viewModel.activeHintSteps.isNotEmpty(),
        onBack = {
          viewModel.navigateTo(if (viewModel.isEndlessActive) GameScreen.ENDLESS else GameScreen.CAMPAIGN_SELECT)
        },
        onUndo = { viewModel.undoMove() },
        onRestart = { viewModel.restartCurrentLevel() },
        onHint = { viewModel.requestHint() },
        onPause = { viewModel.isPaused = true }
      )

      Spacer(modifier = Modifier.weight(0.1f))

      // Responsive Central GameBoard
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
      ) {
        GameBoard(
          level = level,
          orbs = engine.currentOrbs,
          gateStates = engine.gateStates,
          hintDirection = viewModel.activeHintSteps.firstOrNull(),
          swipeSensitivity = config.swipeSensitivity,
          onMove = { dir -> viewModel.makeMove(dir) }
        )
      }

      Spacer(modifier = Modifier.weight(0.1f))

      // Optional On-Screen D-Pad for Tap / Click controls
      if (config.showDpadControls) {
        DpadControls(
          onDirection = { dir -> viewModel.makeMove(dir) },
          modifier = Modifier.padding(bottom = 24.dp)
        )
      } else {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }

    // Pause Dialog
    if (viewModel.isPaused) {
      PauseDialog(
        sfxVolume = config.sfxVolume,
        musicVolume = config.musicVolume,
        hapticsEnabled = config.hapticsEnabled,
        onSfxVolumeChange = { viewModel.updateSfxVolume(it) },
        onMusicVolumeChange = { viewModel.updateMusicVolume(it) },
        onHapticsToggle = { viewModel.updateHaptics(it) },
        onResume = { viewModel.isPaused = false },
        onRestart = {
          viewModel.restartCurrentLevel()
          viewModel.isPaused = false
        },
        onExitToMenu = {
          viewModel.isPaused = false
          viewModel.navigateTo(GameScreen.MENU)
        }
      )
    }

    // Victory Celebration Dialog
    if (viewModel.isCompleted) {
      VictoryDialog(
        stars = viewModel.starsEarned,
        moves = viewModel.moveCount,
        parMoves = level.parMoves,
        isEndless = viewModel.isEndlessActive,
        onNextLevel = { viewModel.advanceToNextLevel() },
        onReplay = { viewModel.restartCurrentLevel() },
        onMenu = {
          viewModel.navigateTo(if (viewModel.isEndlessActive) GameScreen.ENDLESS else GameScreen.CAMPAIGN_SELECT)
        }
      )
    }
  }
}
