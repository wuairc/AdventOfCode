package wuairc.aoc

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day11().solve()
}

class Day11 : Template(11) {
    override fun part1(input: List<String>): Long {
        val universe = parseInput(input, 2)
        return getDistances(universe)
    }

    override fun part2(input: List<String>): Long {
        val universe = parseInput(input, 1_000_000)
        return getDistances(universe)
    }

    private fun getDistances(universe: Universe): Long {
        val galaxies = universe.getGalaxies()
        val galaxySize = galaxies.size
        var sum = 0L
        galaxies.forEachIndexed { index, coordinate ->
            var second = index + 1
            while (second < galaxySize) {
                val peer = galaxies[second]
                val distance = universe.getDistance(coordinate, peer)
                sum += distance
                second++
            }
        }
        return sum
    }

    private fun parseInput(input: List<String>, expandFactor: Long): Universe {
        val charArray = input.map { it.toCharArray() }.toTypedArray()
        val rowCount = input.size
        val columnCount = input[0].length
        val grid = Array(rowCount) { row ->
            Array(columnCount) { col ->
                val char = charArray[row][col]
                val type = Type.entries.find { it.symbol == char }
                check(type != null) { col }
                Point(type, Coordinate(row, col))
            }
        }
        return Universe(grid, rowCount, columnCount, expandFactor)
    }

    enum class Type(val symbol: Char) {
        Space('.'), Galaxy('#');
    }

    data class Coordinate(val row: Int, val col: Int)

    data class Point(val type: Type, val coordinate: Coordinate)

    class Universe(val grid: Array<Array<Point>>, val rowCount: Int, val columnCount: Int, val expandFactor: Long) {
        val rowExpand: List<Boolean> = (0 until rowCount).map { row -> grid[row].all { it.type === Type.Space } }
        val columnExpand: List<Boolean> = (0 until columnCount).map { col ->
            (0 until rowCount).all { row ->
                grid[row][col].type === Type.Space
            }
        }

        fun getGalaxies(): List<Coordinate> {
            return grid.flatMap { it.asIterable() }.filter {
                it.type === Type.Galaxy
            }.map { it.coordinate }
        }

        fun getDistance(a: Coordinate, b: Coordinate): Long {
            return abs(b.row - a.row) + abs(b.col - a.col) +
                    getExpand(rowExpand, b.row, a.row) +
                    getExpand(columnExpand, b.col, a.col)
        }

        private fun getExpand(list: List<Boolean>, a: Int, b: Int): Long {
            val min = min(a, b)
            val max = max(a, b)
            return list.subList(min, max).sumOf { if (it) expandFactor - 1 else 0 }
        }
    }
}