import net.panayotov.util.Lines

// Represents the overlap of a rule and a range. preCount is how many resources at the start don't
// apply and postCount for the end. If there is total overlap (i.e. the rule covers the whole range)
// then both pre and post are 0. If there is no overlap then preCount covers the whole range and
// postCount is 0.
data class Overlap(val range: ResourceRange, val preCount: Long, val postCount: Long) {
    fun disjoint() = preCount == range.end - range.start + 1

    fun preRange(): ResourceRange? = if (preCount > 0) {
        ResourceRange(range.start, range.start + preCount - 1)
    } else {
        null
    }

    fun postRange(): ResourceRange? = if (postCount > 0) {
        ResourceRange(range.end - postCount + 1, range.end)
    } else {
        null
    }

    fun rangeToConvert(): ResourceRange? = if (!disjoint()) {
        ResourceRange(range.start + preCount, range.end - postCount)
    } else {
        null
    }
}

data class ResourceRange(val start: Long, val end: Long) {
    fun inRange(point: Long): Boolean = point >= start && point <= end
}

data class MappingRule(val sourceStart: Long, val destStart: Long, val length: Long) {
    fun convertRange(range: ResourceRange): ResourceRange? {
        val newStart = convert(range.start)
        val newEnd = convert(range.end)

        return if (newStart != null && newEnd != null) {
            ResourceRange(newStart, newEnd)
        } else {
            null
        }
    }

    fun getOverlap(range: ResourceRange): Overlap {
        if (matches(range.start) && matches(range.end)) {
            // The incoming range is entirely within this mapping. Return the whole thing.
            // Rule    |------------|
            // Range      |-----|
            return Overlap(range, 0, 0)
        } else {
            val matchesStart = matches(range.start)
            val matchesEnd = matches(range.end)

            if (!matchesStart && !matchesEnd) {
                if (range.inRange(sourceStart)) {
                    // Rule    |------------|
                    // Range |-----------------|
                    return Overlap(range, sourceStart - range.start, range.end - sourceEnd)
                } else {
                    // Rule    |------------|
                    // Range                  |-------------|
                    return Overlap(range, range.end - range.start + 1, 0)
                }
            } else if (matchesStart) {
                // Rule    |------------|
                // Range      |-----------------|
                return Overlap(range, 0, range.end - sourceEnd)
            } else {
                // Rule    |------------|
                // Range |-----------|
                return Overlap(range, sourceStart - range.start, 0)
            }
        }
    }


    private val sourceEnd
        get(): Long = sourceStart + length - 1
    private fun matches(resource: Long): Boolean = resource >= sourceStart && resource < sourceStart + length
    private fun diff() = destStart - sourceStart

    private fun convert(incoming: Long): Long? = if (matches(incoming)) {
        incoming + diff()
    } else {
        null
    }

}

data class Mapping(val source: String, val dest: String, val rules: List<MappingRule>) {
    fun convertRange(incoming: ResourceRange): List<ResourceRange> {
        // Ranges that still need to be converted.
        val remainingRanges = mutableListOf<ResourceRange>(incoming)

        // Ranges that have been converted.
        val convertedRanges = mutableListOf<ResourceRange>()

        while (remainingRanges.size > 0) {
            val current = remainingRanges.removeFirst()
            var disjoint = true

            for (rule in rules) {
                val overlap = rule.getOverlap(current)
                if (!overlap.disjoint()) {
                    disjoint = false
                    overlap.preRange()?.let { remainingRanges.add(it) }
                    overlap.postRange()?.let { remainingRanges.add(it) }
                    overlap.rangeToConvert()?.let(rule::convertRange)?.apply(convertedRanges::add)

                    break
                }
            }

            if (disjoint) {
                convertedRanges.add(ResourceRange(current.start, current.end))
            }
        }

        return convertedRanges.toList()
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
    val ranges = mutableListOf<MappingRule>()

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
            val mappingRule = line.split(" ")
            ranges.add(MappingRule(mappingRule[1].toLong(), mappingRule[0].toLong(), mappingRule[2].toLong()))
        }
    }

    if (mappingDef != null) {
        mappings.add(Mapping(mappingDef.first, mappingDef.second, ranges.toList()))
    }

    return mappings.toList()
}

fun seedRanges(seeds: List<Long>): List<ResourceRange> {
    var i = 0
    val outSeeds = mutableListOf<ResourceRange>()

    while (i < seeds.size) {
        outSeeds.add(ResourceRange(seeds[i], seeds[i] + seeds[i + 1] - 1))
        i += 2
    }

    return outSeeds.toList()
}

fun main() {
    val seedLine = Lines.next()
    var seeds = seedRanges(seedLine.split(":")[1].trim().split(" ").map { it.toLong() })
    val newSeeds = mutableListOf<ResourceRange>()

    val mappings = readMappings()

    for (mapping in mappings) {
        for (range in seeds) {
            newSeeds.addAll(mapping.convertRange(range))
        }
        seeds = newSeeds.toList()
        newSeeds.clear()
    }

    val firsts = seeds.map { it.start }
    println("Answer = ${firsts.min()}")
}
