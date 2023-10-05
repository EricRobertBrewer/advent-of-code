#!/usr/local/bin node

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
