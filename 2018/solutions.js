#!/usr/local/bin node

const fs = require("fs")

const aocUtil = require("./aocUtil.js")

const YEAR = "2018";
const SOLUTIONS = {
    1: d01_ChronalCalibration,
    2: d02_InventoryManagementSystem,
    3: d03_NoMatterHowYouSliceIt,
    4: d04_ReposeRecord,
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

    let lines = fs.readFileSync(fd, "utf8").split("\n");
    fs.close(fd);
    // Ignore blank last line, if present.
    if (lines[lines.length - 1].length === 0) {
        lines = lines.slice(0, lines.length - 1);
    }

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
            frequency += parseInt(line);
            if (part === 2 && frequencies.has(frequency)) {
                return frequency;
            }
            frequencies.add(frequency);
        }
        if (part === 1) {
            return frequency;
        }
    }
}

function d02_InventoryManagementSystem(lines, part) {
    if (part === 1) {
        let twos = 0;
        let threes = 0;
        lines.forEach(line => {
            let cToCount = new Object();
            line.split("").forEach(c => {
                if (cToCount[c] === undefined) {
                    cToCount[c] = 0;
                }
                cToCount[c]++;
            });
            for (c in cToCount) {
                if (cToCount[c] == 2) {
                    twos++;
                    break;
                }
            }
            for (c in cToCount) {
                if (cToCount[c] == 3) {
                    threes++;
                    break;
                }
            }
        });
        return twos * threes;
    }

    for (let i = 0; i < lines.length - 1; i++) {
        const lineI = lines[i];
        for (let j = i + 1; j < lines.length; j++) {
            const lineJ = lines[j];
            let match = "";
            for (let k = 0; k < lineI.length; k++) {
                if (lineI[k] == lineJ[k]) {
                    match += lineI[k];
                }
            }
            if (match.length === lineI.length - 1) {
                return match;
            }
        }
    }
    throw new Error("Solution not found.");
}

function d03_NoMatterHowYouSliceIt(lines, part) {
    const fabric = new Array(1000);
    for (let i = 0; i < fabric.length; i++) {
        fabric[i] = new Array(1000);
    }
    let overlap = 0;
    const claims = new Array();
    const overlapClaims = new Set();
    lines.forEach(line => {
        const claimDescription = line.split(" @ ");
        const claim = parseInt(claimDescription[0].substring(1));
        claims.push(claim);
        const description = claimDescription[1];
        const startSize = description.split(": ");
        const start = startSize[0];
        const comma = start.indexOf(",");
        const x = parseInt(start.substring(0, comma));
        const y = parseInt(start.substring(comma + 1));
        const size = startSize[1];
        const by = size.indexOf("x");
        const w = parseInt(size.substring(0, by));
        const h = parseInt(size.substring(by + 1));
        for (let i = y; i < y + h; i++) {
            for (let j = x; j < x + w; j++) {
                if (part === 1) {
                    if (fabric[i][j] === undefined) {
                        fabric[i][j] = 1;
                    } else {
                        fabric[i][j]++;
                        if (fabric[i][j] === 2) {
                            overlap++;
                        }
                    }
                } else {
                    if (fabric[i][j] !== undefined) {
                        overlapClaims.add(fabric[i][j]);
                        overlapClaims.add(claim);
                    }
                    fabric[i][j] = claim;
                }
            }
        }
    });

    if (part === 1) {
        return overlap;
    }
    for (const i in claims) {
        if (!overlapClaims.has(claims[i])) {
            return claims[i];
        }
    }
    throw new Error("Solution not found.")
}

function d04_ReposeRecord(lines, part) {
    const guardToShifts = _d04_getGuardToShifts(lines);
    const guardToSleep = new Object();
    const guardToSleepMinutes = new Object();
    for (const guard in guardToShifts) {
        const shifts = guardToShifts[guard];
        if (guardToSleep[guard] === undefined) {
            guardToSleep[guard] = 0;
            guardToSleepMinutes[guard] = new Array();
            for (let i = 0; i < 60; i++) {
                guardToSleepMinutes[guard].push(0);
            }
        }
        for (let i = 0; i < shifts.length; i++) {
            const shift = shifts[i];
            for (let j = 0; j < shift.length; j += 2) {
                guardToSleep[guard] += shift[j + 1] - shift[j];
                for (let k = shift[j]; k < shift[j + 1]; k++) {
                    guardToSleepMinutes[guard][k]++;
                }
            }
        }
    }

    let guardMax = -1;
    let minuteMax = -1;
    if (part === 1) {
        for (const guard in guardToSleep) {
            const sleep = guardToSleep[guard];
            if (guardMax === -1 || sleep > guardToSleep[guardMax]) {
                guardMax = guard;
            }
        }
        for (const minute in guardToSleepMinutes[guardMax]) {
            const sleep = guardToSleepMinutes[guardMax][minute];
            if (minuteMax === -1 || sleep > guardToSleepMinutes[guardMax][minuteMax]) {
                minuteMax = minute;
            }
        }
    } else {
        for (const guard in guardToSleepMinutes) {
            const sleepMinutes = guardToSleepMinutes[guard];
            for (const minute in sleepMinutes) {
                const sleep = sleepMinutes[minute];
                if (guardMax === -1 || sleep > guardToSleepMinutes[guardMax][minuteMax]) {
                    guardMax = guard;
                    minuteMax = minute;
                }
            }
        }
    }
    return guardMax * minuteMax;
}

function _d04_getGuardToShifts(lines) {
    const separators = [0, 5, 8, 11, 14, 17];
    lines.sort((a, b) => {
        for (let i = 0; i < separators.length - 1; i++) {
            const xa = parseInt(a.substring(separators[i] + 1, separators[i + 1]));
            const xb = parseInt(b.substring(separators[i] + 1, separators[i + 1]));
            if (xa !== xb) {
                return xa < xb ? -1 : 1;
            }
        }
        return 0;
    });

    const guardToShifts = new Object();
    let guard = -1;
    let shift = null;
    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const minute = parseInt(line.substring(15, 17));
        const note = line.substring(19);
        if (note === "falls asleep" || note === "wakes up") {
            shift.push(minute);
        } else {
            if (shift !== null) {
                if (guardToShifts[guard] === undefined) {
                    guardToShifts[guard] = new Array();
                }
                guardToShifts[guard].push(shift);
            }
            const pound = note.indexOf("#");
            const space = note.indexOf(" ", pound + 1);
            guard = parseInt(note.substring(pound + 1, space));
            shift = new Array();
        }
    }
    if (shift !== null) {
        if (guardToShifts[guard] === undefined) {
            guardToShifts[guard] = new Array();
        }
        guardToShifts[guard].push(shift);
    }
    return guardToShifts;
}

if (require.main === module) {
    main();
}
