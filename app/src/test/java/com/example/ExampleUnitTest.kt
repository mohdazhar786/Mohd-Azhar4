package com.example

import com.example.game.Direction
import com.example.game.GameEngine
import com.example.game.LevelData
import com.example.game.Orb
import com.example.game.OrbColor
import com.example.game.Position
import com.example.game.TargetSlot
import com.example.generator.PuzzleSolver
import com.example.generator.ReverseLevelGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

  @Test
  fun testAllHandmadeLevelsSolvability() {
    val levels = com.example.data.HandmadeLevels.getAllLevels()
    val unsolvable = mutableListOf<String>()
    for (lvl in levels) {
      val res = PuzzleSolver.solve(lvl)
      if (!res.isSolvable) {
        unsolvable.add("${lvl.id} (${lvl.title}): ${res.validationMessage}")
      }
    }
    assertTrue("Unsolvable levels found: $unsolvable", unsolvable.isEmpty())
  }

  @Test
  fun testSeededGenerationSolvability() {
    // Generated levels built backwards from solved boards must be solvable
    for (difficulty in 0..3) {
      val level = ReverseLevelGenerator.generateLevel(levelNumber = 10, difficultyTier = difficulty)
      val result = PuzzleSolver.solve(level)
      assertTrue("Level generated with tier $difficulty must be solvable", result.isSolvable)
      assertTrue("Par moves must be positive", level.parMoves > 0)
    }
  }

  @Test
  fun testSeededGenerationDeterminism() {
    // Seeded generation: level numbers always generate identical levels for everyone
    val lvlA = ReverseLevelGenerator.generateLevel(levelNumber = 42, difficultyTier = 2)
    val lvlB = ReverseLevelGenerator.generateLevel(levelNumber = 42, difficultyTier = 2)
    assertEquals(lvlA.width, lvlB.width)
    assertEquals(lvlA.height, lvlB.height)
    assertEquals(lvlA.walls, lvlB.walls)
    assertEquals(lvlA.targets, lvlB.targets)
    assertEquals(lvlA.initialOrbs, lvlB.initialOrbs)
  }

  @Test
  fun testSlidingAndUndoMechanics() {
    val level = LevelData(
      id = "test_lvl",
      title = "Test",
      width = 5,
      height = 5,
      walls = setOf(Position(3, 0)),
      targets = listOf(TargetSlot(Position(2, 0), OrbColor.CYAN)),
      initialOrbs = listOf(Orb(0, OrbColor.CYAN, Position(0, 0)))
    )

    val engine = GameEngine(level)
    assertEquals(0, engine.moveCount)

    // Move RIGHT: should slide until hitting wall at (3,0), stopping at (2,0)
    val res = engine.executeMove(Direction.RIGHT)
    assertTrue(res.moved)
    assertEquals(Position(2, 0), engine.currentOrbs[0].position)
    assertTrue(res.isCompleted)
    assertEquals(1, engine.moveCount)

    // Undo should restore position
    val undone = engine.undo()
    assertTrue(undone)
    assertEquals(Position(0, 0), engine.currentOrbs[0].position)
    assertFalse(engine.isCompleted)
    assertEquals(0, engine.moveCount)
  }

  @Test
  fun testLayoutSerializationRoundtrip() {
    val original = LevelData(
      id = "lvl_custom_1",
      title = "Custom Sector",
      chapter = "Workshop",
      width = 6,
      height = 6,
      walls = setOf(Position(1, 1), Position(2, 2)),
      targets = listOf(TargetSlot(Position(4, 4), OrbColor.CYAN)),
      initialOrbs = listOf(Orb(0, OrbColor.CYAN, Position(0, 0))),
      parMoves = 7,
      difficultyTier = 3
    )

    val json = original.toJsonString()
    val restored = LevelData.fromJsonString(json)

    assertNotNull(restored)
    assertEquals(original.id, restored!!.id)
    assertEquals(original.title, restored.title)
    assertEquals(original.walls, restored.walls)
    assertEquals(original.targets, restored.targets)
    assertEquals(original.initialOrbs, restored.initialOrbs)
    assertEquals(original.parMoves, restored.parMoves)
  }

  @Test
  fun testSolverUnsolvableDiagnosis() {
    // Board with target and orb of mismatched colors
    val unsolvableLevel = LevelData(
      id = "broken",
      title = "Broken",
      width = 5,
      height = 5,
      walls = emptySet(),
      targets = listOf(TargetSlot(Position(2, 2), OrbColor.MAGENTA)),
      initialOrbs = listOf(Orb(0, OrbColor.CYAN, Position(0, 0)))
    )

    val result = PuzzleSolver.solve(unsolvableLevel)
    assertFalse(result.isSolvable)
    assertTrue(result.suggestedFixes.isNotEmpty())
  }
}
