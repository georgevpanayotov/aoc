package net.panayotov.util

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private val BOUNDARY = 10

// A helper to draw images in the semantics of the Grid/Point types. Helpful for visualizing tricky
// problems. The width is fixed to 1000 the height is chosen to be proportional to the given bounds.
class ImageMaker(val bottomLeft: Point, val topRight: Point) {
    val width = 1000
    val height =
        (width * (topRight.y - bottomLeft.y).toDouble() / (topRight.x - bottomLeft.x).toDouble())
            .toInt()

    private val cellSize: Int = (width / (topRight.x - bottomLeft.x)).toInt()

    private val image =
        BufferedImage(width + 2 * BOUNDARY, height + 2 * BOUNDARY, BufferedImage.TYPE_INT_ARGB)

    fun drawRect(rectMin: Point, rectMax: Point, color: Color) {
        val (x1, y1) = convert(rectMin) + Point(-cellSize / 2L, cellSize / 2L)
        val (x2, y2) = convert(rectMax) + Point(cellSize / 2L, -cellSize / 2L)

        image.getGraphics().apply {
            setColor(color)
            // NOTE: There is an inversion here because our `Point` class has BottomLeft as 0, 0 but
            // for AWT it is the TopLeft.
            fillRect(x1.toInt(), y2.toInt(), x2.toInt() - x1.toInt(), y1.toInt() - y2.toInt())
        }
    }

    fun drawLine(point1: Point, point2: Point, color: Color) {
        val (x1, y1) = convert(point1)
        val (x2, y2) = convert(point2)

        image.getGraphics().apply {
            setColor(color)
            drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
        }
    }

    // Draws a fixed border around the bounding area.
    fun drawBorder(color: Color) {
        image.getGraphics().apply {
            setColor(color)
            fillRect(0, 0, BOUNDARY, height + 2 * BOUNDARY)
            fillRect(0, 0, width + 2 * BOUNDARY, BOUNDARY)

            fillRect(0, height + BOUNDARY, width + 2 * BOUNDARY, height + 2 * BOUNDARY)
            fillRect(width + BOUNDARY, 0, width + 2 * BOUNDARY, height + 2 * BOUNDARY)
        }
    }

    // Fills the background color. Call this before other methods.
    fun drawBackground(color: Color) {
        image.getGraphics().apply {
            setColor(color)
            fillRect(0, 0, width + 2 * BOUNDARY, height + 2 * BOUNDARY)
        }
    }

    // Writes to $name.png.
    fun write(name: String) {
        val outputfile = File("$name.png")
        ImageIO.write(image, "png", outputfile)
    }

    private fun convert(point: Point): Point {
        val (x, y) = (point - bottomLeft)
        return Point(
            height * x / (topRight.y - bottomLeft.y) + BOUNDARY + cellSize / 2,
            // NOTE: There is an inversion here because our `Point` class has BottomLeft as 0, 0 but
            // for AWT it is the TopLeft.
            height * ((topRight.y - bottomLeft.y) - y - 1) / (topRight.y - bottomLeft.y) +
                BOUNDARY +
                cellSize / 2,
        )
    }
}
