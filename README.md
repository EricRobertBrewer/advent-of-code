# advent-of-code

[Advent of Code](https://adventofcode.com/) solutions.

## Run

Log in to [Advent of Code](https://adventofcode) in a browser.

Copy and export the session token from *Dev Tools* -> *Application* -> *Cookies* in the browser window.

```shell
export AOC_SESSION=<value-of-session-cookie-in-browser>
```

### 2015 (C)

```shell
make -C 2015
./2015/solutions.x <day> <part>
```

### 2016 (C++)

```shell
make -C 2016
./2016/solutions.x <day> <part>
```

### 2017 (Java)

```shell
javac -sourcepath 2017/src -d 2017/bin 2017/src/com/ericrobertbrewer/adventofcode/*.java
java -cp 2017/bin com.ericrobertbrewer.adventofcode.Solutions <day> <part>
```

### 2018 (JavaScript/Node.js)

```shell
node 2018/solutions.js <day> <part>
```

### 2019 ([Kotlin](https://kotlinlang.org/docs/command-line.html))

```shell
kotlin 2019/solutions/Day##_XXXX.kts <part>
```

### 2020 (C#/.NET)

```shell
dotnet script 2020/Solutions/Day##_XXXX.csx <part>
```

### 2022 (Python)

```shell
python 2022/solutions.py <day> <part>
```

### 2023 (Go)

```shell
cd 2023
go run solutions.go <day> <part>
```
