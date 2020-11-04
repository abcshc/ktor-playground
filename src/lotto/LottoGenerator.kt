package com.example.lotto

import com.example.lotto.exception.InvalidSelectNumbersException

class LottoGenerator(val min: Int = 1, val max: Int = 45, val size: Int = 6) {
    private val pool: Set<Int> = IntRange(min, max).toSet()

    fun generateLotto(selectedNumbers: List<Int> = listOf()): List<Int> {
        if (selectedNumbers.size > size ||
            selectedNumbers.maxOfOrNull { it } ?: max > max ||
            selectedNumbers.minOfOrNull { it } ?: min < min
        ) {
            throw InvalidSelectNumbersException()
        }
        val currentPool = pool.toMutableSet()
        val result = selectedNumbers.toMutableList()
        currentPool.removeAll(selectedNumbers)
        for (i in 1..size - selectedNumbers.size) {
            val next = currentPool.random()
            result.add(next)
            currentPool.remove(next)
        }
        return result.sorted()
    }
}