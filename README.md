# advent-of-code

[Advent of Code](https://adventofcode.com/) solutions.

## Run

Log in to [Advent of Code](https://adventofcode) in a browser.

Copy and export the session token from *Dev Tools* -> *Application* -> *Cookies* in the browser window.

```commandline
export AOC_SESSION=<value-of-session-cookie-in-browser>
```

### 2015 (C)

```commandline
make -C 2015
./2015/solutions.x <day> <part>
```

### 2016 (C++)

```commandline
make -C 2016
./2016/solutions.x <day> <part>
```

### 2017 (Java)

```commandline
javac -sourcepath 2017/src -d 2017/bin 2017/src/com/ericrobertbrewer/adventofcode/*.java
java -cp 2017/bin com.ericrobertbrewer.adventofcode.Solutions <day> <part>
```

### 2018 (JavaScript/Node.js)

```commandline
node 2018/solutions.js <day> <part>
```

### 2019 ([Kotlin](https://kotlinlang.org/docs/command-line.html))

```commandline
kotlin 2019/solutions/Day##_XXXX.kts <part>
```

### 2020 (C#/.NET)

```commandline
dotnet script 2020/Solutions/Day##_XXXX.csx <part>
```

### 2021 (Haskell)

```commandline
cd 2021
runghc Day##_XXXX.hs <part>
```

### 2022 (Python)

```commandline
python 2022/solutions.py <day> <part>
```

### 2023 (Go)

```commandline
cd 2023
go run solutions.go <day> <part>
```

### 2024 ~~(Haskell)~~ _I give up_ -> (Python)

```commandline
cd 2024
runghc Day01_HistorianHysteria.hs 1
```

```commandline
cd 2024
python <script> <part> [--example|-x]
```
