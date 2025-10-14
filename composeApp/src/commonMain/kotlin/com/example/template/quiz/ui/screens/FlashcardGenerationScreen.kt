package com.example.template.quiz.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.template.app.ui.components.AppButton
import com.example.template.app.ui.components.AppText
import com.example.template.app.ui.components.AppTextField
import com.example.template.app.ui.viewModels.BaseState
import com.example.template.quiz.ui.viewModels.FlashcardHomeViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import template.composeapp.generated.resources.Res
import template.composeapp.generated.resources.btn_generation
import template.composeapp.generated.resources.flashcard_generation_btn_generation_placeholder
import template.composeapp.generated.resources.title_flashcards

@Composable
@Preview
fun FlashcardGenerationScreen(
    onNavigateToTopics: () -> Unit,
    flashcardHomeViewModel: FlashcardHomeViewModel = koinInject<FlashcardHomeViewModel>()
) {
    var studyTopic: String by remember { mutableStateOf("") }
    val createQuizzesState by flashcardHomeViewModel.createQuizzesState.collectAsState()

    adaptingToChangeUiState(
        onNavigateToTopics = onNavigateToTopics,
        flashcardHomeViewModel = flashcardHomeViewModel,
        createQuizzesState = createQuizzesState
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppText(
            text = stringResource(Res.string.title_flashcards).trim(),
            fontSize = MaterialTheme.typography.headlineMedium.fontSize
        )

        AppTextField(
            value = studyTopic,
            onValueChange = { it ->
                studyTopic = it
            },
            placeholderTextStyle = TextStyle.Default.copy(color = Color.Gray),
            placeholderText = stringResource(Res.string.flashcard_generation_btn_generation_placeholder).trim(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        AppButton(
            onClick = {
//                onNavigateToTopics()
                flashcardHomeViewModel.onChangeStudyTopic(
                   studyTopic
               )
               flashcardHomeViewModel.onCreateQuizzes()
            },

            enabled = studyTopic.isNotBlank()
                    && createQuizzesState !is BaseState.Loading,

            modifier = Modifier
                .defaultMinSize(minWidth = 24.dp)
        ) {
            if (createQuizzesState is BaseState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(MaterialTheme.typography.labelLarge.fontSize.value.dp)
                )
            } else {
                AppText(
                    text = stringResource(Res.string.btn_generation).trim(),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
            }
        }
    }
}

@Composable
private fun adaptingToChangeUiState(
    onNavigateToTopics: () -> Unit,
    flashcardHomeViewModel: FlashcardHomeViewModel,
    createQuizzesState: BaseState<Unit>
) {
    LaunchedEffect(createQuizzesState) {
        when (createQuizzesState) {
            is BaseState.Success -> {
                onNavigateToTopics()
                flashcardHomeViewModel.onCreateQuizzesActionCompleted()
            }

            is BaseState.Error -> {
                val errorMessage = (createQuizzesState as BaseState.Error).message

                flashcardHomeViewModel.onCreateQuizzesActionCompleted()
            }

            else -> {
                // Não faz nada nos estados Idle ou Loading
            }
        }
    }
}
