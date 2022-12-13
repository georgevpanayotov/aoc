sealed class PacketData: Comparable<PacketData>

fun compareLists(lhs: ListPacketData, rhs: ListPacketData): Int {
    var i = 0
    while (i < lhs.items.size && i < rhs.items.size) {
        val comp = lhs.items[i].compareTo(rhs.items[i])
        if (comp != 0) {
            return comp
        }

        i++
    }
    return lhs.items.size.compareTo(rhs.items.size)
}

class ListPacketData(val items: List<PacketData>) : PacketData() {
    override fun toString(): String {
        var str = "["
        for (i in 0..items.size - 2) {
            str += items[i].toString()
            str += ","
        }
        if (items.size > 0) {
            str += items[items.size - 1]
        }
        str += "]"
        return str
    }

    override fun compareTo(other: PacketData): Int =
        when (other) {
            is ListPacketData -> compareLists(this, other)
            is CorePacketData -> compareLists(this, ListPacketData(arrayListOf(other)))
        }
}

class CorePacketData(val item: Int) : PacketData() {
    override fun toString(): String = "$item"

    override fun compareTo(other: PacketData): Int =
        when (other) {
            is ListPacketData -> compareLists(ListPacketData(arrayListOf(this)), other)
            is CorePacketData -> this.item.compareTo(other.item)
        }
}

class Cursor(var pos: Int)

fun parsePacket(line: String, cursor: Cursor): PacketData? {
    if (cursor.pos >= line.length) {
        return null
    }

    if (line[cursor.pos] == '[') {
        cursor.pos++
        val items = arrayListOf<PacketData>()
        var done = false

        while (!done) {
            if (line[cursor.pos] == ']') {
                cursor.pos++
                done = true
            } else {
                val item = parsePacket(line, cursor)
                if (item == null) {
                    error("")
                }

                items.add(item)
                if (line[cursor.pos] == ',') {
                    cursor.pos++
                } else if (line[cursor.pos] == ']') {
                    cursor.pos++
                    done = true
                }
            }
        }

        return ListPacketData(items)
    } else if (line[cursor.pos].isDigit()) {
        var digits = ""
        while (line[cursor.pos].isDigit()) {
            digits += "${line[cursor.pos]}"
            cursor.pos++
        }

        return CorePacketData(digits.toInt())
    } else {
        print("BAD: ${line[cursor.pos]}\n")
        return null
    }
}

fun main() {
    var line = readLine()
    var score = 0
    var pair = 1
    while (line != null) {
        val leftPacket = parsePacket(line, Cursor(0))
        line = readLine()

        val rightPacket = parsePacket(line!!, Cursor(0))
        readLine()

        if (leftPacket!!.compareTo(rightPacket!!) < 0) {
            score += pair
        }

        line = readLine()
        pair++
    }
    print("$score\n")
}
