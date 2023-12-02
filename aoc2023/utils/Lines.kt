package net.panayotov.util

object Lines : Iterator<String> {
    private var line: String? = null
    override fun next(): String {
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
            line = readLine()
        }
    }
}

