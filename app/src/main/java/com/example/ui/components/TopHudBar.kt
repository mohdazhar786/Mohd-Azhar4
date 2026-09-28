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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.LevelData

@Composable
fun TopHudBar(
  level: LevelData,
  moveCount: Int,
  canUndo: Boolean,
  isHintCalculating: Boolean,
  hasActiveHint: Boolean,
  onBack: () -> Unit,
  onUndo: () -> Unit,
  onRestart: () -> Unit,
  onHint: () -> Unit,
  onPause: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = Color(0xFF0B1120),
    tonalElevation = 4.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Back to Menu / Level Select
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(48.dp)
            .testTag("hud_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Exit to Menu",
            tint = Color(0xFF94A3B8)
          )
        }

        // Title and Chapter
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = level.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = level.chapter,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF38BDF8)
          )
        }

        // Pause Button
        IconButton(
          onClick = onPause,
          modifier = Modifier
            .size(48.dp)
            .testTag("hud_pause_button")
        ) {
          Icon(
            imageVector = Icons.Default.Pause,
            contentDescription = "Pause Game",
            tint = Color(0xFF94A3B8)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Bottom Row: Moves/Par Tracker & Action Controls
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Move counter and star tier
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("hud_move_counter")
        ) {
          Text(
            text = "Moves: ",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp
          )
          Text(
            text = "$moveCount",
            fontWeight = FontWeight.Bold,
            color = if (moveCount <= level.parMoves) Color(0xFF38BDF8) else Color(0xFFF59E0B),
            fontSize = 14.sp
          )
          Text(
            text = " / Par ${level.parMoves}",
            color = Color(0xFF64748B),
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.width(8.dp))

          // Star indicators based on current moves vs par
          val currentStars = when {
            moveCount <= level.parMoves -> 3
            moveCount <= level.parMoves + 2 -> 2
            else -> 1
          }
          Row {
            for (i in 1..3) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (i <= currentStars) Color(0xFFFBBF24) else Color(0xFF334155),
                modifier = Modifier.size(15.dp)
              )
            }
          }
        }

        // Gameplay actions: Undo, Restart, Hint
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Undo Button
          IconButton(
            onClick = onUndo,
            enabled = canUndo,
            modifier = Modifier
              .size(48.dp)
              .testTag("hud_undo_button")
          ) {
            Icon(
              imageVector = Icons.Default.Undo,
              contentDescription = "Undo Move",
              tint = if (canUndo) Color(0xFF38BDF8) else Color(0xFF475569)
            )
          }

          // Restart Button
          IconButton(
            onClick = onRestart,
            modifier = Modifier
              .size(48.dp)
              .testTag("hud_restart_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Restart Level",
              tint = Color(0xFF94A3B8)
            )
          }

          // Hint Assistant Button
          IconButton(
            onClick = onHint,
            enabled = !isHintCalculating,
            modifier = Modifier
              .size(48.dp)
              .testTag("hud_hint_button")
          ) {
            if (isHintCalculating) {
              CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color(0xFFFBBF24),
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Hint Assistant",
                tint = if (hasActiveHint) Color(0xFFFBBF24) else Color(0xFF94A3B8)
              )
            }
          }
        }
      }
    }
  }
}
