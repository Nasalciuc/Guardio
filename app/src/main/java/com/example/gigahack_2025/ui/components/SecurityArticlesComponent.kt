package com.example.gigahack_2025.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gigahack_2025.ui.theme.*

@Composable
fun SecurityArticlesComponent(
    onArticleClick: (SecurityArticle) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val articles = getMockSecurityArticles()
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Articles List - no header needed
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            articles.forEach { article ->
                SecurityArticleCard(
                    article = article,
                    onClick = { onArticleClick(article) }
                )
            }
        }
    }
}