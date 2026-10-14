package com.ichigoadam.jarvis.memory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EnglishDictionaryTest {
    @Test
    fun returnsArabicMeaning() {
        assertEquals("gravity معناها الجاذبية.", EnglishDictionary.lookup("gravity", arabicMode = true))
    }

    @Test
    fun returnsEnglishDefinition() {
        val result = EnglishDictionary.lookup("chemistry", arabicMode = false)
        assertNotNull(result)
        assertTrue(result!!.contains("the study of substances"))
    }

    @Test
    fun lookupIgnoresCapitalizationAndOuterPunctuation() {
        assertEquals(
            EnglishDictionary.lookup("gravity", arabicMode = true),
            EnglishDictionary.lookup("  GRAVITY!  ", arabicMode = true)
        )
    }

    @Test
    fun unknownAndMultipleWordsReturnNull() {
        assertNull(EnglishDictionary.lookup("notarealwordxyz"))
        assertNull(EnglishDictionary.lookup("two words"))
    }

    @Test
    fun dictionaryHasMoreThanOneHundredEntries() {
        assertTrue(EnglishDictionary.supportedWords() >= 100)
    }
}
