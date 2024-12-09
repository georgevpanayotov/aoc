sealed interface DiskBlock

data class FileBlock(val id: Int) : DiskBlock {
    override fun toString(): String = "[$id]"
}

data object FreeSpace : DiskBlock {
    override fun toString(): String = "."
}

fun parseDiskMap(line: String): List<DiskBlock> {
    val diskLayout = mutableListOf<DiskBlock>()

    var file = true
    var id = 0

    for (i in 0..<line.length) {
        val count = line[i].code - '0'.code
        for (i in 1..count) {
            if (file) {
                diskLayout.add(FileBlock(id))
            } else {
                diskLayout.add(FreeSpace)
            }
        }

        if (file) {
            id++
        }

        file = !file
    }

    return diskLayout.toList()
}

fun defrag(diskLayout: List<DiskBlock>): List<DiskBlock> {
    val mutableDisk = diskLayout.toMutableList()

    var writeHead = 0
    var readHead = diskLayout.size - 1

    while (writeHead < readHead) {
        while (mutableDisk[readHead] is FreeSpace) {
            readHead--
        }

        while (mutableDisk[writeHead] is FileBlock) {
            writeHead++
        }

        if (writeHead < readHead) {
            mutableDisk[writeHead] = mutableDisk[readHead]
            mutableDisk[readHead] = FreeSpace
            writeHead++
            readHead--
        }
    }

    return mutableDisk.toList()
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

    val layout = parseDiskMap(line)

    val defragged = defrag(layout)

    val score = checksum(defragged)

    println("Answer = $score")
}
