#!/usr/local/bin node

const fs = require("fs")

const aocUtil = require("./aocUtil.js")

const YEAR = "2018";
const SOLUTIONS = {
    1: d01_ChronalCalibration,
    2: d02_InventoryManagementSystem,
    3: d03_NoMatterHowYouSliceIt,
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
        if (line.length === 0) {
            return;
        }
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

if (require.main === module) {
    main();
}
