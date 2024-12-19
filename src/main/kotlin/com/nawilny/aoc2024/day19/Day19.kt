package com.nawilny.aoc2024.day19

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day19", "input.txt")

    val towels = input.first().split(", ")
    val patterns = input.drop(1)

    patterns.forEach {
        "$it -> ${howManyOptions(it, towels)}".println()
    }
    patterns.count { howManyOptions(it, towels) > 0 }.println()
    patterns.sumOf { howManyOptions(it, towels) }.println()
}

val cache = mutableMapOf<String, Long>()

private fun howManyOptions(pattern: String, towels: List<String>): Long {
    if (pattern.isEmpty()) {
        return 1
    }
    if (cache.containsKey(pattern)) {
        return cache[pattern]!!
    }

    val result = towels.filter { pattern.startsWith(it) }.sumOf {
        howManyOptions(pattern.substring(it.length), towels)
    }
    cache[pattern] = result
    return result
}
