package com.example.template.app.ui.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.template.app.navigations.BarItens
import com.example.template.app.navigations.Screen
import com.example.template.app.ui.components.AppText
import com.example.template.app.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun CleanLayout(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    darkTheme: Boolean = true,
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(),
    content: @Composable () -> Unit
) {
    AppTheme(darkTheme = darkTheme) {

        val showBottomBar = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Aplica o padding do Scaffold
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp)
                        .clip(shape = MaterialTheme.shapes.large)
                        .background(color = MaterialTheme.colorScheme.secondary)
                ) {
                    content()
                }
            }
        }
    }
}


@Composable
private fun AppBottomBar(
    onNavigate: (route: String) -> Unit,
    currentRoute: String?
) {
    NavigationBar {
        BarItens.main.forEach { screen ->
            NavigationBarItem(
                selected = screen.route == currentRoute,
                onClick = { onNavigate(screen.route) },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { AppText(screen.title) }
            )
        }
    }
}

@Composable
private fun AppNavigationRail(
    onNavigate: (route: String) -> Unit,
    currentRoute: String?
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        header = {
            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
            FloatingActionButton(
                onClick = { onNavigate(Screen.FlashcardGeneration.route) },
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = Screen.FlashcardGeneration.icon,
                    contentDescription = Screen.FlashcardGeneration.title
                )
            }
        }
    ) {
        Spacer(Modifier.weight(1f))
        BarItens.main.filter { it != Screen.FlashcardGeneration }.forEach { screen ->
            NavigationRailItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { AppText(screen.title) },
                selected = currentRoute == screen.route,
                onClick = { onNavigate(screen.route) },
                alwaysShowLabel = false
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun AppNavigationDrawer(
    onNavigate: (route: String) -> Unit,
    currentRoute: String?,
    content: @Composable () -> Unit
) {
    PermanentNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                )

                BarItens.main.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                        label = { AppText(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = { onNavigate(screen.route) },
                        modifier = Modifier
                            .padding(5.dp)
                    )

                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                    )
                }
            }

        }
    ) {
        content()
    }
}