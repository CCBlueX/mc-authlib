package net.ccbluex.liquidbounce.authlib.utils

import kotlin.test.*
import java.util.UUID

class UuidExtensionsTest {

    @Test
    fun testParseStandardUuid() {
        val input = "123e4567-e89b-12d3-a456-426655440000"
        val uuid = parseUuid(input)
        assertEquals(UUID.fromString(input), uuid, "Should parse standard UUID string correctly")
    }

    @Test
    fun testParseUnformattedUuid() {
        val input = "123e4567e89b12d3a456426655440000"
        val expected = UUID.fromString("123e4567-e89b-12d3-a456-426655440000")
        val uuid = parseUuid(input)
        assertEquals(expected, uuid, "Should parse compact UUID string correctly")
    }

    @Test
    fun testParseInvalidString() {
        val input = "invalid-uuid-string"
        val exception = assertFailsWith<IllegalArgumentException> {
            parseUuid(input)
        }
        assertTrue(exception.message!!.contains("Invalid UUID string") || exception.message!!.contains("UUID string must"), "Should fail for invalid input")
    }

    @Test
    fun testLengthMismatchForUnformatted() {
        val input = "123e4567e89b12d3a45642665544" // only 30 chars
        val exception = assertFailsWith<IllegalArgumentException> {
            parseUuid(input)
        }
        assertTrue(exception.message!!.contains("32"), "Should fail if compact UUID has incorrect length")
    }
}
