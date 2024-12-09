sealed interface DiskBlock

data class FileBlock(val id: Int) : DiskBlock {
    override fun toString(): String = "[$id]"
}

data object FreeBlock : DiskBlock {
    override fun toString(): String = "."
}

data class File(val id: Int, val firstBlock: Int, val size: Int)

data class DiskMap(val layout: List<DiskBlock>, val files: List<File>)

fun parseDiskMap(line: String): DiskMap {
    val diskLayout = mutableListOf<DiskBlock>()
    val files = mutableListOf<File>()

    var file = true
    var id = 0

    for (i in 0..<line.length) {
        val count = line[i].code - '0'.code
        val start = diskLayout.size

        for (i in 1..count) {
            if (file) {
                diskLayout.add(FileBlock(id))
            } else {
                diskLayout.add(FreeBlock)
            }
        }

        if (file) {
            files.add(File(id, start, count))
            id++
        }

        file = !file
    }

    return DiskMap(diskLayout.toList(), files.toList())
}

fun findFreeSpace(diskLayout: List<DiskBlock>, file: File): Pair<Int, Int>? {
    var i = 0
    // Find a freespace earlier in the disk than the file's current location.
    while (i < diskLayout.size && i < file.firstBlock) {
        if (diskLayout[i] is FreeBlock) {
            val start = i
            var count = 0
            while (i < diskLayout.size && diskLayout[i] is FreeBlock) {
                count++
                i++
            }
            if (count >= file.size) {
                return Pair(start, count)
            }
        }

        i++
    }

    return null
}

fun defrag(diskMap: DiskMap): DiskMap {
    val mutableDisk = diskMap.layout.toMutableList()

    var fileToMove = diskMap.files.size - 1

    while (fileToMove >= 0) {
        val file = diskMap.files[fileToMove]

        val maybeWrite = findFreeSpace(mutableDisk, file)
        if (maybeWrite != null) {
            val (writeStart, count) = maybeWrite
            for (i in 0..<file.size) {
                mutableDisk[writeStart + i] = mutableDisk[file.firstBlock + i]
                mutableDisk[file.firstBlock + i] = FreeBlock
            }
        }

        fileToMove--
    }

    return DiskMap(mutableDisk.toList(), diskMap.files)
}

fun checksum(diskLayout: List<DiskBlock>): Long {
    var sum = 0L
    for (i in 0..<diskLayout.size) {
        val block = diskLayout[i]

        if (block is FileBlock) {
            sum += i * block.id
        }
    }
    return sum
}

fun main() {
    val line = readLine()
    if (line == null) {
        error("Bad input.")
    }

    val diskMap = parseDiskMap(line)

    val defragged = defrag(diskMap)

    val score = checksum(defragged.layout)

    println("Answer = $score")
}
