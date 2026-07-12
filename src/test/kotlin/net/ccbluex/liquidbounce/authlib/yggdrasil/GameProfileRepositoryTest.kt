package net.ccbluex.liquidbounce.authlib.yggdrasil

import kotlin.test.*

class GameProfileRepositoryTest {

    private val repo = GameProfileRepository.Default

    @Test
    fun testFetchUuidByUsernameNotch() {
        val uuid = repo.fetchUuidByUsername("Notch")
        assertNotNull(uuid, "Notch's UUID should not be null")
        assertEquals(36, uuid.toString().length) // Standard UUID string length with hyphens
    }

    @Test
    fun testFetchUuidByUsernameJeb() {
        val uuid = repo.fetchUuidByUsername("jeb_")
        assertNotNull(uuid, "jeb_'s UUID should not be null")
    }

    @Test
    fun testFetchUuidByUsernameNonexistent() {
        val uuid = repo.fetchUuidByUsername("ThisUserDoesNotExistAtAll12345")
        assertNull(uuid, "Nonexistent user UUID should be null")
    }

    @Test
    fun testFetchUuidByUsernameEmpty() {
        val uuid = repo.fetchUuidByUsername("")
        assertNull(uuid, "Empty username should return null")
    }

    @Test
    fun testDefaultBaseUrl() {
        assertEquals("https://api.minecraftservices.com", GameProfileRepository.DEFAULT_BASE_URL)
    }

    @Test
    fun testDefaultSingleton() {
        assertSame(GameProfileRepository.Default, GameProfileRepository.Default)
    }
}
