package com.example.generator

import com.example.game.Direction
import com.example.game.GameEngine
import com.example.game.LaserGate
import com.example.game.LevelData
import com.example.game.Orb
import com.example.game.Position
import com.example.game.SwitchButton
import com.example.game.TargetSlot
import java.util.ArrayDeque

data class SolverState(
  val orbPositions: List<Position>,
  val gateStates: Map<Int, Boolean>
)

data class SolverResult(
  val isSolvable: Boolean,
  val minMoves: Int,
  val solutionSteps: List<Direction>,
  val nodesExplored: Int,
  val validationMessage: String,
  val suggestedFixes: List<String> = emptyList()
)

/**
 * High-performance BFS State-Space Solver for GridPulse.
 * Explores possible move combinations, detects unreachable states,
 * and provides hints and level validator feedback.
 */
object PuzzleSolver {

  fun solve(level: LevelData, maxDepth: Int = 30, maxNodes: Int = 12000): SolverResult {
    // Basic validation
    val fixes = mutableListOf<String>()

    if (level.initialOrbs.isEmpty()) {
      return SolverResult(
        isSolvable = false,
        minMoves = 0,
        solutionSteps = emptyList(),
        nodesExplored = 0,
        validationMessage = "No energy orbs placed on the board.",
        suggestedFixes = listOf("Place at least one Orb on an empty cell.")
      )
    }

    if (level.targets.isEmpty()) {
      return SolverResult(
        isSolvable = false,
        minMoves = 0,
        solutionSteps = emptyList(),
        nodesExplored = 0,
        validationMessage = "No target terminals placed on the board.",
        suggestedFixes = listOf("Place matching Target slots for each orb.")
      )
    }

    if (level.initialOrbs.size < level.targets.size) {
      fixes.add("Level has more targets (${level.targets.size}) than orbs (${level.initialOrbs.size}).")
    }

    // Check color matching
    for (target in level.targets) {
      if (level.initialOrbs.none { it.color == target.color }) {
        fixes.add("Target (${target.color.displayName}) has no matching ${target.color.displayName} orb.")
      }
    }

    if (fixes.isNotEmpty()) {
      return SolverResult(
        isSolvable = false,
        minMoves = 0,
        solutionSteps = emptyList(),
        nodesExplored = 0,
        validationMessage = "Board configuration mismatch.",
        suggestedFixes = fixes
      )
    }

    // If already in solved state
    if (GameEngine.checkVictory(level.initialOrbs, level.targets)) {
      return SolverResult(
        isSolvable = true,
        minMoves = 0,
        solutionSteps = emptyList(),
        nodesExplored = 1,
        validationMessage = "Already at victory state!",
        suggestedFixes = emptyList()
      )
    }

    val initialGates = level.gates.associate { it.gateId to it.isInitiallyClosed }
    val initialOrbsSorted = level.initialOrbs.sortedBy { it.id }
    val startState = SolverState(
      orbPositions = initialOrbsSorted.map { it.position },
      gateStates = initialGates
    )

    val queue = ArrayDeque<Pair<SolverState, List<Direction>>>()
    val visited = HashSet<SolverState>()

    queue.add(Pair(startState, emptyList()))
    visited.add(startState)

    var nodesCount = 0

    while (queue.isNotEmpty() && nodesCount < maxNodes) {
      val (current, path) = queue.poll() ?: break
      nodesCount++

      if (path.size >= maxDepth) continue

      for (dir in Direction.entries) {
        val orbsList = current.orbPositions.mapIndexed { idx, pos ->
          Orb(id = initialOrbsSorted[idx].id, color = initialOrbsSorted[idx].color, position = pos)
        }

        val sim = GameEngine.simulatePureMove(level, orbsList, current.gateStates, dir)
        if (sim != null) {
          val (nextOrbs, nextGates) = sim
          val nextOrbsSorted = nextOrbs.sortedBy { it.id }
          val nextState = SolverState(
            orbPositions = nextOrbsSorted.map { it.position },
            gateStates = nextGates
          )

          // Check win
          if (GameEngine.checkVictory(nextOrbs, level.targets)) {
            val solution = path + dir
            return SolverResult(
              isSolvable = true,
              minMoves = solution.size,
              solutionSteps = solution,
              nodesExplored = nodesCount,
              validationMessage = "Board is verified solvable in ${solution.size} moves!",
              suggestedFixes = emptyList()
            )
          }

          if (visited.add(nextState)) {
            queue.add(Pair(nextState, path + dir))
          }
        }
      }
    }

    // Unsolvable diagnosis
    val autoFixes = mutableListOf<String>()

    // Check if targets have any wall or stop in orthogonal lines
    for (target in level.targets) {
      val pos = target.position
      val hasAdjacentWallOrBoundary =
        pos.x == 0 || pos.x == level.width - 1 ||
        pos.y == 0 || pos.y == level.height - 1 ||
        level.walls.any { (it.x == pos.x && kotlin.math.abs(it.y - pos.y) == 1) || (it.y == pos.y && kotlin.math.abs(it.x - pos.x) == 1) }

      if (!hasAdjacentWallOrBoundary) {
        autoFixes.add("Target at (${pos.x}, ${pos.y}) has no stopping wall adjacent. Orbs may slide right past it!")
      }
    }

    if (autoFixes.isEmpty()) {
      autoFixes.add("No valid path found within $maxDepth moves.")
      autoFixes.add("Try adding a wall to create a stopping point near targets.")
      autoFixes.add("Or add an extra orb to serve as a movable barrier.")
    }

    return SolverResult(
      isSolvable = false,
      minMoves = -1,
      solutionSteps = emptyList(),
      nodesExplored = nodesCount,
      validationMessage = if (nodesCount >= maxNodes) "Search space exceeded ($maxNodes states evaluated). Board might be too complex or locked." else "Unsolvable board configuration detected.",
      suggestedFixes = autoFixes
    )
  }
}
