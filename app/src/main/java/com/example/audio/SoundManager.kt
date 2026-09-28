package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural Sound and Haptics Manager.
 * Uses real-time synthesis via AudioTrack so the app has high-quality
 * responsive audio with random pitch variations and volume control without external assets.
 */
class SoundManager(private val context: Context) {

  private val coroutineScope = CoroutineScope(Dispatchers.Default)
  private var musicJob: Job? = null

  var sfxVolume: Float = 0.8f
  var musicVolume: Float = 0.5f
  var hapticsEnabled: Boolean = true

  private val sampleRate = 44100
  private val soundCache = ConcurrentHashMap<String, ShortArray>()

  private val vibrator: Vibrator? by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }

  init {
    pregenerateBuffers()
  }

  private fun pregenerateBuffers() {
    coroutineScope.launch {
      soundCache["click"] = generateSineTone(880f, 0.04f, 0.7f, decay = true)
      soundCache["slide"] = generateChirp(320f, 540f, 0.12f, 0.8f)
      soundCache["bump"] = generateSineTone(140f, 0.08f, 0.9f, decay = true)
      soundCache["target"] = generateDualChime(523.25f, 659.25f, 0.22f, 0.8f)
      soundCache["switch"] = generateChirp(440f, 880f, 0.10f, 0.8f)
      soundCache["portal"] = generateChirp(600f, 300f, 0.18f, 0.8f)
      soundCache["undo"] = generateChirp(600f, 350f, 0.12f, 0.7f)
      soundCache["win"] = generateWinFanfare()
    }
  }

  /**
   * Plays a sound effect with slight randomized pitch jitter and volume scaling.
   */
  fun playSfx(name: String, pitchVariation: Float = 0.08f) {
    if (sfxVolume <= 0.01f) return

    coroutineScope.launch {
      val baseData = soundCache[name] ?: when (name) {
        "click" -> generateSineTone(880f, 0.04f, 0.7f, decay = true)
        "slide" -> generateChirp(320f, 540f, 0.12f, 0.8f)
        "bump" -> generateSineTone(140f, 0.08f, 0.9f, decay = true)
        "target" -> generateDualChime(523.25f, 659.25f, 0.22f, 0.8f)
        "switch" -> generateChirp(440f, 880f, 0.10f, 0.8f)
        "portal" -> generateChirp(600f, 300f, 0.18f, 0.8f)
        "undo" -> generateChirp(600f, 350f, 0.12f, 0.7f)
        "win" -> generateWinFanfare()
        else -> return@launch
      }

      // Random pitch factor between (1.0 - pitchVariation) and (1.0 + pitchVariation)
      val pitchFactor = 1.0f + (Random.nextFloat() * 2f - 1f) * pitchVariation
      val effectiveSampleRate = (sampleRate * pitchFactor).toInt().coerceIn(22050, 64000)

      playPcm(baseData, effectiveSampleRate, sfxVolume)
    }
  }

  fun playMove() {
    playSfx("slide")
    vibrate(15)
  }

  fun playBump() {
    playSfx("bump")
    vibrate(25)
  }

  fun playTargetReached() {
    playSfx("target")
    vibrate(40)
  }

  fun playSwitchToggled() {
    playSfx("switch")
    vibrate(30)
  }

  fun playPortalWarp() {
    playSfx("portal")
    vibrate(35)
  }

  fun playLevelWin() {
    playSfx("win", pitchVariation = 0.02f)
    vibrate(80)
  }

  fun playButtonClick() {
    playSfx("click")
    vibrate(10)
  }

  fun playUndo() {
    playSfx("undo")
    vibrate(15)
  }

  /**
   * Tactile haptic feedback
   */
  fun vibrate(durationMs: Long) {
    if (!hapticsEnabled || vibrator == null || !vibrator!!.hasVibrator()) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator?.vibrate(effect)
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }

  /**
   * Starts ambient background music synthesis: soft ambient evolving synth chords.
   */
  fun startAmbientMusic() {
    if (musicJob != null && musicJob?.isActive == true) return

    musicJob = coroutineScope.launch {
      // Ethereal ambient chord frequencies (Pentatonic ambient C lydian / G major)
      val chordProgressions = listOf(
        floatArrayOf(261.63f, 329.63f, 392.00f, 493.88f), // Cmaj7
        floatArrayOf(220.00f, 261.63f, 329.63f, 392.00f), // Am7
        floatArrayOf(196.00f, 246.94f, 293.66f, 392.00f), // G
        floatArrayOf(174.61f, 220.00f, 261.63f, 329.63f)  // Fmaj7
      )

      var chordIndex = 0
      while (isActive) {
        if (musicVolume > 0.01f) {
          val chord = chordProgressions[chordIndex % chordProgressions.size]
          chordIndex++
          val chordPcm = generateAmbientPad(chord, durationSeconds = 3.6f, volume = musicVolume * 0.4f)
          playPcm(chordPcm, sampleRate, 1.0f)
          delay(3400)
        } else {
          delay(1000)
        }
      }
    }
  }

  fun stopAmbientMusic() {
    musicJob?.cancel()
    musicJob = null
  }

  private fun playPcm(buffer: ShortArray, rate: Int, volume: Float) {
    var audioTrack: AudioTrack? = null
    try {
      val bufferSize = buffer.size * 2
      val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
      val format = AudioFormat.Builder()
        .setSampleRate(rate)
        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
        .build()

      audioTrack = AudioTrack.Builder()
        .setAudioAttributes(attributes)
        .setAudioFormat(format)
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      if (volume < 1.0f) {
        val scaled = ShortArray(buffer.size) { i -> (buffer[i] * volume).toInt().coerceIn(-32767, 32767).toShort() }
        audioTrack.write(scaled, 0, scaled.size)
      } else {
        audioTrack.write(buffer, 0, buffer.size)
      }

      audioTrack.play()
      // AudioTrack will be cleaned up after duration
      val playDurationMs = (buffer.size * 1000L / rate) + 50L
      coroutineScope.launch {
        delay(playDurationMs)
        try {
          audioTrack.stop()
          audioTrack.release()
        } catch (_: Exception) {}
      }
    } catch (_: Exception) {
      audioTrack?.release()
    }
  }

  private fun generateSineTone(freq: Float, durationSec: Float, vol: Float, decay: Boolean): ShortArray {
    val totalSamples = (sampleRate * durationSec).toInt()
    val buffer = ShortArray(totalSamples)
    for (i in 0 until totalSamples) {
      val t = i.toFloat() / sampleRate
      val envelope = if (decay) (1.0f - (i.toFloat() / totalSamples)) else 1.0f
      val wave = sin(2.0 * PI * freq * t).toFloat()
      buffer[i] = (wave * envelope * vol * 32767).toInt().coerceIn(-32767, 32767).toShort()
    }
    return buffer
  }

  private fun generateChirp(startFreq: Float, endFreq: Float, durationSec: Float, vol: Float): ShortArray {
    val totalSamples = (sampleRate * durationSec).toInt()
    val buffer = ShortArray(totalSamples)
    for (i in 0 until totalSamples) {
      val progress = i.toFloat() / totalSamples
      val freq = startFreq + (endFreq - startFreq) * progress
      val t = i.toFloat() / sampleRate
      val envelope = sin(PI * progress).toFloat() // smooth bell envelope
      val wave = sin(2.0 * PI * freq * t).toFloat()
      buffer[i] = (wave * envelope * vol * 32767).toInt().coerceIn(-32767, 32767).toShort()
    }
    return buffer
  }

  private fun generateDualChime(f1: Float, f2: Float, durationSec: Float, vol: Float): ShortArray {
    val totalSamples = (sampleRate * durationSec).toInt()
    val buffer = ShortArray(totalSamples)
    for (i in 0 until totalSamples) {
      val progress = i.toFloat() / totalSamples
      val envelope = (1.0f - progress) * (1.0f - progress)
      val t = i.toFloat() / sampleRate
      val wave = 0.55f * sin(2.0 * PI * f1 * t).toFloat() + 0.45f * sin(2.0 * PI * f2 * t).toFloat()
      buffer[i] = (wave * envelope * vol * 32767).toInt().coerceIn(-32767, 32767).toShort()
    }
    return buffer
  }

  private fun generateWinFanfare(): ShortArray {
    // 3 arpeggiated ascending notes: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
    val notes = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f)
    val noteDuration = 0.13f
    val lastNoteDuration = 0.40f
    val totalSamples = ((notes.size - 1) * noteDuration * sampleRate + lastNoteDuration * sampleRate).toInt()
    val buffer = ShortArray(totalSamples)

    var offset = 0
    for (idx in notes.indices) {
      val dur = if (idx == notes.lastIndex) lastNoteDuration else noteDuration
      val numSamples = (dur * sampleRate).toInt()
      val freq = notes[idx]
      for (i in 0 until numSamples) {
        val destIdx = offset + i
        if (destIdx >= buffer.size) break
        val progress = i.toFloat() / numSamples
        val envelope = if (idx == notes.lastIndex) (1.0f - progress) else (1.0f - progress * 0.4f)
        val t = i.toFloat() / sampleRate
        val wave = sin(2.0 * PI * freq * t).toFloat() + 0.25f * sin(4.0 * PI * freq * t).toFloat()
        buffer[destIdx] = (wave * envelope * 0.7f * 32767).toInt().coerceIn(-32767, 32767).toShort()
      }
      offset += numSamples
    }
    return buffer
  }

  private fun generateAmbientPad(notes: FloatArray, durationSeconds: Float, volume: Float): ShortArray {
    val totalSamples = (sampleRate * durationSeconds).toInt()
    val buffer = ShortArray(totalSamples)
    for (i in 0 until totalSamples) {
      val t = i.toFloat() / sampleRate
      val progress = i.toFloat() / totalSamples
      // Smooth attack and release envelope
      val env = when {
        progress < 0.2f -> progress / 0.2f
        progress > 0.8f -> (1.0f - progress) / 0.2f
        else -> 1.0f
      }

      var sum = 0f
      for (freq in notes) {
        sum += sin(2.0 * PI * freq * t).toFloat()
        // Add subtle harmonic octave overtone
        sum += 0.2f * sin(4.0 * PI * freq * t).toFloat()
      }
      sum /= (notes.size * 1.2f)

      buffer[i] = (sum * env * volume * 32767).toInt().coerceIn(-32767, 32767).toShort()
    }
    return buffer
  }
}
