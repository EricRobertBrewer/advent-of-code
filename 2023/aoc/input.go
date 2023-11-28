package aoc

import (
    "bufio"
    "fmt"
    "io"
    "net/http"
    "os"
    "path/filepath"
    "strconv"
)

const INPUT_DIR = "../input/"
const ENV_VAR = "AOC_SESSION"

func DownloadInputIfNeeded(year int, day int) []string {
    yearDir := filepath.Join(INPUT_DIR, strconv.Itoa(year))
    os.MkdirAll(yearDir, 0755)
    dayPath := filepath.Join(yearDir, fmt.Sprintf("%02d.txt", day))
    dayFile, err := os.Open(dayPath)
    defer dayFile.Close()
    if err != nil {
        token, present := os.LookupEnv(ENV_VAR)
        if !present {
            panic(fmt.Sprintf("Set the environment variable '%s' as your AoC session token. " +
                "(Found in Dev Tools > Application > Cookies > 'session')\n" +
                "export %s=<my_token>", ENV_VAR, ENV_VAR))
        }
        client := &http.Client{}
        req, _ := http.NewRequest("GET", fmt.Sprintf("https://adventofcode.com/%d/day/%d/input", year - 1, day), nil)
        req.Header.Add("cookie", fmt.Sprintf("session=%s", token))
        resp, err := client.Do(req)
        if err != nil {
            panic(err)
        }
        body, _ := io.ReadAll(resp.Body) // Should this be buffered?
        resp.Body.Close()
        dayFile, _ = os.Create(dayPath)
        _, err = dayFile.Write(body)
        if err != nil {
            panic(err)
        }
        dayFile.Close()
        dayFile, _ = os.Open(dayPath)
    }
    return readLines(dayFile)
}

func ExampleInput(year int, day int) []string {
    yearDir := filepath.Join(INPUT_DIR, strconv.Itoa(year))
    path := filepath.Join(yearDir, fmt.Sprintf("%02d-example.txt", day))
    file, err := os.Open(path)
    if err != nil {
        panic(err)
    }
    defer file.Close()
    return readLines(file)
}

func readLines(file *os.File) []string {
    scanner := bufio.NewScanner(file)
    scanner.Split(bufio.ScanLines)
    var lines []string
    for scanner.Scan() {
        lines = append(lines, scanner.Text())
    }
    return lines
}
