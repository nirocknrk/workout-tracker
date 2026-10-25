package com.nova.workouttracker.ui

object Routes {
    const val HOME = "home"
    const val SESSION = "session/{dayId}"
    const val EXERCISE = "exercise/{exerciseId}"
    const val HISTORY = "history"
    const val STATS = "stats"
    const val PROGRAM = "program"

    fun session(dayId: String) = "session/$dayId"
    fun exercise(exerciseId: String) = "exercise/$exerciseId"
}
