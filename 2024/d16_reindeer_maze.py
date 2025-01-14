from queue import PriorityQueue

import aoc


def get_answer(lines, part):
    grid = lines
    p_start, p_end = None, None
    for i, line in enumerate(lines):
        for j, c in enumerate(line):
            if c == 'S':
                if p_start is not None:
                    raise ValueError(f'Multiple starting locations: {p_start}, {(i, j)}')
                p_start = i, j
            elif c == 'E':
                if p_end is not None:
                    raise ValueError(f'Multiple ending locations: {p_end}, {(i, j)}')
                p_end = i, j
    if p_start is None:
        raise ValueError('Unable to find starting location.')
    if p_end is None:
        raise ValueError('Unable to find ending location.')

    deltas = ((-1, 0), (0, 1), (1, 0), (0, -1))

    p_direction_to_d = dict()
    p_direction_to_backs = dict()
    q = PriorityQueue()
    q.put((0, (*p_start, 1), None))  # Facing east; no back-pointer.
    d_target = None
    p_direction_targets = list()
    while not q.empty():
        d, p_direction, back = q.get()
        if d_target is not None and d > d_target:
            break
        i, j, direction = p_direction
        if p_direction in p_direction_to_d.keys():
            if d == p_direction_to_d[p_direction]:
                p_direction_to_backs[p_direction].append(back)
            continue
        p_direction_to_d[p_direction] = d
        p_direction_to_backs[p_direction] = [back]
        if (i, j) == p_end:
            d_target = d
            p_direction_targets.append(p_direction)

        # Forward.
        di, dj = deltas[direction]
        if grid[i + di][j + dj] != '#':
            q.put((d + 1, (i + di, j + dj, direction), p_direction))

        # Turn.
        for direction_turn in ((direction + 3) % 4, (direction + 1) % 4):
            q.put((d + 1000, (i, j, direction_turn), p_direction))

    if part == 1:
        return d_target

    visited = set()
    frontier = [_ for _ in p_direction_targets]
    while len(frontier) > 0:
        p_direction = frontier.pop()
        if p_direction in visited:
            continue
        visited.add(p_direction)

        backs = p_direction_to_backs[p_direction]
        for back in backs:
            if back is not None:
                frontier.append(back)

    return len(set((i, j) for i, j, _ in visited))


if __name__ == '__main__':
    aoc.solve(16, get_answer)
