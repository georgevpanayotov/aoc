package net.panayotov.util

import java.util.Scanner

object Scanners : Iterator<Scanner> {
    private var line: Scanner? = null
    override fun next(): Scanner {
        ensureLine()
        val ret = line ?: error("No next line")
        line = null

        return ret
    }

    override fun hasNext(): Boolean {
        ensureLine()
        return line != null
    }

    private fun ensureLine() {
        if (line == null) {
            line = readLine()?.let(::Scanner)
        }
    }
}
