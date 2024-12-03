package com.nawilny.aoc2024.day03

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

private const val DO_OP = "do()"
private const val DONT_OP = "don't()"
fun main() {
    val memory = Input.readFileLinesNormalized("day03", "input.txt").joinToString()
    val operationsRegexStr = "mul(\\d{1,3},\\d{1,3})|$DO_OP|$DONT_OP"
        .replace("(", "\\(")
        .replace(")", "\\)")
    val operationsRegex = Regex(operationsRegexStr)
    val operations = operationsRegex.findAll(memory).map { it.value }.toList()

    operations
        .filter { it != DO_OP && it != DONT_OP }.sumOf { executeMul(it) }
        .println()

    var enabled = true
    var result = 0L
    operations.forEach {
        when(it) {
            DO_OP -> enabled = true
            DONT_OP -> enabled = false
            else -> {
                if (enabled) {
                    result += executeMul(it)
                }
            }
        }
    }
    result.println()
}

private fun executeMul(operation :String) : Long {
    val args = operation.substring(4..(operation.length - 2)).split(",").map { it.toLong() }
    return args[0] * args[1]
}