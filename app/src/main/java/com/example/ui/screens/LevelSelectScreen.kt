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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.config.GameConfig
import com.example.data.HandmadeLevels
import com.example.game.LevelData
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

@Composable
fun LevelSelectScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  var selectedChapterIndex by remember { mutableIntStateOf(0) }
  val allLevels = HandmadeLevels.getAllLevels()
  val chapters = HandmadeLevels.chapters + "Custom Workshops"
  val customLevelsList by viewModel.customLevels.collectAsState()

  BackHandler {
    viewModel.navigateTo(GameScreen.MENU)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0A0F1D))
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
        modifier = Modifier.testTag("level_select_back_button")
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
          text = "CAMPAIGN SECTORS",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "Select a stage to deploy",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF94A3B8)
        )
      }
    }

    // Chapter Tabs
    ScrollableTabRow(
      selectedTabIndex = selectedChapterIndex,
      containerColor = Color(0xFF0F172A),
      contentColor = Color(0xFF38BDF8),
      edgePadding = 16.dp,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          modifier = Modifier.tabIndicatorOffset(tabPositions[selectedChapterIndex]),
          color = Color(0xFF38BDF8)
        )
      }
    ) {
      chapters.forEachIndexed { index, chapterTitle ->
        Tab(
          selected = selectedChapterIndex == index,
          onClick = {
            viewModel.soundManager.playButtonClick()
            selectedChapterIndex = index
          },
          text = {
            Text(
              text = if (index < 5) "Ch. ${index + 1}" else "Custom",
              fontWeight = if (selectedChapterIndex == index) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedChapterIndex == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
            )
          }
        )
      }
    }

    // Level Cards List
    val currentLevels = if (selectedChapterIndex < 5) {
      allLevels.filter { it.chapter == HandmadeLevels.chapters[selectedChapterIndex] }
    } else {
      customLevelsList.mapNotNull { LevelData.fromJsonString(it.layoutJson) }
    }

    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      contentAlignment = Alignment.TopCenter
    ) {
      if (currentLevels.isEmpty() && selectedChapterIndex == 5) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize()
        ) {
          Text(
            text = "No custom levels found.",
            color = Color(0xFF94A3B8),
            fontSize = 16.sp
          )
          Text(
            text = "Create one in the Level Workshop!",
            color = Color(0xFF64748B),
            fontSize = 13.sp
          )
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
        ) {
          items(currentLevels) { level ->
            val progress = viewModel.progressMap[level.id]
            val stars = progress?.stars ?: 0
            val bestMoves = progress?.bestMoves ?: 0
            val isCompleted = progress?.completed == true

            LevelCardItem(
              level = level,
              stars = stars,
              bestMoves = bestMoves,
              isCompleted = isCompleted,
              onClick = {
                viewModel.startLevel(level, isEndless = false)
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LevelCardItem(
  level: LevelData,
  stars: Int,
  bestMoves: Int,
  isCompleted: Boolean,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCompleted) Color(0xFF1E293B) else Color(0xFF111827)
    ),
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = 1.dp,
        color = if (isCompleted) Color(0xFF0284C7).copy(alpha = 0.5f) else Color(0xFF1F2937),
        shape = RoundedCornerShape(16.dp)
      )
      .clickable { onClick() }
      .testTag("level_card_${level.id}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = level.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Spacer(modifier = Modifier.width(8.dp))
          // Difficulty tier pill
          Box(
            modifier = Modifier
              .background(Color(0xFF334155), RoundedCornerShape(6.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = GameConfig.DIFFICULTY_NAMES.getOrElse(level.difficultyTier) { "Tier" },
              fontSize = 10.sp,
              color = Color(0xFF38BDF8),
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${level.width}x${level.height} Grid • Par: ${level.parMoves}",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
          )
          if (bestMoves > 0) {
            Text(
              text = " • Best: $bestMoves",
              fontSize = 12.sp,
              color = Color(0xFF10B981),
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Star Rating & Play Icon
      Row(verticalAlignment = Alignment.CenterVertically) {
        Row {
          for (i in 1..3) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = if (i <= stars) Color(0xFFFBBF24) else Color(0xFF334155),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(38.dp)
            .background(Color(0xFF0284C7), RoundedCornerShape(10.dp))
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}
