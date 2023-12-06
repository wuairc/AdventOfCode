package wuairc.aoc

import kotlin.math.min

fun main() {
    Day05().solve()
}

class Day05 : Template(5) {
    override fun part1(input: List<String>): Int {
        val redirectMap = parseInput(input, false)
        val lowestLocation = redirectMap.findLowestLocation()
        if (lowestLocation < Int.MAX_VALUE) {
            return lowestLocation.toInt()
        }
        throw AssertionError(lowestLocation)
    }

    override fun part2(input: List<String>): Int {
        val redirectMap = parseInput(input, true)
        val lowestLocation = redirectMap.findLowestLocation()
        if (lowestLocation < Int.MAX_VALUE) {
            return lowestLocation.toInt()
        }
        throw AssertionError(lowestLocation)
    }

    private fun parseInput(input: List<String>, seedsAsRange: Boolean): RedirectMap {
        val blankRegex = Regex("""\s+""")
        val iterator = input.iterator()
        val seedsLine = iterator.next()
        val seedsList = seedsLine.substringAfter(':').trim().split(blankRegex).map(String::toLong).let { list ->
            if (seedsAsRange) {
                list.windowed(2, 2).map { Range(it[0], it[1]) }
            } else {
                list.map { Range(it, 1) }
            }
        }
        iterator.next()

        val mappings = mutableMapOf<String, NumberMap>()
        while (iterator.hasNext()) {
            val head = iterator.next()
            val (sourceName, _, destinationNamePrat) = head.split('-', limit = 3)
            val destinationName = destinationNamePrat.substringBefore(' ')
            val rangeMappings = mutableListOf<RangeMapItem>()
            do {
                val line = iterator.next()
                if (line.isBlank()) {
                    break
                }
                val (destinationStart, sourceStart, rangeLength) = line.split(blankRegex)
                val source = sourceStart.toLong()
                rangeMappings.add(RangeMapItem(Range(source, rangeLength.toLong()), destinationStart.toLong() - source))
            } while (iterator.hasNext())
            rangeMappings.sortBy { it.range.start }
            mappings[sourceName] = NumberMap(sourceName, destinationName, rangeMappings)
        }
        return RedirectMap(seedsList, mappings)
    }

    data class Range(val start: Long, val size: Long) {
        val endExclusive: Long = start + size
        val end: Long = endExclusive - 1

        fun offset(offset: Long): Range {
            return Range(start + offset, size)
        }

        fun offset(offset: Long, newSize: Long): Range {
            return Range(start + offset, newSize)
        }

        override fun toString(): String {
            return "[$start, $end], size=$size"
        }

        companion object {
            fun fromStartEnd(start: Long, endInclusive: Long): Range {
                return Range(start, endInclusive - start + 1)
            }
        }
    }

    data class RangeMapItem(val range: Range, val offset: Long)

    class NumberMap(val source: String, val destination: String, val rangeMappings: List<RangeMapItem>) {
        fun mapNext(ranges: List<Range>): List<Range> {
            val resultRanges = mutableListOf<Range>()
            for (fromRange in ranges) {
                mapRange(fromRange, resultRanges)
            }
            resultRanges.sortBy { it.start }
            // TODO: merge ranges
            return resultRanges
        }

        private fun mapRange(range: Range, result: MutableList<Range>) {
            val bsIndex = rangeMappings.binarySearch { it.range.start.compareTo(range.start) }
            if (bsIndex >= 0) {
                // start aligned
                val item = rangeMappings[bsIndex]
                val size = min(range.size, item.range.size)
                result.add(range.offset(item.offset, size))
                if (range.size > size) {
                    val remaining = item.range.offset(size, range.size - size)
                    mapRange(remaining, result)
                }
                return
            } else {
                // range.start somewhere in between two mapping items
                val insertIndex = -(bsIndex + 1)
                var remainingRange = range
                if (insertIndex > 0) {
                    // not insert at front, check if overlap with previous item
                    val item = rangeMappings[insertIndex - 1]
                    if (item.range.end >= range.start) {
                        // overlap with previous item
                        val end = min(item.range.end, range.end)
                        val overlayRange = Range.fromStartEnd(range.start, end)
                        result.add(overlayRange.offset(item.offset))
                        if (range.end <= end) {
                            // fully contained in previous item
                            return
                        }
                        // start aligned with next item
                        remainingRange = Range.fromStartEnd(item.range.endExclusive, range.end)
                    }
                }
                if (insertIndex == rangeMappings.size) {
                    // not in mapping
                    result.add(remainingRange)
                    return
                }
                val item = rangeMappings[insertIndex]
                assert(remainingRange.start < item.range.start)

                if (remainingRange.end < item.range.start) {
                    // not in mapping
                    result.add(remainingRange)
                    return
                }
                val notInMappingRange = Range.fromStartEnd(remainingRange.start, item.range.start - 1)
                result.add(notInMappingRange)

                // start aligned range
                val remaining = Range.fromStartEnd(item.range.start, remainingRange.end)
                mapRange(remaining, result)
            }
        }
    }

    class RedirectMap(val seedsList: List<Range>, val mapping: Map<String, NumberMap>) {

        fun findLowestLocation(): Long {
            return seedsList.flatMap(::mapToLocation).minOf { it.start }
        }

        fun mapToLocation(seeds: Range): List<Range> {
            var map = mapping["seed"]
            var mappedRanges = listOf(seeds)
            while (map != null) {
                mappedRanges = map.mapNext(mappedRanges)
                map = mapping[map.destination]
            }
            return mappedRanges
        }
    }
}