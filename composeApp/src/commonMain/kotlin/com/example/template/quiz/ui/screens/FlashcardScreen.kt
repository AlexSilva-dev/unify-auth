package com.example.template.quiz.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChipDefaults.IconSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.template.app.ui.components.AppButton
import com.example.template.app.ui.components.AppText
import com.example.template.app.ui.viewModels.BaseState
import com.example.template.quiz.domain.entities.Alternative
import com.example.template.quiz.ui.viewModels.FlashcardData
import com.example.template.quiz.ui.viewModels.FlashcardViewModel
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Checkmark
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import template.composeapp.generated.resources.Res
import template.composeapp.generated.resources.btn_next
import template.composeapp.generated.resources.btn_see_result

@Composable
@Preview
fun FlashcardScreen(
    flashcardViewModel: FlashcardViewModel = koinInject<FlashcardViewModel>(),
    idTopic: String
) {
    val flashcardData: FlashcardData by flashcardViewModel.flashcardData.collectAsState()
    val onGetQuizUiState by flashcardViewModel.onGetQuizzesUiState.collectAsState()

    LaunchedEffect(Unit) {
        flashcardViewModel.onGetQuizzesByTopic(
            idTopic = idTopic
        )
    }

    LaunchedEffect(onGetQuizUiState) {
        when (onGetQuizUiState) {
            is BaseState.Success -> {
                flashcardViewModel.onGetRandomQuiz()
            }
            is BaseState.Error -> {
                // TODO(voltar para tela anterior)
            }
            else -> {

            }
        }
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (
            flashcardData.quizCurrent == null
            || onGetQuizUiState == BaseState.Loading
            || onGetQuizUiState == BaseState.Idle
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(MaterialTheme.typography.titleLarge.fontSize.value.dp)
                )
            }

        } else {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AppText(
                        flashcardData.quizCurrent!!.question ?: "Algo deu errado"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    flashcardData.quizCurrent!!.alternatives.forEachIndexed { index: Int, alternative: Alternative ->
                        var borderStroke: BorderStroke = BorderStroke(
                            width = 1.dp,
                            color = Color.Transparent
                        )
                        if (flashcardData.seeResult && alternative.isCorrect) {
                            borderStroke = BorderStroke(
                                width = 1.dp,
                                color = Color.Green
                            )
                        }
                        this@SingleChoiceSegmentedButtonRow.SegmentedButton(
                            onClick = {
                                flashcardViewModel.onSelectedIndex(index)
                            },
                            enabled = !flashcardData.seeResult,
                            selected = index == flashcardData.selectedIndex,
                            shape = MaterialTheme.shapes.medium,
                            border = borderStroke,
                            modifier = Modifier
                                .fillMaxWidth(),
                            icon = {
                                if (alternative.isCorrect && flashcardData.seeResult) {
                                    Icon(
                                        imageVector = EvaIcons.Outline.Checkmark,
                                        contentDescription = null,
                                        modifier = Modifier.size(IconSize)
                                    )
                                }
                            }
                        ) {
                            AppText(
                                alternative.answer ?: "Algo deu errado"
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            if (!flashcardData.seeResult) {
                AppButton(
                    onClick = {
                        flashcardViewModel.onSeeResult()
                    },
                    enabled = flashcardData.selectedIndex != -1,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    AppText(
                        stringResource(Res.string.btn_see_result).trim()
                    )
                }

            } else {
                AppButton(
                    onClick = {
                        flashcardViewModel.onGetRandomQuiz()
                        flashcardViewModel.onNextQuiz()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    AppText(
                        stringResource(Res.string.btn_next).trim()
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun FlashcardScreenPreview() {
    FlashcardScreen(
        idTopic = "f50bbda4-1e10-4a0b-9e92-e94714363479"
    )
}