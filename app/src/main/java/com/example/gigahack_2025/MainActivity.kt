package com.example.gigahack_2025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import com.example.gigahack_2025.navigation.AppNavigation
import com.example.gigahack_2025.navigation.Screen
import com.example.gigahack_2025.ui.components.*
import com.example.gigahack_2025.ui.theme.GIGAHACK_2025Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GIGAHACK_2025Theme {
                val navigateToReport = intent?.getBooleanExtra("navigate_to_report", false) == true
                MainApp(openReportInitially = navigateToReport)
            }
        }
    }
}

@Composable
fun MainApp(openReportInitially: Boolean = false) {
    val backStack = remember { mutableStateListOf<Screen>() }
    LaunchedEffect(openReportInitially) {
        backStack.clear()
        backStack.add(if (openReportInitially) Screen.ReportProblem else Screen.Home)
    }
    val currentScreen = backStack.lastOrNull() ?: Screen.Home

    fun navigate(to: Screen) {
        if (backStack.isEmpty() || backStack.last() != to) {
            backStack.add(to)
        }
    }

    fun navigateReplace(to: Screen) {
        if (backStack.isNotEmpty()) backStack[backStack.lastIndex] = to else backStack.add(to)
    }

    // Always intercept system back; pop if possible else finish
    val activity = LocalContext.current as? ComponentActivity
    BackHandler(enabled = true) {
        if (backStack.size > 1) backStack.removeLast() else activity?.finish()
    }
    
    Scaffold(
        bottomBar = {
            BottomNavigationComponent(
                items = getDefaultBottomNavItems(
                    selectedIndex = when (currentScreen) {
                        is Screen.Home -> 0
                        is Screen.Documents -> 1
                        is Screen.Payments -> 2
                        is Screen.Account -> 3
                        else -> 0
                    },
                    onItemClick = { index ->
                        val target = when (index) {
                            0 -> Screen.Home
                            1 -> Screen.Documents
                            2 -> Screen.Payments
                            3 -> Screen.Account
                            else -> Screen.Home
                        }
                        navigateReplace(target)
                    }
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        AppNavigation(
            currentScreen = currentScreen,
            onNavigate = { navigate(it) },
            onPop = {
                if (backStack.size > 1) backStack.removeLast()
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
