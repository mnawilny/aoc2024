package com.nawilny.aoc2024.day02

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println
import kotlin.math.abs

fun main() {
    val input = Input.readFileLinesNormalized("day02", "input.txt")
        .map { line -> line.split(" ").map { it.toInt() } }
    input.count { isSafe(it) }.println()
    input.count { isSafeIgnoringElement(it) }.println()
}

private fun isSafe(report: List<Int>): Boolean {
    val increasing = report[0] < report[1]
    var prev = report[0]
    for (r in report.drop(1)) {
        if ((increasing && r < prev) || (!increasing && r > prev)) {
            return false
        }
        val d = abs(prev - r)
        if (d < 1 || d > 3) {
            return false
        }
        prev = r
    }
    return true
}

private fun isSafeIgnoringElement(report: List<Int>): Boolean {
    if (isSafe(report)) {
        return true
    }
    for (i in report.indices) {
        val subReport = report.subList(0, i) + report.subList(i + 1, report.size)
        if (isSafe(subReport)) {
            return true
        }
    }
    return false
}

