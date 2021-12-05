# Elixir
Elixir is a library designed to make minecraft login easier.

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

## Json Form
We provide a json form to make data easier to read and write.
~~~kotlin
me.liuli.elixir.manage.AccountSerializer.toJson(me.liuli.elixir.account.MinecraftAccount) : com.beust.klaxon.JsonObject
me.liuli.elixir.manage.AccountSerializer.fromJson(com.beust.klaxon.JsonObject) : me.liuli.elixir.account.MinecraftAccount
~~~
