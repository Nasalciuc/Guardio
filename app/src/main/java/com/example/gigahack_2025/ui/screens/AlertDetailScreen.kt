package com.example.gigahack_2025.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.gigahack_2025.R
import com.example.gigahack_2025.ui.components.SecurityArticle
import com.example.gigahack_2025.ui.theme.*

@Composable
fun AlertDetailScreen(
    article: SecurityArticle,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FigmaWhite)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = FigmaDarkBlue
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Alert Details",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Normal,
                color = FigmaDarkBlue
            )
        }
        
        // Content with scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Article image
            if (article.imageResource != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = CardDefaults.cardColors(containerColor = FigmaWhite),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(article.imageResource)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Article Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Article content
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = FigmaWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = FigmaBlack,
                        lineHeight = MaterialTheme.typography.headlineMedium.lineHeight
                    )
                    
                    // Date and category
                    Text(
                        text = article.preview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FigmaGray,
                        fontWeight = FontWeight.Normal
                    )
                    
                    // Divider
                    HorizontalDivider(
                        color = FigmaLightGray,
                        thickness = 1.dp
                    )
                    
                    // Full content based on article ID
                    val fullContent = getFullAlertContent(article.id)
                    Text(
                        text = fullContent,
                        style = MaterialTheme.typography.bodyLarge,
                        color = FigmaBlack,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                        textAlign = TextAlign.Start
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun getFullAlertContent(articleId: String): String {
    return when (articleId) {
        "1" -> """
Serviciul Tehnologia Informației și Securitate Cibernetică (STISC) respinge categoric informațiile false care circulă în spațiul public, în special prin intermediul unor canale neoficiale și rețele de tip fake news, referitoare la o presupusă scurgere de date pe site-ul compensatii.gov.md.

Dezinformarea care circulă online reprezintă o încercare evidentă de manipulare și inducere în eroare a opiniei publice. Acest tip de dezinformare are ca scop afectarea încrederii cetățenilor în instituțiile statului și compromiterea eforturilor depuse pentru consolidarea securității cibernetice în Republica Moldova.

Facem apel la cetățeni să consulte exclusiv sursele oficiale și să evite diseminarea unor materiale neverificate.

STISC colaborează cu autoritățile competente pentru identificarea și sancționarea celor implicați în crearea și distribuirea acestor falsuri.

Pentru informații verificate, vă rugăm să urmăriți canalele oficiale STISC.
        """.trimIndent()
        
        "2" -> """
STISC avertizează: în online circulă un nou conținut fals. Sunt folosite ilegal numele și imaginea unor instituții publice pentru a trimite mesaje false.

Ce se întâmplă:
▪️ Sunt create adrese de e-mail care imită instituții oficiale;
▪️ Se trimit mesaje false către angajați sau cetățeni;
▪️ Informațiile sunt folosite pentru a discredita autoritățile.

Scopul atacului: să răspândească dezinformare, să creeze neîncredere și confuzie.

Ce aveți de făcut:
▪️ Verificați întotdeauna informațiile din surse oficiale;
▪️ Raportați orice mesaj suspect la: info@cert.gov.md

STISC monitorizează situația și ia măsuri împreună cu instituțiile competente.
        """.trimIndent()
        
        "3" -> """
Serviciul Tehnologia Informației și Securitate Cibernetică (STISC) vă avertizează despre răspândirea în mediul online a unor tentative de tip scam promovate/distribuite prin e-mail, în care atacatori cibernetici, utilizează imaginea unor autorități, precum: Ministerul Justiției, Inspectoratul General al Poliției, EUROPOL.

Actorii rău intenționați expediază prin email citații false prin care anunță utilizatorul că au intrat în posesia unor imagini compromițătoare cu tentă sexuală/pornografie, fiind folosite amenințări și șantaj.

Dacă ați primit un asemenea email sau citație, vă atenționăm să le blocați și să le raportați.

STISC atenționează asupra necesității sporirii vigilenței pentru a nu deveni victima unor asemenea tipuri de atacuri cibernetice, ce pot duce la pierderea datelor personale și bancare, inclusiv și compromiterea conturilor de pe rețele sociale.

Totodată, vă îndemnăm să verificați mereu sursa reală a mesajului și să respectați regulile de igienă cibernetică.

Fiți precauți și alegeți să fiți în siguranță online!
        """.trimIndent()
        
        else -> "Content not available."
    }
}
