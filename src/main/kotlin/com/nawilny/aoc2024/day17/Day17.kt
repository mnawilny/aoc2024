package com.nawilny.aoc2024.day17

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println
import kotlin.math.pow

fun main() {
    val input = Input.readFileLinesNormalized("day17", "input.txt")
    val a = input[0].substring(12).toLong()
    val b = input[1].substring(12).toLong()
    val c = input[2].substring(12).toLong()
    val instructions = input[3].substring(9).split(",").map { it.toInt() }
    val computer = Computer(a, b, c, instructions)
    computer.run()
    computer.getOut().println()
}

private class Computer(var a: Long, var b: Long, var c: Long, val instructions: List<Int>) {
    var instructionPointer = 0
    val out = mutableListOf<Int>()

    fun run() {
        while (instructionPointer in instructions.indices) {
            val opcode = instructions[instructionPointer++]
            val operand = instructions[instructionPointer++]
            when (opcode) {
                0 -> a = div(operand)
                1 -> b = b xor operand.toLong()
                2 -> b = toCombo(operand) % 8
                3 -> if (a != 0L) instructionPointer = operand
                4 -> b = b xor c
                5 -> out.add(toCombo(operand).toInt() % 8)
                6 -> b = div(operand)
                7 -> c = div(operand)
                else -> error("Invalid opcode $opcode")
            }
        }
    }

    fun div(operand: Int): Long {
        val denominator = 2.0.pow(toCombo(operand).toInt()).toLong()
        return (a / denominator)
    }

    fun toCombo(operand: Int): Long {
        return when {
            operand <= 3 -> operand.toLong()
            operand == 4 -> a
            operand == 5 -> b
            operand == 6 -> c
            else -> error("Invalid operand $operand")
        }
    }

    fun getOut() = out.joinToString(",")
}
