package com.nova.workouttracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nova.workouttracker.data.DayItem
import com.nova.workouttracker.data.Exercise
import com.nova.workouttracker.data.LogRepository
import com.nova.workouttracker.data.LoggedExercise
import com.nova.workouttracker.data.LoggedSet
import com.nova.workouttracker.data.Program
import com.nova.workouttracker.data.ProgramRepository
import com.nova.workouttracker.data.WorkoutLog
import com.nova.workouttracker.ui.ExerciseIcon
import com.nova.workouttracker.ui.targetText
import java.util.UUID

private class SetRow(weight: String = "", reps: String = "") {
    var weight by androidx.compose.runtime.mutableStateOf(weight)
    var reps by androidx.compose.runtime.mutableStateOf(reps)
}

private class SessionExercise(
    val item: DayItem,
    val exercise: Exercise,
    val sets: androidx.compose.runtime.snapshots.SnapshotStateList<SetRow>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    program: Program,
    programRepository: ProgramRepository,
    logRepository: LogRepository,
    dayId: String,
    onOpenExercise: (String) -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val day = program.days.first { it.id == dayId }
    val startedAt = remember { System.currentTimeMillis() }
    val session = remember(dayId) {
        day.items.map { item ->
            val ex = program.exercises[item.exerciseId]!!
            SessionExercise(item, ex, mutableStateListOf<SetRow>().apply {
                repeat(item.sets) { add(SetRow()) }
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(day.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val log = WorkoutLog(
                        id = UUID.randomUUID().toString(),
                        dayId = day.id,
                        dayTitle = day.title,
                        startedAt = startedAt,
                        finishedAt = System.currentTimeMillis(),
                        exercises = session.map { se ->
                            LoggedExercise(
                                exerciseId = se.item.exerciseId,
                                unit = se.item.unit,
                                unilateral = se.item.unilateral,
                                sets = se.sets.map { row ->
                                    LoggedSet(
                                        weightKg = row.weight.toDoubleOrNull() ?: 0.0,
                                        reps = row.reps.toIntOrNull() ?: 0
                                    )
                                }.filter { it.weightKg > 0 || it.reps > 0 }
                            )
                        }.filter { it.sets.isNotEmpty() }
                    )
                    logRepository.save(log)
                    onFinish()
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Finish workout")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(session) { _, se ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onOpenExercise(se.item.exerciseId) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExerciseIcon(se.exercise.icon, programRepository, contentDescription = se.exercise.name)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(se.exercise.name, fontWeight = FontWeight.Bold)
                                Text(
                                    "Target: " + targetText(se.item.unit, se.item.sets, se.item.target, se.item.unilateral),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        val repLabel = if (se.item.unit == "seconds") "Sec" else "Reps"
                        se.sets.forEachIndexed { idx, row ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Text("Set ${idx + 1}", modifier = Modifier.width(56.dp),
                                    style = MaterialTheme.typography.bodyMedium)
                                OutlinedTextField(
                                    value = row.weight,
                                    onValueChange = { row.weight = it.filter { c -> c.isDigit() || c == '.' } },
                                    label = { Text("kg") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                )
                                OutlinedTextField(
                                    value = row.reps,
                                    onValueChange = { row.reps = it.filter { c -> c.isDigit() } },
                                    label = { Text(repLabel) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { se.sets.removeAt(idx) }) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Remove set")
                                }
                            }
                        }
                        IconButton(onClick = { se.sets.add(SetRow()) }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add set")
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
