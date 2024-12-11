package com.nawilny.aoc2024.day11

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

private val cache = mutableMapOf<Pair<Long, Int>, Long>()

fun main() {
    val input = Input.readFileLinesNormalized("day11", "input.txt").first()
        .split(" ").map { it.toLong() }

    calculateForSteps(input, 25).println()
    calculateForSteps(input, 75).println()
}

private fun calculateForSteps(input: List<Long>, steps: Int): Long {
    return input.sumOf { calculateStoneWithCache(it, steps) }
}

private fun calculateStoneWithCache(value: Long, steps: Int): Long {
    val key = Pair(value, steps)
    return if (cache.containsKey(key)) {
        cache[key]!!
    } else {
        val result = calculateStone(value, steps)
        cache[key] = result
        result
    }
}

private fun calculateStone(value: Long, steps: Int): Long {
    if (steps == 0) {
        return 1
    }
    if (value == 0L) {
        return calculateStoneWithCache(1L, steps - 1)
    }
    val str = value.toString()
    if (str.length % 2 == 0) {
        val v1 = str.substring(0..<str.length / 2).toLong()
        val v2 = str.substring(str.length / 2..<str.length).toLong()
        return calculateStoneWithCache(v1, steps - 1).plus(
            calculateStoneWithCache(v2, steps - 1)
        )
    }
    return calculateStoneWithCache(value * 2024, steps - 1)
}
