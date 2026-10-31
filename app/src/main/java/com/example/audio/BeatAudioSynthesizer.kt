package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import com.example.data.model.AudioSynthPattern
import com.example.data.model.MusicTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Procedural beat and rhythm synthesizer using Android's native AudioTrack.
 * Generates rhythmic kick drums, snares, hi-hats, and melodic basslines in real-time
 * perfectly matched to the track's BPM!
 */
class BeatAudioSynthesizer {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val sampleRate = 22050
    private var isPlaying = false
    private var isMuted = false

    fun startBeatTrack(track: MusicTrack, onBeatTick: ((beatIndex: Int) -> Unit)? = null) {
        stopBeatTrack()
        isPlaying = true

        playbackJob = scope.launch {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = minBufferSize.coerceAtLeast(sampleRate / 4)

                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

                val format = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .build()

                val trackInstance = AudioTrack(
                    attributes,
                    format,
                    bufferSize,
                    AudioTrack.MODE_STREAM,
                    android.media.AudioManager.AUDIO_SESSION_ID_GENERATE
                )
                audioTrack = trackInstance
                trackInstance.play()

                val bpm = track.bpm.coerceIn(60, 180)
                val beatDurationSec = 60.0 / bpm
                val beatSamples = (sampleRate * beatDurationSec).toInt()

                var step = 0
                while (isActive && isPlaying) {
                    if (isMuted) {
                        delay((beatDurationSec * 1000).toLong())
                        step++
                        continue
                    }

                    val samples = generateBeatStepSamples(track.audioSynthPattern, step, beatSamples)
                    trackInstance.write(samples, 0, samples.size)
                    onBeatTick?.invoke(step)
                    step = (step + 1) % 16
                }
            } catch (e: Exception) {
                Log.w("BeatAudioSynthesizer", "Audio synth interrupted: ${e.message}")
            } finally {
                releaseTrack()
            }
        }
    }

    fun stopBeatTrack() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        releaseTrack()
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    private fun releaseTrack() {
        try {
            audioTrack?.apply {
                if (state == AudioTrack.STATE_INITIALIZED) {
                    pause()
                    flush()
                    stop()
                }
                release()
            }
        } catch (_: Exception) {}
        audioTrack = null
    }

    private fun generateBeatStepSamples(
        pattern: AudioSynthPattern,
        step: Int,
        beatSamples: Int
    ): ShortArray {
        val buffer = ShortArray(beatSamples)
        val isKick = (step % 4 == 0) // Kick on 1, 5, 9, 13
        val isSnare = (step % 4 == 2) // Snare on 3, 7, 11, 15
        val isHiHat = (step % 2 == 1) // Hihat on offbeats

        // Bassline note frequency according to pattern and step
        val bassFreq = when (pattern) {
            AudioSynthPattern.CYBER_PHONK -> if (step < 8) 65.4f else 55.0f // C2 / A1
            AudioSynthPattern.UPBEAT_ELECTRONIC -> when (step % 8) {
                0, 1 -> 130.8f // C3
                2, 3 -> 146.8f // D3
                4, 5 -> 164.8f // E3
                else -> 174.6f // F3
            }
            AudioSynthPattern.LOFI_CHILL -> if (step % 8 < 4) 87.3f else 98.0f // F2 / G2
            AudioSynthPattern.CINEMATIC_TRAP -> if (step < 8) 43.6f else 49.0f // Low 808
            AudioSynthPattern.SUMMER_POP -> when (step % 4) {
                0 -> 196.0f // G3
                1 -> 220.0f // A3
                2 -> 246.9f // B3
                else -> 261.6f // C4
            }
            AudioSynthPattern.ACOUSTIC_SUNSET -> 110.0f // A2
        }

        for (i in 0 until beatSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toFloat() / beatSamples
            var sample = 0.0

            // 1. Kick drum (pitch drop from 120Hz to 40Hz with fast decay)
            if (isKick) {
                val kickDecay = (1.0 - progress.toDouble().coerceIn(0.0, 1.0)).let { it * it * it }
                val currentKickFreq = 40.0 + 80.0 * kickDecay
                sample += sin(2.0 * PI * currentKickFreq * t) * kickDecay * 0.7
            }

            // 2. Snare drum (noise burst + tone)
            if (isSnare) {
                val snareDecay = (1.0 - (progress * 1.5).toDouble().coerceIn(0.0, 1.0)).let { it * it }
                val noise = (Math.random() * 2.0 - 1.0)
                val tone = sin(2.0 * PI * 180.0 * t)
                sample += (noise * 0.5 + tone * 0.3) * snareDecay * 0.5
            }

            // 3. Hi-Hat (metallic high pass noise)
            if (isHiHat && progress < 0.3f) {
                val hatDecay = (1.0 - (progress / 0.3).toDouble()).let { it * it }
                val noise = (Math.random() * 2.0 - 1.0)
                sample += noise * hatDecay * 0.2
            }

            // 4. Melodic bass/synth tone with envelope
            val bassEnv = (1.0 - progress.toDouble() * 0.8).coerceIn(0.1, 1.0)
            sample += sin(2.0 * PI * bassFreq * t) * bassEnv * 0.3

            // Clamp and convert to 16-bit PCM
            val clamped = sample.coerceIn(-1.0, 1.0)
            buffer[i] = (clamped * Short.MAX_VALUE * 0.8).toInt().toShort()
        }

        return buffer
    }
}
