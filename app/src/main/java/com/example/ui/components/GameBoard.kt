package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.game.Direction
import com.example.game.LaserGate
import com.example.game.LevelData
import com.example.game.Orb
import com.example.game.OrbColor
import com.example.game.PortalPair
import com.example.game.Position
import com.example.game.SwitchButton
import com.example.game.TargetSlot
import kotlin.math.abs
import kotlin.math.min

/**
 * Interactive GameBoard supporting swipe, tap, and click controls.
 * Features responsive adaptive layout, animated energy orbs, glowing laser gates,
 * and directional hint overlays.
 */
@Composable
fun GameBoard(
  level: LevelData,
  orbs: List<Orb>,
  gateStates: Map<Int, Boolean>,
  hintDirection: Direction? = null,
  swipeSensitivity: Float = 1.0f,
  onMove: (Direction) -> Unit,
  onTileClick: ((Position) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  // Swipe drag state tracking
  var totalDragX by remember { mutableFloatStateOf(0f) }
  var totalDragY by remember { mutableFloatStateOf(0f) }
  val dragThreshold = 40f / swipeSensitivity.coerceIn(0.5f, 2.0f)

  // Subtle pulsing animation for target sockets & portals
  val pulseAnim = remember { Animatable(0.85f) }
  LaunchedEffect(Unit) {
    pulseAnim.animateTo(
      targetValue = 1.15f,
      animationSpec = infiniteRepeatable(
        animation = tween(900, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      )
    )
  }

  // Smooth position animators for each orb
  val orbAnimators = remember(orbs.map { it.id }) {
    orbs.associate { orb ->
      orb.id to (Animatable(orb.position.x.toFloat()) to Animatable(orb.position.y.toFloat()))
    }
  }

  orbs.forEach { orb ->
    val pair = orbAnimators[orb.id]
    if (pair != null) {
      LaunchedEffect(orb.position.x) {
        pair.first.animateTo(orb.position.x.toFloat(), tween(140, easing = FastOutSlowInEasing))
      }
      LaunchedEffect(orb.position.y) {
        pair.second.animateTo(orb.position.y.toFloat(), tween(140, easing = FastOutSlowInEasing))
      }
    }
  }

  BoxWithConstraints(
    modifier = modifier
      .aspectRatio(level.width.toFloat() / level.height.toFloat())
      .shadow(16.dp, RoundedCornerShape(20.dp))
      .clip(RoundedCornerShape(20.dp))
      .background(Color(0xFF0F172A))
      .border(2.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
      .testTag("game_board_canvas")
      .pointerInput(level.id, onTileClick) {
        detectTapGestures { offset ->
          val cellW = size.width / level.width
          val cellH = size.height / level.height
          val col = (offset.x / cellW).toInt().coerceIn(0, level.width - 1)
          val row = (offset.y / cellH).toInt().coerceIn(0, level.height - 1)
          onTileClick?.invoke(Position(col, row))
        }
      }
      .pointerInput(level.id, swipeSensitivity) {
        detectDragGestures(
          onDragStart = {
            totalDragX = 0f
            totalDragY = 0f
          },
          onDrag = { change, dragAmount ->
            change.consume()
            totalDragX += dragAmount.x
            totalDragY += dragAmount.y
          },
          onDragEnd = {
            if (abs(totalDragX) > dragThreshold || abs(totalDragY) > dragThreshold) {
              val dir = if (abs(totalDragX) > abs(totalDragY)) {
                if (totalDragX > 0) Direction.RIGHT else Direction.LEFT
              } else {
                if (totalDragY > 0) Direction.DOWN else Direction.UP
              }
              onMove(dir)
            }
            totalDragX = 0f
            totalDragY = 0f
          },
          onDragCancel = {
            totalDragX = 0f
            totalDragY = 0f
          }
        )
      }
  ) {
    val boardWidth = constraints.maxWidth.toFloat()
    val boardHeight = constraints.maxHeight.toFloat()
    val cellWidth = boardWidth / level.width
    val cellHeight = boardHeight / level.height

    Canvas(modifier = Modifier.fillMaxSize()) {
      // 1. Draw Grid Lines
      drawGrid(level.width, level.height, cellWidth, cellHeight)

      // 2. Draw Target Sockets (with pulsing aura)
      level.targets.forEach { target ->
        drawTarget(target, cellWidth, cellHeight, pulseAnim.value)
      }

      // 3. Draw Switches
      level.switches.forEach { switch ->
        drawSwitch(switch, cellWidth, cellHeight)
      }

      // 4. Draw Laser Gates
      level.gates.forEach { gate ->
        val isClosed = gateStates[gate.gateId] ?: gate.isInitiallyClosed
        drawLaserGate(gate, cellWidth, cellHeight, isClosed, pulseAnim.value)
      }

      // 5. Draw Portals
      level.portals.forEach { portal ->
        drawPortalPair(portal, cellWidth, cellHeight, pulseAnim.value)
      }

      // 6. Draw Solid Obstacle Walls
      level.walls.forEach { wall ->
        drawWall(wall, cellWidth, cellHeight)
      }

      // 7. Draw Sliding Energy Orbs
      orbs.forEach { orb ->
        val animator = orbAnimators[orb.id]
        val currentX = animator?.first?.value ?: orb.position.x.toFloat()
        val currentY = animator?.second?.value ?: orb.position.y.toFloat()

        // Check if orb is resting on its matching target
        val isTargetLocked = level.targets.any { it.position == orb.position && it.color == orb.color }
        drawOrb(orb.color, currentX, currentY, cellWidth, cellHeight, isTargetLocked, pulseAnim.value)
      }

      // 8. Draw Hint Direction Arrow
      if (hintDirection != null) {
        drawHintArrow(hintDirection, size.width, size.height, pulseAnim.value)
      }
    }
  }
}

private fun DrawScope.drawGrid(cols: Int, rows: Int, cellW: Float, cellH: Float) {
  val gridColor = Color(0xFF1E293B)
  for (c in 1 until cols) {
    val x = c * cellW
    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.5f)
  }
  for (r in 1 until rows) {
    val y = r * cellH
    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f)
  }
}

private fun DrawScope.drawWall(pos: Position, cellW: Float, cellH: Float) {
  val x = pos.x * cellW
  val y = pos.y * cellH
  val pad = cellW * 0.08f

  // Wall base
  drawRoundRect(
    brush = Brush.linearGradient(
      colors = listOf(Color(0xFF334155), Color(0xFF1E293B)),
      start = Offset(x, y),
      end = Offset(x + cellW, y + cellH)
    ),
    topLeft = Offset(x + pad, y + pad),
    size = Size(cellW - pad * 2, cellH - pad * 2),
    cornerRadius = CornerRadius(cellW * 0.18f, cellW * 0.18f)
  )

  // Futuristic bevel highlight
  drawRoundRect(
    color = Color(0xFF475569),
    topLeft = Offset(x + pad + 2f, y + pad + 2f),
    size = Size(cellW - pad * 2 - 4f, cellH - pad * 2 - 4f),
    cornerRadius = CornerRadius(cellW * 0.14f, cellW * 0.14f),
    style = Stroke(width = 1.5f)
  )
}

private fun DrawScope.drawTarget(target: TargetSlot, cellW: Float, cellH: Float, pulse: Float) {
  val centerX = target.position.x * cellW + cellW / 2
  val centerY = target.position.y * cellH + cellH / 2
  val radius = (min(cellW, cellH) / 2) * 0.72f

  // Glow halo
  drawCircle(
    color = target.color.colorValue.copy(alpha = 0.22f * pulse),
    radius = radius * pulse,
    center = Offset(centerX, centerY)
  )

  // Outer target ring
  drawCircle(
    color = target.color.colorValue,
    radius = radius,
    center = Offset(centerX, centerY),
    style = Stroke(width = 3.5f)
  )

  // Inner socket crosshairs
  val crosshairSize = radius * 0.45f
  drawLine(
    color = target.color.colorValue.copy(alpha = 0.6f),
    start = Offset(centerX - crosshairSize, centerY),
    end = Offset(centerX + crosshairSize, centerY),
    strokeWidth = 2f
  )
  drawLine(
    color = target.color.colorValue.copy(alpha = 0.6f),
    start = Offset(centerX, centerY - crosshairSize),
    end = Offset(centerX, centerY + crosshairSize),
    strokeWidth = 2f
  )
}

private fun DrawScope.drawSwitch(switch: SwitchButton, cellW: Float, cellH: Float) {
  val centerX = switch.position.x * cellW + cellW / 2
  val centerY = switch.position.y * cellH + cellH / 2
  val size = min(cellW, cellH) * 0.45f

  // Diamond shape button
  val path = Path().apply {
    moveTo(centerX, centerY - size)
    lineTo(centerX + size, centerY)
    lineTo(centerX, centerY + size)
    lineTo(centerX - size, centerY)
    close()
  }

  drawPath(path, color = Color(0xFFF59E0B).copy(alpha = 0.35f))
  drawPath(path, color = Color(0xFFF59E0B), style = Stroke(width = 2.5f))
}

private fun DrawScope.drawLaserGate(gate: LaserGate, cellW: Float, cellH: Float, isClosed: Boolean, pulse: Float) {
  val x = gate.position.x * cellW
  val y = gate.position.y * cellH

  if (isClosed) {
    // Glowing laser beam barrier
    val beamColor = Color(0xFFEF4444)
    drawRect(
      color = beamColor.copy(alpha = 0.15f * pulse),
      topLeft = Offset(x, y),
      size = Size(cellW, cellH)
    )

    // Center laser line
    drawLine(
      color = beamColor,
      start = Offset(x + cellW * 0.1f, y + cellH / 2),
      end = Offset(x + cellW * 0.9f, y + cellH / 2),
      strokeWidth = 4f * pulse,
      cap = StrokeCap.Round
    )

    // Node emitters on sides
    drawCircle(Color(0xFFDC2626), radius = 4f, center = Offset(x + cellW * 0.1f, y + cellH / 2))
    drawCircle(Color(0xFFDC2626), radius = 4f, center = Offset(x + cellW * 0.9f, y + cellH / 2))
  } else {
    // Open gate (dormant green nodes)
    drawCircle(Color(0xFF10B981).copy(alpha = 0.5f), radius = 3.5f, center = Offset(x + cellW * 0.1f, y + cellH / 2))
    drawCircle(Color(0xFF10B981).copy(alpha = 0.5f), radius = 3.5f, center = Offset(x + cellW * 0.9f, y + cellH / 2))
  }
}

private fun DrawScope.drawPortalPair(portal: PortalPair, cellW: Float, cellH: Float, pulse: Float) {
  val portalColor = Color(0xFF8B5CF6)
  listOf(portal.nodeA, portal.nodeB).forEach { node ->
    val cx = node.x * cellW + cellW / 2
    val cy = node.y * cellH + cellH / 2
    val r = (min(cellW, cellH) / 2) * 0.65f

    drawCircle(
      color = portalColor.copy(alpha = 0.25f * pulse),
      radius = r * pulse,
      center = Offset(cx, cy)
    )
    drawCircle(
      color = portalColor,
      radius = r,
      center = Offset(cx, cy),
      style = Stroke(width = 3f)
    )
    // Inner vortex dot
    drawCircle(Color.White, radius = 3.5f, center = Offset(cx, cy))
  }
}

private fun DrawScope.drawOrb(
  color: OrbColor,
  gridX: Float,
  gridY: Float,
  cellW: Float,
  cellH: Float,
  isLocked: Boolean,
  pulse: Float
) {
  val cx = gridX * cellW + cellW / 2
  val cy = gridY * cellH + cellH / 2
  val radius = (min(cellW, cellH) / 2) * 0.62f

  if (isLocked) {
    // Halo celebration glow when locked in target
    drawCircle(
      color = color.colorValue.copy(alpha = 0.4f * pulse),
      radius = radius * 1.35f * pulse,
      center = Offset(cx, cy)
    )
  }

  // Core spherical gradient
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color.White, color.colorValue, color.darkColor),
      center = Offset(cx - radius * 0.25f, cy - radius * 0.25f),
      radius = radius * 1.1f
    ),
    radius = radius,
    center = Offset(cx, cy)
  )

  // Specular gleam
  drawCircle(
    color = Color.White.copy(alpha = 0.7f),
    radius = radius * 0.22f,
    center = Offset(cx - radius * 0.35f, cy - radius * 0.35f)
  )
}

private fun DrawScope.drawHintArrow(dir: Direction, boardW: Float, boardH: Float, pulse: Float) {
  val cx = boardW / 2
  val cy = boardH / 2
  val arrowLen = min(boardW, boardH) * 0.18f * pulse
  val arrowColor = Color(0xFFFBBF24).copy(alpha = 0.85f)

  val endX = cx + dir.dx * arrowLen
  val endY = cy + dir.dy * arrowLen

  drawLine(
    color = arrowColor,
    start = Offset(cx, cy),
    end = Offset(endX, endY),
    strokeWidth = 6f,
    cap = StrokeCap.Round
  )

  // Arrow head
  val angle = Math.atan2(dir.dy.toDouble(), dir.dx.toDouble())
  val headLen = 22f * pulse
  val p1 = Offset(
    (endX - headLen * Math.cos(angle - Math.PI / 6)).toFloat(),
    (endY - headLen * Math.sin(angle - Math.PI / 6)).toFloat()
  )
  val p2 = Offset(
    (endX - headLen * Math.cos(angle + Math.PI / 6)).toFloat(),
    (endY - headLen * Math.sin(angle + Math.PI / 6)).toFloat()
  )

  drawLine(arrowColor, Offset(endX, endY), p1, strokeWidth = 6f, cap = StrokeCap.Round)
  drawLine(arrowColor, Offset(endX, endY), p2, strokeWidth = 6f, cap = StrokeCap.Round)
}
