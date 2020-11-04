package com.example.lotto

import com.example.lotto.exception.InvalidSelectNumbersException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LottoGeneratorsKtTest {
    @Test
    fun `generate lotto when using LottoGenerator with custom settings`() {
        val lottoGenerator = LottoGenerator(min = 3, max = 5, size = 2)
        val checker: LinkedHashSet<Int> = LinkedHashSet()
        for (i in 0..300) {
            val generated = lottoGenerator.generateLotto()
            checker.addAll(generated)
            assertEquals(2, generated.size)
        }
        assertEquals(3, checker.size)
        assert(checker.contains(3))
        assert(checker.contains(4))
        assert(checker.contains(5))
    }

    @Test
    fun `generate lotto when using LottoGenerator with default settings`() {
        // default min = 1, max = 45, size = 6
        val lottoGenerator = LottoGenerator()
        val generated = lottoGenerator.generateLotto(listOf(8))
        assert(generated.size == 6)
        assert(generated.contains(8))
        for (number in generated) {
            assert(number >= 1)
            assert(number <= 45)
        }
    }

    @Test
    fun `throw InvalidSelectNumberException by input invalid numbers when calling generateLotto`() {
        val lottoGenerator = LottoGenerator(min = 1, max = 5, size = 3)

        //min
        assertFailsWith<InvalidSelectNumbersException> {
            lottoGenerator.generateLotto(listOf(0))
        }

        //max
        assertFailsWith<InvalidSelectNumbersException> {
            lottoGenerator.generateLotto(listOf(6))
        }

        //size
        assertFailsWith<InvalidSelectNumbersException> {
            lottoGenerator.generateLotto(listOf(1, 2, 3, 4))
        }
    }
}