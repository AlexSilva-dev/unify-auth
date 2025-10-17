package com.example.template.app.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.EvaIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Archive
import compose.icons.evaicons.outline.Plus
import compose.icons.evaicons.outline.Repeat
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import unify_auth.composeapp.generated.resources.*

@Suppress("TRANSIENT_IS_REDUNDANT")
@Serializable
sealed class Screen {

    @Transient
    val title: String
        @Composable
        get() = stringResource(this.titleRes).trim()

    @Transient
    abstract val titleRes: StringResource

    @Transient
    abstract val icon: ImageVector
    abstract val route: String


    data object FlashcardGeneration : Screen() {
        override val titleRes = Res.string.commonGenerationFlashcardsScreenLabel
        override val icon: ImageVector
            get() = EvaIcons.Outline.Plus
        override val route = "/flashcardGeneration"
    }


    data object Topics : Screen() {
        override val titleRes = Res.string.commonTopicsScreenLabel
        override val icon: ImageVector
            get() = EvaIcons.Outline.Archive
        override val route = "/topics"
    }

    @Serializable
    data class FlashcardReview(val id: String) : Screen() {
        @Transient
        override val titleRes = Res.string.commonReviewFlashcardsScreenLabel
        override val icon: ImageVector
            get() = EvaIcons.Outline.Repeat
        override val route = "/flashcard"
    }

    @Serializable
    data object SignIn : Screen() {
        override val titleRes = Res.string.signIn_title
        override val icon: ImageVector
            get() = EvaIcons.Outline.Repeat
        override val route = "/signIn"
    }

    @Serializable
    data object AuthLogin : Screen() {
        override val titleRes = Res.string.title_login
        override val icon: ImageVector
            get() = EvaIcons.Outline.Repeat
        override val route = "/auth/login"
    }

}