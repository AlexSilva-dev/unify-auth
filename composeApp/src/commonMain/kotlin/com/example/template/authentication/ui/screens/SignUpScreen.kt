package com.example.template.authentication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.template.app.ui.components.*
import com.example.template.app.ui.viewModels.BaseState
import com.example.template.authentication.ui.viewModels.SignUpViewModel
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.ArrowBack
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import unify_auth.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onLoginNavigate: () -> Unit,
    signUpViewModel: SignUpViewModel = koinInject<SignUpViewModel>()
) {
    // O estado do ViewModel é a única fonte da verdade.
    val signUpViewModelData by signUpViewModel.signInViewModelData.collectAsState()
    val tooltipState: TooltipState = rememberTooltipState()

    LaunchedEffect(signUpViewModelData.signInState) {
        when (signUpViewModelData.signInState) {
            is BaseState.Success -> {
                onLoginNavigate()
            }

            is BaseState.Error -> {
                // TODO(voltar para tela anterior)
                signUpViewModel.onResetSignInState()
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
            if (signUpViewModelData.signInState == BaseState.Loading) {

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
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            IconButton(
                                onClick = {
                                    onLoginNavigate()
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                            ) {
                                Icon(
                                    EvaIcons.Outline.ArrowBack,
                                    contentDescription = null
                                )
                            }

                            AppText(
                                text = stringResource(
                                    Res.string.signIn_title
                                ).trim(),
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    AppTextField(
                        value = signUpViewModelData.name,
                        onValueChange = { newValue ->
                            signUpViewModel.onUpdateName(name = newValue)
                        },
                        singleLine = true,
                        labelText = stringResource(
                            Res.string.signIn_name_and_lastName
                        ).trim(),
                        placeholderText = "Gustavo Delvale",
                        isError = signUpViewModelData.nameError != null,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    AppTextField(
                        value = signUpViewModelData.email,
                        onValueChange = { newValue ->
                            signUpViewModel.onUpdateEmail(email = newValue)
                        },
                        isError = signUpViewModelData.emailError != null && signUpViewModelData.email.isNotEmpty(),
                        singleLine = true,
                        labelText = stringResource(
                            Res.string.user_textField_placeholder_login
                        ).trim(),
                        placeholderText = "email@exemple.com",
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    AppPasswordTextField(
                        value = signUpViewModelData.password,
                        labelText = stringResource(
                            Res.string.pass_textField_placeholder_login
                        ).trim(),
                        placeholderText = "******",
                        onTextChanged = { newValue ->
                            signUpViewModel.onUpdatePassword(password = newValue)
                        },
                        semanticContentDescription = "Precisa ser forte",
                        validateStrengthPassword = true,
                        onHasStrongPassword = { isStrong ->
                            signUpViewModel.onUpdatePasswordStrength(isStrong)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                    )

                    AppPasswordTextField(
                        value = signUpViewModelData.confirmPassword,
                        labelText = stringResource(
                            Res.string.signIn_confirmPassword
                        ).trim(),
                        placeholderText = "******",
                        onTextChanged = { newValue ->
                            signUpViewModel.onUpdateConfirmPassword(confirmPassword = newValue)
                        },
                        hasError = signUpViewModelData.confirmPasswordError != null,
                        modifier = Modifier
                            .fillMaxWidth(),
                    )

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            when (signUpViewModelData.isFormValid) {
                                true -> {

                                }

                                false -> {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = MaterialTheme.shapes.large,
                                    ) {
                                        AppText(
                                            text = stringResource(Res.string.signin_incorrectData)
                                                .trim(),
                                            modifier = Modifier
                                                .padding(10.dp)
                                        )
                                    }
                                }
                            }
                        },
                        state = tooltipState,
                    ) {
                        AppButton(
                            onClick = {
                                signUpViewModel.onSignIn()
                            },
                            // O botão é habilitado com base no estado `isFormValid` do ViewModel.
                            enabled = signUpViewModelData.isFormValid,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            AppText(
                                text = stringResource(Res.string.signIn_signIn).trim()
                            )
                        }
                    }
                }
            }
        }
    }
}


// @Preview
// @Composable
// fun SignInScreenP() {
//     CleanLayout(
//         currentRoute = null,
//         onNavigate = {}
//     ) {
//         SignInScreen(
//             signInViewModel = SignInViewModel(),
//             onLoginNavigate = {},
//         )
//     }
// }