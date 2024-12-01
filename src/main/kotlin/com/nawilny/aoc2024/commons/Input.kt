package com.nawilny.aoc2024.commons

object Input {

    fun readFileLines(day: String, fileName: String): List<String> {
        val path = "$day/$fileName"
        return this::class.java.classLoader.getResourceAsStream(path)?.bufferedReader()?.readLines()
            ?: throw NullPointerException("File $path not found")
    }

    fun readFileLinesNormalized(day: String, fileName: String): List<String> {
        return readFileLines(day, fileName).map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun <T> T.println() {
        println(this)
    }

}