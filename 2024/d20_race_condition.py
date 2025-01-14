import aoc


def get_answer(lines, part):
    grid = lines
    p_start, p_end = None, None
    for i, row in enumerate(grid):
        for j, c in enumerate(row):
            if c == 'S':
                if p_start is not None:
                    raise ValueError(f'Multiple starts: {p_start} and {(i, j)}')
                p_start = i, j
            if c == 'E':
                if p_end is not None:
                    raise ValueError(f'Multiple ends: {p_end} and {(i, j)}')
                p_end = i, j
    if p_start is None:
        raise ValueError('Unable to find start.')
    if p_end is None:
        raise ValueError('Unable to find end.')

    duration_cheat = 2 if part == 1 else 20

    p_to_d = dijkstra_grid(grid, p_start, None)
    p_to_cheat_to_d = dict()
    for p, d in p_to_d.items():
        i, j = p
        p_to_cheat_to_d[p] = dict()
        for distance_cheat in range(2, duration_cheat + 1):
            side_deltas = (zip(range(-distance_cheat, 1, 1), range(0, distance_cheat + 1, 1)),  # Top right.
                           zip(range(-distance_cheat, 1, 1), range(0, -distance_cheat - 1, -1)),  # Top left.
                           zip(range(distance_cheat, -1, -1), range(0, distance_cheat + 1, 1)),  # Bottom right.
                           zip(range(distance_cheat, -1, -1), range(0, -distance_cheat - 1, -1)))  # Bottom left.
            for deltas in side_deltas:
                for di, dj in deltas:
                    i_, j_ = i + di, j + dj
                    if (0 <= i_ < len(grid) and 0 <= j_ < len(grid[i_]) and grid[i_][j_] != '#' and
                            (i_, j_) in p_to_d.keys() and p_to_d[p] + distance_cheat < p_to_d[(i_, j_)]):
                        p_to_cheat_to_d[p][(i_, j_)] = p_to_d[p] + distance_cheat

    answer = 0
    for p, cheat_to_d in p_to_cheat_to_d.items():
        for p_cheat, d_cheat in cheat_to_d.items():
            if p_to_d[p_cheat] - d_cheat >= 100:
                answer += 1
    return answer


def dijkstra_grid(grid, p_start, p_end):
    deltas = ((-1, 0), (0, 1), (1, 0), (0, -1))
    p_to_d = dict()
    frontier = [p_start]
    distance = 0
    while len(frontier) > 0 and (p_end is None or p_end in p_to_d.keys()):
        frontier_next = list()
        for p in frontier:
            if p in p_to_d.keys():
                continue
            p_to_d[p] = distance

            i, j = p
            for di, dj in deltas:
                i_, j_ = i + di, j + dj
                if 0 <= i_ < len(grid) and 0 <= j_ < len(grid[i_]) and grid[i_][j_] != '#':
                    frontier_next.append((i_, j_))

        frontier = frontier_next
        distance += 1

    return p_to_d


if __name__ == '__main__':
    aoc.solve(20, get_answer)
