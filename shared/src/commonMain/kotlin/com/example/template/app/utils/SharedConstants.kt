package com.example.template.app.utils
import com.example.template.app.BuildKonfig

val SERVER_PORT: Int = BuildKonfig.SERVER_PORT.toInt()
val SERVER_HOST: String = BuildKonfig.SERVER_HOST.toString()
val SERVER_PROTOCOL: String = BuildKonfig.SERVER_PROTOCOL.toString()


/**
 * Para funcionar é necessario configurar o iosApp/iosApp/Info.plist
 * para cadastrar o DeepLink no sistema operacional, tanto no MacOs,
 * tanto no IOS.
 * [Documentação Geral sobre DeepLink](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-deep-links.html#register-deep-links-schemas-in-the-operating-system)
 * [Documentação da Apple](https://developer.apple.com/documentation/xcode/allowing-apps-and-websites-to-link-to-your-content/)
 *
 */