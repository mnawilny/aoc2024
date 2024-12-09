package com.nawilny.aoc2024.day09

import com.nawilny.aoc2024.commons.Input
import com.nawilny.aoc2024.commons.Input.println

fun main() {
    val input = Input.readFileLinesNormalized("day09", "input.txt").first()

    val memory = parseMemory(input)
    defragmentMemory(memory)
    calculateChecksum(memory).println()

    val memoryItems = parseMemoryItems(input)
    defragmentMemoryItems(memoryItems)
    val memory2 = convertToRegularMemory(memoryItems)
    calculateChecksum(memory2).println()
}

private fun parseMemory(input: String): MutableList<Int?> {
    val memory = mutableListOf<Int?>()
    var isFile = true
    var fileId = 0
    input.map { it.digitToInt() }.forEach { v ->
        val valueToPut = if (isFile) {
            fileId++
        } else {
            null
        }
        for (i in 1..v) {
            memory.add(valueToPut)
        }
        isFile = !isFile
    }
    return memory
}

private fun defragmentMemory(memory: MutableList<Int?>) {
    var memoryIter = 0
    var memoryBackwardIter = memory.size - 1

    while (memoryIter < memoryBackwardIter) {
        while (memory[memoryIter] != null) {
            memoryIter++
        }
        while (memory[memoryBackwardIter] == null) {
            memoryBackwardIter--
        }
        if (memoryIter < memoryBackwardIter) {
            memory[memoryIter++] = memory[memoryBackwardIter]
            memory[memoryBackwardIter] = null
        }
    }
}

private fun parseMemoryItems(input: String): MutableList<MemoryItem> {
    val memory = mutableListOf<MemoryItem>()
    var isFile = true
    var fileId = 0
    input.map { it.digitToInt() }.forEach { v ->
        if (isFile) {
            memory.add(MemoryFile(v, fileId++))
        } else {
            memory.add(MemoryEmpty(v))
        }
        isFile = !isFile
    }
    return memory
}

private fun defragmentMemoryItems(memory: MutableList<MemoryItem>) {
    var memoryBackwardIter = memory.size - 1
    while (memoryBackwardIter > 0) {
        if (memory[memoryBackwardIter] is MemoryEmpty) {
            memoryBackwardIter--
            continue
        }
        val file = memory[memoryBackwardIter] as MemoryFile
        val spaceIndex = findEmptySpace(memory, file.size)
        if (spaceIndex == null || spaceIndex >= memoryBackwardIter) {
            memoryBackwardIter--
            continue
        }
        val space = memory[spaceIndex]
        memory[spaceIndex] = file
        memory[memoryBackwardIter] = MemoryEmpty(file.size)
        if (space.size > file.size) {
            if (memory[spaceIndex + 1] is MemoryEmpty) {
                memory[spaceIndex + 1] = MemoryEmpty(space.size - file.size + memory[spaceIndex + 1].size)
            } else {
                memory.add(spaceIndex + 1, MemoryEmpty(space.size - file.size))
            }
        }
        memoryBackwardIter--
    }
}

private fun findEmptySpace(memory: MutableList<MemoryItem>, size: Int): Int? {
    return memory.withIndex().firstOrNull { it.value is MemoryEmpty && it.value.size >= size }?.index
}

private fun calculateChecksum(memory: List<Int?>): Long {
    return memory.withIndex().filter { it.value != null }.sumOf { it.value!!.toLong() * it.index }
}

private fun convertToRegularMemory(memoryItems: MutableList<MemoryItem>): List<Int?> {
    return memoryItems.flatMap { item ->
        List(item.size) { _ ->
            when (item) {
                is MemoryEmpty -> null
                is MemoryFile -> item.id
            }
        }
    }
}

private sealed class MemoryItem(val size: Int)

private class MemoryFile(size: Int, val id: Int) : MemoryItem(size)
private class MemoryEmpty(size: Int) : MemoryItem(size)
