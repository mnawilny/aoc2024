package com.nawilny.aoc2024.day22

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day22", "input.txt").map { it.toInt() }

    val secrets = input.map { generateSecretNumbers(it) }

    secrets.sumOf { it.last().toLong() }.println()

    val prices = secrets.map { generatePrices(it) }
    val bananasBySequence = prices.map { generateBananasBySequence(it) }

    val result = generateSequences().map { s ->
        s to bananasBySequence.mapNotNull { it[s] }.sum()
    }.maxBy { it.second }
    result.println()

    assert(prices.mapNotNull { buyBananas(it, result.first) }.sum() == result.second)
}


private fun generateSequences(): List<Sequence> {
    val result = mutableListOf<Sequence>()
    (-9..9).forEach { d1 ->
        (-9..9).forEach { d2 ->
            (-9..9).forEach { d3 ->
                (-9..9).forEach { d4 ->
                    result.add(Sequence(d1, d2, d3, d4))
                }
            }
        }
    }
    return result
}

private data class Sequence(val d1: Int, val d2: Int, val d3: Int, val d4: Int)

private fun buyBananas(prices: List<Pair<Int, Int>>, sequence: Sequence): Int? {
    var p1 = prices[0].second
    var p2 = prices[1].second
    var p3 = prices[2].second
    prices.drop(3).forEach {
        if (p1 == sequence.d1 && p2 == sequence.d2 && p3 == sequence.d3 && it.second == sequence.d4) {
            return it.first
        }
        p1 = p2
        p2 = p3
        p3 = it.second
    }
    return null
}

private fun generateBananasBySequence(prices: List<Pair<Int, Int>>): Map<Sequence, Int> {
    var p1 = prices[0].second
    var p2 = prices[1].second
    var p3 = prices[2].second
    val result = mutableMapOf<Sequence, Int>()
    prices.drop(3).forEach {
        val s = Sequence(p1, p2, p3, it.second)
        result.putIfAbsent(s, it.first)
        p1 = p2
        p2 = p3
        p3 = it.second
    }
    return result
}

private fun generatePrices(secrets: List<Int>): List<Pair<Int, Int>> {
    var previousPrice = secrets.first() % 10
    val result = mutableListOf<Pair<Int, Int>>()
    secrets.drop(1).forEach {
        val price = it % 10
        val diff = price - previousPrice
        result.add(price to diff)
        previousPrice = price
    }
    return result
}

private fun generateSecretNumbers(initial: Int, n: Int = 2000): List<Int> {
    var number = initial.toLong()
    val result = mutableListOf(initial)
    (1..n).forEach { _ ->
        number = calculateNextSecret(number)
        result.add(number.toInt())
    }
    return result
}

private fun calculateNextSecret(secret: Long): Long {
    val s1 = mixAndPrune(secret, secret * 64L)
    val s2 = mixAndPrune(s1, s1 / 32)
    return mixAndPrune(s2, s2 * 2048)
}

private fun mixAndPrune(secret: Long, result: Long) = (secret xor result) % 16777216

