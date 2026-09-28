package com.example.config

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Centralized Global Game Configuration.
 * Holds all user-configurable parameters: sound volumes, haptics,
 * difficulty tier (0-5), swipe threshold, color contrast, and control styles.
 */
class GameConfig(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("gridpulse_game_config", Context.MODE_PRIVATE)

  // SFX volume (0.0 to 1.0)
  var sfxVolume by mutableFloatStateOf(prefs.getFloat(KEY_SFX_VOL, 0.8f))
    private set

  // Ambient music volume (0.0 to 1.0)
  var musicVolume by mutableFloatStateOf(prefs.getFloat(KEY_MUSIC_VOL, 0.5f))
    private set

  // Haptic feedback enabled
  var hapticsEnabled by mutableStateOf(prefs.getBoolean(KEY_HAPTICS, true))
    private set

  // Difficulty Tier: 0 to 5
  // 0: Casual, 1: Easy, 2: Medium, 3: Hard, 4: Master, 5: Grandmaster
  var difficultyTier by mutableIntStateOf(prefs.getInt(KEY_DIFFICULTY, 2).coerceIn(0, 5))
    private set

  // Show move counter on HUD
  var showMoveCounter by mutableStateOf(prefs.getBoolean(KEY_SHOW_MOVES, true))
    private set

  // High contrast mode for accessibility
  var highContrastMode by mutableStateOf(prefs.getBoolean(KEY_HIGH_CONTRAST, false))
    private set

  // Swipe gesture sensitivity multiplier (0.5 to 2.0)
  var swipeSensitivity by mutableFloatStateOf(prefs.getFloat(KEY_SWIPE_SENSITIVITY, 1.0f))
    private set

  // Show on-screen D-Pad for tap/click controls
  var showDpadControls by mutableStateOf(prefs.getBoolean(KEY_SHOW_DPAD, true))
    private set

  // Theme name
  var currentTheme by mutableStateOf(prefs.getString(KEY_THEME, "CYBER_NEON") ?: "CYBER_NEON")
    private set

  fun setSfxVol(value: Float) {
    val clamped = value.coerceIn(0f, 1f)
    sfxVolume = clamped
    prefs.edit().putFloat(KEY_SFX_VOL, clamped).apply()
  }

  fun setMusicVol(value: Float) {
    val clamped = value.coerceIn(0f, 1f)
    musicVolume = clamped
    prefs.edit().putFloat(KEY_MUSIC_VOL, clamped).apply()
  }

  fun toggleHaptics(enabled: Boolean) {
    hapticsEnabled = enabled
    prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
  }

  fun setDifficulty(tier: Int) {
    val clamped = tier.coerceIn(0, 5)
    difficultyTier = clamped
    prefs.edit().putInt(KEY_DIFFICULTY, clamped).apply()
  }

  fun toggleShowMoveCounter(enabled: Boolean) {
    showMoveCounter = enabled
    prefs.edit().putBoolean(KEY_SHOW_MOVES, enabled).apply()
  }

  fun toggleHighContrast(enabled: Boolean) {
    highContrastMode = enabled
    prefs.edit().putBoolean(KEY_HIGH_CONTRAST, enabled).apply()
  }

  fun setSensitivity(value: Float) {
    val clamped = value.coerceIn(0.5f, 2.0f)
    swipeSensitivity = clamped
    prefs.edit().putFloat(KEY_SWIPE_SENSITIVITY, clamped).apply()
  }

  fun toggleDpadControls(enabled: Boolean) {
    showDpadControls = enabled
    prefs.edit().putBoolean(KEY_SHOW_DPAD, enabled).apply()
  }

  fun setTheme(theme: String) {
    currentTheme = theme
    prefs.edit().putString(KEY_THEME, theme).apply()
  }

  fun resetToDefaults() {
    setSfxVol(0.8f)
    setMusicVol(0.5f)
    toggleHaptics(true)
    setDifficulty(2)
    toggleShowMoveCounter(true)
    toggleHighContrast(false)
    setSensitivity(1.0f)
    toggleDpadControls(true)
    setTheme("CYBER_NEON")
  }

  companion object {
    private const val KEY_SFX_VOL = "sfx_volume"
    private const val KEY_MUSIC_VOL = "music_volume"
    private const val KEY_HAPTICS = "haptics_enabled"
    private const val KEY_DIFFICULTY = "difficulty_tier"
    private const val KEY_SHOW_MOVES = "show_move_counter"
    private const val KEY_HIGH_CONTRAST = "high_contrast_mode"
    private const val KEY_SWIPE_SENSITIVITY = "swipe_sensitivity"
    private const val KEY_SHOW_DPAD = "show_dpad"
    private const val KEY_THEME = "current_theme"

    val DIFFICULTY_NAMES = listOf(
      "Casual",      // 0
      "Novice",      // 1
      "Adept",       // 2
      "Expert",      // 3
      "Master",      // 4
      "Grandmaster"  // 5
    )

    val DIFFICULTY_DESCRIPTIONS = listOf(
      "4x4 Grid • 1-2 Orbs • Relaxed constraints",
      "5x5 Grid • 2 Orbs • Gentle obstacles",
      "5x5 - 6x6 • 2-3 Orbs • Strategic blocker routing",
      "6x6 Grid • 3 Orbs • Laser gates & multi-stops",
      "7x7 Grid • 3-4 Orbs • Complex routing & portals",
      "8x8 Grid • 4 Orbs • Maximum spatial challenge"
    )
  }
}
