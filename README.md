# Elixir
Elixir is a library designed to make minecraft login easier.

# Usage
We have a maven repo for this project.
~~~groovy
repositories {
    maven { url = "https://repo.getfdp.today/" }
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
**MOJANG REMOVED SUPPORT FOR MOJANG ACCOUNTS! We will remove the API in 1.3.0 or later**  
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

###Custom Client Keys
You can use your own client keys. You need to create a AuthMethod instance, and add it with a custom id to the registry in AuthMethod Companion object.
~~~kotlin
MicrosoftAccount.AuthMethod("c6cd7b0f-077d-4fcf-ab5c-9659576e38cb", "vI87Q~GkhVHJSLN5WKBbEKbK0TJc9YRDyOYc5", "http://localhost:1919/login", "XboxLive.signin%20offline_access", "d=<access_token>").also { 
    MicrosoftAccount.AuthMethod.Companion.registry["CUSTOM"] = it
}
~~~

## Json Form
We provide a json form to make data easier to read and write.
~~~kotlin
me.liuli.elixir.manage.AccountSerializer.toJson(me.liuli.elixir.account.MinecraftAccount) : com.google.gson.JsonObject
me.liuli.elixir.manage.AccountSerializer.fromJson(com.google.gson.JsonObject) : me.liuli.elixir.account.MinecraftAccount
~~~
