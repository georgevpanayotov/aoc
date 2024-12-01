sealed class PacketData : Comparable<PacketData>

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
    constructor(item: PacketData) : this(arrayListOf(item)) {
    }

    constructor(x: Int) : this(arrayListOf(CorePacketData(x))) {
    }

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
    val divider2 = ListPacketData(ListPacketData(2))
    val divider6 = ListPacketData(ListPacketData(6))

    val packets = arrayListOf<PacketData>(divider2, divider6)

    while (line != null) {
        val packet = parsePacket(line, Cursor(0))
        if (packet != null) {
            packets.add(packet)
        }

        line = readLine()
    }
    packets.sort()

    // NOTE: To go faster, I'm just printing these then I find the dividers by inspection, multiply
    // manually, and get the result. Due to this form of competition this is possible.
    for (i in 0..packets.size - 1) {
        print("${i + 1}: ${packets[i]}\n")
    }
}
