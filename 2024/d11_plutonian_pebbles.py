from collections import defaultdict

import aoc


def get_answer(lines, part):
    stones = list(map(int, lines[0].split(' ')))

    stone_to_count = defaultdict(int)
    for stone in stones:
        stone_to_count[stone] += 1
    blinks = 25 if part == 1 else 75
    for blink in range(blinks):
        stone_to_count_next = defaultdict(int)
        for stone in stone_to_count.keys():
            count = stone_to_count[stone]
            s = str(stone)
            if stone == 0:
                stone_to_count_next[1] += count
            elif len(s) % 2 == 0:
                stone_to_count_next[int(s[:len(s) // 2])] += count
                stone_to_count_next[int(s[len(s) // 2:])] += count
            else:
                stone_to_count_next[stone * 2024] += count
        stone_to_count = stone_to_count_next

    return sum(stone_to_count.values())


if __name__ == '__main__':
    aoc.solve(11, get_answer)
