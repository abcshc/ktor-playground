package com.example.lotto

import com.example.lotto.exception.InvalidSelectNumbersException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LottoGeneratorTest {
    @Test
    fun `generate lotto when using LottoGenerator with custom settings`() {
        val lottoGenerator = LottoGenerator(min = 4, max = 5, size = 2)
        val generated = lottoGenerator.generateLotto()
        assertEquals(2, generated.size)
        assertEquals(4, generated[0])
        assertEquals(5, generated[1])
    }

    @Test
    fun `generate lotto when using LottoGenerator with default settings`() {
        val lottoGenerator = LottoGenerator()
        val generated = lottoGenerator.generateLotto()
        assert(generated.size == lottoGenerator.size)
        assert(generated.minOf { it } >= lottoGenerator.min)
        assert(generated.maxOf { it } <= lottoGenerator.max)
    }

    @Test
    fun `generate lotto when using LottoGenerator with default settings and selected number`() {
        val lottoGenerator = LottoGenerator()
        val generated = lottoGenerator.generateLotto(listOf(8))
        assert(generated.size == lottoGenerator.size)
        assert(generated.contains(8))
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