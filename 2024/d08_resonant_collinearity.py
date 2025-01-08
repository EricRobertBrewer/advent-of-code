from collections import defaultdict

import aoc


def get_answer(lines, part):
    c_to_positions = defaultdict(list)
    for i, line in enumerate(lines):
        for j, c in enumerate(line):
            if c != '.':
                c_to_positions[c].append((i, j))
    n = len(lines)

    antinode_positions = set()
    for c in c_to_positions.keys():
        positions = c_to_positions[c]
        for a in range(len(positions) - 1):
            i_a, j_a = positions[a]  # 2, 5
            for b in range(a + 1, len(positions)):
                i_b, j_b = positions[b]  # 4, 4
                di, dj = i_b - i_a, j_b - j_a  # 2, -1
                if part == 1:
                    i_up, j_up = i_a - di, j_a - dj  # 0, 6
                    if 0 <= i_up < n and 0 <= j_up < len(lines[i_up]):
                        antinode_positions.add((i_up, j_up))
                    i_down, j_down = i_b + di, j_b + dj  # 6, 3
                    if 0 <= i_down < n and 0 <= j_down < len(lines[i_down]):
                        antinode_positions.add((i_down, j_down))
                else:
                    i, j = i_a, j_a
                    while 0 <= i < n and 0 <= j < len(lines[i]):
                        antinode_positions.add((i, j))
                        i -= di
                        j -= dj
                    i, j = i_a + di, j_a + dj
                    while 0 <= i < n and 0 <= j < len(lines[i]):
                        antinode_positions.add((i, j))
                        i += di
                        j += dj
    return len(antinode_positions)


if __name__ == '__main__':
    aoc.solve(8, get_answer)
