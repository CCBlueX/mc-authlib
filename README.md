# Elixir
Elixir is a library designed to make minecraft login easier.

# Usage
We have a maven repo for this project.
~~~groovy
repositories {
    maven { url = "https://getfdp.today/repo/" }
}

dependencies {
    implementation "me.liuli:Elixir:1.1.0"
}
~~~

# API
All APIs are in the test module.

## Login
AccountSerializer provide an easy way to login.
~~~kotlin
me.liuli.elixir.manage.AccountSerializer.accountInstance(
    name = "username",
    password = "password"
)
~~~

### Cracked Account
Username: `username`, Password: Empty String

### Mojang Account
Username: `example@getfdp.today`, Password: `password`

### Microsoft Account
Username: `ms@original@mail.com`, Password: `password`
Username: `ms@M.R3_BAY.token`, Password: Empty String

## Microsoft OAuth
We provide a way to login microsoft account with 2fa.
~~~kotlin
val microsoftAccount = MicrosoftAccount.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {
    override fun openUrl(url: String) {
        println("Open url: $url")
    }

    override fun authResult(account: MicrosoftAccount) {
        println("Auth result: ${account.session}")
    }

    override fun authError(error: String) {
        println("Auth error: $error")
    }
})
~~~

## Json Form
We provide a json form to make data easier to read and write.
~~~kotlin
me.liuli.elixir.manage.AccountSerializer.toJson(me.liuli.elixir.account.MinecraftAccount) : com.beust.klaxon.JsonObject
me.liuli.elixir.manage.AccountSerializer.fromJson(com.beust.klaxon.JsonObject) : me.liuli.elixir.account.MinecraftAccount
~~~
