package com.nawilny.aoc2024.day21

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day21", "input.txt")
    val keyboard1 = Keyboard1()
    val keyboard2 = Keyboard2()

    val chainLength = 25

    var r: Robot? = null
    (1..chainLength).forEach { _ ->
        r = Robot(keyboard2, r)
    }
    val lastRobot = Robot(keyboard1, r)

    input.sumOf { code ->
        lastRobot.reset()
        val cost = code.sumOf { lastRobot.findCostOf(it) }
        code.replace("A", "").toLong() * cost
    }.println()
}

private data class Position(val x: Int, val y: Int)

private class Robot(val keyboard: Keyboard, val following: Robot?) {

    var curent = 'A'

    val cache = mutableMapOf<Pair<Char, Char>, Long>()

    fun reset() {
        curent = 'A'
        following?.reset()
    }

    fun findCostOf(to: Char): Long {
        val cacheKey = curent to to
        cache.computeIfAbsent(cacheKey) {
            val path = keyboard.findPathsToMoveBetween(curent, to)
            if (following != null) {
                path.sumOf { following.findCostOf(it) }
            } else {
                path.length.toLong()
            }
        }
        curent = to
        return cache[cacheKey]!!
    }

}

private class Keyboard1 : Keyboard(keypad1keys) {

    override fun findPathsToMoveBetween(pStart: Position, pEnd: Position): String {
        val dy = pEnd.y - pStart.y
        val dx = pEnd.x - pStart.x
        if (pStart.y == 3 && pEnd.x == 0) {
            return when {
                dy < 0 -> "^".repeat(-dy) + findPathsToMoveBetween(Position(pStart.x, pEnd.y), pEnd)
                dy > 0 -> "v".repeat(dy) + findPathsToMoveBetween(Position(pStart.x, pEnd.y), pEnd)
                dx > 0 -> ">".repeat(dx) + findPathsToMoveBetween(Position(pEnd.x, pStart.y), pEnd)
                dx < 0 -> "<".repeat(-dx) + findPathsToMoveBetween(Position(pEnd.x, pStart.y), pEnd)
                else -> "A"
            }
        } else if (pStart.x == 0 && pEnd.y == 3) {
            return when {
                dx > 0 -> ">".repeat(dx) + findPathsToMoveBetween(Position(pEnd.x, pStart.y), pEnd)
                dy < 0 -> "^".repeat(-dy) + findPathsToMoveBetween(Position(pStart.x, pEnd.y), pEnd)
                dy > 0 -> "v".repeat(dy) + findPathsToMoveBetween(Position(pStart.x, pEnd.y), pEnd)
                dx < 0 -> "<".repeat(-dx) + findPathsToMoveBetween(Position(pEnd.x, pStart.y), pEnd)
                else -> "A"
            }
        } else {
            return when {
                dx < 0 -> "<".repeat(-dx) + findPathsToMoveBetween(Position(pEnd.x, pStart.y), pEnd)
                dy > 0 -> "v".repeat(dy) + findPathsToMoveBetween(Position(pStart.x, pEnd.y), pEnd)
                dx > 0 -> ">".repeat(dx) + findPathsToMoveBetween(Position(pEnd.x, pStart.y), pEnd)
                dy < 0 -> "^".repeat(-dy) + findPathsToMoveBetween(Position(pStart.x, pEnd.y), pEnd)
                else -> "A"
            }
        }
    }
}

private class Keyboard2 : Keyboard(keypad2keys) {

    override fun findPathsToMoveBetween(pStart: Position, pEnd: Position): String {
        val dx = pEnd.x - pStart.x
        return if (pStart.x == 0) {
            when {
                dx > 0 -> ">".repeat(dx) + findPathsToMoveBetween(Position(pEnd.x, pStart.y), pEnd)
                pEnd.y < pStart.y -> '^' + findPathsToMoveBetween(Position(pStart.x, pStart.y - 1), pEnd)
                pEnd.y > pStart.y -> 'v' + findPathsToMoveBetween(Position(pStart.x, pStart.y + 1), pEnd)
                dx < 0 -> "<".repeat(dx) + findPathsToMoveBetween(Position(pEnd.x, pStart.y), pEnd)
                else -> "A"
            }
        } else if (pStart.y == 0 && pEnd.x == 0) {
            when {
                pEnd.y > pStart.y -> 'v' + findPathsToMoveBetween(Position(pStart.x, pStart.y + 1), pEnd)
                pEnd.x < pStart.x -> '<' + findPathsToMoveBetween(Position(pStart.x - 1, pStart.y), pEnd)
                pEnd.x > pStart.x -> '>' + findPathsToMoveBetween(Position(pStart.x + 1, pStart.y), pEnd)
                pEnd.y < pStart.y -> '^' + findPathsToMoveBetween(Position(pStart.x, pStart.y - 1), pEnd)
                else -> "A"
            }
        } else {
            when {
                pEnd.x < pStart.x -> '<' + findPathsToMoveBetween(Position(pStart.x - 1, pStart.y), pEnd)
                pEnd.y > pStart.y -> 'v' + findPathsToMoveBetween(Position(pStart.x, pStart.y + 1), pEnd)
                pEnd.y < pStart.y -> '^' + findPathsToMoveBetween(Position(pStart.x, pStart.y - 1), pEnd)
                pEnd.x > pStart.x -> '>' + findPathsToMoveBetween(Position(pStart.x + 1, pStart.y), pEnd)
                else -> "A"
            }
        }
    }

}

private abstract class Keyboard(val keys: Map<Char, Position>) {

    val cache = mutableMapOf<Pair<Char, Char>, String>()

    fun findPathsToMoveBetween(from: Char, to: Char): String {
        val cacheKey = from to to
        cache.computeIfAbsent(cacheKey) {
            findPathsToMoveBetween(keys[from]!!, keys[to]!!)
        }
        return cache[cacheKey]!!
    }

    protected abstract fun findPathsToMoveBetween(pStart: Position, pEnd: Position): String
}

private val keypad1keys = mapOf(
    '0' to Position(1, 3),
    '1' to Position(0, 2),
    '2' to Position(1, 2),
    '3' to Position(2, 2),
    '4' to Position(0, 1),
    '5' to Position(1, 1),
    '6' to Position(2, 1),
    '7' to Position(0, 0),
    '8' to Position(1, 0),
    '9' to Position(2, 0),
    'A' to Position(2, 3),
)

private val keypad2keys = mapOf(
    '^' to Position(1, 0),
    'A' to Position(2, 0),
    '<' to Position(0, 1),
    'v' to Position(1, 1),
    '>' to Position(2, 1),
)
