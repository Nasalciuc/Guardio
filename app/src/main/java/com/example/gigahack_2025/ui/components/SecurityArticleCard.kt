package com.example.gigahack_2025.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.gigahack_2025.R
import com.example.gigahack_2025.ui.theme.*

data class SecurityArticle(
    val id: String,
    val title: String,
    val preview: String,
    val imageResource: Int? = null,
    val category: String = "Security Alert",
    val read: Boolean = false
)

@Composable
fun SecurityArticleCard(
    article: SecurityArticle,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FigmaBackgroundGray // rgb(245, 245, 245)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image section - matches Figma exactly
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(233.dp) // Back to original height for proper fitting
                    .padding(12.dp) // Figma: 12dp padding
            ) {
                // Article image
                if (article.imageResource != null) {
                    Image(
                        painter = painterResource(id = article.imageResource),
                        contentDescription = "Article Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.FillBounds // Changed to FillBounds for proper fitting
                    )
                } else {
                    // Fallback placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FigmaLightGray)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = "Security Alert",
                                tint = FigmaGray,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }
            
            // Content section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Content section - removed read time
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Heading
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold, // weight-600
                        color = FigmaBlack, // rgb(18, 18, 18)
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Preview text
                    Text(
                        text = article.preview,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Normal, // weight-400
                        color = FigmaDarkGray, // rgb(56, 56, 56)
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Actions section
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Read more link
                    TextButton(
                        onClick = onClick,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Citește mai mult", // Romanian: "Read more"
                            style = MaterialTheme.typography.bodyMedium,
                            color = FigmaPrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// Mock data for security articles
fun getMockSecurityArticles(): List<SecurityArticle> {
    return listOf(
        SecurityArticle(
            id = "1",
            title = "Atenție, dezinformare!",
            preview = " Alerte , Comunicate de presă / Mar, 27.05.2025 - 15:35 ",
            imageResource = R.drawable.security_alert_3,
            category = "Security Alert",
            read = false
        ),
        SecurityArticle(
            id = "2",
            title = "Atenție la dezinformare!",
            preview = " Alerte , Comunicate de presă / Joi, 22.05.2025 - 16:03 ",
            imageResource = R.drawable.security_alert_2,
            category = "Security Alert",
            read = false
        ),
        SecurityArticle(
            id = "3",
            title = "STISC avertizează: ATENȚIE la e-mail-urile ce pretind a fi din numele unor autorități",
            preview = " Alerte , Comunicate de presă , Conștientizare / Mie, 16.04.2025 - 20:13 ",
            imageResource = R.drawable.security_alert_1,
            category = "Security Alert",
            read = false
        )
    )
}
