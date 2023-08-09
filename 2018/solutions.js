#!/usr/local/bin node

const fs = require("fs")

const aocUtil = require("./aocUtil.js")

const YEAR = "2018";
const SOLUTIONS = {
    1: d01_ChronalCalibration,
};

function main() {
    const args = process.argv.slice(2);
    if (args.length < 2) {
        return console.error("Usage: % node solutions.js <day> <part> [--example|-x]");
    }
    const day = parseInt(args[0]);
    const part = parseInt(args[1]);
    const example = args.length > 2 && (args[2] === "--example" || args[2] === "-x");

    const callback = (fd) => {
        solve(day, part, fd);
    };
    if (!example) {
        aocUtil.getOrDownloadInputFile(YEAR, day, callback);
    } else {
        aocUtil.getExampleFile(YEAR, day, callback);
    }
}

function solve(day, part, fd) {
    const start = Date.now();

    const lines = fs.readFileSync(fd, "utf8").split("\n");
    fs.close(fd);

    const solution = SOLUTIONS[day];
    if (solution === undefined) {
        throw new Error("No solution for day: " + day);
    }
    const answer = solution(lines, part);
    console.log(answer);

    const duration = Date.now() - start;
    console.log("Time: " + duration + " ms");
}

function d01_ChronalCalibration(lines, part) {
    let frequency = 0;
    const frequencies = new Set([0]);
    while (true) {
        for (let i = 0; i < lines.length; i++) {
            const line = lines[i];
            if (line.length > 0) {
                frequency += parseInt(line);
                if (part === 2 && frequencies.has(frequency)) {
                    return frequency;
                }
                frequencies.add(frequency);
            }
        }
        if (part === 1) {
            return frequency;
        }
    }
}

if (require.main === module) {
    main();
}
