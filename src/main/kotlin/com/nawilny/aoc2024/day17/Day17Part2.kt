package com.nawilny.aoc2024.day17

import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val expectedResult = listOf(2, 4, 1, 2, 7, 5, 4, 7, 1, 3, 5, 5, 0, 3, 3, 0)

    val results = expectedResult.foldRight(setOf(0L)) { i, acc ->
        generatePossibleResults(i, acc)
    }
    val result = results.toList().minOf { it }
    exec(result).println()
    assert(exec(result) == expectedResult)

    result.println()
}

fun generatePossibleResults(expected: Int, candidates: Set<Long>): Set<Long> {
    val possibleResults = mutableSetOf<Long>()
    candidates.forEach { c ->
        (0..7).forEach {
            val n = (c * 8) + it
            if (check(n, expected)) {
                possibleResults.add(n)
            }
        }
    }
    return possibleResults
}

private fun check(a: Long, expectedResult: Int): Boolean {
    var b = (a % 8L).toInt()
    b = b xor 2
    val c = ((a shr b) % 8).toInt()
    b = b xor c
    b = b xor 3
    return expectedResult == b % 8
}

private fun exec(aInitialValue: Long): List<Int> {
    var a = aInitialValue
    val out = mutableListOf<Int>()
    while (a != 0L) {
        var b = (a % 8).toInt()
        b = b xor 2
        val c = ((a shr b) % 8).toInt()
        b = b xor c
        b = b xor 3
        out.add(b % 8)
        a /= 8
    }
    return out
}
