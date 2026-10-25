package com.nova.workouttracker.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nova.workouttracker.data.Program
import com.nova.workouttracker.util.isOnline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    program: Program,
    exerciseId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val exercise = program.exercises[exerciseId] ?: run {
        Scaffold(topBar = { TopAppBar(title = { Text("Not found") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
        }) }) { p -> Text("Exercise not found", modifier = Modifier.padding(p).padding(16.dp)) }
        return
    }
    var showOfflineNotice by remember { mutableStateOf(false) }

    if (showOfflineNotice) {
        AlertDialog(
            onDismissRequest = { showOfflineNotice = false },
            title = { Text("You're offline") },
            text = { Text("Technique videos stream from YouTube and need an internet connection. Connect and try again — everything else in this app works offline.") },
            confirmButton = { TextButton(onClick = { showOfflineNotice = false }) { Text("OK") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Video
            item {
                Card(
                    onClick = {
                        if (isOnline(context)) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(exercise.video.watchUrl)))
                        } else {
                            showOfflineNotice = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Watch technique video", fontWeight = FontWeight.Bold)
                            Text(exercise.video.title, style = MaterialTheme.typography.bodySmall)
                            Text("by ${exercise.video.channel}", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            item { SectionCard("Reference") { Text(exercise.referenceNote) } }
            item {
                SectionCard("Setup") {
                    exercise.setup.forEachIndexed { i, step ->
                        Text("${i + 1}. $step", modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tempo: ", fontWeight = FontWeight.Bold)
                    AssistChip(onClick = {}, label = { Text(exercise.tempo) })
                    Spacer(Modifier.width(8.dp))
                    exercise.primaryMuscles.forEach { m ->
                        AssistChip(onClick = {}, label = { Text(m) })
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
            item {
                SectionCard("Dos") {
                    exercise.dos.forEach { Bullet(icon = Icons.Filled.Check, text = it, tint = MaterialTheme.colorScheme.primary) }
                }
            }
            item {
                SectionCard("Don'ts") {
                    exercise.donts.forEach { Bullet(icon = Icons.Filled.Close, text = it, tint = MaterialTheme.colorScheme.error) }
                }
            }
            item {
                SectionCard("Common mistakes") {
                    exercise.mistakes.forEach { Bullet(icon = Icons.Filled.Warning, text = it, tint = MaterialTheme.colorScheme.tertiary) }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Filled.Lightbulb, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
                        Column {
                            Text("Pro tip", fontWeight = FontWeight.Bold)
                            Text(exercise.tip)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun Bullet(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: androidx.compose.ui.graphics.Color
) {
    Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.padding(end = 8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
