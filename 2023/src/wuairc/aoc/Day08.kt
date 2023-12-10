package wuairc.aoc

import java.math.BigInteger

fun main() {
    Day08().solve()
}

class Day08 : Template(8) {
    override fun part1(input: List<String>): Long {
        val rule = Part1Rule
        val gameMap = parseInput(input, rule)
        return navigateBruteForce(gameMap, rule)
    }

    override fun part2(input: List<String>): Long {
        val rule = Part2Rule
        val gameMap = parseInput(input, rule)
        return navigate2(gameMap, rule)
    }

    private fun parseInput(input: List<String>, rule: Rule): GameMap {
        val iterator = input.iterator()
        val directionLine = iterator.next()
        val directions = directionLine.map { if (it == 'L') Direction.Left else Direction.Right }

        iterator.next()

        class RawNode(val index: Int, val left: String, val right: String)

        val rawNodes = mutableMapOf<String, RawNode>()
        for ((index, line) in iterator.withIndex()) {
            val (fromLine, toLine) = line.split('=', limit = 2)
            val left = toLine.substring(toLine.indexOf('(') + 1, toLine.indexOf(',')).trim()
            val right = toLine.substring(toLine.indexOf(',') + 1, toLine.indexOf(')')).trim()
            val nodeValue = fromLine.trim()
            val rawNode = RawNode(index, left, right)
            rawNodes[nodeValue] = rawNode
        }

        val nodes = rawNodes.map {
            val nodeValue = it.key
            val rawNode = it.value
            Node(
                rawNode.index,
                nodeValue,
                rule.isEndNode(nodeValue),
                intArrayOf(rawNodes.getValue(rawNode.left).index, rawNodes.getValue(rawNode.right).index)
            )
        }

        return GameMap(directions, nodes)
    }

    interface Rule {
        fun getStartingNodes(gameMap: GameMap): List<Node>
        fun isEndNode(nodeValue: String): Boolean
    }

    object Part1Rule : Rule {
        override fun getStartingNodes(gameMap: GameMap): List<Node> {
            return listOf(gameMap.nodes.first { it.value == "AAA" })
        }

        override fun isEndNode(nodeValue: String): Boolean {
            return nodeValue == "ZZZ"
        }
    }

    object Part2Rule : Rule {
        override fun getStartingNodes(gameMap: GameMap): List<Node> {
            return gameMap.nodes.filter { it.value.endsWith('A') }
        }

        override fun isEndNode(nodeValue: String): Boolean {
            return nodeValue.endsWith('Z')
        }
    }

    /**
     * brute-force algorithm, no memory allocation during navigation
     */
    private fun navigateBruteForce(gameMap: GameMap, rule: Rule): Long {
        val iterator = DirectionIterator(gameMap.directions.toTypedArray())
        val targetNodes: Array<Node> = rule.getStartingNodes(gameMap).toTypedArray()

        val nodes = gameMap.nodes
        while (iterator.hasNext()) {
            val direction = iterator.next()
            // update targetNodes in place to avoid object allocation, to minimize GC
            repeat(targetNodes.size) { index ->
                targetNodes[index] = navigateToNextNode(targetNodes[index], direction, nodes)
            }
            if (targetNodes.all { it.isEnd }) {
                break
            }
        }
        return iterator.getSteps()
    }

    private fun navigate2(gameMap: GameMap, rule: Rule): Long {
        val targetNodes: Array<Node> = rule.getStartingNodes(gameMap).toTypedArray()
        val nodes = gameMap.nodes

        val loopPeriods = targetNodes.map { targetNode ->
            val iterator = DirectionIterator(gameMap.directions.toTypedArray())
            var current = targetNode
            while (iterator.hasNext()) {
                val direction = iterator.next()
                current = navigateToNextNode(current, direction, nodes)
                if (current.isEnd) {
                    val loopCount = iterator.getLoopCount()
                    println("$targetNode = $loopCount")
                    // I don't like unspoken assumptions in this puzzle
                    assert(loopCount.second == 0L) { loopCount }
                    break
                }
            }
            iterator.getSteps()
        }
        return lcm(loopPeriods).longValueExact()
    }

    private fun BigInteger.lcm(other: BigInteger): BigInteger {
        return this.multiply(other) / this.gcd(other)
    }

    private fun lcm(numbers: Iterable<Long>): BigInteger {
        return numbers.map { BigInteger(it.toString()) }.reduce { acc, n -> acc.lcm(n) }
    }

    private inline fun navigateToNextNode(node: Node, direction: Direction, nodes: List<Node>): Node {
        val nextIndex = node.next[direction.index]
        return nodes[nextIndex]
    }

    private class DirectionIterator(val directions: Array<Direction>) : Iterator<Direction> {

        private val length = directions.size

        private var index: Int = 0

        private var steps: Long = 0

        override fun hasNext(): Boolean {
            if (index == length) {
                index = 0
            }
            return true
        }

        override fun next(): Direction {
            steps++
            if (steps.countTrailingZeroBits() > 26) {
                println(steps)
            }
            return directions[index++]
        }

        fun getSteps(): Long = steps

        fun getLoopCount(): Pair<Long, Long> {
            return steps / length to steps % length
        }
    }

    enum class Direction(val index: Int) {
        Left(0), Right(1)
    }

    data class Node(val index: Int, val value: String, val isEnd: Boolean, val next: IntArray)

    data class GameMap(val directions: List<Direction>, val nodes: List<Node>)
}