package main

import (
    "fmt"
    "os"
    "regexp"
    "slices"
    "sort"
    "strconv"
    "strings"
    "time"

    "ericrobertbrewer.com/aoc/aoc"
    "ericrobertbrewer.com/aoc/cs"
)

const YEAR = 2023

type Solver func(lines []string, part int) int

var SOLVERS = map[int]Solver {
    1: d01_Trebuchet,
    2: d02_CubeConundrum,
    3: d03_GearRatios,
    4: d04_Scratchcards,
    5: d05_IfYouGiveASeedAFertilizer,
    6: d06_WaitForIt,
    7: d07_CamelCards,
    8: d08_HauntedWasteland,
    9: d09_MirageMaintenance,
    10: d10_PipeMaze,
    11: d11_CosmicExpansion,
    12: d12_HotSprings,
    13: d13_PointOfIncidence,
    14: d14_ParabolicReflectorDish,
    15: d15_LensLibrary,
    16: d16_TheFloorWillBeLava,
    17: d17_ClumsyCrucible,
    18: d18_LavaductLagoon,
}

type Point struct {
    I, J int
}

type PointS struct {
    I, J int
    S string
}

const DIRECTION_UP = 0
const DIRECTION_RIGHT = 1
const DIRECTION_DOWN = 2
const DIRECTION_LEFT = 3
var DELTAS = [...][]int{{-1, 0}, {0, 1}, {1, 0}, {0, -1}} // Up, right, down, left.

type Point3 struct {
    I, J, K int
}

type Point4 struct {
    I, J, K, L int
}

func main() {
    if len(os.Args) < 3 || len(os.Args) > 4 {
        panic(fmt.Sprintf("Usage: go run solutions.go <day> <part>[ --example|-x]", YEAR))
    }
    day, _ := strconv.Atoi(os.Args[1])
    part, _ := strconv.Atoi(os.Args[2])
    example := false
    if len(os.Args) > 3 {
        example = os.Args[3] == "--example" || os.Args[3] == "-x"
    }

    var lines []string
    if example {
        lines = aoc.ExampleInput(YEAR, day)
    } else {
        lines = aoc.DownloadInputIfNeeded(YEAR, day)
    }

    start := time.Now()
    answer := solve(day, part, lines)
    duration := time.Since(start)
    fmt.Println(answer)
    fmt.Printf("Time: %.6f s\n", duration.Seconds())
}

func solve(day int, part int, lines []string) int {
    if solver, ok := SOLVERS[day]; ok {
        return solver(lines, part)
    }
    panic(fmt.Sprintf("No solution found for day: %d", day))
}

func d01_Trebuchet(lines []string, part int) int {
    nameToValue := map[string]int {
        "one": 1, "two": 2, "three": 3, "four": 4, "five": 5, "six": 6, "seven": 7, "eight": 8, "nine": 9,
    }
    sum := 0
    for _, line := range lines {
        digitFirst := -1
        digitLast := -1
        for j, c := range line {
            if c >= '1' && c <= '9' {
                if digitFirst == -1 {
                    digitFirst = int(c - '0')
                }
                digitLast = int(c - '0')
            } else if part == 2 {
                front := line[:j + 1]
                for name, value := range nameToValue {
                    if strings.HasSuffix(front, name) {
                        if digitFirst == -1 {
                            digitFirst = value
                        }
                        digitLast = value
                        break
                    }
                }
            }
        }
        sum += 10 * digitFirst + digitLast
    }
    return sum
}

func d02_CubeConundrum(lines []string, part int) int {
    if part == 1 {
        sumPossible := 0
        colorToMax := map[string]int {"red": 12, "green": 13, "blue": 14}
        for i, line := range lines {
            gameSubsets := strings.Split(line, ": ")
            subsets := strings.Split(gameSubsets[1], "; ")
            possible := true
            for _, subset := range subsets {
                subsetSum := 0
                nSpaceColors := strings.Split(subset, ", ")
                for _, nSpaceColor := range nSpaceColors {
                    nColor := strings.Split(nSpaceColor, " ")
                    n, _ := strconv.Atoi(nColor[0])
                    color := nColor[1]
                    if n > colorToMax[color] {
                        possible = false
                        break
                    }
                    subsetSum += n
                }
                if subsetSum > colorToMax["red"] + colorToMax["green"] + colorToMax["blue"] {
                    possible = false
                }
                if !possible {
                    break
                }
            }
            if possible {
                sumPossible += i + 1
            }
        }
        return sumPossible
    }

    sumPower := 0
    for _, line := range lines {
        colorToMin := map[string]int {"red": -1, "green": -1, "blue": -1}
        gameSubsets := strings.Split(line, ": ")
        subsets := strings.Split(gameSubsets[1], "; ")
        for _, subset := range subsets {
            nSpaceColors := strings.Split(subset, ", ")
            for _, nSpaceColor := range nSpaceColors {
                nColor := strings.Split(nSpaceColor, " ")
                n, _ := strconv.Atoi(nColor[0])
                color := nColor[1]
                if colorToMin[color] == -1 || n > colorToMin[color] {
                    colorToMin[color] = n
                }
            }
        }
        power := colorToMin["red"] * colorToMin["green"] * colorToMin["blue"]
        sumPower += power
    }
    return sumPower
}

func d03_GearRatios(lines []string, part int) int {
    var pointNs []PointS
    for i, line := range lines {
        numberStart := -1
        for j, c := range line {
            if c >= '0' && c <= '9' {
                if numberStart == -1 {
                    numberStart = j
                }
            } else {
                if numberStart != -1 {
                    pointNs = append(pointNs, PointS{i, numberStart, line[numberStart:j]})
                }
                numberStart = -1
            }
        }
        if numberStart != -1 {
            pointNs = append(pointNs, PointS{i, numberStart, line[numberStart:]})
        }
    }

    sumNumber := 0
    gearToPointNs := make(map[Point][]PointS)
    for _, pointN := range pointNs {
        isPart := false
        for i := max(0, pointN.I - 1); i <= min(len(lines) - 1, pointN.I + 1); i++ {
            for j := max(0, pointN.J - 1); j <= min(len(lines[pointN.I]) - 1, pointN.J + len(pointN.S)); j++ {
                if i == pointN.I && j >= pointN.J && j < pointN.J + len(pointN.S) {
                    continue
                }
                if lines[i][j] != '.' {
                    isPart = true
                    if lines[i][j] == '*' {
                        gear := Point{i, j}
                        if _, ok := gearToPointNs[gear]; !ok {
                            gearToPointNs[gear] = []PointS{}
                        }
                        gearToPointNs[gear] = append(gearToPointNs[gear], pointN)
                    }
                }
            }
        }
        if isPart {
            n, _ := strconv.Atoi(pointN.S)
            sumNumber += n
        }
    }

    if part == 1 {
        return sumNumber
    }

    sumRatio := 0
    for _, gearPointNs := range gearToPointNs {
        if len(gearPointNs) == 2 {
            n0, _ := strconv.Atoi(gearPointNs[0].S)
            n1, _ := strconv.Atoi(gearPointNs[1].S)
            sumRatio += n0 * n1
        }
    }
    return sumRatio
}

func d04_Scratchcards(lines []string, part int) int {
    var winningSets []map[int]struct{}
    var cardSets []map[int]struct{}
    for _, line := range lines {
        winningSsCardSs := strings.Split(strings.Split(line, ": ")[1], " | ")
        winningSet := make(map[int]struct{})
        for _, winningS := range strings.Split(winningSsCardSs[0], " ") {
            n, err := strconv.Atoi(winningS)
            if err != nil {
                continue
            }
            winningSet[n] = struct{}{}
        }
        winningSets = append(winningSets, winningSet)
        cardSet := make(map[int]struct{})
        for _, cardS := range strings.Split(winningSsCardSs[1], " ") {
            n, err := strconv.Atoi(cardS)
            if err != nil {
                continue
            }
            cardSet[n] = struct{}{}
        }
        cardSets = append(cardSets, cardSet)
    }

    total := 0
    copies := make([]int, len(winningSets))
    for i := range copies {
        copies[i] = 1
    }
    for i, winningSet := range winningSets {
        cardSet := cardSets[i]
        matches := 0
        for n := range winningSet {
            if _, ok := cardSet[n]; ok {
                matches++
            }
        }
        if part == 1 {
            if matches > 0 {
                score := 1
                for m := 0; m < matches - 1; m++ {
                    score *= 2
                }
                total += score
            }
        } else {
            for di := 1; di < 1 + matches; di++ {
                copies[i + di] += copies[i]
            }
            total += copies[i]
        }
    }
    return total
}

func d05_IfYouGiveASeedAFertilizer(lines []string, part int) int {
    var seeds []int
    for _, seedS := range strings.Split(lines[0], " ")[1:] {
        seed, _ := strconv.Atoi(seedS)
        seeds = append(seeds, seed)
    }
    sources := []string{
        "seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location",
    }
    var sourceMapRanges [][][]int
    i := 1
    for i < len(lines) {
        sourceIndex := len(sourceMapRanges)
        i++ // Blank line preceding map name.
        mapNameParts := strings.Split(strings.Split(lines[i], " map:")[0], "-")
        if mapNameParts[0] != sources[sourceIndex] || mapNameParts[2] != sources[sourceIndex + 1] {
            panic(fmt.Sprintf("Unexpected map name at line %d: %s", i, lines[i]))
        }
        i++ // Move to start of map descriptions.
        var mapRanges [][]int
        for i < len(lines) && len(lines[i]) > 0 {
            var mapRange []int
            for _, valueS := range strings.Split(lines[i], " ") {
                value, _ := strconv.Atoi(valueS)
                mapRange = append(mapRange, value)
            }
            if len(mapRange) != 3 {
                panic(fmt.Sprintf("Unexpected source-to-destination map: %d", lines[i]))
            }
            mapRanges = append(mapRanges, mapRange)
            i++ // Move to next map, or to blank line, or to EOF.
        }
        sourceMapRanges = append(sourceMapRanges, mapRanges)
    }

    if part == 1 {
        var locations []int
        for _, seed := range seeds {
            value := seed
            for i := 0; i < len(sourceMapRanges); i++ {
                for _, mapRange := range sourceMapRanges[i] {
                    if value >= mapRange[1] && value < mapRange[1] + mapRange[2] {
                        value = value - mapRange[1] + mapRange[0]
                        break
                    }
                }
            }
            locations = append(locations, value)
        }
        return slices.Min(locations)
    }

    var seedRanges [][]int
    for j := 0; j < len(seeds) / 2; j++ {
        seedRanges = append(seedRanges, seeds[2 * j:2 * j + 2])
    }
    var locationRanges [][][]int
    for _, seedRange := range seedRanges {
        valueRanges := [][]int{seedRange}
        for i := 0; i < len(sourceMapRanges); i++ {
            var valueRangesNext [][]int
            for _, mapRange := range sourceMapRanges[i] {
                mapStart, mapEnd := mapRange[1], mapRange[1] + mapRange[2] - 1
                for j := 0; j < len(valueRanges); j++ {
                    valueRange := valueRanges[j]
                    valueStart, valueEnd := valueRange[0], valueRange[0] + valueRange[1] - 1
                    if valueEnd < mapStart || mapEnd < valueStart {
                        continue // No overlap.
                    }
                    if valueStart < mapStart { // Dangling front.
                        valueRanges = append(valueRanges, []int{valueStart, mapStart - valueStart})
                    }
                    if valueEnd > mapEnd { // Dangling back.
                        valueRanges = append(valueRanges, []int{mapEnd + 1, valueEnd - mapEnd})
                    }
                    startNextSource := max(valueStart, mapStart)
                    startNextDest := startNextSource - mapRange[1] + mapRange[0]
                    valueRangeNext := []int{startNextDest, min(valueEnd, mapEnd) - startNextSource}
                    valueRangesNext = append(valueRangesNext, valueRangeNext)
                    valueRanges = append(valueRanges[:j], valueRanges[j + 1:]...) // Remove current value range.
                    j--
                }
            }
            valueRangesNext = append(valueRangesNext, valueRanges...) // Copy leftovers.
            valueRanges = valueRangesNext
        }
        locationRanges = append(locationRanges, valueRanges)
    }
    locationMin := -1
    for _, ranges := range locationRanges {
        for _, locationRange := range ranges {
            if locationMin == -1 || locationRange[0] < locationMin {
                locationMin = locationRange[0]
            }
        }
    }
    return locationMin
}

func d06_WaitForIt(lines []string, part int) int {
    var times []int
    var distances []int
    if part == 1 {
        spaces := regexp.MustCompile(" +")
        for _, timeS := range spaces.Split(lines[0], -1)[1:] {
            t, _ := strconv.Atoi(timeS)
            times = append(times, t)
        }
        for _, distanceS := range spaces.Split(lines[1], -1)[1:] {
            d, _ := strconv.Atoi(distanceS)
            distances = append(distances, d)
        }
    } else {
        t, _ := strconv.Atoi(strings.Split(strings.ReplaceAll(lines[0], " ", ""), ":")[1])
        times = append(times, t)
        d, _ := strconv.Atoi(strings.Split(strings.ReplaceAll(lines[1], " ", ""), ":")[1])
        distances = append(distances, d)
    }

    var beats []int
    for i, t := range times {
        d := distances[i]
        beat := -1
        left, right := 0, int(t / 2)
        for left <= right {
            pivot := int((left + right) / 2)
            dPivot := pivot * (t - pivot)
            dPivotMinusOne := (pivot - 1) * (t - (pivot - 1))
            if dPivot <= d {
                left = pivot + 1
            } else if (dPivotMinusOne > d) {
                right = pivot
            } else {
                beat = t - pivot - pivot + 1
                break
            }
        }
        if beat == -1 {
            panic(fmt.Sprintf("Unable to find number of winning ways for time/distance: %d / %d", t, d))
        }
        beats = append(beats, beat)
    }
    product := 1
    for _, beat := range beats {
        product *= beat
    }
    return product
}

func d07_CamelCards(lines []string, part int) int {
    const typeFiveOfAKind = 6
    const typeFourOfAKind = 5
    const typeFullHouse = 4
    const typeThreeOfAKind = 3
    const typeTwoPair = 2
    const typeOnePair = 1
    const typeHighCard = 0
    var cardOrder string
    if part == 1 {
        cardOrder = "AKQJT98765432" // 'J' == jack
    } else {
        cardOrder = "AKQT98765432J" // 'J' == joker
    }
    cardToValue := make(map[rune]int)
    for i, c := range cardOrder {
        cardToValue[c] = len(cardOrder) - i
    }

    var indices []int
    var hands [][]rune
    var bids []int
    for i, line := range lines {
        indices = append(indices, i)
        handBid := strings.Split(line, " ")
        hand := []rune(handBid[0])
        hands = append(hands, hand)
        bid, _ := strconv.Atoi(handBid[1])
        bids = append(bids, bid)
    }

    var handTypes []int
    for _, hand := range hands {
        // Count cards.
        cardToCount := make(map[rune]int)
        for _, c := range hand {
            if _, ok := cardToCount[c]; !ok {
                cardToCount[c] = 0
            }
            cardToCount[c]++
        }
        // Jokers wild.
        if part == 2 {
            if jCount, ok := cardToCount['J']; ok {
                if jCount == 5 { // Five jokers -> five aces
                    cardToCount = map[rune]int{'A': 5}
                } else { // Increase other highest card count.
                    cardMax := '-'
                    countMax := -1
                    for card, count := range cardToCount {
                        if card == 'J' {
                            continue
                        }
                        if countMax == -1 || count > countMax {
                            cardMax = card
                            countMax = count
                        }
                    }
                    cardToCount[cardMax] += jCount
                    cardToCount['J'] = 0
                }
            }
        }
        // Count counts.
        countToCards := make(map[int][]rune)
        for card, count := range cardToCount {
            if _, ok := countToCards[count]; !ok {
                countToCards[count] = make([]rune, 0)
            }
            countToCards[count] = append(countToCards[count], card)
        }
        // Determine hand strength.
        _, ok5 := countToCards[5]
        _, ok4 := countToCards[4]
        _, ok3 := countToCards[3]
        cards2, ok2 := countToCards[2]
        handType := typeHighCard
        if ok5 {
            handType = typeFiveOfAKind
        } else if ok4 {
            handType = typeFourOfAKind
        } else if ok3 && ok2 {
            handType = typeFullHouse
        } else if ok3 {
            handType = typeThreeOfAKind
        } else if ok2 && len(cards2) > 1 {
            handType = typeTwoPair
        } else if ok2 {
            handType = typeOnePair
        }
        handTypes = append(handTypes, handType)
    }

    sort.Slice(indices, func(i, j int) bool {
        ii := indices[i]
        ji := indices[j]
        if handTypes[ii] != handTypes[ji] {
            return handTypes[ii] > handTypes[ji]
        }
        for pos := 0; pos < 5; pos++ {
            if hands[ii][pos] != hands[ji][pos] {
                return cardToValue[hands[ii][pos]] > cardToValue[hands[ji][pos]]
            }
        }
        return false
    })

    totalWinnings := 0
    for rankInverse, index := range indices {
        rank := len(indices) - rankInverse
        totalWinnings += rank * bids[index]
    }
    return totalWinnings
}

func d08_HauntedWasteland(lines []string, part int) int {
    var instructions []rune
    for _, c := range lines[0] {
        if c != 'R' && c != 'L' {
            panic(fmt.Sprintf("Unexpected instructions: %s", lines[0]))
        }
        instructions = append(instructions, c)
    }
    network := make(map[string][]string)
    for _, line := range lines[2:] {
        nodeLeftRight := strings.Split(line, " = ")
        node := nodeLeftRight[0]
        leftRight := strings.Split(strings.Trim(strings.Trim(nodeLeftRight[1], "("), ")"), ", ")
        network[node] = leftRight
    }

    if part == 1 {
        node := "AAA"
        step := 0
        index := 0
        for node != "ZZZ" {
            c := instructions[index]
            if c == 'R' {
                node = network[node][1]
            } else {
                node = network[node][0]
            }
            index = (index + 1) % len(instructions)
            step++
        }
        return step
    }

    // Determine starting nodes.
    var nodes []string
    nodeToLast := make(map[string]rune)
    for node, _ := range network {
        var last rune
        for _, c := range node {
            last = c
        }
        if last == 'A' {
            nodes = append(nodes, node)
        }
        nodeToLast[node] = last
    }

    // Find loops.
    type NodeIndex struct {
        node string
        index int
    }
    nodeToZSteps := make(map[string][]int) // Possible end points in loop.
    nodeToLoopStart := make(map[string]int) // Number of steps to enter loop.
    nodeToLoopEnd := make(map[string]int) // Number of steps to re-enter loop.
    for _, node := range nodes {
        nodeToZSteps[node] = make([]int, 0)
        nodeIndexToStep := make(map[NodeIndex]int)
        nodeIndexToStep[NodeIndex{node, 0}] = 0
        nodeCurrent := node
        step := 0
        index := 0
        for true {
            c := instructions[index]
            if c == 'R' {
                nodeCurrent = network[nodeCurrent][1]
            } else {
                nodeCurrent = network[nodeCurrent][0]
            }
            index = (index + 1) % len(instructions)
            step++
            if nodeToLast[nodeCurrent] == 'Z' {
                nodeToZSteps[node] = append(nodeToZSteps[node], step)
            }
            nodeIndex := NodeIndex{nodeCurrent, index}
            if cacheStep, ok := nodeIndexToStep[nodeIndex]; ok {
                nodeToLoopStart[node] = cacheStep
                nodeToLoopEnd[node] = step
                break
            }
            nodeIndexToStep[nodeIndex] = step
        }
    }

    passThroughOrigin := true
    for _, node := range nodes {
        if len(nodeToZSteps[node]) != 1 || nodeToLoopEnd[node] - nodeToLoopStart[node] != nodeToZSteps[node][0] {
            passThroughOrigin = false
            break
        }
    }
    if passThroughOrigin {
        // A special case where the number of steps to reach the only terminal state
        // in each loop is exactly the same as the length of the loop.
        var factors []int
        for _, node := range nodes {
            factors = append(factors, nodeToZSteps[node][0])
        }
        return cs.LCM(factors...)
    }
    // We're not asked to solve general case.
    return 1
}

func d09_MirageMaintenance(lines []string, part int) int {
    var histories [][]int
    for _, line := range lines {
        var history []int
        for _, s := range strings.Split(line, " ") {
            x, _ := strconv.Atoi(s)
            history = append(history, x)
        }
        histories = append(histories, history)
    }

    sumNext := 0
    for _, history := range histories {
        sequences := [][]int{history}
        for true {
            hasNonZero := false
            var sequenceNext []int
            sequence := sequences[len(sequences) - 1]
            for j := 1; j < len(sequence); j++ {
                x := sequence[j] - sequence[j - 1]
                if x != 0 {
                    hasNonZero = true
                }
                sequenceNext = append(sequenceNext, x)
            }
            if hasNonZero {
                sequences = append(sequences, sequenceNext)
            } else {
                xNext := 0
                for i := len(sequences) - 1; i >= 0; i-- {
                    if part == 1 {
                        xNext += sequences[i][len(sequences[i]) - 1]
                    } else {
                        xNext = sequences[i][0] - xNext
                    }
                }
                sumNext += xNext
                break
            }
        }
    }
    return sumNext
}

func d10_PipeMaze(lines []string, part int) int {
    var maze [][]rune
    var startPoint []int = nil
    for i, line := range lines {
        var row []rune
        for j, c := range line {
            if c == 'S' {
                if startPoint != nil {
                    panic(fmt.Sprintf("Multiple start points found: (%d, %d); (%d, %d)", startPoint[0], startPoint[1], i, j))
                }
                startPoint = []int{i, j}
            }
            row = append(row, c)
        }
        maze = append(maze, row)
    }
    if startPoint == nil {
        panic(fmt.Sprintf("No start point found."))
    }

    pipeDirections := []map[rune]int{
        {'7': DIRECTION_LEFT, '|': DIRECTION_UP, 'F': DIRECTION_RIGHT},
        {'J': DIRECTION_UP, '-': DIRECTION_RIGHT, '7': DIRECTION_DOWN},
        {'L': DIRECTION_RIGHT, '|': DIRECTION_DOWN, 'J': DIRECTION_LEFT},
        {'F': DIRECTION_DOWN, '-': DIRECTION_LEFT, 'L': DIRECTION_UP},
    }
    directionsPipe := [][]rune{
        {'|', 'L', '|', 'J'},
        {'L', '-', 'F', '-'},
        {'|', 'F', '|', '7'},
        {'J', '-', '7', '-'},
    }

    var startDirections []int
    for direction, delta := range DELTAS {
        point := []int{startPoint[0] + delta[0], startPoint[1] + delta[1]}
        if point[0] < 0 || point[0] >= len(lines) || point[1] < 0 || point[1] >= len(lines[point[0]]) {
            continue
        }
        c := maze[point[0]][point[1]]
        if _, ok := pipeDirections[direction][c]; ok {
            startDirections = append(startDirections, direction)
        }
    }

    for _, startDirection := range startDirections {
        var path [][]int
        point := []int{startPoint[0], startPoint[1]}
        direction := startDirection
        circuit := true
        step := 0
        for step == 0 || point[0] != startPoint[0] || point[1] != startPoint[1] {
            path = append(path, point)
            delta := DELTAS[direction]
            pointNext := []int{point[0] + delta[0], point[1] + delta[1]}
            c := maze[pointNext[0]][pointNext[1]]
            if c != 'S' {
                directionNext, ok := pipeDirections[direction][c]
                if !ok {
                    circuit = false
                    break
                }
                direction = directionNext
            }
            point = pointNext
            step++
        }
        if !circuit {
            continue
        }
        if part == 1 {
            return int(step / 2)
        }

        // Find total area of enclosures.
        maze[startPoint[0]][startPoint[1]] = directionsPipe[startDirection][(direction + 2) % 4] // Replace 'S'.
        pipes := make([][]bool, len(maze))
        for i := 0; i < len(pipes); i++ {
            pipes[i] = make([]bool, len(maze[i]))
        }
        for _, point := range path {
            pipes[point[0]][point[1]] = true
        }
        area := 0
        for i := 1; i < len(maze) - 1; i++ {
            inside := false // Flag that flips value when crossing a wall.
            above := false // State flag used when following pipe path.
            for j := 0; j < len(maze[i]) - 1; j++ {
                if pipes[i][j] {
                    c := maze[i][j]
                    if c == '|' {
                        inside = !inside
                    } else if c == 'F' {
                        above = true
                    } else if c == 'L' {
                        above = false
                    } else if c == 'J' {
                        if above {
                            inside = !inside
                        }
                    } else if c == '7' {
                        if !above {
                            inside = !inside
                        }
                    }
                } else if inside {
                    area++
                }
            }
        }
        return area
    }
    panic(fmt.Sprintf("Unable to find circuit."))
}

func d11_CosmicExpansion(lines []string, part int) int {
    var image [][]rune
    var galaxies [][]int
    rowsHaveGalaxy := make([]bool, len(lines))
    columnsHaveGalaxy := make([]bool, len(lines[0]))
    for i, line := range lines {
        if len(line) != len(lines[0]) {
            panic(fmt.Sprintf("Image is not a square, lines: 0, %d", i))
        }
        var row []rune
        for j, c := range line {
            if c == '#' {
                rowsHaveGalaxy[i] = true
                columnsHaveGalaxy[j] = true
                galaxy := []int{i, j}
                galaxies = append(galaxies, galaxy)
            }
            row = append(row, c)
        }
        image = append(image, row)
    }

    var expand int
    if part == 1 {
        expand = 2
    } else {
        expand = 1000000
    }
    rowDistances := _d11_distanceMatrix(rowsHaveGalaxy, expand)
    columnDistances := _d11_distanceMatrix(columnsHaveGalaxy, expand)

    sumDistance := 0
    for a := 0; a < len(galaxies) - 1; a++ {
        galaxyA := galaxies[a]
        for b := a + 1; b < len(galaxies); b++ {
            galaxyB := galaxies[b]
            sumDistance += rowDistances[galaxyA[0]][galaxyB[0]] + columnDistances[galaxyA[1]][galaxyB[1]]
        }
    }
    return sumDistance
}

func _d11_distanceMatrix(smallSteps []bool, dBig int) [][]int {
    distances := make([][]int, len(smallSteps))
    for i := 0; i < len(smallSteps); i++ {
        distances[i] = make([]int, len(smallSteps))
    }
    // Fill out adjacent distances (diagonal).
    for i := 1; i < len(distances); i++ {
        var d int
        if smallSteps[i] {
            d = 1
        } else {
            d = dBig
        }
        distances[i - 1][i] = d
        distances[i][i - 1] = d
    }
    // Fill out remaining pair-wise distances.
    for i := 0; i < len(distances) - 2; i++ {
        for di := i + 2; di < len(distances); di++ {
            d := distances[i][di - 1] + distances[di - 1][di]
            distances[i][di] = d
            distances[di][i] = d
        }
    }
    return distances
}

func d12_HotSprings(lines []string, part int) int {
    fold := 1
    if part != 1 {
        fold = 5
    }

    var rowSprings []string
    var rowGroups [][]int
    for _, line := range lines {
        springsGroups := strings.Split(line, " ")
        var springs strings.Builder
        var groups []int
        for f := 0; f < fold; f++ {
            if f > 0 {
                springs.WriteString("?")
            }
            springs.WriteString(springsGroups[0])
            for _, s := range strings.Split(springsGroups[1], ",") {
                group, _ := strconv.Atoi(s)
                groups = append(groups, group)
            }
        }
        rowSprings = append(rowSprings, springs.String())
        rowGroups = append(rowGroups, groups)
    }

    answer := 0
    for row, springs := range rowSprings {
        groups := rowGroups[row]
        fmt.Println(row, springs, groups)
        var sequence []rune
        for _, c := range springs {
            sequence = append(sequence, c)
        }
        kUnknownLeft := 0 // Index of groups.
        kUnknownRight := len(groups) - 1
        jUnknownLeft := 0 // Index of sequence.
        jUnknownRight := len(sequence) - 1

        // Expand filled cells from left.
        for jUnknownLeft <= jUnknownRight {
            c := sequence[jUnknownLeft]
            if c == '?' {
                break
            }
            if c == '#' {
                jEnd := jUnknownLeft + groups[kUnknownLeft] - 1
                for jUnknownLeft < jEnd {
                    jUnknownLeft++
                    sequence[jUnknownLeft] = '#'
                }
                if jUnknownLeft + 1 < len(sequence) {
                    jUnknownLeft++
                    sequence[jUnknownLeft] = '.'
                }
                kUnknownLeft++
            }
            jUnknownLeft++
        }

        // Expand filled cells from right.
        for jUnknownRight >= jUnknownLeft {
            c := sequence[jUnknownRight]
            if c == '?' {
                break
            }
            if c == '#' {
                jStart := jUnknownRight - groups[kUnknownRight] + 1
                for jUnknownRight > jStart {
                    jUnknownRight--
                    sequence[jUnknownRight] = '#'
                }
                if jUnknownRight - 1 >= 0 {
                    jUnknownRight--
                    sequence[jUnknownRight] = '.'
                }
                kUnknownRight--
            }
            jUnknownRight--
        }

        // Find subsequences of damaged springs (#) and union of damaged/unknown springs (#, ?).
        gapSpans := _d12_spans(sequence, jUnknownLeft, jUnknownRight, '#', '?')
        damagedSpans := _d12_spans(sequence, jUnknownLeft, jUnknownRight, '#')

        n := _d12_arrangements(sequence, groups[kUnknownLeft:kUnknownRight + 1], gapSpans, damagedSpans)
        answer += n
    }
    return answer
}

func _d12_spans(springs []rune, jStart, jEnd int, matches ...rune) [][]int {
    var spans [][]int // Closed interval.
    var start int = -1
    for j := jStart; j <= jEnd; j++ {
        c := springs[j]
        isMatch := false
        for _, match := range matches {
            if c == match {
                isMatch = true
                break
            }
        }
        if isMatch {
            if start == -1 {
                start = j
            }
        } else {
            if start != -1 {
                spans = append(spans, []int{start, j - 1})
            }
            start = -1
        }
    }
    if start != -1 {
        spans = append(spans, []int{start, jEnd})
    }
    return spans
}

func _d12_arrangements(sequence []rune, groups []int, gapSpans [][]int, damagedSpans [][]int) int {
    if len(groups) == 0 { // All groups are in gaps.
        if len(damagedSpans) == 0 { // All damaged cells are accounted for.
            return 1
        }
        return 0
    }
    if len(gapSpans) == 0 {
        return 0
    }
    n := 0
    g := 0 // Gap index.
    for j := gapSpans[0][0]; j + groups[0] - 1 <= gapSpans[len(gapSpans) - 1][1]; j++ {
        for j + groups[0] - 1 > gapSpans[g][1] {
            g++
            if g >= len(gapSpans) {
                return n
            }
            j = gapSpans[g][0]
        }
        if len(damagedSpans) > 0 && j > damagedSpans[0][0] {
            break // Can't move past first damaged cell.
        }
        if j + groups[0] < len(sequence) && sequence[j + groups[0]] == '#' {
            continue // Can't be placed immediately to the left of a damaged cell.
        }
        var gapSpansNext [][]int
        if j + groups[0] + 1 <= gapSpans[g][1] {
            // Modify start of first gap span.
            gapSpansNext = append([][]int{{j + groups[0] + 1, gapSpans[g][1]}}, gapSpans[g + 1:]...)
        } else {
            gapSpansNext = gapSpans[g + 1:]
        }
        d := 0 // Number of damaged spans covered.
        for d < len(damagedSpans) && j + groups[0] - 1 >= damagedSpans[d][1] {
            d++
        }
        n += _d12_arrangements(sequence, groups[1:], gapSpansNext, damagedSpans[d:])
    }
    return n
}

func d13_PointOfIncidence(lines []string, part int) int {
    var patterns [][][]rune
    var pattern [][]rune
    for _, line := range lines {
        if len(line) == 0 {
            patterns = append(patterns, pattern)
            pattern = nil
            continue
        }
        var row []rune
        for _, c := range line {
            row = append(row, c)
        }
        pattern = append(pattern, row)
    }
    patterns = append(patterns, pattern)

    answer := 0
    for p, pattern := range patterns {
        // Check horizontal reflection.
        iHorizontal := -1
        for i := 0; i < len(pattern) - 1; i++ {
            smudge := 0
            for di := 0; i - di >= 0 && i + 1 + di < len(pattern); di++ {
                for j := 0; j < len(pattern[i]); j++ {
                    if pattern[i - di][j] != pattern[i + 1 + di][j] {
                        smudge++
                    }
                }
            }
            if (part == 1 && smudge == 0) || (part != 1 && smudge == 1) {
                iHorizontal = i
                break
            }
        }
        if iHorizontal != -1 {
            answer += 100 * (iHorizontal + 1)
            continue
        }

        // Check vertical reflection.
        jVertical := -1
        for j := 0; j < len(pattern[0]) - 1; j++ {
            smudge := 0
            for dj := 0; j - dj >= 0 && j + 1 + dj < len(pattern[0]); dj++ {
                for i := 0; i < len(pattern); i++ {
                    if pattern[i][j - dj] != pattern[i][j + 1 + dj] {
                        smudge++
                    }
                }
            }
            if (part == 1 && smudge == 0) || (part != 1 && smudge == 1) {
                jVertical = j
                break
            }
        }
        if jVertical != -1 {
            answer += jVertical + 1
            continue
        }

        panic(fmt.Sprintf("Unable to find reflection for pattern index: %d", p))
    }
    return answer
}

func d14_ParabolicReflectorDish(lines []string, part int) int {
    var platform [][]rune
    for _, line := range lines {
        platform = append(platform, []rune(line))
    }

    if part == 1 {
        load := 0
        for j := 0; j < len(platform[0]); j++ {
            for i := 0; i < len(platform); i++ {
                c := platform[i][j]
                if c == 'O' {
                    di := i
                    for di - 1 >= 0 && platform[di - 1][j] == '.' {
                        di--
                    }
                    platform[i][j] = '.' // Order of this and below matters.
                    platform[di][j] = 'O'
                    load += len(platform) - di
                }
            }
        }
        return load
    }

    tilesToCycle := make(map[string]int)
    skipped := false
    cycle := 0
    const cycles = 1000000000
    for cycle < cycles {
        // Tilt north.
        for j := 0; j < len(platform[0]); j++ {
            for i := 0; i < len(platform); i++ {
                c := platform[i][j]
                if c == 'O' {
                    di := i
                    for di - 1 >= 0 && platform[di - 1][j] == '.' {
                        di--
                    }
                    platform[i][j] = '.' // Order of this and below matters in case i == di.
                    platform[di][j] = 'O'
                }
            }
        }
        // Tilt west.
        for i := 0; i < len(platform); i++ {
            for j := 0; j < len(platform[i]); j++ {
                c := platform[i][j]
                if c == 'O' {
                    dj := j
                    for dj - 1 >= 0 && platform[i][dj - 1] == '.' {
                        dj--
                    }
                    platform[i][j] = '.'
                    platform[i][dj] = 'O'
                }
            }
        }
        // Tilt south.
        for j := 0; j < len(platform[0]); j++ {
            for i := len(platform) - 1; i >= 0; i-- {
                c := platform[i][j]
                if c == 'O' {
                    di := i
                    for di + 1 < len(platform) && platform[di + 1][j] == '.' {
                        di++
                    }
                    platform[i][j] = '.' // Order of this and below matters.
                    platform[di][j] = 'O'
                }
            }
        }
        // Tilt east.
        for i := 0; i < len(platform); i++ {
            for j := len(platform[i]) - 1; j >= 0; j-- {
                c := platform[i][j]
                if c == 'O' {
                    dj := j
                    for dj + 1 < len(platform[i]) && platform[i][dj + 1] == '.' {
                        dj++
                    }
                    platform[i][j] = '.'
                    platform[i][dj] = 'O'
                }
            }
        }
        cycle++

        if !skipped {
            var tilesSlice []rune
            for _, row := range platform {
                tilesSlice = append(tilesSlice, row...)
            }
            tiles := string(tilesSlice)
            cycleCache, ok := tilesToCycle[tiles]
            if !ok {
                tilesToCycle[tiles] = cycle
            } else {
                interval := cycle - cycleCache
                repeat := (cycles - cycle) / interval
                cycle += repeat * interval
                skipped = true
            }
        }
    }

    load := 0
    for i := 0; i < len(platform); i++ {
        for j := 0; j < len(platform[i]); j++ {
            c := platform[i][j]
            if c == 'O' {
                load += len(platform) - i
            }
        }
    }
    return load
}

func d15_LensLibrary(lines []string, part int) int {
    steps := strings.Split(lines[0], ",")

    if part == 1 {
        answer := 0
        for _, step := range steps {
            answer += _d15_hash(step)
        }
        return answer
    }

    type Lens struct {
        Label string
        Focal int
    }
    boxes := make([][]Lens, 256)
    for _, step := range steps {
        parts := strings.Split(step, "=")
        label := ""
        focal := -1
        if len(parts) == 2 {
            label = parts[0]
            focal, _ = strconv.Atoi(parts[1])
        } else {
            label = strings.Split(step, "-")[0]
        }
        h := _d15_hash(label)
        found := false
        for j := 0; j < len(boxes[h]); j++ {
            if boxes[h][j].Label == label {
                if focal != -1 {
                    boxes[h][j].Focal = focal
                } else {
                    boxes[h] = append(boxes[h][:j], boxes[h][j + 1:]...)
                }
                found = true
                break
            }
        }
        if !found && focal != -1 {
            boxes[h] = append(boxes[h], Lens{label, focal})
        }
    }

    power := 0
    for i, box := range boxes {
        for j, lens := range box {
            power += (i + 1) * (j + 1) * lens.Focal
        }
    }
    return power
}

func _d15_hash(s string) int {
    x := 0
    for _, c := range s {
        x = ((x + int(c)) * 17) % 256
    }
    return x
}

func d16_TheFloorWillBeLava(lines []string, part int) int {
    var grid [][]rune
    for _, line := range lines {
        grid = append(grid, []rune(line))
    }

    if part == 1 {
        return _d16_energized(grid, 0, 0, 1)
    }

    energizedMax := -1
    for i := 0; i < len(grid); i++ {
        energizedMax = max(energizedMax, _d16_energized(grid, i, 0, 1))
        energizedMax = max(energizedMax, _d16_energized(grid, i, len(grid) - 1, 3))
        energizedMax = max(energizedMax, _d16_energized(grid, 0, i, 2))
        energizedMax = max(energizedMax, _d16_energized(grid, len(grid) - 1, 0, 0))
    }
    return energizedMax
}

func _d16_energized(grid [][]rune, iStart, jStart, directionStart int) int {
    cToDirections := map[rune][][]int{
        '.': {{0}, {1}, {2}, {3}},
        '/': {{1}, {0}, {3}, {2}},
        '\\': {{3}, {2}, {1}, {0}},
        '|': {{0}, {0, 2}, {2}, {0, 2}},
        '-': {{1, 3}, {1}, {1, 3}, {3}},
    }
    
    gridBeams := make([][][]int, len(grid))
    for i := 0; i < len(gridBeams); i++ {
        gridBeams[i] = make([][]int, len(grid[i]))
    }
    var q []Point3
    for _, direction := range cToDirections[grid[iStart][jStart]][directionStart] {
        q = append(q, Point3{iStart, jStart, direction})
    }
    for len(q) > 0 {
        pointBeam := q[0]
        q = q[1:]
        i, j, direction := pointBeam.I, pointBeam.J, pointBeam.K
        for !slices.Contains(gridBeams[i][j], direction) {
            gridBeams[i][j] = append(gridBeams[i][j], direction)
            delta := DELTAS[direction]
            i, j = i + delta[0], j + delta[1]
            if i < 0 || i >= len(grid) || j < 0 || j >= len(grid[i]) {
                break // Out of bounds.
            }
            c := grid[i][j]
            directions := cToDirections[c][direction]
            if len(directions) > 1 {
                q = append(q, Point3{i, j, directions[1]})
            }
            direction = directions[0]
        }
    }

    energized := 0
    for i := 0; i < len(gridBeams); i++ {
        for j := 0; j < len(gridBeams[i]); j++ {
            if len(gridBeams[i][j]) > 0 {
                energized++
            }
        }
    }
    return energized
}

func d17_ClumsyCrucible(lines []string, part int) int {
    var lossMap [][]int
    for _, line := range lines {
        var row []int
        for _, c := range line {
            row = append(row, int(c - '0'))
        }
        lossMap = append(lossMap, row)
    }

    stepsMin, stepsMax := 1, 3
    if part != 1 {
        stepsMin, stepsMax = 4, 10
    }

    // Lazy Dijkstra.
    lossToPointHistories := make(map[int][][]Point4)
    // No steps taken in any direction; invalid back pointer.
    lossToPointHistories[0] = [][]Point4{{Point4{0, 0, -1, 0}, Point4{-1, -1, -1, -1}}}
    pointHistoryToLoss := make(map[Point4]int)
    backtrace := make(map[Point4]Point4)
    lossCurrent := -1
    for true {
        lossCurrent++
        if _, ok := lossToPointHistories[lossCurrent]; !ok {
            continue
        }
        for _, pointHistories := range lossToPointHistories[lossCurrent] {
            pointHistory := pointHistories[0]
            if _, ok := pointHistoryToLoss[pointHistory]; ok {
                continue // Already minimized.
            }
            pointHistoryToLoss[pointHistory] = lossCurrent
            // Fill backtrace.
            pointHistoryPrev := pointHistories[1]
            if pointHistoryPrev.I != -1 {
                backtrace[pointHistory] = pointHistoryPrev
            }
            // Check for termination.
            if pointHistory.I == len(lossMap) - 1 && pointHistory.J == len(lossMap[pointHistory.I]) - 1 && pointHistory.L >= stepsMin {
                var path []Point4
                pointHistoryReverse, ok := pointHistory, true
                for ok {
                    path = append([]Point4{pointHistoryReverse}, path...)
                    pointHistoryReverse, ok = backtrace[pointHistoryReverse]
                }
                lossTotal := -lossMap[0][0]
                for i, point := range path {
                    lossTotal += lossMap[point.I][point.J]
                    fmt.Println(i, point, lossTotal)
                }
                return lossCurrent
            }
            // Check every direction.
            for direction, delta := range DELTAS {
                if pointHistory.K != -1 && direction == (pointHistory.K + 2) % len(DELTAS) {
                    continue // Can't reverse direction.
                }
                i, j := pointHistory.I + delta[0], pointHistory.J + delta[1]
                if i < 0 || i >= len(lossMap) || j < 0 || j >= len(lossMap[i]) {
                    continue // Out of bounds.
                }
                steps := 1
                if direction == pointHistory.K {
                    if pointHistory.L == stepsMax {
                        continue // Can't travel in same direction more than three steps.
                    }
                    steps = pointHistory.L + 1
                } else if pointHistory.K != -1 && pointHistory.L < stepsMin {
                    continue
                }
                loss := lossCurrent + lossMap[i][j]
                pointHistoryNext := Point4{i, j, direction, steps}
                lossToPointHistories[loss] = append(lossToPointHistories[loss], []Point4{pointHistoryNext, pointHistory})
            }
        }
    }
    return 1
}

func d18_LavaductLagoon(lines []string, part int) int {
    nameToDirection := map[string]int{"U": DIRECTION_UP, "R": DIRECTION_RIGHT, "D": DIRECTION_DOWN, "L": DIRECTION_LEFT}
    var directions []int
    var lengths []int
    for _, line := range lines {
        tokens := strings.Split(line, " ")
        if part == 1 {
            directions = append(directions, nameToDirection[tokens[0]])
            length, _ := strconv.Atoi(tokens[1])
            lengths = append(lengths, length)
        } else {
            color := []rune(tokens[2][2:8])
            directions = append(directions, int(color[5] - '0'))
            length := 0
            for i := 0; i < 5; i++ {
                length *= 16
                if color[i] >= '0' && color[i] <= '9' {
                    length += int(color[i] - '0')
                } else {
                    length += int(10 + color[i] - 'a')
                }
            }
            lengths = append(lengths, length)
        }
    }

    // TODO: Time: 1766.420956 s
    pointToHole := make(map[Point]rune)
    iToHoles := make(map[int][]int)
    iMin, iMax := 0, 0
    i, j := 0, 0
    for index, direction := range directions {
        length := lengths[index]
        delta := DELTAS[direction]
        if delta[0] != 0 {
            for d := 0; d < length; d++ {
                i += delta[0]
                iMin = min(iMin, i)
                iMax = max(iMax, i)
                pointToHole[Point{i, j}] = '#'
                iToHoles[i] = append(iToHoles[i], j)
            }
        } else {
            j += delta[1] * length
            pointToHole[Point{i, j}] = '#'
            iToHoles[i] = append(iToHoles[i], j)
        }
    }

    holes := 0
    stateAbove := -1
    stateNeither := 0
    stateBelow := 1
    for i = iMin; i <= iMax; i++ {
        slices.Sort(iToHoles[i])
        var jLast int
        hasJLast := false
        inside := false
        state := stateNeither
        for _, j := range iToHoles[i] {
            if !hasJLast {
                holes++
                hasJLast = true
            } else if inside || state != stateNeither {
                holes += j - jLast
            } else {
                holes++
            }
            jLast = j

            _, okAbove := pointToHole[Point{i - 1, j}]
            _, okBelow := pointToHole[Point{i + 1, j}]
            if okAbove && okBelow {
                // Straight through a vertical wall.
                inside = !inside
            } else if okAbove {
                // A wall above, but not below.
                if state == stateNeither {
                    state = stateBelow
                } else {
                    if state == stateAbove {
                        inside = !inside
                    }
                    state = stateNeither
                }
            } else {
                // A wall below, but not above.
                if state == stateNeither {
                    state = stateAbove
                } else {
                    if state == stateBelow {
                        inside = !inside
                    }
                    state = stateNeither
                }
            }
        }
    }
    return holes
}