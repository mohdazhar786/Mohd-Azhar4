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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun TutorialScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  var currentStep by remember { mutableIntStateOf(0) }

  BackHandler {
    viewModel.navigateTo(GameScreen.MENU)
  }

  val tutorialSteps = listOf(
    TutorialStep(
      title = "SLIDE CONTROLS",
      badge = "Rule 1 of 5",
      icon = Icons.Default.SwapHoriz,
      color = Color(0xFF0284C7),
      description = "Swipe in any direction (Up, Down, Left, Right), or use the on-screen D-Pad. When you swipe, energy orbs slide across the grid continuously until hitting a solid wall, grid boundary, or another orb!",
      tip = "Orbs cannot stop mid-air in empty space. You must plan which surface will catch them!"
    ),
    TutorialStep(
      title = "COLOR TERMINALS",
      badge = "Rule 2 of 5",
      icon = Icons.Default.CheckCircle,
      color = Color(0xFF10B981),
      description = "Your objective is to guide each Energy Orb into its matching colored Target Terminal. When all terminals are simultaneously occupied by their matching orbs, the matrix destabilizes and victory is achieved!",
      tip = "Orbs can slide across non-matching terminals without getting stuck."
    ),
    TutorialStep(
      title = "ORB TEAMWORK & BLOCKING",
      badge = "Rule 3 of 5",
      icon = Icons.Default.Lightbulb,
      color = Color(0xFFF59E0B),
      description = "In multi-orb stages, use one orb as a movable stopping barrier for another! Position Orb A into an open lane so Orb B can slam into it and stop directly in line with its target terminal.",
      tip = "Moving orbs collide safely with each other and stop adjacent."
    ),
    TutorialStep(
      title = "LASER SWITCHES & GATES",
      badge = "Rule 4 of 5",
      icon = Icons.Default.ToggleOn,
      color = Color(0xFFEF4444),
      description = "Pressure switches on the grid toggle red Laser Gates between open and closed. Slide an orb over the switch to open laser barriers or to raise a new wall to bounce against!",
      tip = "Closed laser gates act as solid walls; open gates can be slid through."
    ),
    TutorialStep(
      title = "QUANTUM PORTALS & REVERSE LOGIC",
      badge = "Rule 5 of 5",
      icon = Icons.Default.PlayArrow,
      color = Color(0xFF8B5CF6),
      description = "Portals teleport sliding orbs across spatial anomalies, maintaining momentum. All generated levels are built backwards from solved boards. Think backwards from the terminal sockets to unlock the solution!",
      tip = "Need guidance? Tap the Lightbulb icon in any game for instant step-by-step hints."
    )
  )

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
        modifier = Modifier.testTag("tutorial_back_button")
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
          text = "HOW TO PLAY",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "Master GridPulse mechanics",
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
        .widthIn(max = 540.dp)
    ) {
      val step = tutorialSteps[currentStep]

      // Step Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.5.dp, step.color.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
          .padding(vertical = 8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .background(step.color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = step.badge,
                color = step.color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(step.color.copy(alpha = 0.2f))
            ) {
              Icon(
                imageVector = step.icon,
                contentDescription = null,
                tint = step.color,
                modifier = Modifier.size(26.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = step.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = Color.White
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = step.description,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = Color(0xFFE2E8F0)
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Tactical Tip Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
              .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
              .padding(12.dp)
          ) {
            Row(verticalAlignment = Alignment.Top) {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "TACTICAL INTEL",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFFBBF24)
                )
                Text(
                  text = step.tip,
                  fontSize = 12.sp,
                  color = Color(0xFF94A3B8)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Navigation buttons between steps
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedButton(
          onClick = {
            if (currentStep > 0) {
              currentStep--
              viewModel.soundManager.playButtonClick()
            }
          },
          enabled = currentStep > 0,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.height(48.dp)
        ) {
          Text("PREVIOUS")
        }

        if (currentStep < tutorialSteps.lastIndex) {
          Button(
            onClick = {
              currentStep++
              viewModel.soundManager.playButtonClick()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(48.dp)
          ) {
            Text("NEXT RULE")
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        } else {
          Button(
            onClick = {
              viewModel.startLevel(HandmadeLevels.getAllLevels()[0], isEndless = false)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(48.dp)
          ) {
            Text("PLAY LEVEL 1")
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

private data class TutorialStep(
  val title: String,
  val badge: String,
  val icon: ImageVector,
  val color: Color,
  val description: String,
  val tip: String
)
