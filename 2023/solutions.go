package main

import (
    "fmt"
    "os"
    "strconv"

    "ericrobertbrewer.com/aoc/aoc"
)

const YEAR = 2023

func main() {
    if len(os.Args) < 3 {
        panic(fmt.Sprintf("Usage: go run %d/solutions.go <day> <part>", YEAR))
    }
    day, _ := strconv.Atoi(os.Args[1])
    part, _ := strconv.Atoi(os.Args[2])

    path := aoc.DownloadInputIfNeeded(YEAR, day)
    fmt.Printf("Hello, <%d> <%d>, %s.\n", day, part, path)
}