package wuairc.aoc

fun main() {
    Day10().solve()
}

class Day10 : Template(10) {
    override fun part1(input: List<String>): Long {
        val diagram = parseInput(input)
        return searchPath(diagram)
    }

    override fun part2(input: List<String>): Long {
        val diagram = parseInput(input)
        searchPath(diagram)
        return getEnclosedTiles(diagram)
    }

    private fun parseInput(input: List<String>): Diagram {
        val tileMap = TileType.entries.associateBy { it.symbol }
        val charArray = input.map { it.toCharArray() }.toTypedArray()

        val rows = input.size
        val cols = input[0].length
        val grid = Array(cols) { col ->
            Array(rows) { row ->
                val char = charArray[row][col]
                Tile(col, row, tileMap.getValue(char), false)
            }
        }
        return Diagram(grid, rows, cols)
    }

    private fun searchPath(diagram: Diagram): Long {
        var (firstEnd, secondEnd) = getNextToStartPoints(diagram)
        var steps = 1L
        while (firstEnd.tile !== secondEnd.tile) {
            firstEnd.tile.onPath = true
            firstEnd = getNextTile(diagram, firstEnd)

            secondEnd.tile.onPath = true
            secondEnd = getNextTile(diagram, secondEnd)
            steps++
        }
        firstEnd.tile.onPath = true
        return steps
    }

    /**
     * count from left to right, row by row
     */
    private fun getEnclosedTiles(diagram: Diagram): Long {
        var insideCount = 0L
        repeat(diagram.rows) { y ->
            var paths = 0
            var prevType: TileType? = null
            repeat(diagram.cols) { x ->
                val tile = diagram[x, y]
                val type: Char
                if (tile.onPath) {
                    type = tile.tileType.symbol
                    val tileType = if (tile.tileType == TileType.Start) {
                        getStartTileType(diagram)
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

    private fun getNextToStartPoints(diagram: Diagram): List<TileDirection> {
        val startTile = diagram.startTile
        startTile.onPath = true

        val nextToStartCandidates = mutableListOf<TileDirection>()
        if (startTile.col > 0) {
            val nextTile = getTileBeside(diagram, startTile, Direction.West)
            if (nextTile.tileType == TileType.NorthEast || nextTile.tileType == TileType.SouthEast || nextTile.tileType == TileType.Horizontal) {
                nextToStartCandidates.add(TileDirection(nextTile, Direction.West))
            }
        }
        if (startTile.col < diagram.cols - 1) {
            val nextTile = getTileBeside(diagram, startTile, Direction.East)
            if (nextTile.tileType == TileType.NorthWest || nextTile.tileType == TileType.SouthWest || nextTile.tileType == TileType.Horizontal) {
                nextToStartCandidates.add(TileDirection(nextTile, Direction.East))
            }
        }
        if (startTile.row > 0) {
            val nextTile = getTileBeside(diagram, startTile, Direction.North)
            if (nextTile.tileType == TileType.SouthWest || nextTile.tileType == TileType.SouthEast || nextTile.tileType == TileType.Vertical) {
                nextToStartCandidates.add(TileDirection(nextTile, Direction.North))
            }
        }
        if (startTile.row < diagram.rows - 1) {
            val nextTile = getTileBeside(diagram, startTile, Direction.South)
            if (nextTile.tileType == TileType.NorthEast || nextTile.tileType == TileType.NorthWest || nextTile.tileType == TileType.Vertical) {
                nextToStartCandidates.add(TileDirection(nextTile, Direction.South))
            }
        }
        val nextToStartPoints = nextToStartCandidates.filter { it.tile.tileType != TileType.Ground }
        assert(nextToStartPoints.size == 2) { nextToStartPoints }

        return nextToStartPoints
    }

    private fun getStartTileType(diagram: Diagram): TileType {
        val (d1, d2) = getNextToStartPoints(diagram).map { it.direction }.sorted()
        // North < East < South < West
        return when {
            d1 == Direction.North && d2 == Direction.East -> TileType.NorthEast
            d1 == Direction.North && d2 == Direction.South -> TileType.Vertical
            d1 == Direction.North && d2 == Direction.West -> TileType.NorthWest
            d1 == Direction.East && d2 == Direction.South -> TileType.SouthEast
            d1 == Direction.East && d2 == Direction.West -> TileType.Horizontal
            d1 == Direction.South && d2 == Direction.West -> TileType.SouthWest
            else -> throw AssertionError("$d1, $d2")
        }
    }

    private fun getNextTile(diagram: Diagram, tileDirection: TileDirection): TileDirection {
        val tile = tileDirection.tile
        val direction = tileDirection.direction

        val nextDirection = when (tile.tileType) {
            TileType.Vertical, TileType.Horizontal -> direction

            TileType.NorthEast -> when (direction) {
                Direction.South -> Direction.East
                Direction.West -> Direction.North
                else -> throw AssertionError(direction)
            }

            TileType.NorthWest -> when (direction) {
                Direction.South -> Direction.West
                Direction.East -> Direction.North
                else -> throw AssertionError(direction)
            }

            TileType.SouthWest -> when (direction) {
                Direction.North -> Direction.West
                Direction.East -> Direction.South
                else -> throw AssertionError(direction)
            }

            TileType.SouthEast -> when (direction) {
                Direction.North -> Direction.East
                Direction.West -> Direction.South
                else -> throw AssertionError(direction)
            }

            TileType.Ground, TileType.Start -> throw AssertionError(tile)
        }

        val nextTile = getTileBeside(diagram, tile, nextDirection)
        return TileDirection(nextTile, nextDirection)
    }

    private fun getTileBeside(diagram: Diagram, tile: Tile, direction: Direction): Tile {
        return when (direction) {
            Direction.North -> diagram[tile.col, tile.row - 1]
            Direction.South -> diagram[tile.col, tile.row + 1]
            Direction.East -> diagram[tile.col + 1, tile.row]
            Direction.West -> diagram[tile.col - 1, tile.row]
        }
    }

    enum class Direction {
        North, East, South, West
    }

    enum class TileType(val symbol: Char) {
        Vertical('|'),
        Horizontal('-'),
        NorthEast('L'),
        NorthWest('J'),
        SouthWest('7'),
        SouthEast('F'),
        Ground('.'),
        Start('S')
    }

    data class Tile(val col: Int, val row: Int, val tileType: TileType, var onPath: Boolean)

    data class TileDirection(val tile: Tile, val direction: Direction)

    class Diagram(private val grid: Array<Array<Tile>>, val rows: Int, val cols: Int) {
        val startTile: Tile by lazy { grid.flatMap { it.asIterable() }.find { it.tileType == TileType.Start }!! }

        operator fun get(x: Int, y: Int): Tile {
            return grid[x][y]
        }

        fun isOnDiagram(x: Int, y: Int): Boolean {
            return x >= 0 && y >= 0 && x < cols && y < rows
        }
    }
}