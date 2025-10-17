package com.example.template.authentication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.template.app.ui.components.AppButton
import com.example.template.app.ui.components.AppSurface
import com.example.template.app.ui.components.AppText
import com.example.template.app.ui.components.AppTextField
import com.example.template.app.ui.viewModels.BaseState
import com.example.template.authentication.ui.viewModels.LoginViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import unify_auth.composeapp.generated.resources.*

@Composable
fun LoginScreen(
    uriHandler: UriHandler = LocalUriHandler.current,
    loginViewModel: LoginViewModel = koinInject<LoginViewModel>(),
    onHomeNavigate: () -> Unit,
    onSignInNavigate: () -> Unit
) {
    val loginData by loginViewModel.loginData.collectAsState()
    val loginState by loginViewModel.onLoginState.collectAsState<BaseState<Unit>>()
    val user: String = remember { "" }
    LaunchedEffect(loginState) {
        when (loginState) {
            is BaseState.Success -> {
                onHomeNavigate()
            }

            is BaseState.Error -> {
                // TODO(voltar para tela anterior)
                loginViewModel.onResetLoginState()
            }

            else -> {
            }
        }
    }

    Box(
        Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {

        Image(
            painter = painterResource(Res.drawable.background_login),
            contentDescription = "back",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        AppSurface(
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .size(width = 450.dp, height = 500.dp)
                .padding(horizontal = 4.dp)

        ) {
            if (loginState == BaseState.Loading) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(color = Color.Black.copy(alpha = 0.3f))
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AppText(
                        text = stringResource(
                            Res.string.title_login
                        ).trim(),
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        textAlign = TextAlign.Center
                    )
                    AppTextField(
                        value = user,
                        onValueChange = {},
                        placeholderText = stringResource(Res.string.user_textField_placeholder_login).trim()
                    )

                    AppTextField(
                        value = user,
                        onValueChange = {},
                        placeholderText = stringResource(Res.string.pass_textField_placeholder_login).trim()
                    )

                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                    )

                    AppButton(
                        onClick = {
                        },
                    ) {
                        AppText(
                            "Login"
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    ) {
                        AppText(
                            text = stringResource(
                                Res.string.loginScreen_create_account
                            ).trim()
                        )
                        AppText(
                            text = stringResource(
                                Res.string.loginScreen_page_create_account
                            ).trim(),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(5.dp, 0.dp)
                                .wrapContentWidth()
                                .clickable(
                                    enabled = true,
                                    onClick = {
                                        onSignInNavigate()
                                    }
                                )
                        )
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        thickness = 2.dp,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(vertical = 20.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        ButtonGoogleAuth(loginViewModel)
                    }
                }
            }
        }

    }
}

@Composable
expect fun ButtonGoogleAuth(
    loginViewModel: LoginViewModel
)

