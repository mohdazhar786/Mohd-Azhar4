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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.config.GameConfig
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

@Composable
fun EndlessScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  var selectedTier by remember { mutableIntStateOf(viewModel.config.difficultyTier) }
  var customLevelInput by remember { mutableStateOf(viewModel.endlessLevelNumber.toString()) }

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
        modifier = Modifier.testTag("endless_back_button")
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
          text = "ENDLESS GENERATOR",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "Seeded • 100% Reverse-Solvable",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFFC084FC)
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
      // Endless Status Card
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Color(0xFF818CF8).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
      ) {
        Row(
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("CURRENT FLOOR", fontSize = 11.sp, color = Color(0xFFA5B4FC))
            Text(
              "#${viewModel.endlessLevelNumber}",
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(34.dp)
              .background(Color(0xFF4338CA))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("STREAK", fontSize = 11.sp, color = Color(0xFFA5B4FC))
            Text(
              "${viewModel.endlessStreak}",
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFFFBBF24)
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(34.dp)
              .background(Color(0xFF4338CA))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("HIGHEST REACHED", fontSize = 11.sp, color = Color(0xFFA5B4FC))
            Text(
              "#${viewModel.endlessHighest}",
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF38BDF8)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Difficulty Tier Picker (0 to 5)
      Text(
        text = "DIFFICULTY CONSTRAINT (0 TO 5)",
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = Color(0xFFE2E8F0),
        modifier = Modifier
          .align(Alignment.Start)
          .padding(bottom = 8.dp)
      )

      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        for (tier in 0..5) {
          val isSelected = selectedTier == tier
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF818CF8) else Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp)
              )
              .clickable {
                selectedTier = tier
                viewModel.config.setDifficulty(tier)
                viewModel.soundManager.playButtonClick()
              }
              .testTag("endless_tier_$tier")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(if (isSelected) Color(0xFF818CF8) else Color(0xFF334155))
              ) {
                Text(
                  text = "$tier",
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  fontSize = 13.sp
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = GameConfig.DIFFICULTY_NAMES[tier],
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else Color(0xFF94A3B8),
                  fontSize = 14.sp
                )
                Text(
                  text = GameConfig.DIFFICULTY_DESCRIPTIONS[tier],
                  color = Color(0xFF64748B),
                  fontSize = 11.sp
                )
              }

              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = Color(0xFF818CF8),
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Custom Seed Jump
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = customLevelInput,
          onValueChange = { customLevelInput = it.filter { ch -> ch.isDigit() }.take(6) },
          label = { Text("Jump to Level #") },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF818CF8),
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("endless_level_input")
        )

        Button(
          onClick = {
            val randomLvl = (1..9999).random()
            customLevelInput = randomLvl.toString()
            viewModel.soundManager.playButtonClick()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
          modifier = Modifier.height(56.dp)
        ) {
          Icon(Icons.Default.Casino, contentDescription = "Random Seed")
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Launch Endless Game Button
      Button(
        onClick = {
          val lvl = customLevelInput.toIntOrNull() ?: viewModel.endlessLevelNumber
          viewModel.startEndless(lvl.coerceAtLeast(1))
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("endless_play_button")
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "LAUNCH LEVEL #${customLevelInput.ifBlank { "1" }}",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
