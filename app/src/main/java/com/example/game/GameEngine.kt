package com.example.game

/**
 * Snapshot of a game move for undo functionality
 */
data class MoveSnapshot(
  val orbs: List<Orb>,
  val gateStates: Map<Int, Boolean>,
  val direction: Direction
)

/**
 * Event result of applying a move
 */
data class MoveResult(
  val moved: Boolean,
  val newOrbs: List<Orb>,
  val newGateStates: Map<Int, Boolean>,
  val hitWall: Boolean,
  val reachedTarget: Boolean,
  val toggledSwitch: Boolean,
  val warpedPortal: Boolean,
  val isCompleted: Boolean
)

/**
 * Physics and Logic engine for sliding orbs, switches, gates, and portals.
 */
class GameEngine(val level: LevelData) {

  // Mutable runtime state
  var currentOrbs: List<Orb> = level.initialOrbs.map { it.copy() }
    private set

  // Gate states: gateId -> isClosed
  var gateStates: Map<Int, Boolean> = level.gates.associate { it.gateId to it.isInitiallyClosed }
    private set

  var moveCount: Int = 0
    private set

  val undoStack: MutableList<MoveSnapshot> = mutableListOf()

  val isCompleted: Boolean
    get() = checkVictory(currentOrbs, level.targets)

  fun reset() {
    currentOrbs = level.initialOrbs.map { it.copy() }
    gateStates = level.gates.associate { it.gateId to it.isInitiallyClosed }
    moveCount = 0
    undoStack.clear()
  }

  fun canUndo(): Boolean = undoStack.isNotEmpty()

  fun undo(): Boolean {
    if (undoStack.isEmpty()) return false
    val last = undoStack.removeAt(undoStack.lastIndex)
    currentOrbs = last.orbs
    gateStates = last.gateStates
    moveCount = (moveCount - 1).coerceAtLeast(0)
    return true
  }

  /**
   * Performs a move in the given direction.
   * Orbs slide until they hit a wall, border, closed gate, or another stopped orb.
   */
  fun executeMove(direction: Direction): MoveResult {
    if (isCompleted) {
      return MoveResult(
        moved = false,
        newOrbs = currentOrbs,
        newGateStates = gateStates,
        hitWall = false,
        reachedTarget = false,
        toggledSwitch = false,
        warpedPortal = false,
        isCompleted = true
      )
    }

    val snapshot = MoveSnapshot(
      orbs = currentOrbs.map { it.copy() },
      gateStates = gateStates.toMap(),
      direction = direction
    )

    val mutableOrbs = currentOrbs.map { it.copy() }.toMutableList()
    var mutableGateStates = gateStates.toMutableMap()
    var anyMoved = false
    var hitWall = false
    var reachedTarget = false
    var toggledSwitch = false
    var warpedPortal = false

    // Sort orbs based on movement direction so leading orbs slide first:
    // UP: smallest y first
    // DOWN: largest y first
    // LEFT: smallest x first
    // RIGHT: largest x first
    val order = when (direction) {
      Direction.UP -> mutableOrbs.sortedBy { it.position.y }
      Direction.DOWN -> mutableOrbs.sortedByDescending { it.position.y }
      Direction.LEFT -> mutableOrbs.sortedBy { it.position.x }
      Direction.RIGHT -> mutableOrbs.sortedByDescending { it.position.x }
    }

    for (orb in order) {
      val orbIndex = mutableOrbs.indexOfFirst { it.id == orb.id }
      if (orbIndex == -1) continue

      var currentPos = orb.position
      var sliding = true
      var stepsSlid = 0

      while (sliding) {
        val nextPos = currentPos.move(direction)

        // Check boundary
        if (nextPos.x !in 0 until level.width || nextPos.y !in 0 until level.height) {
          hitWall = true
          sliding = false
          break
        }

        // Check walls
        if (level.walls.contains(nextPos)) {
          hitWall = true
          sliding = false
          break
        }

        // Check closed gates
        val gateAtNext = level.gates.firstOrNull { it.position == nextPos }
        if (gateAtNext != null && mutableGateStates[gateAtNext.gateId] == true) {
          hitWall = true
          sliding = false
          break
        }

        // Check other orbs (excluding this orb's previous position)
        val otherOrbAtNext = mutableOrbs.any { it.id != orb.id && it.position == nextPos }
        if (otherOrbAtNext) {
          hitWall = true
          sliding = false
          break
        }

        // Move to next tile
        currentPos = nextPos
        stepsSlid++
        anyMoved = true

        // Check for switch button on this tile
        val switchOnTile = level.switches.firstOrNull { it.position == currentPos }
        if (switchOnTile != null) {
          toggledSwitch = true
          val currentGateState = mutableGateStates[switchOnTile.gateId] ?: true
          mutableGateStates[switchOnTile.gateId] = !currentGateState
        }

        // Check for portal on this tile
        val portalPair = level.portals.firstOrNull { it.nodeA == currentPos || it.nodeB == currentPos }
        if (portalPair != null) {
          val dest = portalPair.destinationFor(currentPos)
          if (dest != null) {
            // Portal teleport requires destination to be free of obstacles & other orbs
            val destBlocked = level.walls.contains(dest) ||
              (level.gates.firstOrNull { it.position == dest }?.let { mutableGateStates[it.gateId] == true } ?: false) ||
              mutableOrbs.any { it.id != orb.id && it.position == dest }

            if (!destBlocked) {
              currentPos = dest
              warpedPortal = true
              // continue sliding out of the portal if next in direction is open
            }
          }
        }
      }

      if (stepsSlid > 0) {
        mutableOrbs[orbIndex] = orb.copy(position = currentPos)
      }
    }

    if (anyMoved) {
      undoStack.add(snapshot)
      currentOrbs = mutableOrbs
      gateStates = mutableGateStates
      moveCount++

      // Check if any orb is now on its matching target
      val matchingCount = currentOrbs.count { orb ->
        level.targets.any { it.position == orb.position && it.color == orb.color }
      }
      if (matchingCount > 0) {
        reachedTarget = true
      }
    }

    val won = isCompleted

    return MoveResult(
      moved = anyMoved,
      newOrbs = currentOrbs,
      newGateStates = gateStates,
      hitWall = hitWall,
      reachedTarget = reachedTarget,
      toggledSwitch = toggledSwitch,
      warpedPortal = warpedPortal,
      isCompleted = won
    )
  }

  companion object {
    /**
     * Checks if all targets are occupied by matching orbs
     */
    fun checkVictory(orbs: List<Orb>, targets: List<TargetSlot>): Boolean {
      if (targets.isEmpty() || orbs.size < targets.size) return false
      return targets.all { target ->
        orbs.any { orb -> orb.position == target.position && orb.color == target.color }
      }
    }

    /**
     * Pure static simulation step for AI solver / reverse generator
     */
    fun simulatePureMove(
      level: LevelData,
      currentOrbs: List<Orb>,
      currentGates: Map<Int, Boolean>,
      direction: Direction
    ): Pair<List<Orb>, Map<Int, Boolean>>? {
      val mutableOrbs = currentOrbs.map { it.copy() }.toMutableList()
      val mutableGates = currentGates.toMutableMap()
      var anyMoved = false

      val order = when (direction) {
        Direction.UP -> mutableOrbs.sortedBy { it.position.y }
        Direction.DOWN -> mutableOrbs.sortedByDescending { it.position.y }
        Direction.LEFT -> mutableOrbs.sortedBy { it.position.x }
        Direction.RIGHT -> mutableOrbs.sortedByDescending { it.position.x }
      }

      for (orb in order) {
        val orbIndex = mutableOrbs.indexOfFirst { it.id == orb.id }
        if (orbIndex == -1) continue

        var currentPos = orb.position
        var sliding = true
        var stepsSlid = 0

        while (sliding) {
          val nextPos = currentPos.move(direction)

          if (nextPos.x !in 0 until level.width || nextPos.y !in 0 until level.height) {
            sliding = false
            break
          }
          if (level.walls.contains(nextPos)) {
            sliding = false
            break
          }
          val gateAtNext = level.gates.firstOrNull { it.position == nextPos }
          if (gateAtNext != null && mutableGates[gateAtNext.gateId] == true) {
            sliding = false
            break
          }
          if (mutableOrbs.any { it.id != orb.id && it.position == nextPos }) {
            sliding = false
            break
          }

          currentPos = nextPos
          stepsSlid++
          anyMoved = true

          val switchOnTile = level.switches.firstOrNull { it.position == currentPos }
          if (switchOnTile != null) {
            val curr = mutableGates[switchOnTile.gateId] ?: true
            mutableGates[switchOnTile.gateId] = !curr
          }

          val portalPair = level.portals.firstOrNull { it.nodeA == currentPos || it.nodeB == currentPos }
          if (portalPair != null) {
            val dest = portalPair.destinationFor(currentPos)
            if (dest != null) {
              val destBlocked = level.walls.contains(dest) ||
                (level.gates.firstOrNull { it.position == dest }?.let { mutableGates[it.gateId] == true } ?: false) ||
                mutableOrbs.any { it.id != orb.id && it.position == dest }
              if (!destBlocked) {
                currentPos = dest
              }
            }
          }
        }

        if (stepsSlid > 0) {
          mutableOrbs[orbIndex] = orb.copy(position = currentPos)
        }
      }

      return if (anyMoved) Pair(mutableOrbs, mutableGates) else null
    }
  }
}
