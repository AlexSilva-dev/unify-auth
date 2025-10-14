package com.example.template.app.navigations

// Em um pacote como /common/navigation/
object ExternalUriHandler {
    // Armazena o link se ele chegar antes da UI estar pronta para ouvi-lo
    private var cached: String? = null

    var listener: ((uri: String) -> Unit)? = null
        set(value) {
            field = value
            // Se um listener for definido e houver um link na "memória",
            // entrega o link imediatamente.
            if (value != null) {
                cached?.let { uri ->
                    value.invoke(uri)
                    cached = null
                }
            }
        }

    // Método chamado pela plataforma nativa quando um novo link chega
    fun onNewUri(uri: String) {
        // Se a UI já estiver ouvindo, entrega na hora.
        // Se não, armazena para entregar depois.
        val currentListener = listener
        if (currentListener != null) {
            currentListener.invoke(uri)
        } else {
            cached = uri
        }
    }
}