# MC AuthLib
MC AuthLib is a library designed to make the integration of different account types in Minecraft easier. \
Based on [Elixir](https://github.com/UnlegitMC/Elixir) - a discontinued project by UnlegitMC.

## Used by
- [LiquidBounce](https://liquidbounce.net/) - A free Minecraft Hacked Client for Fabric 1.20.4

# Features
- Login to Minecraft using different account types
- Favorite Account
- Ban Tracker
- Replicated Yggdrasil API for usage in newer versions of Minecraft
- Fully compatible with Elixir Account Format
- Pulls UUIDs from Mojang API for Cracked Accounts

## Supported account types
- Microsoft (Premium via Xbox Sign In)
- Altening (Premium provider)
- Cracked (Username)
- Session (Session Token)

# Installation
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation('com.github.CCBlueX:mc-authlib:1.4.0') {
        exclude group: 'com.google.code.gson', module: 'gson'
        exclude group: 'org.apache.logging.log4j', module: 'log4j-core'
        exclude group: 'org.apache.logging.log4j', module: 'log4j-api'
        exclude group: 'org.apache.logging.log4j', module: 'log4j-slf4j-impl'
        exclude group: 'org.slf4j', module: 'slf4j-api'
        exclude group: 'com.mojang', module: 'authlib'
    }
}
```

# Usage
```kotlin
fun loginUsingMicrosoft() {
    MicrosoftAccount.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {
        override fun openUrl(url: String) {
            println("Open url: $url")
        }

        override fun authResult(account: MicrosoftAccount) {
            println("Auth result: ${account.login()}")
            println(AccountSerializer.toJson(account).toJsonString(prettyPrint = true))
        }

        override fun authError(error: String) {
            println("Auth error: $error")
        }
    }, MicrosoftAccount.AuthMethod.AZURE_APP)
}
```

```kotlin
fun loginUsingTheAltening() {
    val token = "_________"
    
    // Warning: Blocking Request to TheAltening API
    val alteningAccount = AlteningAccount.fromToken(token)
    println(alteningAccount.login())
    println(AccountSerializer.toJson(alteningAccount).toJsonString())
}

```

```kotlin
fun loginUsingCracked() {
    val username = "_________"
    
    val crackedAccount = CrackedAccount(username)
    crackedAccount.refresh()
    println(crackedAccount.login())
    println(AccountSerializer.toJson(crackedAccount).toJsonString())
}

```

```kotlin
fun loginUsingSession() {
    val sessionToken = ""

    val sessionAccount = SessionAccount.fromToken(sessionToken)
    println(sessionAccount.login())
    println(AccountSerializer.toJson(sessionAccount).toJsonString())
}
```

