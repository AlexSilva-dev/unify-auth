package com.example.template.quiz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.template.app.ui.components.AppButton
import com.example.template.app.ui.components.AppText
import com.example.template.app.utils.DateStyle
import com.example.template.app.utils.TimeStyle
import com.example.template.app.utils.formatDate
import com.example.template.app.utils.formatTime
import com.example.template.app.ui.viewModels.AppViewModel
import com.example.template.app.ui.viewModels.BaseState
import com.example.template.quiz.domain.entities.Topic
import com.example.template.quiz.ui.viewModels.TopicsViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import template.composeapp.generated.resources.Res
import template.composeapp.generated.resources.title_topics
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun TopicsScreen(
    topicsViewModel: TopicsViewModel = koinInject<TopicsViewModel>(),
    appViewModel: AppViewModel = koinInject<AppViewModel>(),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(),
    onNavegateToFlashcard: (String) -> Unit
) {
    val topicsData by topicsViewModel.topicsData.collectAsState()
    val listScrollState: LazyGridState = rememberLazyGridState()
    val onGetAllTopicsState by topicsViewModel.onGetAllTopicsState.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(50.dp)
            ) {
                AppText(
                    text = stringResource(Res.string.title_topics).trim(),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                HorizontalDivider(
                    thickness = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(400.dp),
                    state = listScrollState,
                    userScrollEnabled = true,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(
                        items = topicsData.topics
                    ) { topic: Topic ->
                        Column {
                            Row(
                                modifier = Modifier
                                    .height(60.dp)
                                    .fillMaxWidth(),
                            ) {
                                AppButton(
                                    onClick = {
                                        onNavegateToFlashcard(
                                            topic.id.toString()
                                        )
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                                        disabledContentColor = MaterialTheme.colorScheme.background
                                    ),
                                    modifier = Modifier
                                        .fillMaxSize(),
                                ) {
                                    Box(
                                        contentAlignment = Alignment.CenterStart,
                                        modifier = Modifier
                                            .fillMaxSize(),
                                    ) {
                                        AppText(text = topic.text ?: "")
                                        Row(
                                            modifier = Modifier
                                                .align(alignment = Alignment.BottomEnd)
                                                .absolutePadding(0.dp)
                                                .let { currentModifier ->
                                                    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                                                        currentModifier.absoluteOffset(y = 10.dp)
                                                    } else {
                                                        currentModifier
                                                    }
                                                }
                                        ) {
                                            topic.createdAt?.let { timestamp ->
                                                AppText(
                                                    text = timestamp.formatDate(
                                                        style = DateStyle.SHORT,
                                                        localeId = appViewModel.getLocaleSettings().localeId
                                                    ),
                                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                    color = Color.Gray.copy(alpha = 0.5f)
                                                )
                                                Spacer(
                                                    modifier = Modifier.width(10.dp)
                                                )
                                                AppText(
                                                    text = timestamp.formatTime(
                                                        style = TimeStyle.SHORT,
                                                        localeId = appViewModel.getLocaleSettings().localeId
                                                    ),
                                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                                    color = Color.Gray.copy(alpha = 0.5f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        if (onGetAllTopicsState == BaseState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Black.copy(alpha = 0.5f)
                    )
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(MaterialTheme.typography.titleLarge.fontSize.value.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
fun TopicsScreenPreview() {
    val navController = rememberNavController()
}