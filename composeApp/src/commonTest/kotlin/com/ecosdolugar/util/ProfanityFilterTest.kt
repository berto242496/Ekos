package com.ecosdolugar.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProfanityFilterTest {
    @Test
    fun detectsObfuscatedProfanity() {
        assertTrue(ProfanityFilter.containsProfanity("PUUUTA"))
        assertTrue(ProfanityFilter.containsProfanity("p u t a"))
        assertTrue(ProfanityFilter.containsProfanity("m1erd@"))
        assertTrue(ProfanityFilter.containsProfanity("Cabrón"))
    }

    @Test
    fun acceptsOrdinaryNames() {
        assertFalse(ProfanityFilter.containsProfanity("María do Carme"))
        assertFalse(ProfanityFilter.containsProfanity("Xoán"))
    }

    @Test
    fun displayNameErrorValidatesLengthAndContent() {
        assertNull(ProfanityFilter.displayNameError("  Ana  "))
        assertEquals("Escribe el nombre que quieres mostrar.", ProfanityFilter.displayNameError("   "))
        assertEquals(
            "El nombre no puede superar los 60 caracteres.",
            ProfanityFilter.displayNameError("a".repeat(61))
        )
        assertEquals("Ese nombre contiene palabras no permitidas.", ProfanityFilter.displayNameError("fuck"))
    }
}
