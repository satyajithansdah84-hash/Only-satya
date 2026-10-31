package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.CaptionStyle
import com.example.data.model.TimedCaption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiReelGenerationResult(
    val title: String,
    val hookHeadline: String,
    val captions: List<TimedCaption>,
    val hashtags: List<String>,
    val suggestedBpm: Int,
    val suggestedTransition: String,
    val rawIdeaSummary: String
)

class GeminiReelService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateReelContent(
        topicOrVibe: String,
        clipCount: Int,
        totalDurationSeconds: Float
    ): AiReelGenerationResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateSmartFallback(topicOrVibe, clipCount, totalDurationSeconds)
        }

        try {
            val prompt = """
                You are a world-class Instagram Reel & TikTok viral growth editor.
                Create an ultra-engaging, high-retention reel script for: "$topicOrVibe".
                Total clips: $clipCount. Total duration: $totalDurationSeconds seconds.
                
                Respond strictly with a JSON object in this format (no markdown code blocks, just raw JSON):
                {
                  "title": "Short catchy project name (max 4 words)",
                  "hookHeadline": "High-CTR opening visual hook text (e.g. 'Stop scrolling if you want X', 'POV: You did this...')",
                  "hashtags": ["#tag1", "#tag2", "#tag3", "#tag4", "#tag5"],
                  "suggestedBpm": 120,
                  "suggestedTransition": "ZOOM_PULSE",
                  "captions": [
                    {
                      "text": "Caption phrase for clip 1",
                      "subtitle": "Secondary punchy note",
                      "startSec": 0.0,
                      "endSec": 2.0
                    }
                  ]
                }
            """.trimIndent()

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                })
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("GeminiReelService", "Gemini HTTP error ${response.code}, falling back")
                return@withContext generateSmartFallback(topicOrVibe, clipCount, totalDurationSeconds)
            }

            val respBody = response.body?.string() ?: ""
            val jsonRoot = JSONObject(respBody)
            val candidates = jsonRoot.optJSONArray("candidates")
            val firstPartText = candidates
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")

            if (firstPartText.isNullOrBlank()) {
                return@withContext generateSmartFallback(topicOrVibe, clipCount, totalDurationSeconds)
            }

            // Clean json text if wrapped
            val cleanJson = firstPartText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val parsed = JSONObject(cleanJson)

            val title = parsed.optString("title", "AI Viral Reel")
            val hook = parsed.optString("hookHeadline", "Wait for the reveal... ✨")
            val hashtagsJson = parsed.optJSONArray("hashtags")
            val tags = mutableListOf<String>()
            if (hashtagsJson != null) {
                for (i in 0 until hashtagsJson.length()) {
                    tags.add(hashtagsJson.getString(i))
                }
            } else {
                tags.addAll(listOf("#reels", "#viral", "#aesthetic", "#fyp"))
            }

            val bpm = parsed.optInt("suggestedBpm", 120)
            val transition = parsed.optString("suggestedTransition", "ZOOM_PULSE")

            val captionsJson = parsed.optJSONArray("captions")
            val captionsList = mutableListOf<TimedCaption>()
            if (captionsJson != null && captionsJson.length() > 0) {
                for (i in 0 until captionsJson.length()) {
                    val capObj = captionsJson.getJSONObject(i)
                    captionsList.add(
                        TimedCaption(
                            id = "ai_cap_$i",
                            text = capObj.optString("text", "Vibe check"),
                            startSeconds = capObj.optDouble("startSec", (i * 2.0)).toFloat(),
                            endSeconds = capObj.optDouble("endSec", ((i + 1) * 2.0)).toFloat(),
                            hookSubtitle = capObj.optString("subtitle", "")
                        )
                    )
                }
            } else {
                captionsList.addAll(generateFallbackCaptions(topicOrVibe, clipCount, totalDurationSeconds))
            }

            AiReelGenerationResult(
                title = title,
                hookHeadline = hook,
                captions = captionsList,
                hashtags = tags,
                suggestedBpm = bpm,
                suggestedTransition = transition,
                rawIdeaSummary = "Generated with Gemini 3.5 Flash for '$topicOrVibe'"
            )
        } catch (e: Exception) {
            Log.e("GeminiReelService", "Error calling Gemini: ${e.message}", e)
            generateSmartFallback(topicOrVibe, clipCount, totalDurationSeconds)
        }
    }

    private fun generateSmartFallback(
        topicOrVibe: String,
        clipCount: Int,
        totalDurationSeconds: Float
    ): AiReelGenerationResult {
        val topicLower = topicOrVibe.lowercase()

        val (title, hook, hashtags, bpm, trans) = when {
            topicLower.contains("travel") || topicLower.contains("trip") || topicLower.contains("vacation") -> {
                Tuple5(
                    "Wanderlust Moments",
                    "POV: You booked the flight and never looked back ✈️",
                    listOf("#travelreels", "#wanderlust", "#aesthetic", "#bucketlist", "#cinematic"),
                    110,
                    "ZOOM_PULSE"
                )
            }
            topicLower.contains("gym") || topicLower.contains("workout") || topicLower.contains("fitness") -> {
                Tuple5(
                    "Beast Mode Grind",
                    "NO TALKING. JUST RESULTS. 🔥",
                    listOf("#gymtok", "#fitnessmotivation", "#discipline", "#workoutreels", "#beastmode"),
                    135,
                    "FLASH_BEAT"
                )
            }
            topicLower.contains("night") || topicLower.contains("cyber") || topicLower.contains("city") -> {
                Tuple5(
                    "Tokyo Neon Drift",
                    "City lights hit different at 2 AM ⚡",
                    listOf("#cyberpunk", "#citylights", "#nightdrive", "#phonk", "#aesthetic"),
                    140,
                    "GLITCH"
                )
            }
            topicLower.contains("coffee") || topicLower.contains("morning") || topicLower.contains("daily") -> {
                Tuple5(
                    "Slow Morning Aesthetic",
                    "Romanticize your daily routine ☕",
                    listOf("#vlog", "#morningvibes", "#cozy", "#dailyroutine", "#aesthetic"),
                    88,
                    "FADE"
                )
            }
            else -> {
                Tuple5(
                    if (topicOrVibe.isNotBlank()) topicOrVibe.take(20).replaceFirstChar { it.uppercase() } else "Viral Reel Cut",
                    "Wait until the beat drops... 💥",
                    listOf("#reelsinstagram", "#viralreels", "#trending", "#fyp", "#creator"),
                    120,
                    "ZOOM_PULSE"
                )
            }
        }

        val captions = generateFallbackCaptions(topicOrVibe, clipCount, totalDurationSeconds)

        return AiReelGenerationResult(
            title = title,
            hookHeadline = hook,
            captions = captions,
            hashtags = hashtags,
            suggestedBpm = bpm,
            suggestedTransition = trans,
            rawIdeaSummary = "AI Beat-Matched Reel ($clipCount clips, ${totalDurationSeconds}s)"
        )
    }

    private fun generateFallbackCaptions(
        topicOrVibe: String,
        clipCount: Int,
        totalDurationSeconds: Float
    ): List<TimedCaption> {
        val count = if (clipCount > 0) clipCount else 3
        val segmentDuration = totalDurationSeconds / count
        val phrases = listOf(
            Pair("The journey starts here.", "Take it all in ✨"),
            Pair("Nothing beats this feeling.", "Pure dopamine ⚡"),
            Pair("Proof that magic is real.", "Save this memory 🌊"),
            Pair("Every second was worth it.", "Drop a comment below 💬"),
            Pair("Wait for the final shot.", "Unbelievable view 🔥")
        )

        return (0 until count).map { i ->
            val (main, sub) = phrases[i % phrases.size]
            TimedCaption(
                id = "cap_$i",
                text = main,
                startSeconds = i * segmentDuration,
                endSeconds = (i + 1) * segmentDuration,
                hookSubtitle = sub
            )
        }
    }

    private data class Tuple5<A, B, C, D, E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
    )
}
