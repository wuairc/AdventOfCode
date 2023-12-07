package wuairc.aoc

import kotlin.math.*

fun main() {
    Day06().solve()
}

class Day06 : Template(6) {
    override fun part1(input: List<String>): Int {
        val raceList = parseInput(input)
        return raceList.map(::getBreakRecordRaceCount).reduce { acc, i -> acc * i }
    }

    override fun part2(input: List<String>): Int {
        val raceList = parseInput(input)
        val timeString = raceList.joinToString(separator = "") { it.time.toInt().toString() }
        val distanceString = raceList.joinToString(separator = "") { it.distance.toInt().toString() }
        val race = Race(timeString.toDouble(), distanceString.toDouble())
        return getBreakRecordRaceCount(race)
    }

    /**
     * x: time spend hold the button
     * y: distance
     * T: total time
     * x(T - x) > y -> x^2 - Tx + y < 0
     */
    private fun getBreakRecordRaceCount(race: Race): Int {
        val delta = sqrt(race.time.pow(2) - 4 * race.distance)
        assert(delta >= 0)
        val left = (race.time - delta) / 2
        val right = (race.time + delta) / 2
        val recordLeft = ceil(left.nextUp()).toInt()
        val recordRight = floor(right.nextDown()).toInt()
        assert(recordLeft < recordRight)
        return recordRight - recordLeft + 1
    }

    private fun parseInput(input: List<String>): List<Race> {
        val blankRegex = Regex("""\s+""")
        val times = input[0].splitToSequence(blankRegex).drop(1).map(String::toDouble)
        val distances = input[1].splitToSequence(blankRegex).drop(1).map(String::toDouble)
        return times.zip(distances).map { Race(it.first, it.second) }.toList()
    }

    data class Race(val time: Double, val distance: Double)
}