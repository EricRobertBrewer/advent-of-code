#!/usr/local/bin node

module.exports.aMin = function(a) {
    let minValue = a[0];
    for (let i = 1; i < a.length; i++) {
        if (a[i] < minValue) {
            minValue = a[i];
        }
    }
    return minValue;
};

module.exports.aMax = function(a) {
    let maxValue = a[0];
    for (let i = 1; i < a.length; i++) {
        if (a[i] > maxValue) {
            maxValue = a[i];
        }
    }
    return maxValue;
};

// Takes a comma-separated string; outputs an array.
module.exports.gridYxToVector = function(yx) {
    return yx.split(",").map(z => parseInt(z));
};

// Takes an array; outputs a comma-separated string (to use as an object key).
module.exports.gridVectorToYx = function(v) {
    return "" + v[0] + "," + v[1];
};

module.exports.gridCompareReadingYx = function(ayx, byx) {
    const av = module.exports.gridYxToVector(ayx);
    const bv = module.exports.gridYxToVector(byx);
    return module.exports.gridCompareReadingVector(av, bv);
};

module.exports.gridCompareReadingVector = function(av, bv) {
    const ay = av[0], ax = av[1];
    const by = bv[0], bx = bv[1];
    if (ay !== by) {
        return ay < by ? -1 : 1;
    }
    return ax < bx ? -1 : 1;
}

module.exports.gridDijkstra = function(grid, vSource, vTarget, openChars) {
    let d = new Array();
    for (let y = 0; y < grid.length; y++) {
        let row = new Array();
        for (let x = 0; x < grid[y].length; x++) {
            row.push(-1);
        }
        d.push(row);
    }
    d[vSource[0]][vSource[1]] = 0;

    const vDeltas = [[-1, 0], [0, 1], [1, 0], [0, -1]];
    let visitedYxs = new Set();
    let frontierVs = [vSource];
    while (frontierVs.length > 0) {
        const v = frontierVs.shift();
        const y = v[0], x = v[1];
        if (vTarget && y === vTarget[0] && x === vTarget[1]) {
            break;
        }
        const yx = module.exports.gridVectorToYx(v);
        if (visitedYxs.has(yx)) {
            continue;
        }
        visitedYxs.add(yx);
        for (const vDelta of vDeltas) {
            const yStep = y + vDelta[0], xStep = x + vDelta[1];
            if (yStep < 0 || yStep >= grid.length || xStep < 0 || xStep >= grid[yStep].length) {
                continue;
            }
            if (openChars.has(grid[yStep][xStep]) && d[yStep][xStep] === -1) {
                d[yStep][xStep] = d[y][x] + 1;
                frontierVs.push([yStep, xStep]);
            }
        }
    }
    return d;
};

class Node {

    constructor(value) {
        this.next = null;
        this.prev = null;
        this.value = value;
    }

    insertNext(value) {
        const node = new Node(value);
        if (this.next !== null) {
            const next = this.next;
            node.next = next;
            next.prev = node;
        }
        this.next = node;
        node.prev = this;
        return node;
    }

    remove() {
        const next = this.next;
        const prev = this.prev;
        this.next = null;
        this.prev = null;
        if (next !== null) {
            next.prev = prev;
        }
        if (prev !== null) {
            prev.next = next;
        }
        return next;
    }
}
module.exports.Node = Node;
