package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

@Composable
fun VictoryDialog(
  stars: Int,
  moves: Int,
  parMoves: Int,
  isEndless: Boolean,
  onNextLevel: () -> Unit,
  onReplay: () -> Unit,
  onMenu: () -> Unit
) {
  // Animated scale for stars
  val star1Scale = remember { Animatable(0f) }
  val star2Scale = remember { Animatable(0f) }
  val star3Scale = remember { Animatable(0f) }

  LaunchedEffect(stars) {
    if (stars >= 1) {
      delay(120)
      star1Scale.animateTo(1.25f, tween(150, easing = FastOutSlowInEasing))
      star1Scale.animateTo(1.0f, tween(100))
    }
    if (stars >= 2) {
      delay(120)
      star2Scale.animateTo(1.25f, tween(150, easing = FastOutSlowInEasing))
      star2Scale.animateTo(1.0f, tween(100))
    }
    if (stars >= 3) {
      delay(120)
      star3Scale.animateTo(1.25f, tween(150, easing = FastOutSlowInEasing))
      star3Scale.animateTo(1.0f, tween(100))
    }
  }

  Dialog(onDismissRequest = {}) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("victory_dialog")
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        Text(
          text = if (isEndless) "STAGE CLEARED!" else "LEVEL SOLVED!",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Black,
          color = Color(0xFF38BDF8)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3 Animated Stars
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          // Star 1
          Box(modifier = Modifier.scale(if (stars >= 1) star1Scale.value else 1f)) {
            Icon(
              imageVector = if (stars >= 1) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = "Star 1",
              tint = if (stars >= 1) Color(0xFFFBBF24) else Color(0xFF334155),
              modifier = Modifier.size(46.dp)
            )
          }

          // Star 2 (slightly larger center)
          Box(
            modifier = Modifier
              .padding(horizontal = 8.dp)
              .scale(if (stars >= 2) star2Scale.value else 1f)
          ) {
            Icon(
              imageVector = if (stars >= 2) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = "Star 2",
              tint = if (stars >= 2) Color(0xFFFBBF24) else Color(0xFF334155),
              modifier = Modifier.size(56.dp)
            )
          }

          // Star 3
          Box(modifier = Modifier.scale(if (stars >= 3) star3Scale.value else 1f)) {
            Icon(
              imageVector = if (stars >= 3) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = "Star 3",
              tint = if (stars >= 3) Color(0xFFFBBF24) else Color(0xFF334155),
              modifier = Modifier.size(46.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Performance summary badge
        Row(
          horizontalArrangement = Arrangement.SpaceAround,
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B), RoundedCornerShape(14.dp))
            .padding(14.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("YOUR MOVES", fontSize = 11.sp, color = Color(0xFF94A3B8))
            Text(
              "$moves",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("PAR TARGET", fontSize = 11.sp, color = Color(0xFF94A3B8))
            Text(
              "$parMoves",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF38BDF8)
            )
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("RATING", fontSize = 11.sp, color = Color(0xFF94A3B8))
            val ratingText = when (stars) {
              3 -> "PERFECT"
              2 -> "GREAT"
              else -> "CLEARED"
            }
            Text(
              ratingText,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFFBBF24)
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Button(
          onClick = onNextLevel,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("victory_next_button")
        ) {
          Text("NEXT LEVEL", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedButton(
            onClick = onReplay,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("victory_replay_button")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(6.dp))
            Text("REPLAY", color = Color(0xFF38BDF8))
          }

          OutlinedButton(
            onClick = onMenu,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("victory_menu_button")
          ) {
            Text("LEVELS", color = Color(0xFF94A3B8))
          }
        }
      }
    }
  }
}
