package wuairc.aoc

fun main() {
    Day10Optimized().solve()
}

/**
 * inspired by https://github.com/OskarSigvardsson/adventofcode/blob/master/2023/day10/day10.py
 *
 * optimize direction handling
 *
 * see file [Day10.py]
 */
class Day10Optimized : Template(10) {
    override fun part1(input: List<String>): Long {
        val diagram = parseInput(input)
        return searchPath(diagram)
    }

    override fun part2(input: List<String>): Long {
        val diagram = parseInput(input)
        searchPath(diagram)
        return getEnclosedTiles(diagram)
    }

    private fun parseInput(input: List<String>): Matrix {
        val tileMap = TileType.entries.associateBy { it.symbol }
        val charArray = input.map { it.toCharArray() }.toTypedArray()

        val rows = input.size
        val cols = input[0].length
        val grid = Array(cols) { col ->
            Array(rows) { row ->
                val char = charArray[row][col]
                Tile(Point(col, row), tileMap.getValue(char), false)
            }
        }
        return Matrix(grid, rows, cols)
    }

    private fun searchPath(matrix: Matrix): Long {
        var (firstEnd, secondEnd) = matrix.getNextToStartPoints()
        var steps = 1L
        while (firstEnd.tile.point != secondEnd.tile.point) {
            firstEnd.tile.onPath = true
            firstEnd = matrix.getNextTile(firstEnd)

            secondEnd.tile.onPath = true
            secondEnd = matrix.getNextTile(secondEnd)
            steps++
        }
        firstEnd.tile.onPath = true
        return steps
    }

    /**
     * count from left to right, row by row
     */
    private fun getEnclosedTiles(matrix: Matrix): Long {
        var insideCount = 0L
        repeat(matrix.rows) { y ->
            var paths = 0
            var prevType: TileType? = null
            repeat(matrix.cols) { x ->
                val tile = matrix[x, y]
                val type: Char
                if (tile.onPath) {
                    type = tile.tileType.symbol
                    val tileType = if (tile.tileType == TileType.Start) {
                        matrix.getStartTileType()
                    } else {
                        tile.tileType
                    }
                    when (tileType) {
                        TileType.Vertical -> paths++
                        TileType.SouthEast, TileType.NorthEast -> {
                            paths++
                            prevType = tileType
                        }

                        TileType.SouthWest, TileType.NorthWest -> {
                            check(prevType != null)
                            if (prevType == TileType.SouthEast && tileType == TileType.SouthWest
                                || prevType == TileType.NorthEast && tileType == TileType.NorthWest
                            ) {
                                paths--
                            }
                        }

                        TileType.Horizontal -> {}
                        TileType.Start,
                        TileType.Ground -> throw AssertionError(tile)
                    }
                } else {
                    if (paths % 2 == 1) {
                        insideCount++
                        type = 'I'
                    } else {
                        type = 'O'
                    }
                }
                print(type)
            }
            println()
        }
        return insideCount
    }

    data class Offset(val dx: Int, val dy: Int)

    data class Point(val x: Int, val y: Int) {
        fun go(direction: Direction): Point {
            val offset = direction.offset
            return Point(x + offset.dx, y + direction.offset.dy)
        }
    }

    enum class Direction(val offset: Offset) {
        North(Offset(0, -1)) {
            override fun opposite(): Direction = South
        },
        East(Offset(1, 0)) {
            override fun opposite(): Direction = West
        },
        South(Offset(0, 1)) {
            override fun opposite(): Direction = North
        },
        West(Offset(-1, 0)) {
            override fun opposite(): Direction = East
        };

        abstract fun opposite(): Direction
    }

    enum class TileType(val symbol: Char, val directions: List<Direction>) {
        Vertical('|', listOf(Direction.North, Direction.South)),
        Horizontal('-', listOf(Direction.East, Direction.West)),
        NorthEast('L', listOf(Direction.North, Direction.East)),
        NorthWest('J', listOf(Direction.North, Direction.West)),
        SouthWest('7', listOf(Direction.South, Direction.West)),
        SouthEast('F', listOf(Direction.East, Direction.South)),
        Ground('.', emptyList()),
        Start('S', emptyList());

        init {
            check(directions == directions.sorted()) { directions }
        }

        fun getOutcomingDirection(searchDirection: Direction): Direction {
            check(directions.size == 2) { this }
            val incomingDirection = searchDirection.opposite()
            val first = directions.first()
            return if (first === incomingDirection) {
                directions.last()
            } else {
                first
            }
        }
    }

    data class Tile(val point: Point, val tileType: TileType, var onPath: Boolean)

    data class TileAndDirection(val tile: Tile, val searchDirection: Direction)

    class Matrix(private val grid: Array<Array<Tile>>, val rows: Int, val cols: Int) {

        val startTile: Tile by lazy {
            grid.flatMap { it.asIterable() }.find { it.tileType == TileType.Start }!!
        }

        operator fun get(x: Int, y: Int): Tile {
            return grid[x][y]
        }

        operator fun get(point: Point): Tile {
            return grid[point.x][point.y]
        }

        fun isOnDiagram(point: Point): Boolean {
            val x = point.x
            val y = point.y
            return x >= 0 && y >= 0 && x < cols && y < rows
        }

        fun getNextTile(current: TileAndDirection): TileAndDirection {
            val tile = current.tile
            val direction = current.searchDirection
            val nextSearchDirection = tile.tileType.getOutcomingDirection(direction)
            val nextPoint = tile.point.go(nextSearchDirection)
            check(isOnDiagram(nextPoint))
            val nextTile = get(nextPoint)
            return TileAndDirection(nextTile, nextSearchDirection)
        }

        fun getStartTileType(): TileType {
            val directions = getNextToStartPoints().map { it.searchDirection }.sorted()
            val tileType = TileType.entries.find { it.directions == directions }
            check(tileType != null)
            return tileType
        }

        fun getNextToStartPoints(): List<TileAndDirection> {
            val startTile = this.startTile
            startTile.onPath = true

            val nextToStartPoints = Direction.entries.mapNotNull { direction ->
                val nextPoint = startTile.point.go(direction)
                if (isOnDiagram(nextPoint)) {
                    val nextTile = get(nextPoint)
                    val opposite = direction.opposite()
                    if (nextTile.tileType.directions.contains(opposite)) {
                        return@mapNotNull TileAndDirection(nextTile, direction)
                    }
                }
                return@mapNotNull null
            }

            check(nextToStartPoints.size == 2) { nextToStartPoints }

            return nextToStartPoints
        }
    }
}