package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HandmadeLevels
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

@Composable
fun StatsScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val totalStars by viewModel.totalStars.collectAsState()
  val completedCount by viewModel.completedCount.collectAsState()
  val customLevelsList by viewModel.customLevels.collectAsState()

  BackHandler {
    viewModel.navigateTo(GameScreen.MENU)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF070B14))
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
        modifier = Modifier.testTag("stats_back_button")
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
          text = "PLAYER STATISTICS",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "Lifetime records & accolades",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF38BDF8)
        )
      }
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .widthIn(max = 560.dp)
    ) {
      // Main Stars Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.5.dp, Color(0xFFFBBF24).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(20.dp)
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(Color(0xFFFBBF24).copy(alpha = 0.2f))
          ) {
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = null,
              tint = Color(0xFFFBBF24),
              modifier = Modifier.size(34.dp)
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column {
            Text(
              text = "TOTAL STARS COLLECTED",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFFBBF24)
            )
            Text(
              text = "${totalStars ?: 0} / 75",
              fontSize = 28.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
            val pct = (((totalStars ?: 0).toFloat() / 75f) * 100).toInt()
            Text(
              text = "$pct% Campaign Mastered",
              fontSize = 12.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }
      }

      // Stats Grid
      StatRowItem(
        icon = Icons.Default.CheckCircle,
        iconColor = Color(0xFF0284C7),
        title = "Campaign Stages Cleared",
        value = "$completedCount / ${HandmadeLevels.getAllLevels().size}"
      )

      StatRowItem(
        icon = Icons.Default.AllInclusive,
        iconColor = Color(0xFF8B5CF6),
        title = "Endless Highest Floor",
        value = "Floor #${viewModel.endlessHighest}"
      )

      StatRowItem(
        icon = Icons.Default.DirectionsRun,
        iconColor = Color(0xFF10B981),
        title = "Endless Win Streak",
        value = "${viewModel.endlessStreak} in a row"
      )

      StatRowItem(
        icon = Icons.Default.Build,
        iconColor = Color(0xFFF59E0B),
        title = "Custom Workshop Levels",
        value = "${customLevelsList.size} created"
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun StatRowItem(
  icon: ImageVector,
  iconColor: Color,
  title: String,
  value: String
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(iconColor.copy(alpha = 0.2f))
      ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(text = title, fontSize = 12.sp, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
      }
    }
  }
}
