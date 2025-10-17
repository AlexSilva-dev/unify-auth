package com.example.template.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import unify_auth.composeapp.generated.resources.Res
import unify_auth.composeapp.generated.resources.signin_warning_password_level
import unify_auth.composeapp.generated.resources.signin_warning_password_level_strong
import unify_auth.composeapp.generated.resources.signin_warning_password_level_weak

@Composable
fun AppPasswordTextField(
    value: String,
    modifier: Modifier = Modifier,
    semanticContentDescription: String = "",
    labelText: String = "",
    placeholderText: String = "",
    validateStrengthPassword: Boolean = false,
    hasError: Boolean = false,
    onHasStrongPassword: (isStrong: Boolean) -> Unit = {},
    onTextChanged: (text: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val showPassword = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        AppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .semantics { contentDescription = semanticContentDescription },
            value = value,
            onValueChange = onTextChanged,
            placeholderText = placeholderText,
            labelText = labelText,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            singleLine = true,
            isError = hasError,
            visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val (icon, tint) = if (showPassword.value) {
                    Pair(
                        Icons.Default.Visibility,
                        Color.White
                    )
                } else {
                    Pair(
                        Icons.Default.VisibilityOff,
                        Color.White
                    )
                }

                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Toggle password visibility",
                        tint = tint
                    )
                }
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (validateStrengthPassword && value.isNotEmpty()) {
            val strengthPasswordType = strengthChecker(value)
            if (strengthPasswordType == StrengthPasswordTypes.STRONG) {
                onHasStrongPassword(true)
            } else {
                onHasStrongPassword(false)
            }
            Text(
                modifier = Modifier.semantics { contentDescription = "StrengthPasswordMessage" },
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 10.sp,
                        )
                    ) {
                        append(stringResource(Res.string.signin_warning_password_level).trim())
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                            when (strengthPasswordType) {
                                StrengthPasswordTypes.STRONG ->
                                    append(
                                        "\n${stringResource(Res.string.signin_warning_password_level_strong).trim()}"
                                    )

                                StrengthPasswordTypes.WEAK ->
                                    append(
                                        "\n${stringResource(Res.string.signin_warning_password_level_weak).trim()}"
                                    )
                            }
                        }
                    }
                }
            )
        }
    }
}

fun strengthChecker(password: String): StrengthPasswordTypes {
    return if (password.length > 8) {
        StrengthPasswordTypes.STRONG
    } else {
        StrengthPasswordTypes.WEAK
    }
}


enum class StrengthPasswordTypes {
    STRONG,
    WEAK
}