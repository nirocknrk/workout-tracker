package com.nova.workouttracker.ui

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nova.workouttracker.data.ProgramRepository
import java.io.File

/** Exercise icon loaded from the SVG stored in private storage (bundled or imported). */
@Composable
fun ExerciseIcon(
    iconName: String,
    programRepository: ProgramRepository,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val file: File? = programRepository.iconFile(iconName)
    AsyncImage(
        model = file,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(40.dp)
    )
}

/** "4 × 6-10 reps" / "3 × 45-60 sec" / "4 × 8-12 reps per side" */
fun targetText(unit: String, sets: Int, target: String, unilateral: String?): String {
    val unitLabel = when {
        unit == "seconds" -> "sec"
        unilateral == "side" -> "reps per side"
        unilateral == "leg" -> "reps per leg"
        else -> "reps"
    }
    return "$sets × $target $unitLabel"
}

fun formatTension(totalSec: Int): String =
    if (totalSec >= 60) "${totalSec / 60}m ${totalSec % 60}s" else "${totalSec}s"

fun formatVolume(kg: Double): String =
    if (kg >= 1000) "%,.1f".format(kg / 1000) + "t" else "%,.0f".format(kg) + " kg"
