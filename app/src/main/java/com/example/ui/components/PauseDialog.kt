package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PauseDialog(
  sfxVolume: Float,
  musicVolume: Float,
  hapticsEnabled: Boolean,
  onSfxVolumeChange: (Float) -> Unit,
  onMusicVolumeChange: (Float) -> Unit,
  onHapticsToggle: (Boolean) -> Unit,
  onResume: () -> Unit,
  onRestart: () -> Unit,
  onExitToMenu: () -> Unit
) {
  Dialog(onDismissRequest = onResume) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("pause_dialog")
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        Text(
          text = "GAME PAUSED",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SFX Volume Slider
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.GraphicEq,
            contentDescription = "SFX Volume",
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = "SFX: ${(sfxVolume * 100).toInt()}%",
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
              .weight(1f)
              .padding(start = 12.dp)
          )
        }
        Slider(
          value = sfxVolume,
          onValueChange = onSfxVolumeChange,
          colors = SliderDefaults.colors(
            thumbColor = Color(0xFF38BDF8),
            activeTrackColor = Color(0xFF0284C7)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("slider_sfx")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Music Volume Slider
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Music Volume",
            tint = Color(0xFFC084FC),
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = "Music: ${(musicVolume * 100).toInt()}%",
            color = Color(0xFF94A3B8),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
              .weight(1f)
              .padding(start = 12.dp)
          )
        }
        Slider(
          value = musicVolume,
          onValueChange = onMusicVolumeChange,
          colors = SliderDefaults.colors(
            thumbColor = Color(0xFFC084FC),
            activeTrackColor = Color(0xFF9333EA)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("slider_music")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Haptics switch
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Vibration,
              contentDescription = "Haptics",
              tint = Color(0xFFF59E0B),
              modifier = Modifier.size(24.dp)
            )
            Text(
              text = "Haptic Vibration",
              color = Color.White,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(start = 12.dp)
            )
          }
          Switch(
            checked = hapticsEnabled,
            onCheckedChange = onHapticsToggle,
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color(0xFFF59E0B),
              checkedTrackColor = Color(0xFFB45309)
            ),
            modifier = Modifier.testTag("switch_haptics")
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Button(
          onClick = onResume,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("pause_resume_button")
        ) {
          Text("RESUME", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = onRestart,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("pause_restart_button")
        ) {
          Text("RESTART LEVEL", color = Color(0xFF38BDF8))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = onExitToMenu,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("pause_menu_button")
        ) {
          Text("EXIT TO MENU", color = Color(0xFFEF4444))
        }
      }
    }
  }
}
