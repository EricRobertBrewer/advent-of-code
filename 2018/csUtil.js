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
