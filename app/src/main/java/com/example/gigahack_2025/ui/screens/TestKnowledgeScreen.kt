package com.example.gigahack_2025.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.gigahack_2025.data.*
import com.example.gigahack_2025.ui.components.*
import com.example.gigahack_2025.ui.theme.*

@Composable
fun TestKnowledgeScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val questions = getCybersecurityQuizQuestions()
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var showResultDialog by remember { mutableStateOf(false) }
    var quizResult by remember { mutableStateOf<QuizResult?>(null) }
    
    // Calculate score
    fun calculateScore(): QuizResult {
        var score = 0
        questions.forEachIndexed { index, question ->
            val selectedAnswer = selectedAnswers[index]
            if (selectedAnswer == question.correctAnswerIndex) {
                score++
            }
        }
        
        val percentage = (score * 100) / questions.size
        val message = getQuizResultMessage(score, questions.size)
        
        return QuizResult(
            score = score,
            totalQuestions = questions.size,
            percentage = percentage,
            message = message
        )
    }
    
    // Handle answer selection
    fun onAnswerSelected(answerIndex: Int) {
        selectedAnswers = selectedAnswers + (currentQuestionIndex to answerIndex)
        
        // Move to next question or show results
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
        } else {
            // Quiz completed
            quizResult = calculateScore()
            showResultDialog = true
        }
    }
    
    // Restart quiz
    fun restartQuiz() {
        currentQuestionIndex = 0
        selectedAnswers = emptyMap()
        showResultDialog = false
        quizResult = null
    }
    
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
                    contentDescription = stringResource(id = com.example.gigahack_2025.R.string.back),
                    tint = FigmaDarkBlue
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = com.example.gigahack_2025.R.string.quiz_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Normal,
                color = FigmaDarkBlue
            )
        }
        
        // Quiz content
        if (currentQuestionIndex < questions.size) {
            val currentQuestion = questions[currentQuestionIndex]
            val selectedAnswer = selectedAnswers[currentQuestionIndex]
            
            QuizQuestionCard(
                question = currentQuestion,
                questionNumber = currentQuestionIndex + 1,
                totalQuestions = questions.size,
                selectedAnswerIndex = selectedAnswer,
                onAnswerSelected = ::onAnswerSelected
            )
        }
        
        // Result dialog
        if (showResultDialog && quizResult != null) {
            QuizResultDialog(
                result = quizResult!!,
                onDismiss = { showResultDialog = false },
                onRestart = ::restartQuiz
            )
        }
    }
}
