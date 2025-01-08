from collections import defaultdict

import aoc


def get_answer(lines, part):
    grid = [list(line) for line in lines]  # Mutable.
    position_start = None
    for i, row in enumerate(grid):
        for j, c in enumerate(row):
            if c == '^':
                if position_start is not None:
                    raise ValueError(f'Found multiple starting positions: {position_start} and {(i, j)}')
                position_start = (i, j)
    if position_start is None:
        raise ValueError('Unable to find starting position.')

    position_to_directions = traverse(grid, position_start, 0)
    if part == 1:
        return len(position_to_directions.keys())

    obstacle_positions = set()
    for position in position_to_directions.keys():
        if position == position_start:
            continue
        i, j = position
        grid[i][j] = '#'
        try:
            _ = traverse(grid, position_start, 0)
        except LoopError:
            obstacle_positions.add(position)
        grid[i][j] = '.'
    return len(obstacle_positions)


def traverse(grid, position_start, direction_start):
    deltas = ((-1, 0), (0, 1), (1, 0), (0, -1))
    direction = direction_start
    position = position_start
    position_to_directions = defaultdict(set)
    while True:
        if direction in position_to_directions[position]:
            raise LoopError()
        position_to_directions[position].add(direction)

        i_front = position[0] + deltas[direction][0]
        j_front = position[1] + deltas[direction][1]
        if not (0 <= i_front < len(grid)) or not (0 <= j_front < len(grid[i_front])):
            break

        if grid[i_front][j_front] == '#':
            # Turn.
            direction = (direction + 1) % 4
        else:
            # Move forward.
            position = (i_front, j_front)

    return position_to_directions


class LoopError(Exception):
    pass


if __name__ == '__main__':
    aoc.solve(6, get_answer)
