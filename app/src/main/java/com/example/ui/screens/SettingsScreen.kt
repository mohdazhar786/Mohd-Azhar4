package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.config.GameConfig
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

@Composable
fun SettingsScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val config = viewModel.config
  var showResetConfirmDialog by remember { mutableStateOf(false) }

  BackHandler {
    viewModel.navigateTo(GameScreen.MENU)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF090D16))
  ) {
    // Header Row
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      IconButton(
        onClick = { viewModel.navigateTo(GameScreen.MENU) },
        modifier = Modifier.testTag("settings_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = Color.White
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column {
        Text(
          text = "GAME CONFIGURATION",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "Centralized Config & Preferences",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF38BDF8)
        )
      }
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .widthIn(max = 560.dp)
    ) {
      // AUDIO CONFIGURATION SECTION
      SettingsSectionHeader("AUDIO & SYNTHESIS")

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // SFX Volume Slider
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color(0xFF38BDF8))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "Sound Effects: ${(config.sfxVolume * 100).toInt()}%",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.weight(1f)
            )
          }
          Slider(
            value = config.sfxVolume,
            onValueChange = {
              viewModel.updateSfxVolume(it)
            },
            onValueChangeFinished = {
              viewModel.soundManager.playTargetReached()
            },
            colors = SliderDefaults.colors(
              thumbColor = Color(0xFF38BDF8),
              activeTrackColor = Color(0xFF0284C7)
            ),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Music Volume Slider
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFFC084FC))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "Ambient Music: ${(config.musicVolume * 100).toInt()}%",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              modifier = Modifier.weight(1f)
            )
          }
          Slider(
            value = config.musicVolume,
            onValueChange = { viewModel.updateMusicVolume(it) },
            colors = SliderDefaults.colors(
              thumbColor = Color(0xFFC084FC),
              activeTrackColor = Color(0xFF9333EA)
            ),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Haptics Switch
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Vibration, contentDescription = null, tint = Color(0xFFF59E0B))
              Spacer(modifier = Modifier.width(12.dp))
              Text("Haptic Feedback", color = Color.White, fontSize = 14.sp)
            }
            Switch(
              checked = config.hapticsEnabled,
              onCheckedChange = { viewModel.updateHaptics(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFF59E0B))
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // CONTROLS & ACCESSIBILITY SECTION
      SettingsSectionHeader("CONTROLS & ACCESSIBILITY")

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // On-Screen Dpad Switch
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color(0xFF38BDF8))
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text("On-Screen D-Pad", color = Color.White, fontSize = 14.sp)
                Text("Tap & click controls for PC/accessibility", color = Color(0xFF94A3B8), fontSize = 11.sp)
              }
            }
            Switch(
              checked = config.showDpadControls,
              onCheckedChange = { config.toggleDpadControls(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Swipe Gesture Sensitivity
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Swipe Sensitivity: ${"%.1f".format(config.swipeSensitivity)}x",
              color = Color.White,
              fontSize = 14.sp,
              modifier = Modifier.weight(1f)
            )
          }
          Slider(
            value = config.swipeSensitivity,
            onValueChange = { config.setSensitivity(it) },
            valueRange = 0.5f..2.0f,
            colors = SliderDefaults.colors(
              thumbColor = Color(0xFF10B981),
              activeTrackColor = Color(0xFF047857)
            ),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          // High Contrast Switch
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFFFBBF24))
              Spacer(modifier = Modifier.width(12.dp))
              Text("High Contrast Sockets", color = Color.White, fontSize = 14.sp)
            }
            Switch(
              checked = config.highContrastMode,
              onCheckedChange = { config.toggleHighContrast(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFBBF24))
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // DATA MANAGEMENT & PROGRESS RESET
      SettingsSectionHeader("DATA MANAGEMENT")

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Saved level scores, stars, and endless progress are stored locally in Room Database.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
          )

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = { showResetConfirmDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("settings_reset_progress_button")
          ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("RESET ALL PROGRESS & STATS", fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))
    }
  }

  // Safety Confirmation Dialog for Reset
  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text("Reset All Progress?", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Text("This will permanently clear all level stars, high scores, endless streaks, and restore default configurations. This action cannot be undone.")
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.resetAllProgress()
            showResetConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
        ) {
          Text("CONFIRM RESET")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("CANCEL")
        }
      }
    )
  }
}

@Composable
private fun SettingsSectionHeader(title: String) {
  Text(
    text = title,
    fontWeight = FontWeight.Bold,
    fontSize = 11.sp,
    letterSpacing = 1.sp,
    color = Color(0xFF64748B),
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 4.dp, bottom = 6.dp)
  )
}
