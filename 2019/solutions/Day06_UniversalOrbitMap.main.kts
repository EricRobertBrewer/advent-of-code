@file:Import("../AocUtil.kts")


solve(6, fun(lines: List<String>, part: Int): Long {
    val orbits = mutableMapOf<String, String>()
    for (line in lines) {
        val leftRight = line.split(")")
        val left = leftRight[0]
        val right = leftRight[1]
        orbits[right] = left
    }

    if (part == 1) {
        val itemToDistance = mutableMapOf<String, Int>()
        itemToDistance["COM"] = 0
        var answer = 0L
        for (item in orbits.keys) {
            answer += getDistance(item, orbits, itemToDistance)
        }
        return answer
    }

    // Tally up YOU path distances.
    val youToDistance = mutableMapOf("YOU" to 0)
    var youItem = "YOU"
    while (youItem != "COM") {
        youItem = orbits[youItem]!!
        youToDistance[youItem] = youToDistance.count()
    }
    // Find common ancestor.
    val sanToDistance = mutableMapOf("SAN" to 0)
    var sanItem = "SAN"
    while (!youToDistance.containsKey(sanItem)) {
        sanItem = orbits[sanItem]!!
        sanToDistance[sanItem] = sanToDistance.count()
    }
    return youToDistance[sanItem]!! + sanToDistance[sanItem]!! - 2L
})

fun getDistance(item: String, orbits: Map<String, String>, itemToDistance: MutableMap<String, Int>): Int {
    if (itemToDistance.containsKey(item)) {
        return itemToDistance[item]!!
    }
    val distance = getDistance(orbits[item]!!, orbits, itemToDistance) + 1
    itemToDistance[item] = distance
    return distance
}
