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
    10: d10_TheStarsAlign,
    11: d11_ChronalCharge,
    12: d12_SubterraneanSustainability,
    13: d13_MineCartMadness,
    14: d14_ChocolateCharts,
    15: d15_BeverageBandits,
    16: d16_ChronalClassification,
    17: d17_ReservoirResearch,
    18: d18_SettlersOfTheNorthPole,
    19: d19_GoWithTheFlow,
    20: d20_ARegularMap,
    21: d21_ChronalConversion,
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

function d10_TheStarsAlign(lines, part) {
    // Read points.
    const n = lines.length
    const px = Array(n);
    const py = Array(n);
    const vx = Array(n);
    const vy = Array(n);
    let xMin = null, xMax = null;
    let yMin = null, yMax = null;
    for (let i = 0; i < n; i++) {
        const line = lines[i];
        const plt = line.indexOf("<");
        const pgt = line.indexOf(">", plt + 1);
        const vlt = line.indexOf("<", pgt + 1);
        const vgt = line.indexOf(">", vlt + 1);
        const pxys = line.substring(plt + 1, pgt);
        const vxys = line.substring(vlt + 1, vgt);
        const pxy = pxys.split(",").map(s => s.trim()).map(s => parseInt(s));
        const vxy = vxys.split(",").map(s => s.trim()).map(s => parseInt(s));
        px[i] = pxy[0];
        py[i] = pxy[1];
        vx[i] = vxy[0];
        vy[i] = vxy[1];
        if (xMin === null || px[i] < xMin) {
            xMin = px[i];
        }
        if (xMax === null || px[i] > xMax) {
            xMax = px[i];
        }
        if (yMin === null || py[i] < yMin) {
            yMin = py[i];
        }
        if (yMax === null || py[i] > yMax) {
            yMax = py[i];
        }
    }

    // Move.
    let seconds = 0;
    while (yMax - yMin > 10) {
        for (let i = 0; i < n; i++) {
            px[i] += vx[i];
            py[i] += vy[i];
        }
        xMin = csUtil.aMin(px);
        xMax = csUtil.aMax(px);
        yMin = csUtil.aMin(py);
        yMax = csUtil.aMax(py);
        seconds++;
    }

    // Print.
    const h = yMax - yMin + 1;
    const w = xMax - xMin + 1;
    const grid = Array(h);
    for (let i = 0; i < h; i++) {
        grid[i] = Array(w).fill(" ");
    }
    for (let i = 0; i < n; i++) {
        grid[py[i] - yMin][px[i] - xMin] = "#";
    }
    for (let i = 0; i < h; i++) {
        console.log(grid[i].join(""));
    }
    return seconds;
}

function d11_ChronalCharge(lines, part) {
    const serial = parseInt(lines[0]);
    const h = 300, w = 300;
    const grid = Array(h);
    for (let i = 0; i < h; i++) {
        const y = i + 1;
        grid[i] = Array(w);
        for (let j = 0; j < w; j++) {
            const x = j + 1;
            const rackId = x + 10;
            const power = Math.floor(((rackId * y + serial) * rackId) / 100) % 10 - 5;
            grid[i][j] = power;
        }
    }

    let xMax = null, yMax = null, totalMax = null, sizeTotalMax = null;
    const sizeMin = part == 1 ? 3 : 1;
    const sizeMax = part == 1 ? 3 : 300;
    // TODO Fix lazy brute force; ~270,000 ms.
    for (let size = sizeMin; size <= sizeMax; size++) {
        for (let i = 0; i < h + 1 - size; i++) {
            for (let j = 0; j < w + 1 - size; j++) {
                let total = 0;
                for (let di = 0; di < size; di++) {
                    for (let dj = 0; dj < size; dj++) {
                        total += grid[i + di][j + dj];
                    }
                }
                if (totalMax === null || total > totalMax) {
                    xMax = j + 1;
                    yMax = i + 1;
                    totalMax = total;
                    sizeTotalMax = size;
                }
            }
        }
    }
    return "" + xMax + "," + yMax + "," + sizeTotalMax + ": " + totalMax;
}

function d12_SubterraneanSustainability(lines, part) {
    const initialPrefix = "initial state: ";
    let potToPlant = new Object();
    let left = null, right = null;
    for (let j = initialPrefix.length; j < lines[0].length; j++) {
        const pot = j - initialPrefix.length;
        const plant = lines[0][j];
        potToPlant[pot] = plant;
        if (plant === '#') {
            if (left === null) {
                left = pot;
            }
            right = pot;
        }
    }
    const patternToPlant = new Object();
    for (let i = 2; i < lines.length; i++) {
        const patternPlant = lines[i].split(" => ");
        patternToPlant[patternPlant[0]] = patternPlant[1];
    }

    const generations = part === 1 ? 20 : 50000000000;
    const plantsToGenerationLeft = new Object();
    let skipped = false;
    for (let generation = 0; generation < generations; generation++) {
        const potToPlantNext = new Object();
        let leftNext = null, rightNext = null;
        for (let pot = left - 2; pot <= right + 2; pot++) {
            let pattern = "";
            for (let di = -2; di <= 2; di++) {
                if (potToPlant[pot + di] === undefined) {
                    pattern += ".";
                } else {
                    pattern += potToPlant[pot + di];
                }
            }
            const plantNext = patternToPlant[pattern];
            potToPlantNext[pot] = plantNext;
            if (plantNext === "#") {
                if (leftNext === null) {
                    leftNext = pot;
                }
                rightNext = pot;
            }
        }

        if (!skipped) {
            let plants = "";
            for (let pot = leftNext; pot <= rightNext; pot++) {
                plants += potToPlantNext[pot];
            }
            if (plantsToGenerationLeft[plants] === undefined) {
                const generationLeft = [generation, leftNext];
                plantsToGenerationLeft[plants] = generationLeft;
            } else {
                const generationLeft = plantsToGenerationLeft[plants];
                const generationPrev = generationLeft[0];
                const leftPrev = generationLeft[1];
                const interval = generation - generationPrev;
                const repeat = Math.floor((generations - 1 - generation) / interval);
                const offset = leftNext - leftPrev;
                potToPlant = new Object();
                for (let pot = leftNext; pot <= rightNext; pot++) {
                    potToPlant[pot + offset * repeat] = potToPlantNext[pot];
                }
                left = leftNext + offset * repeat;
                right = rightNext + offset * repeat;
                generation = generation + interval * repeat;
                skipped = true;
                continue;
            }
        }
        potToPlant = potToPlantNext;
        left = leftNext;
        right = rightNext;
    }

    let potSum = 0;
    for (const pot in potToPlant) {
        if (potToPlant[pot] === "#") {
            potSum += parseInt(pot);
        }
    }
    return potSum;
}

function d13_MineCartMadness(lines, part) {
    const trackLines = new Array();
    const turns = [-1, 0, 1];
    const deltas = [[-1, 0], [0, 1], [1, 0], [0, -1]];
    const d = deltas.length;
    const yxToDirectionTurn = new Object();
    for (let y = 0; y < lines.length; y++) {
        let trackLine = "";
        for (let x = 0; x < lines[y].length; x++) {
            const c = lines[y][x];
            if (c === "^") {
                yxToDirectionTurn[csUtil.gridVectorToYx([y, x])] = [0, 0];
                trackLine += "|";
            } else if (c === ">") {
                yxToDirectionTurn[csUtil.gridVectorToYx([y, x])] = [1, 0];
                trackLine += "-";
            } else if (c === "v") {
                yxToDirectionTurn[csUtil.gridVectorToYx([y, x])] = [2, 0];
                trackLine += "|";
            } else if (c === "<") {
                yxToDirectionTurn[csUtil.gridVectorToYx([y, x])] = [3, 0];
                trackLine += "-";
            } else {
                trackLine += c;
            }
        }
        trackLines.push(trackLine);
    }

    while (Object.keys(yxToDirectionTurn).length > 1) {
        const carts = Object.keys(yxToDirectionTurn).sort(csUtil.gridCompareReadingYx);
        for (const yx of carts) {
            // Ignore deletions.
            if (yxToDirectionTurn[yx] === undefined) {
                continue;
            }
            const comma = yx.indexOf(",");
            const y = parseInt(yx.substring(0, comma));
            const x = parseInt(yx.substring(comma + 1));
            const directionTurn = yxToDirectionTurn[yx];
            const direction = directionTurn[0];
            const turn = directionTurn[1];
            // Move.
            const delta = deltas[direction];
            const yNext = y + delta[0];
            const xNext = x + delta[1];
            const yxNext = csUtil.gridVectorToYx([yNext, xNext]);
            if (yxToDirectionTurn[yxNext] === undefined) {
                // Possibly change direction, and possibly turn.
                const c = trackLines[yNext][xNext];
                let directionNext = direction;
                let turnNext = turn;
                if (c === "+") {
                    // Turn.
                    directionNext = (direction + turns[turn] + d) % d;
                    turnNext = (turn + 1) % turns.length;
                } else if (c === "\\") {
                    directionNext = (direction + (direction % 2 === 0 ? -1 : 1) + d) % d;
                } else if (c === "/") {
                    directionNext = (direction + (direction % 2 === 0 ? 1 : -1) + d) % d;
                }
                delete yxToDirectionTurn[yx];
                yxToDirectionTurn[yxNext] = [directionNext, turnNext];
            } else if (part === 1) {
                // Crash.
                return yxNext;
            } else {
                // Remove carts.
                delete yxToDirectionTurn[yx];
                delete yxToDirectionTurn[yxNext];
            }
        }
    }
    for (const yx in yxToDirectionTurn) {
        return yx;
    }
}

function d14_ChocolateCharts(lines, part) {
    const recipes = parseInt(lines[0]);
    const sequence = ("" + recipes).split("").map(c => parseInt(c));
    let scores = [3, 7];
    let paren = 0, bracket = 1;
    while (part === 2 || scores.length < recipes + 10) {
        const combine = scores[paren] + scores[bracket];
        const newScores = combine >= 10 ? [Math.floor(combine / 10), combine % 10] : [combine % 10];
        for (const score of newScores) {
            scores.push(score);
            if (part === 2 && scores.length >= sequence.length) {
                let match = true;
                for (let i = 0; i < sequence.length; i++) {
                    if (scores[scores.length - sequence.length + i] !== sequence[i]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return scores.length - sequence.length;
                }
            }
        }
        paren = (paren + 1 + scores[paren]) % scores.length;
        bracket = (bracket + 1 + scores[bracket]) % scores.length;
    }

    let ten = "";
    for (let i = 0; i < 10; i++) {
        ten += "" + scores[recipes + i];
    }
    return ten;
}

function d15_BeverageBandits(lines, part) {
    const arena = new Array(lines.length);
    for (let i = 0; i < lines.length; i++) {
        arena[i] = lines[i].split("");
    }
    const yxToUnit = new Object();
    for (let y = 0; y < arena.length; y++) {
        for (let x = 0; x < arena[y].length; x++) {
            const c = arena[y][x];
            if (c === "E" || c === "G") {
                const yx = csUtil.gridVectorToYx([y, x]);
                yxToUnit[yx] = {"race": c, "atk": 3, "hp": 200, "round": -1};
            }
        }
    }

    if (part === 1) {
        return _d15_getOutcome(arena, yxToUnit);
    }

    const elves = _d15_getElfCount(yxToUnit);
    let atkElf = 4;
    while (true) {
        const arenaCopy = new Array(arena.length);
        for (let i = 0; i < arena.length; i++) {
            const row = new Array(arena[i].length);
            for (let j = 0; j < arena[i].length; j++) {
                row[j] = arena[i][j];
            }
            arenaCopy[i] = row;
        }
        const yxToUnitCopy = new Object();
        for (const yx in yxToUnit) {
            const unitCopy = new Object();
            for (const key in yxToUnit[yx]) {
                unitCopy[key] = yxToUnit[yx][key];
            }
            if (unitCopy["race"] === "E") {
                unitCopy["atk"] = atkElf;
            }
            yxToUnitCopy[yx] = unitCopy;
        }
        const outcome = _d15_getOutcome(arenaCopy, yxToUnitCopy);
        if (_d15_getElfCount(yxToUnitCopy) === elves) {
            return outcome;
        }
        atkElf++;
    }
}

function _d15_getOutcome(arena, yxToUnit) {
    const vDeltas = [[-1, 0], [0, -1], [0, 1], [1, 0]]; // In reading order.
    const openChars = new Set(["."]); // For Dijkstra function.
    let round = 0;
    while (true) {
        const orderedYxs = Object.keys(yxToUnit).sort(csUtil.gridCompareReadingYx);
        for (let yx of orderedYxs) {
            const unit = yxToUnit[yx];
            if (unit === undefined || unit["round"] === round) {
                continue; // Killed or another unit moved onto previously-killed unit's space.
            }
            unit["round"] = round;
            // Check for adjacent enemies.
            let v = csUtil.gridYxToVector(yx); // Updated after moving, hence not `const`.
            let hasAdjacentEnemy = false;
            for (const vDelta of vDeltas) {
                const yxAdjacent = csUtil.gridVectorToYx([v[0] + vDelta[0], v[1] + vDelta[1]]);
                if (yxToUnit[yxAdjacent] !== undefined && yxToUnit[yxAdjacent]["race"] !== unit["race"]) {
                    hasAdjacentEnemy = true;
                    break;
                }
            }
            if (!hasAdjacentEnemy) {
                // Collect all spaces adjacent to enemies.
                let enemies = 0;
                let enemyAdjacentYxs = new Set();
                for (const yxOther in yxToUnit) {
                    const unitOther = yxToUnit[yxOther];
                    if (unitOther["race"] === unit["race"]) {
                        continue; // Ally or self.
                    }
                    enemies++;
                    const vOther = csUtil.gridYxToVector(yxOther);
                    for (const vDelta of vDeltas) {
                        const yCand = vOther[0] + vDelta[0], xCand = vOther[1] + vDelta[1];
                        if (yCand < 0 || yCand >= arena.length || xCand < 0 || xCand >= arena[yCand].length) {
                            continue;
                        }
                        if (arena[yCand][xCand] === ".") {
                            const yxCand = csUtil.gridVectorToYx([yCand, xCand]);
                            enemyAdjacentYxs.add(yxCand);
                        }
                    }
                }
                // Check if combat ceases.
                if (enemies === 0) {
                    let hpSum = 0;
                    for (const yxAlly in yxToUnit) {
                        hpSum += yxToUnit[yxAlly]["hp"];
                    }
                    return round * hpSum;
                }
                // Identify nearest space via BFS, in reading order, adjacent to an enemy.
                if (enemyAdjacentYxs.size === 0) {
                    continue; // No spaces are in range.
                }
                let vMove = null;
                let visitedYxs = new Set();
                let frontierVs = [v];
                while (frontierVs.length > 0) {
                    let frontierNextVs = new Array();
                    for (const vSpace of frontierVs) {
                        const yxSpace = csUtil.gridVectorToYx(vSpace);
                        if (enemyAdjacentYxs.has(yxSpace)) {
                            vMove = vSpace;
                            break;
                        }
                        if (visitedYxs.has(yxSpace)) {
                            continue;
                        }
                        visitedYxs.add(yxSpace);
                        for (const vDelta of vDeltas) {
                            const yStep = vSpace[0] + vDelta[0], xStep = vSpace[1] + vDelta[1];
                            if (yStep < 0 || yStep >= arena.length || xStep < 0 || xStep >= arena[yStep].length) {
                                continue;
                            }
                            if (openChars.has(arena[yStep][xStep])) {
                                frontierNextVs.push([yStep, xStep]);
                            }
                        }
                    }
                    if (vMove !== null) {
                        break;
                    }
                    frontierVs = frontierNextVs.sort(csUtil.gridCompareReadingVector);
                }
                if (vMove === null) {
                    continue; // Unreachable.
                }
                // Move.
                arena[v[0]][v[1]] = ".";
                const d = csUtil.gridDijkstra(arena, vMove, v, openChars);
                let vStep = null;
                let dStep = null;
                for (const vDelta of vDeltas) {
                    const yStep = v[0] + vDelta[0], xStep = v[1] + vDelta[1];
                    if (yStep < 0 || yStep >= arena.length || xStep < 0 || xStep >= arena[yStep].length) {
                        continue;
                    }
                    if (d[yStep][xStep] === -1) {
                        continue;
                    }
                    if (vStep === null || d[yStep][xStep] < dStep) {
                        vStep = [yStep, xStep];
                        dStep = d[yStep][xStep];
                    }
                }
                v = vStep;
                arena[vStep[0]][vStep[1]] = unit["race"];
                delete yxToUnit[yx];
                yx = csUtil.gridVectorToYx(v);
                yxToUnit[yx] = unit;
            }
            // Attack.
            let yxAttack = null;
            for (const vDelta of vDeltas) {
                const yAdj = v[0] + vDelta[0], xAdj = v[1] + vDelta[1];
                if (yAdj < 0 || yAdj >= arena.length || xAdj < 0 || xAdj >= arena[yAdj.length]) {
                    continue;
                }
                const yxAdj = csUtil.gridVectorToYx([yAdj, xAdj]);
                if (yxToUnit[yxAdj] !== undefined && yxToUnit[yxAdj]["race"] !== unit["race"]) {
                    if (yxAttack === null || yxToUnit[yxAdj]["hp"] < yxToUnit[yxAttack]["hp"]) {
                        yxAttack = yxAdj;
                    }
                }
            }
            if (yxAttack !== null) {
                yxToUnit[yxAttack]["hp"] -= unit["atk"];
                if (yxToUnit[yxAttack]["hp"] <= 0) {
                    delete yxToUnit[yxAttack];
                    const vAttack = csUtil.gridYxToVector(yxAttack);
                    arena[vAttack[0]][vAttack[1]] = ".";
                }
            }
        }
        round++;
    }
}

function _d15_getElfCount(yxToUnit) {
    let elves = 0;
    for (const yx in yxToUnit) {
        if (yxToUnit[yx]["race"] === "E") {
            elves++;
        }
    }
    return elves;
}

function d16_ChronalClassification(lines, part) {
    const opcodes = [
        "addr", "addi", // Addition.
        "mulr", "muli", // Multiplication.
        "banr", "bani", // Bitwise AND.
        "borr", "bori", // Bitwise OR.
        "setr", "seti", // Assignment.
        "gtir", "gtri", "gtrr", // Greater-than.
        "eqir", "eqri", "eqrr", // Equality.
    ];

    let behaveLikeThree = 0;
    let idToOpcodeCandidates = new Array();
    for (let j = 0; j < opcodes.length; j++) {
        idToOpcodeCandidates.push(new Set(opcodes));
    }
    let idToOpcode = new Array(opcodes.length);
    idToOpcode.fill(null);
    let i = 0;
    while (true) {
        const beforeStart = lines[i].indexOf("[");
        if (beforeStart === -1) {
            i += 2;
            break;
        }
        const beforeEnd = lines[i].indexOf("]", beforeStart + 1);
        const rBefore = lines[i].substring(beforeStart + 1, beforeEnd).split(", ").map(s => parseInt(s));
        const inst = lines[i + 1].split(" ").map(s => parseInt(s));
        const afterStart = lines[i + 2].indexOf("[");
        const afterEnd = lines[i + 2].indexOf("]");
        const rAfter = lines[i + 2].substring(afterStart + 1, afterEnd).split(", ").map(s => parseInt(s));

        const id = inst[0], a = inst[1], b = inst[2], c = inst[3];
        let matches = new Set();
        for (const opcode of opcodes) {
            let r = rBefore.map(x => x);
            _d16_execute(r, opcode, a, b, c);
            if (r[c] === rAfter[c]) {
                matches.add(opcode);
            } else {
                _d16_eliminate(id, opcode, idToOpcodeCandidates, idToOpcode);
            }
        }
        if (matches.size >= 3) {
            behaveLikeThree++;
        }

        i += 4;
    }

    if (part === 1) {
        return behaveLikeThree;
    }

    const r = [0, 0, 0, 0];
    while (i < lines.length) {
        const inst = lines[i].split(" ").map(s => parseInt(s));
        const id = inst[0], a = inst[1], b = inst[2], c = inst[3];
        _d16_execute(r, idToOpcode[id], a, b, c);
        i++;
    }
    return r[0];
}

function _d16_execute(r, opcode, a, b, c) {
    if (opcode === "addr") {
        r[c] = r[a] + r[b];
    } else if (opcode === "addi") {
        r[c] = r[a] + b;
    } else if (opcode === "mulr") {
        r[c] = r[a] * r[b];
    } else if (opcode === "muli") {
        r[c] = r[a] * b;
    } else if (opcode === "banr") {
        r[c] = r[a] & r[b];
    } else if (opcode === "bani") {
        r[c] = r[a] & b;
    } else if (opcode === "borr") {
        r[c] = r[a] | r[b];
    } else if (opcode === "bori") {
        r[c] = r[a] | b;
    } else if (opcode === "setr") {
        r[c] = r[a];
    } else if (opcode === "seti") {
        r[c] = a;
    } else if (opcode === "gtir") {
        r[c] = a > r[b] ? 1 : 0;
    } else if (opcode === "gtri") {
        r[c] = r[a] > b ? 1 : 0;
    } else if (opcode === "gtrr") {
        r[c] = r[a] > r[b] ? 1 : 0;
    } else if (opcode === "eqir") {
        r[c] = a === r[b] ? 1 : 0;
    } else if (opcode === "eqri") {
        r[c] = r[a] === b ? 1 : 0;
    } else if (opcode === "eqrr") {
        r[c] = r[a] === r[b] ? 1 : 0;
    } else {
        throw new Error("Unrecognized opcode: " + opcode);
    }
}

function _d16_eliminate(id, opcode, idToOpcodeCandidates, idToOpcode) {
    if (idToOpcode[id] !== null) {
        return;
    }
    idToOpcodeCandidates[id].delete(opcode);
    if (idToOpcodeCandidates[id].size === 1) {
        let opcodeSure = null;
        for (const opcodeCandidate of idToOpcodeCandidates[id]) {
            opcodeSure = opcodeCandidate;
            break;
        }
        idToOpcode[id] = opcodeSure;
        for (const idOther in idToOpcodeCandidates) {
            if (idOther === id) {
                continue;
            }
            _d16_eliminate(idOther, opcodeSure, idToOpcodeCandidates, idToOpcode);
        }
    }
}

function d17_ReservoirResearch(lines, part) {
    const yxToSquare = new Object();
    let yMin = null, yMax = null;
    for (const line of lines) {
        const constantRangeExpressions = line.split(", ");
        const constantExpression = constantRangeExpressions[0].split("=");
        const variableC = constantExpression[0];
        const constant = parseInt(constantExpression[1]);
        const rangeExpression = constantRangeExpressions[1].split("=");
        const variableR = rangeExpression[0];
        const range = rangeExpression[1].split("..").map(s => parseInt(s));
        if (variableC === variableR || range.length !== 2) {
            throw new Error("Unable to parse line: " + line);
        }
        if (variableC === "x") {
            for (let y = range[0]; y <= range[1]; y++) {
                const yx = csUtil.gridVectorToYx([y, constant]);
                yxToSquare[yx] = "#";
            }
            if (yMin === null || range[0] < yMin) {
                yMin = range[0];
            }
            if (yMax === null || range[1] > yMax) {
                yMax = range[1];
            }
        } else {
            for (let x = range[0]; x <= range[1]; x++) {
                const yx = csUtil.gridVectorToYx([constant, x]);
                yxToSquare[yx] = "#";
            }
            if (yMin === null || constant < yMin) {
                yMin = constant;
            }
            if (yMax === null || constant > yMax) {
                yMax = constant;
            }
        }
    }

    yxToSquare[csUtil.gridVectorToYx([yMin, 500])] = "|";
    let flowVs = [[yMin, 500]];
    while (flowVs.length > 0) {
        const flowV = flowVs.shift();
        let y = flowV[0], x = flowV[1];
        // Fall downward.
        let yxDown = null;
        while (y < yMax) {
            yxDown = csUtil.gridVectorToYx([y + 1, x]);
            if (yxToSquare[yxDown] !== undefined) {
                break;
            }
            yxToSquare[yxDown] = "|";
            y++;
        }
        if (y == yMax || yxToSquare[yxDown] === "|") {
            continue; // Reached bottom or another flow.
        }
        // Settle upwards.
        while (true) {
            // Look for left wall.
            let xLeft = x;
            let yxLeft = null;
            let yxLeftDown = yxDown;
            let settleLeft = false;
            while (yxToSquare[yxLeftDown] !== undefined && yxToSquare[yxLeftDown] !== "|") {
                xLeft--;
                yxLeft = csUtil.gridVectorToYx([y, xLeft]);
                if (yxToSquare[yxLeft] !== undefined && yxToSquare[yxLeft] === "#") {
                    settleLeft = true;
                    break;
                }
                yxToSquare[yxLeft] = "|";
                yxLeftDown = csUtil.gridVectorToYx([y + 1, xLeft]);
            }
            // Continue the left flow.
            if (yxToSquare[yxLeftDown] === undefined) {
                flowVs.push([y, xLeft]);
            }
            // Look for right wall.
            let xRight = x;
            let yxRight = null;
            let yxRightDown = yxDown;
            let settleRight = false;
            while (yxToSquare[yxRightDown] !== undefined && yxToSquare[yxRightDown] !== "|") {
                xRight++;
                yxRight = csUtil.gridVectorToYx([y, xRight]);
                if (yxToSquare[yxRight] !== undefined && yxToSquare[yxRight] === "#") {
                    settleRight = true;
                    break;
                }
                yxToSquare[yxRight] = "|";
                yxRightDown = csUtil.gridVectorToYx([y + 1, xRight]);
            }
            // Continue the right flow.
            if (yxToSquare[yxRightDown] === undefined) {
                flowVs.push([y, xRight]);
            }
            // Quit when not bound by walls.
            if (!settleLeft || !settleRight) {
                break;
            }
            // Settle.
            for (let xSettle = xLeft + 1; xSettle <= xRight - 1; xSettle++) {
                const yxSettle = csUtil.gridVectorToYx([y, xSettle]);
                yxToSquare[yxSettle] = "~";
            }
            y--;
            yxToSquare[csUtil.gridVectorToYx([y, x])] = "|"; // May be sand when two equal-height flows have merged.
        }
    }

//    const xMin = 495, xMax = 506;
//    const xMin = 414, xMax = 576;
//    for (let y = yMin; y <= yMax; y++) {
//        let row = "";
//        for (let x = xMin - 1; x <= xMax + 1; x++) {
//            const yx = csUtil.gridVectorToYx([y, x]);
//            const square = yxToSquare[yx];
//            if (square === undefined) {
//                row += ".";
//            } else {
//                row += square;
//            }
//        }
//        console.log(row);
//    }

    let waterSquares = 0;
    for (const yx in yxToSquare) {
        if ((part === 1 && yxToSquare[yx] === "|") || yxToSquare[yx] === "~") {
            waterSquares++;
        }
    }
    return waterSquares;
}

function d18_SettlersOfTheNorthPole(lines, part) {
    let acres = lines.map(line => line.split(""));

    const minutes = part === 1 ? 10 : 1000000000;
    let minute = 0;
    const flatToMinute = new Object();
    let skipped = false;
    while (minute < minutes) {
        let acresNext = acres.map(row => row.join("").split(""));
        for (let y = 0; y < acres.length; y++) {
            for (let x = 0; x < acres[y].length; x++) {
                const acre = acres[y][x];
                let opens = 0, trees = 0, lumbers = 0;
                for (let dy = -1; dy <= 1; dy++) {
                    for (let dx = -1; dx <= 1; dx++) {
                        if (dy === 0 && dx === 0) {
                            continue;
                        }
                        const yz = y + dy, xz = x + dx;
                        if (yz < 0 || yz >= acres.length || xz < 0 || xz >= acres[y].length) {
                            continue;
                        }
                        const acreAdj = acres[yz][xz];
                        if (acreAdj === ".") {
                            opens++;
                        } else if (acreAdj === "|") {
                            trees++;
                        } else {
                            lumbers++;
                        }
                    }
                }
                if (acre === ".") {
                    if (trees >= 3) {
                        acresNext[y][x] = "|";
                    }
                } else if (acre === "|") {
                    if (lumbers >= 3) {
                        acresNext[y][x] = "#";
                    }
                } else {
                    if (lumbers < 1 || trees < 1) {
                        acresNext[y][x] = ".";
                    }
                }
            }
        }
        acres = acresNext;
        minute++;

        if (!skipped) {
            const flat = acres.map(row => row.join("")).join("");
            if (flatToMinute[flat] === undefined) {
                flatToMinute[flat] = minute;
            } else {
                const minutePrev = flatToMinute[flat];
                const interval = minute - minutePrev;
                const repeat = Math.floor((minutes - minute) / interval);
                minute += interval * repeat;
                skipped = true;
            }
        }
    }

    let trees = 0, lumbers = 0;
    for (let y = 0; y < acres.length; y++) {
        for (let x = 0; x < acres[y].length; x++) {
            const acre = acres[y][x];
            if (acre === "|") {
                trees++;
            } else if (acre === "#") {
                lumbers++;
            }
        }
    }
    return trees * lumbers;
}

function d19_GoWithTheFlow(lines, part) {
    const bound = parseInt(lines[0][lines[0].length - 1]);
    const instructions = new Array();
    for (let i = 1; i < lines.length; i++) {
        const tokens = lines[i].split(" ");
        instructions.push([tokens[0], parseInt(tokens[1]), parseInt(tokens[2]), parseInt(tokens[3])]);
    }

    const r = [0, 0, 0, 0, 0, 0];
    if (part === 2) {
        r[0] = 1;
    }
    let ip = 0;
    while (ip >= 0 && ip < instructions.length) {
        if (ip === 1) {
            const n = r[5];
            let divisor_sum = 0;
            for (let i = 1; i <= n; i++) {
                if (n % i === 0) {
                    divisor_sum += i;
                }
            }
            return divisor_sum;
        }
        r[bound] = ip;
        const inst = instructions[ip];
        const opcode = inst[0], a = inst[1], b = inst[2], c = inst[3];
        _d16_execute(r, opcode, a, b, c);
        ip = r[bound];
        ip++;
    }
}

function d20_ARegularMap(lines, part) {
    const s = lines[0];
    if (s[0] !== "^" || s[s.length - 1] !== "$") {
        throw new Error("Illegal surrounding characters in regular expression: " + s);
    }
    const ip = [0];
    const yxStart = csUtil.gridVectorToYx([0, 0]);
    const yxToD = new Object();
    yxToD[yxStart] = 0;
    const cToDelta = {"N": [-1, 0], "E": [0, 1], "S": [1, 0], "W": [0, -1]};
    const yxEnd = _d20_traverse_regex(s.substring(1, s.length - 1), ip, yxStart, yxToD, cToDelta);

    if (part === 1) {
        let yxMax = null;
        for (const yx in yxToD) {
            if (yxMax === null || yxToD[yx] > yxToD[yxMax]) {
                yxMax = yx;
            }
        }
        return yxToD[yxMax];
    }

    let far = 0;
    for (const yx in yxToD) {
        if (yxToD[yx] >= 1000) {
            far++;
        }
    }
    return far;
}

function _d20_traverse_regex(s, ip, yxStart, yxToD, cToDelta) {
    let yx = yxStart;
    while (ip[0] < s.length) {
        const c = s[ip[0]];
        if (c === "(") {
            // Recurse starting from the current position.
            ip[0]++;
            yx = _d20_traverse_regex(s, ip, yx, yxToD, cToDelta);
            continue;
        } else if (c === "|") {
            // Traverse again starting from the beginning.
            ip[0]++;
            yx = yxStart;
            continue;
        } else if (c === ")") {
            // Give new position back to caller.
            // Assumes branch ending positions are uniform if they continue, as per:
            // | Regardless of which option is taken, the route continues from the position it is left at
            // | after taking those steps.
            ip[0]++;
            return yx;
        }

        // Process direction.
        const delta = cToDelta[c];
        const v = csUtil.gridYxToVector(yx);
        const vStep = [v[0] + delta[0], v[1] + delta[1]];
        const yxStep = csUtil.gridVectorToYx(vStep);
        if (yxToD[yxStep] === undefined || yxToD[yx] + 1 < yxToD[yxStep]) {
            yxToD[yxStep] = yxToD[yx] + 1; // Relax edge.
        }
        yx = yxStep;
        ip[0]++;
    }
}

function d21_ChronalConversion(lines, part) {
    const bound = parseInt(lines[0][lines[0].length - 1]);
    const instructions = new Array();
    for (let i = 1; i < lines.length; i++) {
        const tokens = lines[i].split(" ");
        instructions.push([tokens[0], parseInt(tokens[1]), parseInt(tokens[2]), parseInt(tokens[3])]);
    }

    const r = [0, 0, 0, 0, 0, 0];
    let ip = 0;
    const zSet = new Set();
    let zPrev = null;
    while (ip >= 0 && ip < instructions.length) {
        if (ip === 30) {
            const z = r[5];
            if (part === 1) {
                return z;
            }
            // TODO: ~128985 ms; could do bit-wise division.
            if (!zSet.has(z)) {
                zSet.add(z);
                zPrev = z;
            } else {
                return zPrev;
            }
        }
        r[bound] = ip;
        const inst = instructions[ip];
        const opcode = inst[0], a = inst[1], b = inst[2], c = inst[3];
        _d16_execute(r, opcode, a, b, c);
        ip = r[bound];
        ip++;
    }
}

if (require.main === module) {
    main();
}
