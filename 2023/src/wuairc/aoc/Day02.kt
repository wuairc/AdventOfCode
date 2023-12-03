package wuairc.aoc

fun main() {
    Day02().solve()
}

class Day02 : Template(2) {
    override fun part1(input: List<String>): Int {
        val condition = CubeInfo(12, 13, 14)
        val gameInfo = parseInput(input)
        return gameInfo.filter { game ->
            game.sampling.all { cubeInfo ->
                condition.contains(cubeInfo)
            }
        }.sumOf { it.id }
    }

    override fun part2(input: List<String>): Int {
        val gameInfo = parseInput(input)
        return gameInfo.sumOf { game ->
            game.sampling.maxOf { it.red } * game.sampling.maxOf { it.blue } * game.sampling.maxOf { it.green }
        }
    }

    private fun parseInput(input: List<String>): List<Game> {
        return input.map { line ->
            val gameId = line.substringBefore(":").substringAfter("Game ").trim().toInt()
            val cubeInfoList = line.substringAfter(":").splitToSequence(';').map { round ->
                val map = round.splitToSequence(',').associate { colorCount ->
                    val (value, key) = colorCount.trim().split(' ', limit = 2)
                    key.trim() to value.trim().toInt()
                }
                val red = map.getOrDefault("red", 0)
                val green = map.getOrDefault("green", 0)
                val blue = map.getOrDefault("blue", 0)
                CubeInfo(red, green, blue)
            }.toList()
            Game(gameId, cubeInfoList)
        }
    }
}

data class CubeInfo(val red: Int, val green: Int, val blue: Int) {
    fun contains(other: CubeInfo): Boolean {
        return red >= other.red && green >= other.green && blue >= other.blue
    }
}

data class Game(val id: Int, val sampling: List<CubeInfo>)