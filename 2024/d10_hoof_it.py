import aoc

DELTAS = ((-1, 0), (0, 1), (1, 0), (0, -1))


def get_answer(lines, part):
    grid = [list(map(int, list(line))) for line in lines]  # [[int]]
    answer = 0
    for i, row in enumerate(grid):
        for j, x in enumerate(row):
            if x == 0:
                if part == 1:
                    peaks = set(find_peaks(grid, i, j))
                else:
                    peaks = list(find_peaks(grid, i, j))
                answer += len(peaks)
    return answer


def find_peaks(grid, i, j):
    x = grid[i][j]
    if x == 9:
        yield i, j
    else:
        for delta in DELTAS:
            i_, j_ = i + delta[0], j + delta[1]
            if 0 <= i_ < len(grid) and 0 <= j_ < len(grid[i_]) and grid[i_][j_] == x + 1:
                for peak in find_peaks(grid, i_, j_):
                    yield peak


if __name__ == '__main__':
    aoc.solve(10, get_answer)
