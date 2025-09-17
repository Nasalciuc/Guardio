package com.example.gigahack_2025.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gigahack_2025.R
import com.example.gigahack_2025.ui.theme.*
import kotlinx.coroutines.launch

data class IdentityCard(
    val title: String,
    val cardNumber: String,
    val expiryDate: String,
    val type: String = "identity"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IdentityCardCarousel(
    cards: List<IdentityCard> = getDefaultCards(),
    onQrCodeClick: (IdentityCard) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { cards.size })
    val coroutineScope = rememberCoroutineScope()
    
    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            IdentityCardItem(
                card = cards[page],
                onQrCodeClick = { onQrCodeClick(cards[page]) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Page indicators
        if (cards.size > 1) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(cards.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (index == pagerState.currentPage) 
                                    FigmaPrimaryBlue 
                                else 
                                    FigmaLightBlueAccent
                            )
                    )
                    if (index < cards.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun IdentityCardItem(
    card: IdentityCard,
    onQrCodeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FigmaLightBlue // Light blue background matching Figma design
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Romanian emblem in the background (top-right area)
            Image(
                painter = painterResource(id = R.drawable.romanian_emblem),
                contentDescription = "Romanian Emblem",
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopEnd),
                contentScale = ContentScale.Fit,
                alpha = 0.15f
            )
            
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Title
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = FigmaDarkBlue
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Card number
                Text(
                    text = card.cardNumber,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Normal,
                    color = FigmaDarkBlue
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Bottom row with expiry date and QR button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = card.expiryDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = FigmaGray // Gray color matching Figma design
                    )
                    
                    // QR Code button - black square with QR icon
                    IconButton(
                        onClick = onQrCodeClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.Black,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCode,
                            contentDescription = "QR Code",
                            tint = FigmaWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun getDefaultCards(): List<IdentityCard> {
    return listOf(
        IdentityCard(
            title = "Buletin de identitate",
            cardNumber = "A38078016",
            expiryDate = "Expiră la 20.05.2030"
        ),
        IdentityCard(
            title = "Card de sănătate",
            cardNumber = "FS-0098-202",
            expiryDate = "Expiră la 15.09.2029"
        ),
        IdentityCard(
            title = "Permis de conducere",
            cardNumber = "B1234567",
            expiryDate = "Expiră la 10.12.2028"
        )
    )
}
