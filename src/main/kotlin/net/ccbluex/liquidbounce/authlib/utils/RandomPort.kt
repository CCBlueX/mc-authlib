package net.ccbluex.liquidbounce.authlib.utils

/**
 * Generate a random port that we can assign to our oauth server to listen on.
 * It should be application level high port, so we don't have to worry about.
 */
fun randomPort(): Int {
    return (49152..65535).random();
}


/**
 * Port used by the OAuth server for listening.
 */
val oauthPort = randomPort()