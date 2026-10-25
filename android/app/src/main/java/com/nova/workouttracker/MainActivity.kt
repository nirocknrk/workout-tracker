package com.nova.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.decode.SvgDecoder
import androidx.compose.runtime.CompositionLocalProvider
import com.nova.workouttracker.data.LogRepository
import com.nova.workouttracker.data.Program
import com.nova.workouttracker.data.ProgramRepository
import com.nova.workouttracker.ui.Routes
import com.nova.workouttracker.ui.screens.ExerciseDetailScreen
import com.nova.workouttracker.ui.screens.HistoryScreen
import com.nova.workouttracker.ui.screens.HomeScreen
import com.nova.workouttracker.ui.screens.ProgramScreen
import com.nova.workouttracker.ui.screens.SessionScreen
import com.nova.workouttracker.ui.screens.StatsScreen
import com.nova.workouttracker.ui.theme.WorkoutTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val programRepository = ProgramRepository(this).apply { ensureDefaults() }
        val logRepository = LogRepository(this)
        val imageLoader = ImageLoader.Builder(this)
            .components { add(SvgDecoder.Factory()) }
            .build()

        setContent {
            WorkoutTrackerTheme {
                CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                    var programVersion by remember { mutableIntStateOf(0) }
                    // Reloaded whenever programVersion changes (e.g. after a bundle import).
                    val program: Program = remember(programVersion) { programRepository.load() }
                    App(program, programRepository, logRepository) {
                        programVersion++
                    }
                }
            }
        }
    }
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

@Composable
private fun App(
    program: Program,
    programRepository: ProgramRepository,
    logRepository: LogRepository,
    onProgramChanged: () -> Unit
) {
    val navController = rememberNavController()
    val tabs = listOf(
        Tab(Routes.HOME, "Home", Icons.Filled.Home),
        Tab(Routes.HISTORY, "History", Icons.Filled.History),
        Tab(Routes.STATS, "Stats", Icons.Filled.BarChart),
        Tab(Routes.PROGRAM, "Program", Icons.Filled.Settings)
    )
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { i, tab ->
                    NavigationBarItem(
                        selected = selectedTab == i,
                        onClick = {
                            selectedTab = i
                            navController.navigate(tab.route) {
                                popUpTo(Routes.HOME) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                selectedTab = 0
                HomeScreen(
                    program = program,
                    programRepository = programRepository,
                    onOpenExercise = { navController.navigate(Routes.exercise(it)) },
                    onStartDay = { navController.navigate(Routes.session(it)) }
                )
            }
            composable(
                Routes.SESSION,
                arguments = listOf(navArgument("dayId") { type = NavType.StringType })
            ) { backStackEntry ->
                val dayId = backStackEntry.arguments?.getString("dayId") ?: program.days.first().id
                SessionScreen(
                    program = program,
                    programRepository = programRepository,
                    logRepository = logRepository,
                    dayId = dayId,
                    onOpenExercise = { navController.navigate(Routes.exercise(it)) },
                    onFinish = {
                        navController.popBackStack(Routes.HOME, inclusive = false)
                        selectedTab = 0
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                Routes.EXERCISE,
                arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                ExerciseDetailScreen(
                    program = program,
                    exerciseId = exerciseId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.HISTORY) {
                selectedTab = 1
                HistoryScreen(program = program, logRepository = logRepository)
            }
            composable(Routes.STATS) {
                selectedTab = 2
                StatsScreen(logRepository = logRepository)
            }
            composable(Routes.PROGRAM) {
                selectedTab = 3
                ProgramScreen(
                    program = program,
                    programRepository = programRepository,
                    onProgramChanged = onProgramChanged
                )
            }
        }
    }
}
