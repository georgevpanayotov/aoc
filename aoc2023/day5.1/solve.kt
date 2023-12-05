import net.panayotov.util.Lines

data class MappingRange(val sourceStart: Long, val destStart: Long, val length: Long) {
    fun convert(incoming: Long): Long? = if (incoming >= sourceStart && incoming < sourceStart + length) {
        incoming + diff()
    } else {
        null
    }

    private fun diff() = destStart - sourceStart
}

data class Mapping(val source: String, val dest: String, val ranges: List<MappingRange>) {
    fun convert(incoming: Long): Long {
        for (range in ranges) {
            val outgoing = range.convert(incoming)
            if (outgoing != null) {
                return outgoing
            }
        }

        return incoming
    }
}

fun getMapping(line: String): Pair<String, String> {
    var i = 0
    var source = StringBuilder()
    var dest = StringBuilder()

    while (i < line.length && line[i] != '-') {
        source.append(line[i])
        i++
    }

    i += 4

    while (i < line.length && line[i] != ' ') {
        dest.append(line[i])
        i++
    }

    return Pair(source.toString(), dest.toString())
}

fun readMappings(): List<Mapping> {
    var mappingDef: Pair<String, String>? = null
    val ranges = mutableListOf<MappingRange>()

    val mappings = mutableListOf<Mapping>()

    for (line in Lines) {
        if (line.length == 0) {
            if (mappingDef != null) {
                mappings.add(Mapping(mappingDef.first, mappingDef.second, ranges.toList()))
            }

            mappingDef = null
            ranges.clear()
            continue
        }

        if (mappingDef == null) {
            mappingDef = getMapping(line)
        } else {
            val mappingRange = line.split(" ")
            ranges.add(MappingRange(mappingRange[1].toLong(), mappingRange[0].toLong(), mappingRange[2].toLong()))
        }
    }

    if (mappingDef != null) {
        mappings.add(Mapping(mappingDef.first, mappingDef.second, ranges.toList()))
    }

    return mappings.toList()
}

fun main() {
    val seedLine = Lines.next()
    var seeds = seedLine.split(":")[1].trim().split(" ").map { it.toLong() }
    val mappings = readMappings()

    for (mapping in mappings) {
        seeds = seeds.map { mapping.convert(it) }
    }

    println("Answer = ${seeds.min()}")
}
