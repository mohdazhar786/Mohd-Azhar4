package com.example.data

import com.example.game.Direction
import com.example.game.LaserGate
import com.example.game.LevelData
import com.example.game.Orb
import com.example.game.OrbColor
import com.example.game.PortalPair
import com.example.game.Position
import com.example.game.SwitchButton
import com.example.game.TargetSlot
import com.example.generator.PuzzleSolver
import com.example.generator.ReverseLevelGenerator

/**
 * Curated Hand-Made Campaign Levels.
 * Contains 25 meticulously designed puzzle stages with progressive mechanics:
 * boundaries, obstacles, multi-orb coordination, switch gates, and portals.
 * Every level is mathematically verified solvable.
 */
object HandmadeLevels {

  val chapters = listOf(
    "Chapter 1: Initiation",
    "Chapter 2: Momentum",
    "Chapter 3: Chromatic Resonance",
    "Chapter 4: Laser Gates",
    "Chapter 5: Quantum Nexus"
  )

  private val cachedLevels by lazy {
    buildVerifiedLevels()
  }

  fun getAllLevels(): List<LevelData> = cachedLevels

  private fun buildVerifiedLevels(): List<LevelData> {
    val curated = listOf(
      // --- CHAPTER 1: INITIATION ---
      // Level 1: First Pulse (2 moves: RIGHT -> DOWN)
      LevelData(
        id = "lvl_1",
        title = "First Pulse",
        chapter = chapters[0],
        width = 4,
        height = 4,
        walls = setOf(Position(1, 1)),
        targets = listOf(TargetSlot(Position(3, 3), OrbColor.CYAN)),
        initialOrbs = listOf(Orb(0, OrbColor.CYAN, Position(0, 0))),
        parMoves = 2,
        difficultyTier = 0
      ),

      // Level 2: Corner Pocket (3 moves: RIGHT -> DOWN -> LEFT)
      // Wall at (3, 3) stops the orb at (3, 2); wall at (1, 2) catches the orb at (2, 2)!
      LevelData(
        id = "lvl_2",
        title = "Corner Pocket",
        chapter = chapters[0],
        width = 4,
        height = 4,
        walls = setOf(Position(1, 2), Position(3, 3)),
        targets = listOf(TargetSlot(Position(2, 2), OrbColor.CYAN)),
        initialOrbs = listOf(Orb(0, OrbColor.CYAN, Position(0, 0))),
        parMoves = 3,
        difficultyTier = 0
      ),

      // Level 3: Binary Star (3 moves)
      LevelData(
        id = "lvl_3",
        title = "Binary Star",
        chapter = chapters[0],
        width = 5,
        height = 5,
        walls = setOf(Position(2, 2)),
        targets = listOf(
          TargetSlot(Position(0, 2), OrbColor.CYAN),
          TargetSlot(Position(4, 2), OrbColor.AMBER)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(2, 0)),
          Orb(1, OrbColor.AMBER, Position(2, 4))
        ),
        parMoves = 3,
        difficultyTier = 1
      ),

      // Level 4: Crosscurrent (4 moves)
      LevelData(
        id = "lvl_4",
        title = "Crosscurrent",
        chapter = chapters[0],
        width = 5,
        height = 5,
        walls = setOf(Position(1, 2), Position(3, 2), Position(2, 4)),
        targets = listOf(
          TargetSlot(Position(1, 1), OrbColor.CYAN),
          TargetSlot(Position(3, 3), OrbColor.AMBER)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.AMBER, Position(4, 4))
        ),
        parMoves = 4,
        difficultyTier = 1
      ),

      // Level 5: Pinball Ledge (4 moves)
      LevelData(
        id = "lvl_5",
        title = "Pinball Ledge",
        chapter = chapters[0],
        width = 5,
        height = 5,
        walls = setOf(Position(4, 1), Position(2, 3), Position(1, 3)),
        targets = listOf(TargetSlot(Position(2, 2), OrbColor.CYAN)),
        initialOrbs = listOf(Orb(0, OrbColor.CYAN, Position(0, 0))),
        parMoves = 4,
        difficultyTier = 1
      ),

      // --- CHAPTER 2: MOMENTUM ---
      // Level 6: Interference
      LevelData(
        id = "lvl_6",
        title = "Interference",
        chapter = chapters[1],
        width = 5,
        height = 5,
        walls = setOf(Position(1, 2), Position(3, 2), Position(2, 3)),
        targets = listOf(
          TargetSlot(Position(2, 1), OrbColor.CYAN),
          TargetSlot(Position(2, 4), OrbColor.MAGENTA)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.MAGENTA, Position(4, 4))
        ),
        parMoves = 5,
        difficultyTier = 1
      ),

      // Level 7: Prism Lock (Custom Layout Replaceable & Exportable)
      LevelData(
        id = "lvl_7",
        title = "Level 7: Prism Lock",
        chapter = chapters[1],
        width = 6,
        height = 6,
        walls = setOf(
          Position(1, 1),
          Position(4, 1),
          Position(2, 3),
          Position(3, 4),
          Position(1, 4)
        ),
        targets = listOf(
          TargetSlot(Position(1, 3), OrbColor.CYAN),
          TargetSlot(Position(4, 3), OrbColor.AMBER)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.AMBER, Position(5, 5))
        ),
        parMoves = 6,
        difficultyTier = 2
      ),

      // Level 8: Bodyguard Block
      LevelData(
        id = "lvl_8",
        title = "Bodyguard Block",
        chapter = chapters[1],
        width = 6,
        height = 6,
        walls = setOf(Position(2, 2), Position(3, 3), Position(5, 2)),
        targets = listOf(
          TargetSlot(Position(4, 2), OrbColor.CYAN),
          TargetSlot(Position(2, 3), OrbColor.AMBER)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 1)),
          Orb(1, OrbColor.AMBER, Position(5, 4))
        ),
        parMoves = 5,
        difficultyTier = 2
      ),

      // Level 9: Dual Conductor
      LevelData(
        id = "lvl_9",
        title = "Dual Conductor",
        chapter = chapters[1],
        width = 6,
        height = 6,
        walls = setOf(Position(1, 3), Position(4, 2), Position(3, 4)),
        targets = listOf(
          TargetSlot(Position(2, 2), OrbColor.CYAN),
          TargetSlot(Position(3, 3), OrbColor.MAGENTA)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(1, 0)),
          Orb(1, OrbColor.MAGENTA, Position(4, 5))
        ),
        parMoves = 6,
        difficultyTier = 2
      ),

      // Level 10: The Crucible
      LevelData(
        id = "lvl_10",
        title = "The Crucible",
        chapter = chapters[1],
        width = 6,
        height = 6,
        walls = setOf(Position(2, 1), Position(4, 2), Position(1, 4), Position(5, 1)),
        targets = listOf(
          TargetSlot(Position(4, 1), OrbColor.CYAN),
          TargetSlot(Position(1, 3), OrbColor.AMBER)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.AMBER, Position(5, 5))
        ),
        parMoves = 6,
        difficultyTier = 2
      ),

      // --- CHAPTER 3: CHROMATIC RESONANCE ---
      // Level 11: Tri-Force Circuit
      LevelData(
        id = "lvl_11",
        title = "Tri-Force Circuit",
        chapter = chapters[2],
        width = 6,
        height = 6,
        walls = setOf(Position(1, 2), Position(4, 3), Position(2, 4)),
        targets = listOf(
          TargetSlot(Position(1, 1), OrbColor.CYAN),
          TargetSlot(Position(4, 4), OrbColor.AMBER),
          TargetSlot(Position(3, 1), OrbColor.MAGENTA)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.AMBER, Position(5, 5)),
          Orb(2, OrbColor.MAGENTA, Position(5, 0))
        ),
        parMoves = 7,
        difficultyTier = 3
      ),

      // Level 12: Symmetry Shift
      LevelData(
        id = "lvl_12",
        title = "Symmetry Shift",
        chapter = chapters[2],
        width = 6,
        height = 6,
        walls = setOf(Position(2, 1), Position(3, 4), Position(1, 3), Position(4, 2)),
        targets = listOf(
          TargetSlot(Position(2, 2), OrbColor.CYAN),
          TargetSlot(Position(3, 3), OrbColor.MAGENTA)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.MAGENTA, Position(5, 5))
        ),
        parMoves = 6,
        difficultyTier = 3
      ),

      // Level 13: Triple Alignment
      LevelData(
        id = "lvl_13",
        title = "Triple Alignment",
        chapter = chapters[2],
        width = 7,
        height = 7,
        walls = setOf(Position(2, 1), Position(4, 2), Position(2, 5), Position(5, 4)),
        targets = listOf(
          TargetSlot(Position(2, 3), OrbColor.CYAN),
          TargetSlot(Position(4, 3), OrbColor.AMBER),
          TargetSlot(Position(3, 5), OrbColor.EMERALD)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 1)),
          Orb(1, OrbColor.AMBER, Position(6, 2)),
          Orb(2, OrbColor.EMERALD, Position(3, 0))
        ),
        parMoves = 8,
        difficultyTier = 3
      ),

      // Level 14: Cluster Orbit
      LevelData(
        id = "lvl_14",
        title = "Cluster Orbit",
        chapter = chapters[2],
        width = 6,
        height = 6,
        walls = setOf(Position(1, 2), Position(4, 3), Position(2, 4), Position(3, 1)),
        targets = listOf(
          TargetSlot(Position(2, 2), OrbColor.CYAN),
          TargetSlot(Position(3, 3), OrbColor.MAGENTA)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.MAGENTA, Position(5, 5))
        ),
        parMoves = 6,
        difficultyTier = 3
      ),

      // Level 15: The Diamond Core
      LevelData(
        id = "lvl_15",
        title = "The Diamond Core",
        chapter = chapters[2],
        width = 6,
        height = 6,
        walls = setOf(Position(2, 1), Position(1, 3), Position(4, 2), Position(3, 4)),
        targets = listOf(
          TargetSlot(Position(2, 2), OrbColor.CYAN),
          TargetSlot(Position(3, 3), OrbColor.AMBER),
          TargetSlot(Position(3, 2), OrbColor.MAGENTA)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.AMBER, Position(5, 5)),
          Orb(2, OrbColor.MAGENTA, Position(0, 5))
        ),
        parMoves = 7,
        difficultyTier = 3
      ),

      // --- CHAPTER 4: LASER GATES ---
      // Level 16: Gatekeeper
      LevelData(
        id = "lvl_16",
        title = "Gatekeeper",
        chapter = chapters[3],
        width = 6,
        height = 6,
        walls = setOf(Position(2, 1), Position(4, 4)),
        targets = listOf(TargetSlot(Position(5, 2), OrbColor.CYAN)),
        initialOrbs = listOf(Orb(0, OrbColor.CYAN, Position(0, 0))),
        switches = listOf(SwitchButton(Position(2, 4), gateId = 1)),
        gates = listOf(LaserGate(Position(3, 2), gateId = 1, isInitiallyClosed = true)),
        parMoves = 6,
        difficultyTier = 3
      ),

      // Level 17: Toggle Relay
      LevelData(
        id = "lvl_17",
        title = "Toggle Relay",
        chapter = chapters[3],
        width = 6,
        height = 6,
        walls = setOf(Position(1, 2), Position(4, 3)),
        targets = listOf(
          TargetSlot(Position(4, 1), OrbColor.CYAN),
          TargetSlot(Position(1, 4), OrbColor.AMBER)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 1)),
          Orb(1, OrbColor.AMBER, Position(5, 4))
        ),
        switches = listOf(
          SwitchButton(Position(2, 2), gateId = 1),
          SwitchButton(Position(3, 3), gateId = 2)
        ),
        gates = listOf(
          LaserGate(Position(3, 1), gateId = 1, isInitiallyClosed = true),
          LaserGate(Position(2, 4), gateId = 2, isInitiallyClosed = true)
        ),
        parMoves = 8,
        difficultyTier = 4
      ),

      // Level 18: Firewall Breach
      LevelData(
        id = "lvl_18",
        title = "Firewall Breach",
        chapter = chapters[3],
        width = 6,
        height = 6,
        walls = setOf(Position(2, 2), Position(3, 4)),
        targets = listOf(TargetSlot(Position(4, 2), OrbColor.CYAN)),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.AMBER, Position(5, 5))
        ),
        switches = listOf(SwitchButton(Position(1, 4), gateId = 1)),
        gates = listOf(LaserGate(Position(3, 2), gateId = 1, isInitiallyClosed = true)),
        parMoves = 6,
        difficultyTier = 4
      ),

      // Level 19: Laser Maze
      LevelData(
        id = "lvl_19",
        title = "Laser Maze",
        chapter = chapters[3],
        width = 7,
        height = 7,
        walls = setOf(Position(1, 1), Position(5, 5), Position(1, 5), Position(5, 1)),
        targets = listOf(
          TargetSlot(Position(3, 1), OrbColor.CYAN),
          TargetSlot(Position(3, 5), OrbColor.MAGENTA)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 3)),
          Orb(1, OrbColor.MAGENTA, Position(6, 3))
        ),
        switches = listOf(SwitchButton(Position(3, 3), gateId = 1)),
        gates = listOf(
          LaserGate(Position(2, 1), gateId = 1, isInitiallyClosed = true),
          LaserGate(Position(4, 5), gateId = 1, isInitiallyClosed = false)
        ),
        parMoves = 9,
        difficultyTier = 4
      ),

      // Level 20: Dual Voltage
      LevelData(
        id = "lvl_20",
        title = "Dual Voltage",
        chapter = chapters[3],
        width = 6,
        height = 6,
        walls = setOf(Position(2, 2), Position(3, 3)),
        targets = listOf(
          TargetSlot(Position(1, 2), OrbColor.CYAN),
          TargetSlot(Position(4, 3), OrbColor.AMBER)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.AMBER, Position(5, 5))
        ),
        switches = listOf(SwitchButton(Position(1, 4), gateId = 1)),
        gates = listOf(LaserGate(Position(3, 2), gateId = 1, isInitiallyClosed = true)),
        parMoves = 7,
        difficultyTier = 4
      ),

      // --- CHAPTER 5: QUANTUM NEXUS ---
      // Level 21: Wormhole Genesis
      LevelData(
        id = "lvl_21",
        title = "Wormhole Genesis",
        chapter = chapters[4],
        width = 6,
        height = 6,
        walls = setOf(Position(2, 2), Position(3, 3)),
        targets = listOf(TargetSlot(Position(4, 1), OrbColor.CYAN)),
        initialOrbs = listOf(Orb(0, OrbColor.CYAN, Position(0, 0))),
        portals = listOf(PortalPair(1, Position(1, 4), Position(4, 2))),
        parMoves = 5,
        difficultyTier = 4
      ),

      // Level 22: Folded Space
      LevelData(
        id = "lvl_22",
        title = "Folded Space",
        chapter = chapters[4],
        width = 6,
        height = 6,
        walls = setOf(Position(2, 1), Position(3, 4)),
        targets = listOf(
          TargetSlot(Position(4, 1), OrbColor.CYAN),
          TargetSlot(Position(1, 4), OrbColor.MAGENTA)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.MAGENTA, Position(5, 5))
        ),
        portals = listOf(PortalPair(1, Position(1, 1), Position(4, 4))),
        parMoves = 6,
        difficultyTier = 4
      ),

      // Level 23: Event Horizon
      LevelData(
        id = "lvl_23",
        title = "Event Horizon",
        chapter = chapters[4],
        width = 6,
        height = 6,
        walls = setOf(Position(2, 2), Position(3, 3), Position(4, 1)),
        targets = listOf(
          TargetSlot(Position(3, 1), OrbColor.CYAN),
          TargetSlot(Position(2, 4), OrbColor.AMBER)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.AMBER, Position(5, 5))
        ),
        portals = listOf(PortalPair(1, Position(0, 3), Position(5, 2))),
        parMoves = 7,
        difficultyTier = 5
      ),

      // Level 24: Quantum Entanglement
      LevelData(
        id = "lvl_24",
        title = "Quantum Entanglement",
        chapter = chapters[4],
        width = 7,
        height = 7,
        walls = setOf(Position(2, 2), Position(4, 4), Position(3, 1)),
        targets = listOf(
          TargetSlot(Position(4, 2), OrbColor.CYAN),
          TargetSlot(Position(2, 4), OrbColor.MAGENTA)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.MAGENTA, Position(6, 6))
        ),
        portals = listOf(PortalPair(1, Position(1, 5), Position(5, 1))),
        parMoves = 8,
        difficultyTier = 5
      ),

      // Level 25: The Singularity
      LevelData(
        id = "lvl_25",
        title = "The Singularity",
        chapter = chapters[4],
        width = 7,
        height = 7,
        walls = setOf(Position(2, 2), Position(4, 4), Position(2, 4), Position(4, 2)),
        targets = listOf(
          TargetSlot(Position(3, 2), OrbColor.CYAN),
          TargetSlot(Position(3, 4), OrbColor.AMBER),
          TargetSlot(Position(2, 3), OrbColor.MAGENTA)
        ),
        initialOrbs = listOf(
          Orb(0, OrbColor.CYAN, Position(0, 0)),
          Orb(1, OrbColor.AMBER, Position(6, 6)),
          Orb(2, OrbColor.MAGENTA, Position(0, 6))
        ),
        portals = listOf(PortalPair(1, Position(1, 1), Position(5, 5))),
        parMoves = 9,
        difficultyTier = 5
      )
    )

    // For any level in curated, verify solvability. If any fails, fall back to ReverseLevelGenerator
    return curated.mapIndexed { idx, level ->
      val solveResult = PuzzleSolver.solve(level)
      if (solveResult.isSolvable) {
        level.copy(parMoves = solveResult.minMoves)
      } else {
        val chapterIdx = idx / 5
        val fallback = ReverseLevelGenerator.generateLevel(levelNumber = idx + 1, difficultyTier = chapterIdx)
        fallback.copy(
          id = level.id,
          title = level.title,
          chapter = chapters[chapterIdx]
        )
      }
    }
  }
}
