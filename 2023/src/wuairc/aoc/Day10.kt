package wuairc.aoc

fun main() {
    Day10().solve()
}

class Day10 : Template(10) {
    override fun part1(input: List<String>): Long {
        val diagram = parseInput(input)
        return search(diagram)
    }

    override fun part2(input: List<String>): Long {
        TODO()
    }

    private fun parseInput(input: List<String>): Diagram {
        val tileMap = TileType.entries.associateBy { it.symbol }
        val charArray = input.map { it.toCharArray() }.toTypedArray()

        val grid = Array(input[0].length) { x ->
            Array(input.size) { y ->
                val char = charArray[y][x]
                Tile(x, y, tileMap.getValue(char))
            }
        }
        return Diagram(grid)
    }

    private fun search(diagram: Diagram): Long {
        val grid = diagram.grid
        var (firstEnd, secondEnd) = getNextToStartPoints(grid)
        var steps = 1L
        while (firstEnd.tile !== secondEnd.tile) {
            firstEnd = getNextTile(grid, firstEnd)
            secondEnd = getNextTile(grid, secondEnd)
            steps++
        }
        return steps
    }

    private fun getNextToStartPoints(grid: Array<Array<Tile>>): List<TileDirection> {
        val startPile = grid.flatMap { it.asIterable() }.find { it.type == TileType.Start }!!

        val nextToStartCandidates = mutableListOf<TileDirection>()
        if (startPile.x > 0) {
            val nextTile = getTileBeside(grid, startPile, Direction.West)
            if (nextTile.type == TileType.NorthEast || nextTile.type == TileType.SouthEast || nextTile.type == TileType.Horizontal) {
                nextToStartCandidates.add(TileDirection(nextTile, Direction.West))
            }
        }
        if (startPile.x < grid[0].size) {
            val nextTile = getTileBeside(grid, startPile, Direction.East)
            if (nextTile.type == TileType.NorthWest || nextTile.type == TileType.SouthWest || nextTile.type == TileType.Horizontal) {
                nextToStartCandidates.add(TileDirection(nextTile, Direction.East))
            }
        }
        if (startPile.y > 0) {
            val nextTile = getTileBeside(grid, startPile, Direction.North)
            if (nextTile.type == TileType.SouthWest || nextTile.type == TileType.SouthEast || nextTile.type == TileType.Vertical) {
                nextToStartCandidates.add(TileDirection(nextTile, Direction.North))
            }
        }
        if (startPile.y < grid.size) {
            val nextTile = getTileBeside(grid, startPile, Direction.South)
            if (nextTile.type == TileType.NorthEast || nextTile.type == TileType.NorthWest || nextTile.type == TileType.Vertical) {
                nextToStartCandidates.add(TileDirection(nextTile, Direction.South))
            }
        }
        val nextToStartPoints = nextToStartCandidates.filter { it.tile.type != TileType.Ground }
        assert(nextToStartPoints.size == 2) { nextToStartPoints }

        return nextToStartPoints
    }

    private fun getNextTile(grid: Array<Array<Tile>>, tileDirection: TileDirection): TileDirection {
        val tile = tileDirection.tile
        val direction = tileDirection.direction

        val nextDirection = when (tile.type) {
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

        val nextTile = getTileBeside(grid, tile, nextDirection)
        return TileDirection(nextTile, nextDirection)
    }

    private fun getTileBeside(grid: Array<Array<Tile>>, tile: Tile, direction: Direction): Tile {
        return when (direction) {
            Direction.North -> grid[tile.x][tile.y - 1]
            Direction.South -> grid[tile.x][tile.y + 1]
            Direction.East -> grid[tile.x + 1][tile.y]
            Direction.West -> grid[tile.x - 1][tile.y]
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

    data class Tile(val x: Int, val y: Int, val type: TileType)

    data class TileDirection(val tile: Tile, val direction: Direction)

    class Diagram(val grid: Array<Array<Tile>>)
}