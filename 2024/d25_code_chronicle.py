import aoc


def get_answer(lines, part):
    locks, keys = list(), list()
    l = 0
    while l < len(lines):
        grid = lines[l:l + 7]
        if not all(len(line) == 5 for line in grid):
            raise ValueError('Bad grid: {}'.format('\n'.join(grid)))
        if grid[0] == '#####' and grid[-1] == '.....':
            locks.append(get_code(grid))
        elif grid[0] == '.....' and grid[-1] == '#####':
            keys.append(get_code(list(reversed(grid))))
        else:
            raise ValueError(f'Schematic at line {l:d} is neither a lock nor key: {grid[0]} {grid[-1]}')
        l += 8

    answer = 0
    for lock in locks:
        for key in keys:
            if all(lock[j] + key[j] < 6 for j in range(5)):
                answer += 1
    return answer


def get_code(grid):
    heights = list()
    for j in range(5):
        i = 1
        while grid[i][j] == '#':
            i += 1
        heights.append(i - 1)
    return tuple(heights)


if __name__ == '__main__':
    aoc.solve(25, get_answer)
