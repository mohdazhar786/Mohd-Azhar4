package com.example.game

import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONObject

/**
 * 2D Grid Coordinate
 */
data class Position(val x: Int, val y: Int) {
  fun move(dir: Direction): Position = Position(x + dir.dx, y + dir.dy)
}

/**
 * 4-Way Sliding Directions
 */
enum class Direction(val dx: Int, val dy: Int, val symbol: String, val displayName: String) {
  UP(0, -1, "▲", "Up"),
  DOWN(0, 1, "▼", "Down"),
  LEFT(-1, 0, "◀", "Left"),
  RIGHT(1, 0, "▶", "Right");

  fun opposite(): Direction = when (this) {
    UP -> DOWN
    DOWN -> UP
    LEFT -> RIGHT
    RIGHT -> LEFT
  }
}

/**
 * Color identity for Orbs and Matching Targets
 */
enum class OrbColor(val displayName: String, val hexCode: String, val colorValue: Color, val darkColor: Color) {
  CYAN("Cyan", "#06B6D4", Color(0xFF06B6D4), Color(0xFF0E7490)),
  AMBER("Amber", "#F59E0B", Color(0xFFF59E0B), Color(0xFFB45309)),
  MAGENTA("Magenta", "#D946EF", Color(0xFFD946EF), Color(0xFFA21CAF)),
  EMERALD("Emerald", "#10B981", Color(0xFF10B981), Color(0xFF047857));

  companion object {
    fun fromName(name: String): OrbColor {
      return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: CYAN
    }
  }
}

/**
 * Energy Orb that slides across the grid
 */
data class Orb(
  val id: Int,
  val color: OrbColor,
  val position: Position
)

/**
 * Terminal Socket / Target for an Orb
 */
data class TargetSlot(
  val position: Position,
  val color: OrbColor
)

/**
 * Pressure Switch button on floor: toggles corresponding laser gate when stepped on
 */
data class SwitchButton(
  val position: Position,
  val gateId: Int
)

/**
 * Laser Gate barrier: impassable when closed, passable when open
 */
data class LaserGate(
  val position: Position,
  val gateId: Int,
  val isInitiallyClosed: Boolean = true
)

/**
 * Portal pair: warping an orb from node A to node B
 */
data class PortalPair(
  val id: Int,
  val nodeA: Position,
  val nodeB: Position
) {
  fun destinationFor(pos: Position): Position? {
    return when (pos) {
      nodeA -> nodeB
      nodeB -> nodeA
      else -> null
    }
  }
}

/**
 * Complete Level Definition. Supports serialization to/from JSON for external layouts.
 */
data class LevelData(
  val id: String,
  val title: String,
  val chapter: String = "Campaign",
  val width: Int,
  val height: Int,
  val walls: Set<Position>,
  val targets: List<TargetSlot>,
  val initialOrbs: List<Orb>,
  val switches: List<SwitchButton> = emptyList(),
  val gates: List<LaserGate> = emptyList(),
  val portals: List<PortalPair> = emptyList(),
  val parMoves: Int = 10,
  val difficultyTier: Int = 2,
  val isCustom: Boolean = false
) {
  /**
   * Serializes level layout to JSON string (external layout format)
   */
  fun toJsonString(): String {
    val json = JSONObject()
    json.put("id", id)
    json.put("title", title)
    json.put("chapter", chapter)
    json.put("width", width)
    json.put("height", height)
    json.put("parMoves", parMoves)
    json.put("difficulty", difficultyTier)

    // Walls
    val wallsArray = JSONArray()
    walls.forEach {
      val w = JSONObject()
      w.put("x", it.x)
      w.put("y", it.y)
      wallsArray.put(w)
    }
    json.put("walls", wallsArray)

    // Targets
    val targetsArray = JSONArray()
    targets.forEach {
      val t = JSONObject()
      t.put("x", it.position.x)
      t.put("y", it.position.y)
      t.put("color", it.color.name)
      targetsArray.put(t)
    }
    json.put("targets", targetsArray)

    // Orbs
    val orbsArray = JSONArray()
    initialOrbs.forEach {
      val o = JSONObject()
      o.put("id", it.id)
      o.put("x", it.position.x)
      o.put("y", it.position.y)
      o.put("color", it.color.name)
      orbsArray.put(o)
    }
    json.put("orbs", orbsArray)

    // Switches
    val switchesArray = JSONArray()
    switches.forEach {
      val s = JSONObject()
      s.put("x", it.position.x)
      s.put("y", it.position.y)
      s.put("gateId", it.gateId)
      switchesArray.put(s)
    }
    json.put("switches", switchesArray)

    // Gates
    val gatesArray = JSONArray()
    gates.forEach {
      val g = JSONObject()
      g.put("x", it.position.x)
      g.put("y", it.position.y)
      g.put("gateId", it.gateId)
      g.put("initiallyClosed", it.isInitiallyClosed)
      gatesArray.put(g)
    }
    json.put("gates", gatesArray)

    // Portals
    val portalsArray = JSONArray()
    portals.forEach {
      val p = JSONObject()
      p.put("id", it.id)
      p.put("ax", it.nodeA.x)
      p.put("ay", it.nodeA.y)
      p.put("bx", it.nodeB.x)
      p.put("by", it.nodeB.y)
      portalsArray.put(p)
    }
    json.put("portals", portalsArray)

    return json.toString(2)
  }

  companion object {
    /**
     * Parses level layout from JSON string
     */
    fun fromJsonString(jsonStr: String): LevelData? {
      return try {
        val json = JSONObject(jsonStr)
        val id = json.optString("id", "custom_${System.currentTimeMillis()}")
        val title = json.optString("title", "Custom Level")
        val chapter = json.optString("chapter", "Custom")
        val width = json.optInt("width", 6).coerceIn(4, 10)
        val height = json.optInt("height", 6).coerceIn(4, 10)
        val parMoves = json.optInt("parMoves", 10)
        val difficulty = json.optInt("difficulty", 2).coerceIn(0, 5)

        val walls = mutableSetOf<Position>()
        val wallsArray = json.optJSONArray("walls")
        if (wallsArray != null) {
          for (i in 0 until wallsArray.length()) {
            val w = wallsArray.getJSONObject(i)
            walls.add(Position(w.getInt("x"), w.getInt("y")))
          }
        }

        val targets = mutableListOf<TargetSlot>()
        val targetsArray = json.optJSONArray("targets")
        if (targetsArray != null) {
          for (i in 0 until targetsArray.length()) {
            val t = targetsArray.getJSONObject(i)
            targets.add(
              TargetSlot(
                Position(t.getInt("x"), t.getInt("y")),
                OrbColor.fromName(t.optString("color", "CYAN"))
              )
            )
          }
        }

        val orbs = mutableListOf<Orb>()
        val orbsArray = json.optJSONArray("orbs")
        if (orbsArray != null) {
          for (i in 0 until orbsArray.length()) {
            val o = orbsArray.getJSONObject(i)
            orbs.add(
              Orb(
                id = o.optInt("id", i),
                color = OrbColor.fromName(o.optString("color", "CYAN")),
                position = Position(o.getInt("x"), o.getInt("y"))
              )
            )
          }
        }

        val switches = mutableListOf<SwitchButton>()
        val switchesArray = json.optJSONArray("switches")
        if (switchesArray != null) {
          for (i in 0 until switchesArray.length()) {
            val s = switchesArray.getJSONObject(i)
            switches.add(SwitchButton(Position(s.getInt("x"), s.getInt("y")), s.getInt("gateId")))
          }
        }

        val gates = mutableListOf<LaserGate>()
        val gatesArray = json.optJSONArray("gates")
        if (gatesArray != null) {
          for (i in 0 until gatesArray.length()) {
            val g = gatesArray.getJSONObject(i)
            gates.add(
              LaserGate(
                Position(g.getInt("x"), g.getInt("y")),
                g.getInt("gateId"),
                g.optBoolean("initiallyClosed", true)
              )
            )
          }
        }

        val portals = mutableListOf<PortalPair>()
        val portalsArray = json.optJSONArray("portals")
        if (portalsArray != null) {
          for (i in 0 until portalsArray.length()) {
            val p = portalsArray.getJSONObject(i)
            portals.add(
              PortalPair(
                id = p.optInt("id", i),
                nodeA = Position(p.getInt("ax"), p.getInt("ay")),
                nodeB = Position(p.getInt("bx"), p.getInt("by"))
              )
            )
          }
        }

        LevelData(
          id = id,
          title = title,
          chapter = chapter,
          width = width,
          height = height,
          walls = walls,
          targets = targets,
          initialOrbs = orbs,
          switches = switches,
          gates = gates,
          portals = portals,
          parMoves = parMoves,
          difficultyTier = difficulty,
          isCustom = true
        )
      } catch (_: Exception) {
        null
      }
    }
  }
}
