#!/usr/local/bin node

const fs = require("fs");

const aocUtil = require("./aocUtil.js");
const csUtil = require("./csUtil.js");

const YEAR = "2018";
const SOLUTIONS = {
    1: d01_ChronalCalibration,
    2: d02_InventoryManagementSystem,
    3: d03_NoMatterHowYouSliceIt,
    4: d04_ReposeRecord,
    5: d05_AlchemicalReduction,
    6: d06_ChronalCoordinates,
    7: d07_TheSumOfItsParts,
    8: d08_MemoryManeuver,
    9: d09_MarbleMania,
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
            for (const c in cToCount) {
                if (cToCount[c] == 2) {
                    twos++;
                    break;
                }
            }
            for (const c in cToCount) {
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
    for (const claim of claims) {
        if (!overlapClaims.has(claim)) {
            return claim;
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

function d05_AlchemicalReduction(lines, part) {
    let polymer = lines[0];
    const lower = "abcdefghijklmnopqrstuvwxyz";
    const upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    const cToOpposite = new Object();
    for (let i = 0; i < lower.length; i++) {
        cToOpposite[lower[i]] = upper[i];
        cToOpposite[upper[i]] = lower[i];
    }

    if (part === 1) {
        const reaction = _d05_getReaction(polymer, cToOpposite);
        return reaction.length;
    }

    let lengthMin = -1;
    for (let i = 0; i < lower.length; i++) {
        let polymerRemove = "";
        for (let j = 0; j < polymer.length; j++) {
            const c = polymer[j];
            if (c !== lower[i] && c !== upper[i]) {
                polymerRemove += c;
            }
        }
        const reactionRemove = _d05_getReaction(polymerRemove, cToOpposite);
        if (lengthMin === -1 || reactionRemove.length < lengthMin) {
            lengthMin = reactionRemove.length;
        }
    }
    return lengthMin;
}

function _d05_getReaction(polymer, cToOpposite) {
    let i = 1;
    while (i < polymer.length) {
        if (polymer[i] === cToOpposite[polymer[i - 1]]) {
            polymer = polymer.substring(0, i - 1) + polymer.substring(i + 1);
            if (i > 1) {
                i--;
            }
        } else {
            i++;
        }
    }
    return polymer;
}

function d06_ChronalCoordinates(lines, part) {
    const pointSToIndex = new Object();
    const indexPoints = new Array();
    let xMin = null, xMax = null;
    let yMin = null, yMax = null;
    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const xy = line.split(", ");
        const x = parseInt(xy[0]);
        if (xMin === null || x < xMin) {
            xMin = x;
        }
        if (xMax === null || x > xMax) {
            xMax = x;
        }
        const y = parseInt(xy[1]);
        if (yMin === null || y < yMin) {
            yMin = y;
        }
        if (yMax === null || y > yMax) {
            yMax = y;
        }
        pointSToIndex["" + x + "," + y] = i;
        indexPoints.push([[x, y]]);
    }

    if (part === 2) {
        let region = 0;
        for (let x = xMin; x <= xMax; x++) {
            for (let y = yMin; y <= yMax; y++) {
                let d = 0;
                for (const points of indexPoints) {
                    const cx = points[0][0];
                    const cy = points[0][1];
                    d += Math.abs(x - cx) + Math.abs(y - cy);
                }
                if (d < 10000) {
                    region++;
                }
            }
        }
        return region;
    }

    const indicesInfinite = new Set();
    while (Object.keys(pointSToIndex).length < (xMax - xMin + 1) * (yMax - yMin + 1)) {
        const pointSToIndexNew = new Object();
        for (let i in indexPoints) {
            const points = indexPoints[i];
            for (let j = 0; j < points.length; j++) {
                const x = points[j][0];
                const y = points[j][1];
                // Claim all neighboring points, reconcile ties, and declare infinite areas.
                const neighbors = [[x, y - 1], [x + 1, y], [x, y + 1], [x - 1, y]];
                for (let k = 0; k < neighbors.length; k++) {
                    const nx = neighbors[k][0];
                    const ny = neighbors[k][1];
                    if (xMin <= nx && nx <= xMax && yMin <= ny && ny <= yMax) {
                        const pointNS = "" + nx + "," + ny;
                        if (pointSToIndex[pointNS] !== undefined) {
                            continue; // Skip previously claimed points.
                        }
                        if (pointSToIndexNew[pointNS] === undefined) {
                            pointSToIndexNew[pointNS] = i; // The first coordinate to claim this point.
                        } else if (pointSToIndexNew[pointNS] !== i) {
                            pointSToIndexNew[pointNS] = -1; // A tie with another coordinate.
                        }
                    } else {
                        indicesInfinite.add(i); // At a boundary.
                    }
                }
            }
        }
        for (const pointS in pointSToIndexNew) {
            const i = pointSToIndexNew[pointS];
            pointSToIndex[pointS] = i;
            if (i !== -1) {
                const comma = pointS.indexOf(",");
                const x = parseInt(pointS.substring(0, comma));
                const y = parseInt(pointS.substring(comma + 1));
                indexPoints[i].push([x, y]);
            }
        }
    }

    let areaMax = -1;
    for (let i in indexPoints) {
        if (indicesInfinite.has(i)) {
            continue;
        }
        const area = indexPoints[i].length;
        if (areaMax === -1 || area > areaMax) {
            areaMax = area;
        }
    }
    return areaMax;
}

function d07_TheSumOfItsParts(lines, part) {
    const stepToDependencies = new Object();
    for (let i = 0; i < lines.length; i++) {
        const tokens = lines[i].split(" ");
        const dependency = tokens[1];
        if (stepToDependencies[dependency] === undefined) {
            stepToDependencies[dependency] = new Set();
        }
        const step = tokens[7];
        if (stepToDependencies[step] === undefined) {
            stepToDependencies[step] = new Set();
        }
        stepToDependencies[step].add(dependency);
    }
    const steps = Object.keys(stepToDependencies).sort();

    if (part === 1) {
        let order = "";
        const added = new Set();
        while (added.size < Object.keys(stepToDependencies).length) {
            for (const step of steps) {
                if (added.has(step)) {
                    continue;
                }
                let ready = true;
                for (const dependency of stepToDependencies[step]) {
                    if (!added.has(dependency)) {
                        ready = false;
                        break;
                    }
                }
                if (ready) {
                    order += step;
                    added.add(step);
                    break;
                }
            }
        }
        return order;
    }

    const stepToSeconds = new Object();
    for (let i = 0; i < steps.length; i++) {
        stepToSeconds[steps[i]] = 61 + i;
    }
    const finished = new Set();
    const workerSteps = new Array();
    const workerSeconds = new Array();
    let secondsTotal = 0;
    while (finished.size < steps.length) {
        // Add new steps.
        for (let step of steps) {
            if (workerSteps.length == 5) {
                break;
            }
            if (finished.has(step) || workerSteps.includes(step)) {
                continue;
            }
            let ready = true;
            for (const dependency of stepToDependencies[step]) {
                if (!finished.has(dependency)) {
                    ready = false;
                    break;
                }
            }
            if (ready) {
                workerSteps.push(step);
                workerSeconds.push(stepToSeconds[step]);
            }
        }
        // Tick down steps in progress.
        for (let i = 0; i < workerSeconds.length; i++) {
            workerSeconds[i]--;
        }
        // Check off steps that are complete.
        for (let i = 0; i < workerSteps.length; i++) {
            if (workerSeconds[i] === 0) {
                const step = workerSteps[i];
                workerSteps.splice(i, 1);
                workerSeconds.splice(i, 1);
                finished.add(step);
                i--;
            }
        }
        secondsTotal++;
    }
    return secondsTotal;
}

function d08_MemoryManeuver(lines, part) {
    const values = lines[0].split(" ").map(s => parseInt(s));
    const root = _d08_getTree(values, [0]);
    if (part === 1) {
        return root["sum"];
    }
    return root["value"];
}

function _d08_getTree(values, ip) {
    const node = new Object();
    const n = values[ip[0]]; // Number of children.
    const m = values[ip[0] + 1]; // Number of metadata entries.
    ip[0] += 2;
    node["sum"] = 0;
    // Recursively collect children.
    node["children"] = new Array();
    for (let i = 0; i < n; i++) {
        const child = _d08_getTree(values, ip);
        node["children"].push(child);
        node["sum"] += child["sum"];
    }
    // Read metadata values.
    node["metadata"] = new Array();
    for (let j = 0; j < m; j++) {
        const value = values[ip[0]];
        node["metadata"].push(value);
        node["sum"] += value;
        ip[0]++;
    }
    // Calculate value.
    node["value"] = 0;
    if (n === 0) {
        // Sum of metadata values.
        node["value"] = node["sum"];
    } else {
        // Sum of childrens' values.
        for (let j = 0; j < m; j++) {
            const i = node["metadata"][j] - 1;
            if (i < n) {
                node["value"] += node["children"][i]["value"];
            }
        }
    }
    return node;
}

function d09_MarbleMania(lines, part) {
    const tokens = lines[0].split(" ");
    const p = parseInt(tokens[0]);
    const n = part === 1 ? parseInt(tokens[6]) : 100 * parseInt(tokens[6]);

    const scores = new Array(p);
    for (let j = 0; j < p; j++) {
        scores[j] = 0;
    }

    // `0` marble is placed first.
    let current = new csUtil.Node(0);
    current.next = current; // Circle.
    current.prev = current;

    let player = 0;
    for (let i = 1; i < n; i++) {
        if (i % 23 !== 0) {
            current = current.next.insertNext(i);
        } else {
            scores[player] += i;
            let marble = current;
            for (let m = 0; m < 7; m++) {
                marble = marble.prev;
            }
            scores[player] += marble.value;
            current = marble.remove();
        }
        player = (player + 1) % p;
    }

    let scoreMax = scores[0];
    for (let j = 1; j < p; j++) {
        if (scores[j] > scoreMax) {
            scoreMax = scores[j];
        }
    }
    return scoreMax;
}

if (require.main === module) {
    main();
}
