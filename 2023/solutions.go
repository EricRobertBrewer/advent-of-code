package main

import (
    "fmt"
    "os"
    "strconv"
    "strings"
    "time"

    "ericrobertbrewer.com/aoc/aoc"
)

const YEAR = 2023

type Solver func(lines []string, part int) int

var SOLVERS = map[int]Solver {
    1: d01_Trebuchet,
    2: d02_CubeConundrum,
    3: d03_GearRatios,
}

type Point struct {
    I, J int
}

type PointS struct {
    I, J int
    S string
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
    duration := time.Now().Sub(start)
    fmt.Println(answer)
    fmt.Printf("Time: %.3f s\n", duration.Seconds())
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
