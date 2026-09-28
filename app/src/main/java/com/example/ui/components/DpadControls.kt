package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.game.Direction

/**
 * On-Screen Directional D-Pad for accessible tap and click controls.
 * Adheres to 48dp minimum interactive component sizing and clear visual affordance.
 */
@Composable
fun DpadControls(
  onDirection: (Direction) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
    modifier = modifier.testTag("dpad_controls")
  ) {
    // UP
    DpadButton(
      icon = Icons.Default.KeyboardArrowUp,
      contentDescription = "Slide Up",
      testTag = "dpad_up",
      onClick = { onDirection(Direction.UP) }
    )

    // LEFT, CENTER DECORATION, RIGHT
    Row(
      horizontalArrangement = Arrangement.spacedBy(28.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      DpadButton(
        icon = Icons.Default.KeyboardArrowLeft,
        contentDescription = "Slide Left",
        testTag = "dpad_left",
        onClick = { onDirection(Direction.LEFT) }
      )

      DpadButton(
        icon = Icons.Default.KeyboardArrowRight,
        contentDescription = "Slide Right",
        testTag = "dpad_right",
        onClick = { onDirection(Direction.RIGHT) }
      )
    }

    // DOWN
    DpadButton(
      icon = Icons.Default.KeyboardArrowDown,
      contentDescription = "Slide Down",
      testTag = "dpad_down",
      onClick = { onDirection(Direction.DOWN) }
    )
  }
}

@Composable
private fun DpadButton(
  icon: ImageVector,
  contentDescription: String,
  testTag: String,
  onClick: () -> Unit
) {
  Surface(
    onClick = onClick,
    shape = CircleShape,
    color = Color(0xFF1E293B),
    tonalElevation = 6.dp,
    shadowElevation = 4.dp,
    modifier = Modifier
      .size(54.dp)
      .testTag(testTag)
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = Color(0xFF38BDF8),
        modifier = Modifier.size(32.dp)
      )
    }
  }
}
