package com.nova.workouttracker.data

import android.content.Context
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Persists one JSON file per finished workout under filesDir/logs/.
 * Fully offline — plain files, no database, no network.
 */
class LogRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val logsDir: File get() = File(context.filesDir, "logs").apply { mkdirs() }

    fun save(log: WorkoutLog) {
        val stamp = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.US).format(Date(log.finishedAt))
        val file = File(logsDir, "${stamp}_${log.dayId}.json")
        file.writeText(json.encodeToString(WorkoutLog.serializer(), log))
    }

    fun list(): List<WorkoutLog> =
        logsDir.listFiles { f -> f.isFile && f.name.endsWith(".json") }
            .orEmpty()
            .mapNotNull { f ->
                runCatching { json.decodeFromString(WorkoutLog.serializer(), f.readText()) }.getOrNull()
            }
            .sortedByDescending { it.finishedAt }

    fun delete(log: WorkoutLog) {
        logsDir.listFiles().orEmpty().forEach { f ->
            val parsed = runCatching { json.decodeFromString(WorkoutLog.serializer(), f.readText()) }.getOrNull()
            if (parsed?.id == log.id) f.delete()
        }
    }

    /** Logs finished within the last [days] days. */
    fun recent(days: Int): List<WorkoutLog> {
        val cutoff = System.currentTimeMillis() - days * 24L * 60 * 60 * 1000
        return list().filter { it.finishedAt >= cutoff }
    }
}

/** Unit-aware weekly summary used by the Stats screen. */
data class WeeklyStats(
    val sessions: Int,
    /** kg lifted; timed holds excluded, unilateral work counted on both sides */
    val loadedVolumeKg: Double,
    /** total seconds of timed holds (e.g. planks) */
    val timeUnderTensionSec: Int
)

fun summarizeWeek(logs: List<WorkoutLog>): WeeklyStats {
    var volume = 0.0
    var tension = 0
    logs.forEach { log ->
        log.exercises.forEach { ex ->
            val sideMultiplier = if (ex.unilateral == "side" || ex.unilateral == "leg") 2 else 1
            ex.sets.forEach { set ->
                if (ex.unit == "seconds") {
                    tension += set.reps
                } else {
                    volume += set.weightKg * set.reps * sideMultiplier
                }
            }
        }
    }
    return WeeklyStats(sessions = logs.size, loadedVolumeKg = volume, timeUnderTensionSec = tension)
}
