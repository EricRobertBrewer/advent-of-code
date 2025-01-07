from collections import defaultdict

import aoc


def get_answer(lines, part):
    left_right = [tuple(map(int, line.split('   '))) for line in lines]
    left, right = zip(*left_right)

    if part == 1:
        return sum(abs(l - r) for l, r in zip(sorted(left), sorted(right)))

    left_counts, right_counts = to_counts(left), to_counts(right)
    return sum(x * left_counts[x] * right_counts[x] for x in left_counts.keys())


def to_counts(a):
    d = defaultdict(int)
    for x in a:
        d[x] += 1
    return d


if __name__ == '__main__':
    aoc.solve(1, get_answer)
