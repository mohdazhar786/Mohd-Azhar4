package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

  @Query("SELECT * FROM level_progress")
  fun getAllProgress(): Flow<List<LevelProgressEntity>>

  @Query("SELECT * FROM level_progress WHERE levelId = :levelId")
  suspend fun getProgressForLevel(levelId: String): LevelProgressEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveProgress(progress: LevelProgressEntity)

  @Query("SELECT COUNT(*) FROM level_progress WHERE completed = 1")
  fun getCompletedLevelsCount(): Flow<Int>

  @Query("SELECT SUM(stars) FROM level_progress")
  fun getTotalStarsCount(): Flow<Int?>

  @Query("DELETE FROM level_progress")
  suspend fun clearAllProgress()

  // Custom Levels
  @Query("SELECT * FROM custom_levels ORDER BY createdAt DESC")
  fun getAllCustomLevels(): Flow<List<CustomLevelEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveCustomLevel(level: CustomLevelEntity)

  @Query("DELETE FROM custom_levels WHERE id = :id")
  suspend fun deleteCustomLevel(id: String)
}
