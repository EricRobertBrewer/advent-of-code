import aoc


def get_answer(lines, part):
    grid = lines

    deltas = ((-1, 0), (0, 1), (1, 0), (0, -1))
    r = 0
    position_to_r = dict()
    region_positions = list()
    region_perimeters = list()
    for i, line in enumerate(lines):
        for j, c in enumerate(line):
            if (i, j) in position_to_r.keys():
                continue
            positions = list()
            perimeters = set()
            frontier = [(i, j)]
            while len(frontier) > 0:
                ip, jp = frontier.pop()
                if (ip, jp) in position_to_r.keys():
                    continue
                position_to_r[(ip, jp)] = r
                positions.append((ip, jp))
                for d, (di, dj) in enumerate(deltas):
                    ix, jx = ip + di, jp + dj
                    if 0 <= ix < len(grid) and 0 <= jx < len(grid[ix]) and grid[ix][jx] == c:
                        frontier.append((ix, jx))
                    else:
                        perimeters.add((ip, jp, d))
            region_positions.append(positions)
            region_perimeters.append(perimeters)
            r += 1

    answer = 0
    for positions, perimeters in zip(region_positions, region_perimeters):
        if part == 1:
            answer += len(positions) * len(perimeters)
        else:
            visited = set()
            sides = 0
            for i, j, d in perimeters:
                if (i, j, d) in visited:
                    continue
                visited.add((i, j, d))
                if d % 2 == 0:
                    for _, dj in (deltas[1], deltas[3]):
                        j_delta = j + dj
                        while 0 <= j_delta < len(grid[i]) and (i, j_delta, d) in perimeters:
                            visited.add((i, j_delta, d))
                            j_delta += dj
                else:
                    for di, _ in (deltas[0], deltas[2]):
                        i_delta = i + di
                        while 0 <= i_delta < len(grid) and 0 <= j < len(grid[i_delta]) and (i_delta, j, d) in perimeters:
                            visited.add((i_delta, j, d))
                            i_delta += di
                sides += 1
            answer += len(positions) * sides
    return answer


if __name__ == '__main__':
    aoc.solve(12, get_answer)
