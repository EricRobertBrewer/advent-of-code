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

    p_to_cheat_to_d = dijkstra_breadth_first(grid, p_start, p_end)

    answer = 0
    cheat_to_d_end = p_to_cheat_to_d[p_end]
    d_end_none = cheat_to_d_end[None]
    for cheat in cheat_to_d_end.keys():
        d = cheat_to_d_end[cheat]
        if d_end_none - d >= 100:
            answer += 1
    return answer


def dijkstra_breadth_first(grid, p_start, p_end):
    deltas = ((-1, 0), (0, 1), (1, 0), (0, -1))

    p_to_cheat_to_d = dict()
    frontier = [(*p_start, None)]
    distance = 0
    while len(frontier) > 0:
        frontier_next = list()
        for i, j, cheat in frontier:
            p = i, j
            if p in p_to_cheat_to_d.keys():
                if None in p_to_cheat_to_d[p].keys() or cheat in p_to_cheat_to_d[p].keys():
                    # Don't bother back-tracking while carrying a cheat if you've already gotten there without one.
                    continue
            else:
                p_to_cheat_to_d[p] = dict()
            p_to_cheat_to_d[p][cheat] = distance

            if p == p_end:
                continue  # Don't explore from end.

            for di, dj in deltas:
                i_, j_ = i + di, j + dj
                if 0 <= i_ < len(grid) and 0 <= j_ < len(grid[i_]):
                    if grid[i_][j_] == '#':
                        if cheat is None:
                            frontier_next.append((i_, j_, (i_, j_)))
                    else:
                        frontier_next.append((i_, j_, cheat))

        frontier = frontier_next
        distance += 1

    return p_to_cheat_to_d


if __name__ == '__main__':
    aoc.solve(20, get_answer)
