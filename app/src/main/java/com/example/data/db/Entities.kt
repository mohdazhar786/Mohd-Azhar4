package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted user progress for campaign and endless levels
 */
@Entity(tableName = "level_progress")
data class LevelProgressEntity(
  @PrimaryKey
  val levelId: String,
  val stars: Int,
  val bestMoves: Int,
  val completed: Boolean,
  val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Persisted custom levels created in the Level Editor or imported
 */
@Entity(tableName = "custom_levels")
data class CustomLevelEntity(
  @PrimaryKey
  val id: String,
  val name: String,
  val width: Int,
  val height: Int,
  val layoutJson: String,
  val difficulty: Int,
  val createdAt: Long = System.currentTimeMillis()
)
