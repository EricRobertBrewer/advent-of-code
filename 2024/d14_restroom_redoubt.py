import aoc


def get_answer(lines, part):
    h, w = 103, 101
    h_mid, w_mid = (h - 1) // 2, (w - 1) // 2

    ps, vs = list(), list()
    for line in lines:
        if not line.startswith('p='):
            raise ValueError(f'Unexpected start of line: {line}')
        p_v = line[2:].split(' v=')
        ps.append(tuple(map(int, p_v[0].split(','))))
        vs.append(tuple(map(int, p_v[1].split(','))))

    if part == 1:
        seconds = 100
        ps_end = [((px + seconds * vx) % w, (py + seconds * vy) % h)
                  for (px, py), (vx, vy) in zip(ps, vs)]

        q1, q2, q3, q4 = 0, 0, 0, 0
        for x, y in ps_end:
            if x < w_mid:
                if y < h_mid:
                    q1 += 1
                elif y > h_mid:
                    q2 += 1
            elif x > w_mid:
                if y < h_mid:
                    q3 += 1
                elif y > h_mid:
                    q4 += 1
        return q1 * q2 * q3 * q4

    second = 0
    while True:
        # Find unique positions.
        p_to_count = dict()
        for p in ps:
            if p not in p_to_count.keys():
                p_to_count[p] = 1
            else:
                p_to_count[p] += 1

        # Find the largest contiguous group.
        visited = set()
        group_max = None
        for p_start in p_to_count.keys():
            frontier = [p_start]
            group = 0
            while len(frontier) > 0:
                p = frontier.pop()
                if p in visited:
                    continue
                visited.add(p)
                group += 1
                x, y = p
                for dy in range(-1, 2):
                    for dx in range(-1, 2):
                        if dy == 0 and dx == 0:
                            continue
                        p_adj = x + dx, y + dy
                        if p_adj in p_to_count.keys():
                            frontier.append(p_adj)
            if group_max is None or group > group_max:
                group_max = group

        # Print.
        if group_max >= len(ps) // 10:
            print_grid(p_to_count, h, w)
            q = input(f'Second={second:d} (\'q\' to quit):')
            if q == 'q':
                break

        # Update.
        ps = [((px + vx) % w, (py + vy) % h)
              for (px, py), (vx, vy) in zip(ps, vs)]
        second += 1
    return second


def print_grid(p_to_count, h, w):
    for y in range(h):
        for x in range(w):
            p = x, y
            if p in p_to_count.keys():
                count = p_to_count[p]
                if count < 10:
                    print(count, end='')
                else:
                    print('#', end='')
            else:
                print('.', end='')
        print()


if __name__ == '__main__':
    aoc.solve(14, get_answer)
