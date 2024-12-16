import net.panayotov.util.Grid

fun main() {
    val grid = Grid.read('.')
    val outGrid = Grid<Char>(grid.height, grid.width, '.')

    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            outGrid[y, grid.width - x - 1] = grid[x, y]
        }
    }

    print(outGrid)
}
