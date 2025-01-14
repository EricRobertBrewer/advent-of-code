import aoc


def get_answer(lines, part):
    ps = [tuple(map(int, reversed(line.split(',')))) for line in lines]  # As (y, x).

    h, w = 71, 71
    (i_start, j_start), (i_end, j_end) = (0, 0), (h - 1, w - 1)
    grid = [['.' for _ in range(w)] for _ in range(h)]

    for i, j in ps[:1024]:
        grid[i][j] = '#'

    if part == 1:
        d = dijkstra_breadth_first(grid, i_start, j_start, i_end, j_end)
        return d[i_end][j_end]

    for i_byte, j_byte in ps[1024:]:
        grid[i_byte][j_byte] = '#'
        d = dijkstra_breadth_first(grid, i_start, j_start, i_end, j_end)
        if d[i_end][j_end] == -1:
            return f'{j_byte:d},{i_byte:d}'  # As (x, y).

    raise ValueError('Unable to find blocking byte.')


def dijkstra_breadth_first(grid, i_start, j_start, i_end, j_end):
    deltas = ((-1, 0), (0, 1), (1, 0), (0, -1))
    h, w = len(grid), len(grid[0])

    d = [[-1 for _ in range(w)] for _ in range(h)]
    frontier = [(i_start, j_start)]
    distance = 0
    while len(frontier) > 0 and d[i_end][j_end] == -1:
        frontier_next = list()
        for i, j in frontier:
            if d[i][j] != -1:
                continue
            d[i][j] = distance

            for di, dj in deltas:
                i_, j_ = i + di, j + dj
                if 0 <= i_ < h and 0 <= j_ < w and grid[i_][j_] != '#':
                    frontier_next.append((i_, j_))
        frontier = frontier_next
        distance += 1
    return d


if __name__ == '__main__':
    aoc.solve(18, get_answer)
