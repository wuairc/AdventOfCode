package wuairc.aoc

fun main() {
    Day08().solve()
}

class Day08 : Template(8) {
    override fun part1(input: List<String>): Long {
        val gameMap = parseInput(input)
        return navigate(gameMap, Part1Rule)
    }

    override fun part2(input: List<String>): Long {
        val gameMap = parseInput(input)
        return navigate(gameMap, Part2Rule)
    }

    private fun parseInput(input: List<String>): GameMap {
        val iterator = input.iterator()
        val directionLine = iterator.next()
        val directions = directionLine.map { if (it == 'L') Direction.Left else Direction.Right }

        iterator.next()

        val nodes = mutableMapOf<String, NextNode>()
        for (line in iterator) {
            val (fromLine, toLine) = line.split('=', limit = 2)
            val left = toLine.substring(toLine.indexOf('(') + 1, toLine.indexOf(',')).trim()
            val right = toLine.substring(toLine.indexOf(',') + 1, toLine.indexOf(')')).trim()
            val nextNode = NextNode(left, right)
            nodes[fromLine.trim()] = nextNode
        }
        return GameMap(directions, nodes)
    }

    interface Rule {
        fun getStartingNodes(gameMap: GameMap): List<String>
        fun isReachEnd(nodes: List<String>): Boolean
    }

    object Part1Rule : Rule {
        override fun getStartingNodes(gameMap: GameMap): List<String> {
            return listOf("AAA")
        }

        override fun isReachEnd(nodes: List<String>): Boolean {
            return nodes.all { it == "ZZZ" }
        }
    }

    object Part2Rule : Rule {
        override fun getStartingNodes(gameMap: GameMap): List<String> {
            return gameMap.nodes.keys.filter { it.endsWith('A') }
        }

        override fun isReachEnd(nodes: List<String>): Boolean {
            return nodes.all { it.endsWith('Z') }
        }
    }

    private fun navigate(gameMap: GameMap, rule: Rule): Long {
        val iterator = DirectionIterator(gameMap.directions)
        var current = rule.getStartingNodes(gameMap)

        val nodes = gameMap.nodes
        while (iterator.hasNext()) {
            val nextDirection = iterator.next()
            current = current.map { node ->
                navigateToNextNode(node, nextDirection, nodes)
            }
            if (rule.isReachEnd(current)) {
                break
            }
        }
        return iterator.getSteps()
    }

    private fun navigateToNextNode(node: String, nextDirection: Direction, nodes: Map<String, NextNode>): String {
        val nextNode = nodes.getOrElse(node) { throw AssertionError() }
        return when (nextDirection) {
            Direction.Left -> nextNode.left
            Direction.Right -> nextNode.right
        }
    }

    private class DirectionIterator(val directions: List<Direction>) : Iterator<Direction> {

        private var iterator: Iterator<Direction> = directions.iterator()

        private var steps: Long = 0

        override fun hasNext(): Boolean {
            if (!iterator.hasNext()) {
                iterator = directions.iterator()
                iterator.hasNext()
            }
            return true
        }

        override fun next(): Direction {
            steps++
            return iterator.next()
        }

        fun getSteps(): Long = steps
    }

    enum class Direction {
        Left, Right
    }

    data class NextNode(val left: String, val right: String)

    data class GameMap(val directions: List<Direction>, val nodes: Map<String, NextNode>)
}