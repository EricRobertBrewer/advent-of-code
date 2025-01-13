import aoc


def get_answer(lines, part):
    l_blank = None
    for l, line in enumerate(lines):
        if line == '':
            if l_blank is not None:
                raise ValueError(f'Multiple blank lines found: {l_blank:d} and {l:d}')
            l_blank = l
    if l_blank is None:
        raise ValueError('Unable to find blank line.')

    grid = [list(line) for line in lines[:l_blank]]  # Mutable.
    p_robot = None
    for i, row in enumerate(grid):
        for j, c in enumerate(row):
            if c == '@':
                if p_robot is not None:
                    raise ValueError(f'Found multiple robot starting locations: {p_robot} and {(i, j)}')
                p_robot = i, j
    if p_robot is None:
        raise ValueError('Unable to find robot starting location.')
    c_box = 'O'

    if part == 2:
        c_to_cc = {'#': '##', 'O': '[]', '.': '..', '@': '@.'}
        grid = [list(''.join([c_to_cc[c] for c in row]))
                for row in grid]
        p_robot = p_robot[0], p_robot[1] * 2
        c_box = '['

    moves = ''.join(lines[l_blank + 1:])
    move_to_delta = {'^': (-1, 0), '>': (0, 1), 'v': (1, 0), '<': (0, -1)}

    for move in moves:
        # print()
        # print(f'Move: {move}; {p_robot}')
        # for row in grid:
        #     print(''.join(row))

        if move not in move_to_delta.keys():
            raise ValueError(f'Unknown move: {move}')
        di, dj = move_to_delta[move]
        i, j = p_robot

        if part == 1 or move == '<' or move == '>':
            while grid[i][j] != '#' and grid[i][j] != '.':
                i, j = i + di, j + dj

            if grid[i][j] == '.':
                while (i, j) != p_robot:
                    grid[i][j] = grid[i - di][j - dj]
                    i, j = i - di, j - dj
                grid[i][j] = '.'
                p_robot = i + di, j + dj
        else:
            row_objs = [{(i, j)}]
            blocked = False
            while not blocked:
                objs_next = set()
                for i_obj, j_obj in row_objs[-1]:
                    i_next, j_next = i_obj + di, j_obj + dj  # dj is always `0`, though.
                    c_next = grid[i_next][j_next]
                    if c_next == '#':
                        blocked = True
                        break
                    elif c_next == '[':
                        objs_next.add((i_next, j_next))
                        objs_next.add((i_next, j_next + 1))
                    elif c_next == ']':
                        objs_next.add((i_next, j_next - 1))
                        objs_next.add((i_next, j_next))

                if blocked or len(objs_next) == 0:
                    break
                row_objs.append(objs_next)

            if not blocked:
                for objs in row_objs[::-1]:
                    for i_obj, j_obj in objs:
                        grid[i_obj + di][j_obj + dj] = grid[i_obj][j_obj]
                        grid[i_obj][j_obj] = '.'
                p_robot = i + di, j + dj

    answer = 0
    for i, row in enumerate(grid):
        for j, c in enumerate(row):
            if c == c_box:
                answer += 100 * i + j
    return answer


if __name__ == '__main__':
    aoc.solve(15, get_answer)
