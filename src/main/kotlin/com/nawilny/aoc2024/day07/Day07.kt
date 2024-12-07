package com.nawilny.aoc2024.day07

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day07", "input.txt")
        .map { it.split(": ") }
        .map { line -> line[0].toLong() to line[1].split(" ").map { it.toLong() } }

    input.filter { canBeMadeMethod1(it.first, 0L, it.second, listOf(Operators.ADD, Operators.MULTIPLY)) }
        .sumOf { it.first }.println()
    input.filter { canBeMadeMethod1(it.first, 0L, it.second, Operators.values().toList()) }
        .sumOf { it.first }.println()

    input.filter { canBeMadeMethod2(it.first, it.second, listOf(Operators.ADD, Operators.MULTIPLY)) }
        .sumOf { it.first }.println()
    input.filter { canBeMadeMethod2(it.first, it.second, Operators.values().toList()) }
        .sumOf { it.first }.println()
}

// Going left to right - simpler but slower
private fun canBeMadeMethod1(
    expectedValue: Long,
    valueSoFar: Long,
    parts: List<Long>,
    operators: List<Operators>
): Boolean {
    if (parts.isEmpty()) {
        return expectedValue == valueSoFar
    }
    val firstPart = parts.first()
    val remainingParts = parts.drop(1)
    return operators
        .map { it.execute(valueSoFar, firstPart) }
        .any { canBeMadeMethod1(expectedValue, it, remainingParts, operators) }
}

// Going right to left - faster but more complicated
private fun canBeMadeMethod2(expectedValue: Long, parts: List<Long>, operators: List<Operators>): Boolean {
    val lastPart = parts.last()
    if (parts.size == 1) {
        return expectedValue == lastPart
    }
    val remainingParts = parts.dropLast(1)
    return operators
        .filter { it.canBeAppliedWhenReversing(expectedValue, lastPart) }
        .map { it.executeReversed(expectedValue, lastPart) }
        .any { canBeMadeMethod2(it, remainingParts, operators) }
}

private enum class Operators {
    ADD {
        override fun execute(v1: Long, v2: Long) = v1 + v2
        override fun executeReversed(v1: Long, v2: Long) = v1 - v2
        override fun canBeAppliedWhenReversing(v1: Long, v2: Long) = true
    },
    MULTIPLY {
        override fun execute(v1: Long, v2: Long) = v1 * v2
        override fun executeReversed(v1: Long, v2: Long) = v1 / v2
        override fun canBeAppliedWhenReversing(v1: Long, v2: Long) = v1 % v2 == 0L
    },
    CONCATENATE {
        override fun execute(v1: Long, v2: Long) = (v1.toString() + v2.toString()).toLong()
        override fun executeReversed(v1: Long, v2: Long): Long {
            val remainingValue = v1.toString().dropLast(v2.toString().length)
            return if (remainingValue.isEmpty() || remainingValue == "-") 0L else remainingValue.toLong()
        }

        override fun canBeAppliedWhenReversing(v1: Long, v2: Long) = v1.toString().endsWith(v2.toString())
    };

    abstract fun execute(v1: Long, v2: Long): Long
    abstract fun executeReversed(v1: Long, v2: Long): Long
    abstract fun canBeAppliedWhenReversing(v1: Long, v2: Long): Boolean
}
