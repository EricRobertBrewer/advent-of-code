package main

import (
    "fmt"
    "os"
    "strconv"
    "time"

    "ericrobertbrewer.com/aoc/aoc"
)

const YEAR = 2023

type Solver func(lines []string, part int) int

var SOLVERS = map[int]Solver {
    1: d01_,
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
    fmt.Printf("%.3f s\n", duration.Seconds())
}

func solve(day int, part int, lines []string) int {
    if solver, ok := SOLVERS[day]; ok {
        return solver(lines, part)
    }
    panic(fmt.Sprintf("No solution found for day: %d", day))
}

func d01_(lines []string, part int) int {
    fmt.Println(len(lines))
//     for i, line := range lines {
//         fmt.Println(i, line)
//     }
    return 1
}
