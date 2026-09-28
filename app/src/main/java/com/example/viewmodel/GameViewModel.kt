package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.config.GameConfig
import com.example.data.HandmadeLevels
import com.example.data.db.CustomLevelEntity
import com.example.data.db.GameDatabase
import com.example.data.db.LevelProgressEntity
import com.example.game.Direction
import com.example.game.GameEngine
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
import com.example.generator.SolverResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class GameScreen {
  MENU,
  CAMPAIGN_SELECT,
  PLAYING,
  ENDLESS,
  EDITOR,
  TUTORIAL,
  SETTINGS,
  STATS
}

enum class EditorTool(val label: String, val category: String) {
  WALL("Wall", "Obstacles"),
  ORB_CYAN("Cyan Orb", "Orbs"),
  ORB_AMBER("Amber Orb", "Orbs"),
  ORB_MAGENTA("Magenta Orb", "Orbs"),
  ORB_EMERALD("Emerald Orb", "Orbs"),
  TARGET_CYAN("Cyan Target", "Targets"),
  TARGET_AMBER("Amber Target", "Targets"),
  TARGET_MAGENTA("Magenta Target", "Targets"),
  TARGET_EMERALD("Emerald Target", "Targets"),
  SWITCH("Switch", "Interact"),
  GATE("Laser Gate", "Interact"),
  PORTAL("Portal", "Interact"),
  ERASER("Eraser", "Tools")
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

  private val database = GameDatabase.getDatabase(application)
  private val dao = database.gameDao()

  val config = GameConfig(application)
  val soundManager = SoundManager(application).apply {
    sfxVolume = config.sfxVolume
    musicVolume = config.musicVolume
    hapticsEnabled = config.hapticsEnabled
  }

  // Navigation state
  var currentScreen by mutableStateOf(GameScreen.MENU)
    private set

  // Progress cached in memory
  val progressMap = mutableStateMapOf<String, LevelProgressEntity>()

  val customLevels = dao.getAllCustomLevels()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalStars = dao.getTotalStarsCount()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val completedCount = dao.getCompletedLevelsCount()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  // Active Game State
  var activeLevel by mutableStateOf(HandmadeLevels.getAllLevels()[0])
    private set

  var gameEngine by mutableStateOf(GameEngine(activeLevel))
    private set

  var moveCount by mutableIntStateOf(0)
    private set

  var isCompleted by mutableStateOf(false)
    private set

  var starsEarned by mutableIntStateOf(0)
    private set

  var isPaused by mutableStateOf(false)

  // Hint State
  var activeHintSteps by mutableStateOf<List<Direction>>(emptyList())
    private set

  var isCalculatingHint by mutableStateOf(false)
    private set

  // Endless Mode
  var endlessLevelNumber by mutableIntStateOf(1)
    private set

  var endlessStreak by mutableIntStateOf(0)
    private set

  var endlessHighest by mutableIntStateOf(1)
    private set

  var isEndlessActive by mutableStateOf(false)
    private set

  // --- LEVEL EDITOR STATE ---
  var editorWidth by mutableIntStateOf(6)
  var editorHeight by mutableIntStateOf(6)
  var editorWalls by mutableStateOf<Set<Position>>(emptySet())
  var editorTargets by mutableStateOf<List<TargetSlot>>(emptyList())
  var editorOrbs by mutableStateOf<List<Orb>>(emptyList())
  var editorSwitches by mutableStateOf<List<SwitchButton>>(emptyList())
  var editorGates by mutableStateOf<List<LaserGate>>(emptyList())
  var editorPortals by mutableStateOf<List<PortalPair>>(emptyList())
  var selectedTool by mutableStateOf(EditorTool.WALL)
  var editorValidation by mutableStateOf<SolverResult?>(null)
  var isTestingPlay by mutableStateOf(false)
  var testGameEngine by mutableStateOf<GameEngine?>(null)

  init {
    loadProgressFromDb()
    soundManager.startAmbientMusic()
  }

  override fun onCleared() {
    super.onCleared()
    soundManager.stopAmbientMusic()
  }

  fun navigateTo(screen: GameScreen) {
    soundManager.playButtonClick()
    currentScreen = screen
  }

  fun updateSfxVolume(vol: Float) {
    config.setSfxVol(vol)
    soundManager.sfxVolume = vol
  }

  fun updateMusicVolume(vol: Float) {
    config.setMusicVol(vol)
    soundManager.musicVolume = vol
  }

  fun updateHaptics(enabled: Boolean) {
    config.toggleHaptics(enabled)
    soundManager.hapticsEnabled = enabled
  }

  fun startLevel(level: LevelData, isEndless: Boolean = false) {
    activeLevel = level
    gameEngine = GameEngine(level)
    moveCount = 0
    isCompleted = false
    starsEarned = 0
    isPaused = false
    activeHintSteps = emptyList()
    isEndlessActive = isEndless
    currentScreen = GameScreen.PLAYING
  }

  fun startEndless(levelNum: Int = endlessLevelNumber) {
    endlessLevelNumber = levelNum
    val generated = ReverseLevelGenerator.generateLevel(levelNum, config.difficultyTier)
    startLevel(generated, isEndless = true)
  }

  fun restartCurrentLevel() {
    soundManager.playButtonClick()
    gameEngine.reset()
    moveCount = 0
    isCompleted = false
    starsEarned = 0
    activeHintSteps = emptyList()
    isPaused = false
  }

  fun undoMove() {
    if (gameEngine.canUndo() && !isCompleted) {
      gameEngine.undo()
      moveCount = gameEngine.moveCount
      soundManager.playUndo()
      activeHintSteps = emptyList()
    }
  }

  fun makeMove(dir: Direction) {
    if (isCompleted || isPaused) return

    val result = gameEngine.executeMove(dir)
    if (result.moved) {
      moveCount = gameEngine.moveCount
      soundManager.playMove()

      if (result.hitWall) soundManager.playBump()
      if (result.toggledSwitch) soundManager.playSwitchToggled()
      if (result.warpedPortal) soundManager.playPortalWarp()
      if (result.reachedTarget) soundManager.playTargetReached()

      if (result.isCompleted) {
        handleVictory()
      } else {
        // Advance or clear hint if user moved
        if (activeHintSteps.isNotEmpty()) {
          activeHintSteps = if (activeHintSteps.first() == dir) {
            activeHintSteps.drop(1)
          } else {
            emptyList()
          }
        }
      }
    } else {
      soundManager.playBump()
    }
  }

  private fun handleVictory() {
    isCompleted = true
    soundManager.playLevelWin()

    val par = activeLevel.parMoves
    val stars = when {
      moveCount <= par -> 3
      moveCount <= par + 2 -> 2
      else -> 1
    }
    starsEarned = stars

    if (isEndlessActive) {
      endlessStreak++
      if (endlessLevelNumber >= endlessHighest) {
        endlessHighest = endlessLevelNumber + 1
      }
    }

    viewModelScope.launch(Dispatchers.IO) {
      val existing = dao.getProgressForLevel(activeLevel.id)
      val bestStars = maxOf(stars, existing?.stars ?: 0)
      val bestMoves = if (existing != null && existing.bestMoves > 0) {
        minOf(moveCount, existing.bestMoves)
      } else moveCount

      val entity = LevelProgressEntity(
        levelId = activeLevel.id,
        stars = bestStars,
        bestMoves = bestMoves,
        completed = true
      )
      dao.saveProgress(entity)
      withContext(Dispatchers.Main) {
        progressMap[activeLevel.id] = entity
      }
    }
  }

  fun advanceToNextLevel() {
    if (isEndlessActive) {
      startEndless(endlessLevelNumber + 1)
    } else {
      val allLevels = HandmadeLevels.getAllLevels()
      val currentIndex = allLevels.indexOfFirst { it.id == activeLevel.id }
      if (currentIndex in 0 until allLevels.lastIndex) {
        startLevel(allLevels[currentIndex + 1], isEndless = false)
      } else {
        navigateTo(GameScreen.CAMPAIGN_SELECT)
      }
    }
  }

  fun requestHint() {
    if (isCompleted || isCalculatingHint) return
    isCalculatingHint = true

    viewModelScope.launch(Dispatchers.Default) {
      // Build a level from current orbs
      val currentSubLevel = activeLevel.copy(
        initialOrbs = gameEngine.currentOrbs,
        gates = activeLevel.gates.map {
          it.copy(isInitiallyClosed = gameEngine.gateStates[it.gateId] ?: it.isInitiallyClosed)
        }
      )
      val result = PuzzleSolver.solve(currentSubLevel, maxDepth = 20)
      withContext(Dispatchers.Main) {
        isCalculatingHint = false
        if (result.isSolvable && result.solutionSteps.isNotEmpty()) {
          activeHintSteps = result.solutionSteps
          soundManager.playTargetReached()
        } else {
          soundManager.playBump()
        }
      }
    }
  }

  // --- EDITOR FUNCTIONS ---
  fun selectEditorTool(tool: EditorTool) {
    selectedTool = tool
    soundManager.playButtonClick()
  }

  fun handleEditorTileClick(pos: Position) {
    if (isTestingPlay) return

    val mutableWalls = editorWalls.toMutableSet()
    val mutableTargets = editorTargets.toMutableList()
    val mutableOrbs = editorOrbs.toMutableList()
    val mutableSwitches = editorSwitches.toMutableList()
    val mutableGates = editorGates.toMutableList()
    val mutablePortals = editorPortals.toMutableList()

    // Clear existing item at this position
    mutableWalls.remove(pos)
    mutableTargets.removeAll { it.position == pos }
    mutableOrbs.removeAll { it.position == pos }
    mutableSwitches.removeAll { it.position == pos }
    mutableGates.removeAll { it.position == pos }
    mutablePortals.removeAll { it.nodeA == pos || it.nodeB == pos }

    when (selectedTool) {
      EditorTool.WALL -> mutableWalls.add(pos)
      EditorTool.ORB_CYAN -> mutableOrbs.add(Orb(mutableOrbs.size, OrbColor.CYAN, pos))
      EditorTool.ORB_AMBER -> mutableOrbs.add(Orb(mutableOrbs.size, OrbColor.AMBER, pos))
      EditorTool.ORB_MAGENTA -> mutableOrbs.add(Orb(mutableOrbs.size, OrbColor.MAGENTA, pos))
      EditorTool.ORB_EMERALD -> mutableOrbs.add(Orb(mutableOrbs.size, OrbColor.EMERALD, pos))
      EditorTool.TARGET_CYAN -> mutableTargets.add(TargetSlot(pos, OrbColor.CYAN))
      EditorTool.TARGET_AMBER -> mutableTargets.add(TargetSlot(pos, OrbColor.AMBER))
      EditorTool.TARGET_MAGENTA -> mutableTargets.add(TargetSlot(pos, OrbColor.MAGENTA))
      EditorTool.TARGET_EMERALD -> mutableTargets.add(TargetSlot(pos, OrbColor.EMERALD))
      EditorTool.SWITCH -> mutableSwitches.add(SwitchButton(pos, gateId = 1))
      EditorTool.GATE -> mutableGates.add(LaserGate(pos, gateId = 1, isInitiallyClosed = true))
      EditorTool.PORTAL -> {
        // Portal needs two points: if odd number, pair with another
        val otherPortalPos = Position((pos.x + 2) % editorWidth, (pos.y + 2) % editorHeight)
        mutablePortals.add(PortalPair(mutablePortals.size + 1, pos, otherPortalPos))
      }
      EditorTool.ERASER -> { /* already cleared */ }
    }

    editorWalls = mutableWalls
    editorTargets = mutableTargets
    editorOrbs = mutableOrbs
    editorSwitches = mutableSwitches
    editorGates = mutableGates
    editorPortals = mutablePortals

    soundManager.vibrate(10)
    editorValidation = null
  }

  fun validateEditorBoard() {
    val levelCandidate = buildEditorLevel("Validation Test")
    viewModelScope.launch(Dispatchers.Default) {
      val result = PuzzleSolver.solve(levelCandidate)
      withContext(Dispatchers.Main) {
        editorValidation = result
        if (result.isSolvable) {
          soundManager.playTargetReached()
        } else {
          soundManager.playBump()
        }
      }
    }
  }

  fun toggleTestPlay() {
    if (isTestingPlay) {
      isTestingPlay = false
      testGameEngine = null
    } else {
      val levelCandidate = buildEditorLevel("Test Stage")
      testGameEngine = GameEngine(levelCandidate)
      isTestingPlay = true
      soundManager.playMove()
    }
  }

  fun makeEditorTestMove(dir: Direction) {
    val engine = testGameEngine ?: return
    val res = engine.executeMove(dir)
    if (res.moved) {
      soundManager.playMove()
      if (res.isCompleted) soundManager.playLevelWin()
    }
  }

  fun resetEditorGrid(size: Int) {
    editorWidth = size
    editorHeight = size
    editorWalls = emptySet()
    editorTargets = emptyList()
    editorOrbs = emptyList()
    editorSwitches = emptyList()
    editorGates = emptyList()
    editorPortals = emptyList()
    editorValidation = null
    isTestingPlay = false
  }

  fun buildEditorLevel(name: String): LevelData {
    return LevelData(
      id = "custom_${System.currentTimeMillis()}",
      title = name,
      chapter = "Custom Workshop",
      width = editorWidth,
      height = editorHeight,
      walls = editorWalls,
      targets = editorTargets,
      initialOrbs = editorOrbs,
      switches = editorSwitches,
      gates = editorGates,
      portals = editorPortals,
      parMoves = 8,
      difficultyTier = config.difficultyTier,
      isCustom = true
    )
  }

  fun saveCustomLevel(name: String) {
    val level = buildEditorLevel(name.ifBlank { "Custom Level" })
    viewModelScope.launch(Dispatchers.IO) {
      dao.saveCustomLevel(
        CustomLevelEntity(
          id = level.id,
          name = level.title,
          width = level.width,
          height = level.height,
          layoutJson = level.toJsonString(),
          difficulty = level.difficultyTier
        )
      )
    }
    soundManager.playTargetReached()
  }

  fun importLevelJson(jsonStr: String): Boolean {
    val level = LevelData.fromJsonString(jsonStr) ?: return false
    editorWidth = level.width
    editorHeight = level.height
    editorWalls = level.walls
    editorTargets = level.targets
    editorOrbs = level.initialOrbs
    editorSwitches = level.switches
    editorGates = level.gates
    editorPortals = level.portals
    editorValidation = null
    soundManager.playTargetReached()
    return true
  }

  fun resetAllProgress() {
    viewModelScope.launch(Dispatchers.IO) {
      dao.clearAllProgress()
      withContext(Dispatchers.Main) {
        progressMap.clear()
        endlessStreak = 0
        endlessHighest = 1
        endlessLevelNumber = 1
        config.resetToDefaults()
        soundManager.sfxVolume = config.sfxVolume
        soundManager.musicVolume = config.musicVolume
        soundManager.hapticsEnabled = config.hapticsEnabled
      }
    }
  }

  private fun loadProgressFromDb() {
    viewModelScope.launch(Dispatchers.IO) {
      dao.getAllProgress().collect { list ->
        withContext(Dispatchers.Main) {
          list.forEach { entity ->
            progressMap[entity.levelId] = entity
          }
        }
      }
    }
  }
}
