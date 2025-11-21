# MC AuthLib
MC AuthLib is a library designed to make the integration of different account types in Minecraft easier. \
Based on [Elixir](https://github.com/UnlegitMC/Elixir) - a discontinued project by UnlegitMC.

## Used by
- [LiquidBounce](https://liquidbounce.net/) - A free Minecraft Hacked Client for Fabric 1.21.5

# Features
- Login to Minecraft using different account types
- Favorite Account
- Ban Tracker
- Replicated Yggdrasil API for usage in newer versions of Minecraft
- Pulls UUIDs from Mojang API for Cracked Accounts

## Supported account types
- Microsoft (Premium via Xbox Sign In)
- The Altening (Alt-service)
- Cracked (Username)
- Session (Access Token)

# Installation

Add the repository once and depend on the artifact shown below.

#### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://maven.ccbluex.net/releases")
}

dependencies {
    implementation("net.ccbluex:mc-authlib:1.5.0")
}
```

#### Gradle (Groovy DSL)

```groovy
repositories {
    maven {
        url "https://maven.ccbluex.net/releases"
    }
}

dependencies {
    implementation 'net.ccbluex:mc-authlib:1.5.0'
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>ccbluex-maven</id>
        <url>https://maven.ccbluex.net/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.ccbluex</groupId>
        <artifactId>mc-authlib</artifactId>
        <version>1.5.0</version>
    </dependency>
</dependencies>
```

# Usage

[LiquidBounce's `AccountManager`](https://github.com/CCBlueX/LiquidBounce/blob/nextgen/src/main/kotlin/net/ccbluex/liquidbounce/features/account/AccountManager.kt) uses MC AuthLib in practice.

## 1. Account creation

```kotlin
object AccountRegistry {
    private val accounts = mutableListOf<MinecraftAccount>()

    fun addMicrosoftAccount(openBrowser: (String) -> Unit, onComplete: (Result<MinecraftAccount>) -> Unit) {
        MicrosoftAccount.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {
            override fun openUrl(url: String) = openBrowser(url)

            override fun authResult(account: MicrosoftAccount) {
                accounts += account
                onComplete(Result.success(account))
            }

            override fun authError(error: String) {
                onComplete(Result.failure(IllegalStateException(error)))
            }
        })
    }

    fun addCrackedAccount(username: String, onlineLookup: Boolean = false): MinecraftAccount =
        CrackedAccount(username, onlineLookup).also { cracked ->
            cracked.refresh()
            accounts += cracked
        }

    fun addSessionAccount(token: String): MinecraftAccount =
        SessionAccount(token).also { session ->
            session.refresh()
            accounts += session
        }
}
```

## 2. Log in with Minecraft

```kotlin
fun login(account: MinecraftAccount): SessionBundle {
    val (compatSession, service) = account.login()

    // Adapt compat session to the actual Minecraft client session.
    val clientSession = net.minecraft.client.session.Session(
        compatSession.username,
        compatSession.uuid.toString(),
        compatSession.token,
        compatSession.type
    )

    MinecraftClient.getInstance().session = clientSession
}
```

## 3. Store and load accounts

```kotlin
fun saveAccounts(path: Path, accounts: List<MinecraftAccount>) {
    val jsonArray = JsonArray()
    accounts.forEach { jsonArray.add(it.toJson()) }
    Files.writeString(path, jsonArray.toString())
}

fun loadAccounts(path: Path): MutableList<MinecraftAccount> {
    if (!Files.exists(path)) return mutableListOf()

    val jsonArray = JsonParser.parseString(Files.readString(path)).asJsonArray
    return jsonArray.map { MinecraftAccount.fromJson(it.asJsonObject) }.toMutableList()
}
```
