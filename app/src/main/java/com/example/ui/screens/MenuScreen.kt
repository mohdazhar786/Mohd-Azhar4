package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.GameScreen

@Composable
fun MenuScreen(
  totalStars: Int,
  completedCount: Int,
  endlessHighest: Int,
  onNavigate: (GameScreen) -> Unit,
  modifier: Modifier = Modifier
) {
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
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 32.dp)
        .widthIn(max = 480.dp)
        .align(Alignment.Center)
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Logo Icon / Pulse Emblem
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              colors = listOf(Color(0xFF0284C7), Color(0xFF0F172A))
            )
          )
          .border(2.dp, Color(0xFF38BDF8), CircleShape)
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = null,
          tint = Color(0xFF38BDF8),
          modifier = Modifier.size(44.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // App Title
      Text(
        text = "GRIDPULSE",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Black,
        letterSpacing = 4.sp,
        color = Color.White
      )
      Text(
        text = "QUANTUM LOGIC MATRIX",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        color = Color(0xFF38BDF8)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Progress Overview Badge
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Total Stars",
              tint = Color(0xFFFBBF24),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text("STARS", fontSize = 10.sp, color = Color(0xFF94A3B8))
              Text("$totalStars", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            }
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(28.dp)
              .background(Color(0xFF334155))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("SOLVED", fontSize = 10.sp, color = Color(0xFF94A3B8))
            Text("$completedCount", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 15.sp)
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(28.dp)
              .background(Color(0xFF334155))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("ENDLESS", fontSize = 10.sp, color = Color(0xFF94A3B8))
            Text("Tier $endlessHighest", fontWeight = FontWeight.Bold, color = Color(0xFFC084FC), fontSize = 15.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Menu Buttons List
      MenuActionButton(
        title = "CAMPAIGN QUEST",
        subtitle = "25 Handcrafted Logic Sectors",
        icon = Icons.Default.PlayArrow,
        color = Color(0xFF0284C7),
        testTag = "menu_btn_campaign",
        onClick = { onNavigate(GameScreen.CAMPAIGN_SELECT) }
      )

      Spacer(modifier = Modifier.height(12.dp))

      MenuActionButton(
        title = "ENDLESS GENERATOR",
        subtitle = "Infinite Reverse-Solvable Levels",
        icon = Icons.Default.AllInclusive,
        color = Color(0xFF8B5CF6),
        testTag = "menu_btn_endless",
        onClick = { onNavigate(GameScreen.ENDLESS) }
      )

      Spacer(modifier = Modifier.height(12.dp))

      MenuActionButton(
        title = "LEVEL WORKSHOP",
        subtitle = "Build, Validate & Export Custom Layouts",
        icon = Icons.Default.Build,
        color = Color(0xFFF59E0B),
        testTag = "menu_btn_editor",
        onClick = { onNavigate(GameScreen.EDITOR) }
      )

      Spacer(modifier = Modifier.height(12.dp))

      MenuActionButton(
        title = "HOW TO PLAY",
        subtitle = "Interactive Tutorial & Rules",
        icon = Icons.Default.HelpOutline,
        color = Color(0xFF10B981),
        testTag = "menu_btn_tutorial",
        onClick = { onNavigate(GameScreen.TUTORIAL) }
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Bottom Row: Settings & Statistics
      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedButton(
          onClick = { onNavigate(GameScreen.SETTINGS) },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("menu_btn_settings")
        ) {
          Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("SETTINGS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = { onNavigate(GameScreen.STATS) },
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("menu_btn_stats")
        ) {
          Icon(Icons.Default.Leaderboard, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("STATS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun MenuActionButton(
  title: String,
  subtitle: String,
  icon: ImageVector,
  color: Color,
  testTag: String,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .height(68.dp)
      .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
      .testTag(testTag)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.2f))
      ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = Color.White
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = Color(0xFF94A3B8)
        )
      }
    }
  }
}
