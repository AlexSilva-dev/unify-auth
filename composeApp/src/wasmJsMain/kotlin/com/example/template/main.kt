package com.example.template

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.example.template.app.App
import com.example.template.app.initializations.KoinInit
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    try {

        KoinInit.execute()
        ComposeViewport(document.body!!) {
            App()
        }
    } catch (e: Exception) {
        println(e.toString())
        // TODO:
    }
}