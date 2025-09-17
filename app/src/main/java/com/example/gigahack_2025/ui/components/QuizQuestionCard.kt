package com.example.gigahack_2025.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gigahack_2025.data.QuizQuestion
import com.example.gigahack_2025.ui.theme.*
import androidx.compose.ui.res.stringResource
import com.example.gigahack_2025.R

@Composable
fun QuizQuestionCard(
    question: QuizQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    selectedAnswerIndex: Int?,
    onAnswerSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Progress indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.question_progress, questionNumber, totalQuestions),
                style = MaterialTheme.typography.bodyMedium,
                color = FigmaGray
            )
            
            // Progress bar
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(4.dp)
                    .background(FigmaBackgroundGray, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width((100.dp * questionNumber / totalQuestions))
                        .background(FigmaPrimaryBlue, RoundedCornerShape(2.dp))
                )
            }
        }
        
        // Question text
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = FigmaWhite),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    color = FigmaBlack,
                    textAlign = TextAlign.Start,
                    lineHeight = MaterialTheme.typography.headlineSmall.lineHeight
                )
            }
        }
        
        // Answer options
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            question.options.forEachIndexed { index, option ->
                AnswerOption(
                    text = option,
                    isSelected = selectedAnswerIndex == index,
                    onClick = { onAnswerSelected(index) }
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AnswerOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) FigmaPrimaryBlue else FigmaLightGray,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) FigmaPrimaryBlue.copy(alpha = 0.1f) else FigmaWhite
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Option indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) FigmaPrimaryBlue else FigmaBackgroundGray
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(id = R.string.selected),
                        tint = FigmaWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) FigmaPrimaryBlue else FigmaBlack,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
