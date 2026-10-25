package com.nova.workouttracker.data

import kotlinx.serialization.Serializable

// ---------- Program (driven by program.json) ----------

@Serializable
data class ProgramMeta(
    val programName: String,
    val version: String,
    val updatedAt: String
)

@Serializable
data class DayItem(
    val exerciseId: String,
    val sets: Int,
    /** e.g. "6-10" or "45-60" */
    val target: String,
    /** "reps" or "seconds" */
    val unit: String,
    /** null, "side" or "leg" */
    val unilateral: String? = null
)

@Serializable
data class Day(
    val id: String,
    val title: String,
    val focus: String,
    val items: List<DayItem>
)

@Serializable
data class VideoRef(
    val watchUrl: String,
    val embedUrl: String,
    val channel: String,
    val title: String
)

@Serializable
data class Exercise(
    val name: String,
    /** drawable/svg base name, e.g. "ic_flat_db_bench" */
    val icon: String,
    val primaryMuscles: List<String>,
    val video: VideoRef,
    val referenceNote: String,
    val setup: List<String>,
    val tempo: String,
    val dos: List<String>,
    val donts: List<String>,
    val mistakes: List<String>,
    val tip: String
)

@Serializable
data class Program(
    val meta: ProgramMeta,
    val days: List<Day>,
    val exercises: Map<String, Exercise>
)

// ---------- Workout logs ----------

@Serializable
data class LoggedSet(
    val weightKg: Double,
    /** reps, or seconds when unit == "seconds" */
    val reps: Int
)

@Serializable
data class LoggedExercise(
    val exerciseId: String,
    val unit: String,
    val unilateral: String? = null,
    val sets: List<LoggedSet>
)

@Serializable
data class WorkoutLog(
    val id: String,
    val dayId: String,
    val dayTitle: String,
    /** epoch millis */
    val startedAt: Long,
    val finishedAt: Long,
    val exercises: List<LoggedExercise>,
    val notes: String = ""
)
