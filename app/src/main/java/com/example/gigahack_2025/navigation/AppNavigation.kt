package com.example.gigahack_2025.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gigahack_2025.ui.screens.*
import com.example.gigahack_2025.ui.components.getMockSecurityArticles

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Documents : Screen("documents")
    object Payments : Screen("payments")
    object Account : Screen("account")
    object Security : Screen("security")
    object ScanHistory : Screen("scan_history")
    object ReportProblem : Screen("report_problem")
    object TestKnowledge : Screen("test_knowledge")
    data class AlertDetail(val articleId: String) : Screen("alert_detail/$articleId")
}

@Composable
fun AppNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onPop: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (currentScreen) {
        is Screen.Home -> HomeScreen(
            onQrCodeClick = { /* Handle QR code click */ },
            onNotificationsClick = { /* Handle notifications click */ },
            onServiceClick = { serviceName -> 
                if (serviceName == "Securitate") {
                    onNavigate(Screen.Security)
                }
                // Handle other service clicks here
            },
            modifier = modifier
        )
        is Screen.Documents -> DocumentsScreen(modifier = modifier)
        is Screen.Payments -> PaymentsScreen(modifier = modifier)
        is Screen.Account -> AccountScreen(modifier = modifier)
        is Screen.Security -> SecurityScreen(
            onBackClick = { onPop() },
            onHistoryClick = { onNavigate(Screen.ScanHistory) },
            onReportClick = { onNavigate(Screen.ReportProblem) },
            onTestKnowledgeClick = { onNavigate(Screen.TestKnowledge) },
            onArticleClick = { articleId -> onNavigate(Screen.AlertDetail(articleId)) },
            modifier = modifier
        )
            is Screen.ScanHistory -> ScanHistoryScreen(
                onBackClick = { onPop() },
                modifier = modifier
            )
            is Screen.ReportProblem -> ReportProblemScreen(
                onBackClick = { onPop() },
                modifier = modifier
            )
            is Screen.TestKnowledge -> TestKnowledgeScreen(
                onBackClick = { onPop() },
                modifier = modifier
            )
            is Screen.AlertDetail -> {
                val article = getMockSecurityArticles().find { it.id == currentScreen.articleId }
                if (article != null) {
                    AlertDetailScreen(
                        article = article,
                        onBackClick = { onPop() },
                        modifier = modifier
                    )
                }
            }
    }
}
