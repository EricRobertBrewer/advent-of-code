package aoc

import (
    "fmt"
    "io"
    "net/http"
    "os"
    "path/filepath"
    "strconv"
)

const INPUT_DIR = "../input/"
const ENV_VAR = "AOC_SESSION"

func DownloadInputIfNeeded(year int, day int) string {
    yearDir := filepath.Join(INPUT_DIR, strconv.Itoa(year))
    err := os.MkdirAll(yearDir, 0755)
    if err != nil {
        panic(err)
    }
    dayPath := filepath.Join(yearDir, fmt.Sprintf("%02d.txt", day))
    dayFile, err := os.Open(dayPath)
    if err != nil {
        token, present := os.LookupEnv(ENV_VAR)
        if !present {
            panic(fmt.Sprintf("Set the environment variable '%s' as your AoC session token. " +
                "(Found in Dev Tools > Application > Cookies > 'session')\n" +
                "export %s=<my_token>", ENV_VAR, ENV_VAR))
        }
        client := &http.Client{}
        req, err := http.NewRequest("GET", fmt.Sprintf("https://adventofcode.com/%d/day/%d/input", year, day), nil)
        if err != nil {
            panic(err)
        }
        req.Header.Add("cookie", fmt.Sprintf("session=%s", token))
        resp, err := client.Do(req)
        if err != nil {
            panic(err)
        }
        body, err := io.ReadAll(resp.Body)
        if err != nil {
            panic(err)
        }
        resp.Body.Close()
        dayFile, err := os.Create(dayPath)
        dayFile.Write(body)
    }
    dayFile.Close()
    return dayPath
}

func ExamplePath(year int, day int) string {
    yearDir := filepath.Join(INPUT_DIR, strconv.Itoa(year))
    return filepath.Join(yearDir, fmt.Sprintf("%02d-example.txt", day))
}