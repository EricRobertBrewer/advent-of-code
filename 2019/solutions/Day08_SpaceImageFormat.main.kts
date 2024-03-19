@file:Import("../AocUtil.kts")

solve(8, fun(lines: List<String>, part: Int): Long {
    val sif = lines[0]
    val width = 25
    val height = 6
    val size = width * height
    assert(sif.count() % size == 0)
    val layers = List(sif.count() / size) { itLayer ->
        List(height) { sif.substring(itLayer * size + it * width, itLayer * size + (it + 1) * width) }
    }

    if (part == 1) {
        val layerDigitCounts = List(layers.count()) { mutableMapOf('0' to 0, '1' to 0, '2' to 0) }
        for (index in layers.indices) {
            for (row in layers[index]) {
                for (c in row) {
                    layerDigitCounts[index][c] = layerDigitCounts[index][c]!! + 1
                }
            }
        }
        val indexMin = layerDigitCounts.indices.minBy { layerDigitCounts[it]['0']!! }
        return layerDigitCounts[indexMin]['1']!! * layerDigitCounts[indexMin]['2']!!.toLong()
    }

    for (h in 0..height - 1) {
        for (w in 0..width - 1) {
            var index = 0
            while (index < layers.count() && layers[index][h][w] == '2') {
                index++
            }
            print(if (index == layers.count()) '_' else if (layers[index][h][w] == '0') '.' else '#')
        }
        println()
    }
    return 0
})
