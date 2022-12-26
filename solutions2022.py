import argparse
import functools
import math
import operator
import time

import aoc_util
import cs_util

YEAR = '2022'


def main():
    parser = argparse.ArgumentParser(
        description='Advent of Code 2022'
    )
    parser.add_argument(
        'day',
        type=int,
        help='Day of the month.'
    )
    parser.add_argument(
        'part',
        type=int,
        help='Part (1 or 2) of the daily problem.'
    )
    parser.add_argument(
        '--example',
        '-x',
        action='store_true',
        help='Use example file instead of daily input. Default is false.'
    )
    args = parser.parse_args()
    day = args.day
    part = args.part
    example = args.example

    start = time.time()
    answer = solve(day, part, example=example)
    duration = time.time() - start
    print(answer)
    print('Time: {:.3f} s'.format(duration))


def solve(day: int, part: int, example: bool = False):
    if example:
        day_path = aoc_util.get_example_day_path(YEAR, day)
    else:
        day_path = aoc_util.download_input_if_needed(YEAR, day)
    with open(day_path, 'r') as fd:
        lines = [line.strip('\n') for line in fd.readlines()]

    key = str(day) + '-' + str(part)
    if key not in SOLVERS:
        raise ValueError('No solution for day `{:d}` part `{:d}`'.format(day, part))
    solver = SOLVERS[key]
    return solver(lines)


def d01_1_calorie_counting(lines):
    max_ = None
    sum_ = 0
    for line in lines:
        if len(line) == 0:
            if max_ is None or sum_ > max_:
                max_ = sum_
            sum_ = 0
        else:
            sum_ += int(line)
    if max_ is None or sum_ > max_:
        max_ = sum_
    return max_


def d01_2_calorie_counting(lines):
    top = list()  # Ascending.
    sum_ = 0
    for line in lines:
        if len(line) == 0:
            if len(top) < 3:
                top.append(sum_)
                top.sort()
            elif sum_ > top[0]:
                top[0] = sum_
                top.sort()
            sum_ = 0
        else:
            sum_ += int(line)
    if sum_ > top[0]:
        top[0] = sum_
        top.sort()
    return sum(top)


def d02_1_rock_paper_scissors(lines):
    sum_ = 0
    opp = {'A': 0, 'B': 1, 'C': 2}
    you = {'X': 0, 'Y': 1, 'Z': 2}

    for line in lines:
        o, y = line.split(' ')
        outcome = 3 * ((4 + you[y] - opp[o]) % 3)
        sum_ += outcome
        sum_ += you[y] + 1
    return sum_


def d02_2_rock_paper_scissors(lines):
    sum_ = 0
    opp = {'A': 0, 'B': 1, 'C': 2}
    outcome = {'X': 0, 'Y': 1, 'Z': 2}

    for line in lines:
        o, u = line.split(' ')
        sum_ += 3 * outcome[u]
        you = (2 + opp[o] + outcome[u]) % 3
        sum_ += you + 1
    return sum_


def d03_1_rucksack_reorganization(lines):
    priority = {c: i + 1 for i, c in
                enumerate('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')}
    x = 0
    for line in lines:
        n = len(line)//2
        items = set(line[:n])
        for c in line[n:]:
            if c in items:
                x += priority[c]
                break
    return x


def d03_2_rucksack_reorganization(lines):
    priority = {c: i + 1 for i, c in
                enumerate('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')}
    x = 0
    for i in range(0, len(lines), 3):
        groups = lines[i:i+3]
        common = set(groups[0]).intersection(set(groups[1])).intersection(set(groups[2]))
        if len(common) != 1:
            raise ValueError('Bad groups: {}'.format('\n'.join(groups)))
        c = next(iter(common))
        x += priority[c]
    return x


def d04_1_camp_cleanup(lines):
    x = 0
    for line in lines:
        a, b = [tuple(map(int, part.split('-'))) for part in line.split(',')]
        if (a[0] <= b[0] and a[1] >= b[1]) or (b[0] <= a[0] and b[1] >= a[1]):
            x += 1
    return x


def d04_2_camp_cleanup(lines):
    x = 0
    for line in lines:
        a, b = [tuple(map(int, part.split('-'))) for part in line.split(',')]
        if (a[0] <= b[0] and a[1] >= b[1]) or (b[0] <= a[0] and b[1] >= a[1]) or \
           (a[0] <= b[0] <= a[1]) or (b[0] <= a[0] <= b[1]):
            x += 1
    return x


def d05_1_supply_stacks(lines, part=1):
    crate_lines = list()
    for line in lines:
        if '[' not in line:
            break
        crate_lines.append(line)
    crates = [list() for _ in range(len(crate_lines[0])//4+1)]
    for crate_line in crate_lines[::-1]:
        for i in range(len(crates)):
            crate = crate_line[4*i+1]
            if crate != ' ':
                crates[i].append(crate)

    for line in lines[len(crate_lines)+2:]:
        parts = line.split(' ')
        n, src, dst = int(parts[1]), int(parts[3]), int(parts[5])
        head, tail = crates[src-1][:-n], crates[src-1][-n:]
        crates[src-1] = head
        if part == 1:
            crates[dst-1].extend(tail[::-1])
        else:
            crates[dst-1].extend(tail)
    return ''.join(crate[-1] for crate in crates)


def d05_2_supply_stacks(lines):
    return d05_1_supply_stacks(lines, part=2)


def d06_1_tuning_trouble(lines, k=4):
    s = lines[0]
    for i in range(k, len(s)):
        if len(set(s[i-k:i])) == k:
            return i
    return -1


def d06_2_tuning_trouble(lines):
    return d06_1_tuning_trouble(lines, k=14)


def _d07_no_space_left_on_device(lines):
    # Parse file structure.
    root = dict()
    dirs = [root]
    ls = False
    for line in lines:
        parts = line.split(' ')
        if parts[0] == '$':
            cmd = parts[1]
            if cmd == 'cd':
                dst = parts[2]
                if dst == '/':
                    dirs = [root]
                elif dst == '..':
                    if len(dirs) == 1:
                        raise ValueError('Cannot `cd` out of `/`.')
                    dirs = dirs[:-1]
                else:
                    wd = dirs[-1]
                    if dst not in wd.keys():
                        raise ValueError('Directory `{}` not found.'.format(dst))
                    dirs.append(wd[dst])
                ls = False
            elif cmd == 'ls':
                ls = True
            else:
                raise ValueError('Unknown command `{}`.'.format(cmd))
        elif ls:
            wd = dirs[-1]
            name = parts[1]
            if name not in wd.keys():
                if parts[0] == 'dir':
                    wd[name] = dict()
                else:
                    wd[name] = int(parts[0])
        else:
            raise ValueError('Unexpected file listing without `ls` command `{}`.'.format(line))

    # Calculate sizes.
    def _get_size(_dir: dict, _path: str, _sizes: dict):
        if _path in _sizes.keys():
            return _sizes[_path]
        _size = 0
        for _k, _v in _dir.items():
            if type(_v) == int:
                _size += _v
            else:
                _size += _get_size(_v, _path + _k + '/', _sizes)
        _sizes[_path] = _size
        return _size

    sizes = dict()
    _ = _get_size(root, '/', sizes)
    return sizes


def d07_1_no_space_left_on_device(lines):
    sizes = _d07_no_space_left_on_device(lines)
    return sum(size for size in sizes.values() if size < 100000)


def d07_2_no_space_left_on_device(lines):
    sizes = _d07_no_space_left_on_device(lines)
    target = 30000000 - (70000000 - sizes['/'])
    # TODO Binary search.
    for size in sorted(list(sizes.values())):
        if size >= target:
            return size
    raise ValueError('No directory found with size `{:d}`.'.format(target))


def d08_1_treetop_tree_house(lines):
    grid = [list(map(int, line)) for line in lines]
    assert all(len(grid[i]) == len(grid[0]) for i in range(1, len(grid)))
    h, w = len(grid), len(grid[0])

    max_top, max_bottom, max_left, max_right =\
        [[[grid[i][j] for j in range(w)] for i in range(h)] for _ in range(4)]
    for i in range(1, h-2):
        for j in range(w):
            max_top[i][j] = max(max_top[i][j], max_top[i-1][j])
    for i in range(h-2, 1, -1):
        for j in range(w):
            max_bottom[i][j] = max(max_bottom[i][j], max_bottom[i+1][j])
    for j in range(1, w-2):
        for i in range(h):
            max_left[i][j] = max(max_left[i][j], max_left[i][j-1])
    for j in range(w-2, 1, -1):
        for i in range(h):
            max_right[i][j] = max(max_right[i][j], max_right[i][j+1])

    x = 2 * h + 2 * w - 4  # Visible trees.
    for i in range(1, h-1):
        for j in range(1, w-1):
            v = grid[i][j]
            if max_top[i-1][j] < v or max_bottom[i+1][j] < v or max_left[i][j-1] < v or max_right[i][j+1] < v:
                x += 1
    return x


def d08_2_treetop_tree_house(lines):
    grid = [list(map(int, line)) for line in lines]
    assert all(len(grid[i]) == len(grid[0]) for i in range(1, len(grid)))
    h, w = len(grid), len(grid[0])

    s_max = None
    for i in range(1, h-1):
        for j in range(1, w-1):
            up, down, left, right = 1, 1, 1, 1
            v = grid[i][j]
            while i-up-1 >= 0 and grid[i-up][j] < v:
                up += 1
            while i+down+1 < h and grid[i+down][j] < v:
                down += 1
            while j-left-1 >= 0 and grid[i][j-left] < v:
                left += 1
            while j+right+1 < w and grid[i][j+right] < v:
                right += 1
            s = up * down * left * right
            if s_max is None or s > s_max:
                s_max = s
    return s_max


def d09_1_rope_bridge(lines, k=2):
    knots = [[0, 0] for _ in range(k)]
    head = knots[0]
    tail = knots[-1]
    coords = {tuple(tail)}
    for line in lines:
        direction, steps = line.split(' ')
        sx, sy = 0, 0
        if direction == 'U':
            sy = 1
        elif direction == 'D':
            sy = -1
        elif direction == 'L':
            sx = -1
        elif direction == 'R':
            sx = 1
        else:
            raise ValueError('Unexpected direction: {}'.format(direction))
        for _ in range(int(steps)):
            head[0] += sx
            head[1] += sy
            for i in range(1, len(knots)):
                prev = knots[i-1]
                knot = knots[i]
                dx, dy = prev[0] - knot[0], prev[1] - knot[1]
                if abs(dx) == 2 and abs(dy) == 2:
                    knot[0] += dx // 2
                    knot[1] += dy // 2
                elif abs(dx) == 2:
                    knot[0] += dx // 2
                    knot[1] += dy
                elif abs(dy) == 2:
                    knot[0] += dx
                    knot[1] += dy // 2
                else:
                    break
            coords.add(tuple(tail))
    return len(coords)


def d09_2_rope_bridge(lines):
    return d09_1_rope_bridge(lines, k=10)


def _d10_cathode_ray_tube(lines, while_cond, exec_fn):
    cycle = 1
    wait = 0
    x = 1
    v = None
    i = 0
    while while_cond(cycle):
        if wait == 0:
            if v is not None:
                x += v
                v = None
            line = lines[i]
            parts = line.split(' ')
            cmd = parts[0]
            if cmd == 'addx':
                v = int(parts[1])
                wait = 1
            elif cmd == 'noop':
                pass
            i += 1
        else:
            wait -= 1

        exec_fn(cycle, x)

        cycle += 1


def d10_1_cathode_ray_tube(lines):
    sum_ = 0
    signals = (20, 60, 100, 140, 180, 220)
    j = 0

    def _while_cond(_):
        return j < len(signals)

    def _exec_fn(_cycle, _x):
        nonlocal j, sum_  # Needed because these variables are ASSIGNED!!
        if _cycle == signals[j]:
            sum_ += _x * _cycle
            j += 1

    _d10_cathode_ray_tube(lines, _while_cond, _exec_fn)
    return sum_


def d10_2_cathode_ray_tube(lines):
    width = 40
    height = 6
    pixels = [['.' for _ in range(width)] for _ in range(height)]

    def _while_cond(_cycle):
        return _cycle <= width * height

    def _exec_fn(_cycle, _x):
        _px = (_cycle-1) % width
        if abs(_x - _px) <= 1:
            _py = (_cycle-1) // width
            pixels[_py][_px] = '#'

    _d10_cathode_ray_tube(lines, _while_cond, _exec_fn)
    rows = [''.join(row) for row in pixels]
    for row in rows:
        print(row)
    return None


def _d11_monkey_in_the_middle(lines):
    monkey_items = list()
    operations = list()
    tests = list()
    monkey_targets = list()

    monkey_prefix = 'Monkey '
    monkey_suffix = ':'
    items_prefix = '  Starting items: '
    operation_prefix = '  Operation: new = old '
    test_prefix = '  Test: divisible by '
    target_true_prefix = '    If true: throw to monkey '
    target_false_prefix = '    If false: throw to monkey '
    i = 0
    while i < len(lines):
        if len(lines[i]) == 0:
            i += 1
            continue

        line_monkey = lines[i]
        assert line_monkey.startswith(monkey_prefix) and line_monkey.endswith(monkey_suffix)
        assert int(line_monkey[len(monkey_prefix):-len(monkey_suffix)]) == len(monkey_items)

        line_items = lines[i+1]
        assert line_items.startswith(items_prefix)
        monkey_items.append(list(map(int, line_items[len(items_prefix):].split(', '))))

        line_operation = lines[i+2]
        assert line_operation.startswith(operation_prefix)
        op, operand = line_operation[len(operation_prefix):].split(' ')
        assert op in {'+', '*'}
        operations.append((op, operand))

        line_test = lines[i+3]
        assert line_test.startswith(test_prefix)
        tests.append(int(line_test[len(test_prefix):]))

        line_target_true = lines[i+4]
        assert line_target_true.startswith(target_true_prefix)
        target_true = int(line_target_true[len(target_true_prefix):])
        line_target_false = lines[i+5]
        assert line_target_false.startswith(target_false_prefix)
        target_false = int(line_target_false[len(target_false_prefix):])
        monkey_targets.append((target_true, target_false))

        i += 6

    return monkey_items, operations, tests, monkey_targets


def d11_1_monkey_in_the_middle(lines):
    monkey_items, operations, tests, monkey_targets = _d11_monkey_in_the_middle(lines)
    inspection_counts = [0 for _ in monkey_items]
    for _ in range(20):
        for monkey, items in enumerate(monkey_items):
            while len(items) > 0:
                item = items.pop(0)
                op, operand = operations[monkey]
                operand_ = item if operand == 'old' else int(operand)
                if op == '+':
                    item += operand_
                else:  # op == '*'
                    item *= operand_
                item //= 3
                if item % tests[monkey] == 0:
                    target = monkey_targets[monkey][0]
                else:
                    target = monkey_targets[monkey][1]
                monkey_items[target].append(item)
                inspection_counts[monkey] += 1
    top_counts = sorted(inspection_counts)[-2:]
    return top_counts[0] * top_counts[1]


def d11_2_monkey_in_the_middle(lines):
    monkey_items, operations, tests, monkey_targets = _d11_monkey_in_the_middle(lines)
    monkey_item_mods = [[{test: item % test for test in set(tests)}
                         for item in items]
                        for items in monkey_items]
    inspection_counts = [0 for _ in monkey_item_mods]
    for _ in range(10000):
        for monkey, item_mods in enumerate(monkey_item_mods):
            while len(item_mods) > 0:
                item_mod = item_mods.pop(0)
                op, operand = operations[monkey]
                for test, value in item_mod.items():
                    operand_ = value if operand == 'old' else int(operand)
                    if op == '+':
                        item_mod[test] = (value + operand_) % test
                    else:  # op == '*'
                        item_mod[test] = (value * (operand_ % test)) % test
                if item_mod[tests[monkey]] == 0:
                    target = monkey_targets[monkey][0]
                else:
                    target = monkey_targets[monkey][1]
                monkey_item_mods[target].append(item_mod)
                inspection_counts[monkey] += 1
    top_counts = sorted(inspection_counts)[-2:]
    return top_counts[0] * top_counts[1]


def _d12_hill_climbing_algorithm(lines):
    assert all(len(line) == len(lines[0]) for line in lines[1:])
    n, m = len(lines), len(lines[0])
    letter_to_elevation = {letter: i + 1
                           for i, letter in enumerate('abcdefghijklmnopqrstuvwxyz')}
    letter_to_elevation['S'] = letter_to_elevation['a']
    letter_to_elevation['E'] = letter_to_elevation['z']
    si, sj, ei, ej = None, None, None, None
    for i, line in enumerate(lines):
        for j, c in enumerate(line):
            if c == 'S':
                si, sj = i, j
            elif c == 'E':
                ei, ej = i, j
    assert all(v is not None for v in (si, sj, ei, ej))

    return n, m, si, sj, ei, ej, letter_to_elevation


def d12_1_hill_climbing_algorithm(lines):
    n, m, si, sj, ei, ej, letter_to_elevation = _d12_hill_climbing_algorithm(lines)

    def _is_neighbor_fn(_i, _j, _oi, _oj):
        _elevation_change = letter_to_elevation[lines[_oi][_oj]] - letter_to_elevation[lines[_i][_j]]
        return _elevation_change <= 1  # Invalid (edge does not exist, according to problem).

    return cs_util.dijkstra_grid(n, m, si, sj, ei, ej, is_neighbor_fn=_is_neighbor_fn)


def d12_2_hill_climbing_algorithm(lines):
    n, m, si, sj, ei, ej, letter_to_elevation = _d12_hill_climbing_algorithm(lines)

    def _is_neighbor_fn(_i, _j, _oi, _oj):
        _elevation_change = letter_to_elevation[lines[_i][_j]] - letter_to_elevation[lines[_oi][_oj]]
        return _elevation_change <= 1  # Flipped from previous; starting from `E`.

    d = cs_util.dijkstra_grid(n, m, ei, ej, ei=None, ej=None, is_neighbor_fn=_is_neighbor_fn)
    z_min = None
    for i in range(n):
        for j in range(m):
            if lines[i][j] == 'S' or lines[i][j] == 'a':
                if d[i][j] == -1:
                    continue
                if z_min is None or d[i][j] < z_min:
                    z_min = d[i][j]
    return z_min


def _d13_distress_signal(lines):
    pairs = list()

    def _parse_line(_line):
        if _line[0] != '[':
            raise ValueError()

        def __parse_list(__s):
            __a = list()
            __i = 0
            __last = None
            while __i < len(__s):
                __c = __s[__i]
                if __c == ']':
                    return __a, __i + 1
                if __c == '[':
                    __list, __n = __parse_list(__s[__i + 1:])
                    __a.append(__list)
                    __i += __n
                elif __c == ',':
                    pass
                elif __last is None or __last == ',':
                    __a.append(int(__c))
                else:
                    __a[-1] = __a[-1] * 10 + int(__c)
                __last = __c
                __i += 1

        _list, _n = __parse_list(_line[1:])
        assert _n == len(_line) - 1
        return _list

    i = 0
    while i < len(lines):
        if len(lines[i]) == 0:
            i += 1
            continue
        assert len(lines[i+1]) > 0
        pairs.append((_parse_line(lines[i]), _parse_line(lines[i+1])))
        i += 2

    def _compare(_left, _right):
        if type(_left) == int and type(_right) == int:
            if _left < _right:
                return -1
            elif _left > _right:
                return 1
            return 0

        if type(_left) == int and type(_right) == list:
            return _compare([_left], _right)

        if type(_left) == list and type(_right) == int:
            return _compare(_left, [_right])

        if type(_left) == list and type(_right) == list:
            _i = 0
            while _i < len(_left):
                if _i >= len(_right):
                    return 1
                v = _compare(_left[_i], _right[_i])
                if v != 0:
                    return v
                _i += 1
            if _i < len(_right):
                return -1
            return 0

        return 0

    return pairs, _compare


def d13_1_distress_signal(lines):
    pairs, _compare = _d13_distress_signal(lines)
    x = 0
    for index, (left, right) in enumerate(pairs):
        if _compare(left, right) == -1:
            x += (index + 1)
    return x


def d13_2_distress_signal(lines):
    pairs, _compare = _d13_distress_signal(lines)
    packets = list()
    for left, right in pairs:
        packets.append(left)
        packets.append(right)
    two, six = [[2]], [[6]]
    packets.append(two)
    packets.append(six)
    packets.sort(key=functools.cmp_to_key(_compare))
    i2, i6 = packets.index(two), packets.index(six)
    return (i2 + 1) * (i6 + 1)


def _d14_regolith_reservoir(lines):
    rock_paths = list()
    x_min, x_max, y_max = None, None, None
    for line in lines:
        path = [tuple(map(int, x_y.split(','))) for x_y in line.split(' -> ')]
        if x_min is None:
            x_max, y_max = path[0]
            x_min = x_max
        for x, y in path[1:]:
            x_min = min(x, x_min)
            x_max = max(x, x_max)
            y_max = max(y, y_max)
        rock_paths.append(path)

    n, m = y_max + 1, x_max - x_min + 1
    cave = [['.' for _ in range(m)] for _ in range(n)]
    for path in rock_paths:
        x_prev, y_prev = path[0]
        for x, y in path[1:]:
            if x != x_prev:
                assert y == y_prev
                i = y
                start = min(x, x_prev) - x_min
                stop = max(x, x_prev) - x_min + 1
                for j in range(start, stop):
                    cave[i][j] = '#'
            elif y != y_prev:
                assert x == x_prev
                j = x - x_min
                start = min(y, y_prev)
                stop = max(y, y_prev) + 1
                for i in range(start, stop):
                    cave[i][j] = '#'
            x_prev, y_prev = x, y

    return cave, n, m, x_min


def d14_1_regolith_reservoir(lines):
    cave, n, m, x_min = _d14_regolith_reservoir(lines)
    sand = 0
    while True:
        coords = 500, 0
        while True:
            x, y = coords
            i, j = y, x - x_min
            if i + 1 == n:
                coords = None  # Bottom boundary.
                break
            if cave[i+1][j] == '.':
                coords = x, y + 1
                continue
            if j == 0:
                coords = None  # Left boundary.
                break
            if cave[i+1][j-1] == '.':
                coords = x - 1, y + 1
                continue
            if j == m - 1:
                coords = None  # Right boundary.
                break
            if cave[i+1][j+1] == '.':
                coords = x + 1, y + 1
                continue
            break  # Sand rests.
        if coords is None:
            break
        x, y = coords
        i, j = y, x - x_min
        cave[i][j] = 'o'
        sand += 1
    return sand


def d14_2_regolith_reservoir(lines):
    cave, n, m, x_min = _d14_regolith_reservoir(lines)
    cave.append(['.' for _ in range(m)])
    n += 1
    sand = 0
    while cave[0][500-x_min] == '.':
        coords = 500, 0
        while True:
            x, y = coords
            i, j = y, x - x_min
            if i + 1 == n:
                break  # Rest at bottom boundary.
            if cave[i+1][j] == '.':
                coords = x, y + 1
                continue
            if j == 0:
                # Extend left boundary.
                for row in cave:
                    row.insert(0, '.')
                x_min -= 1
                m += 1
                continue
            if cave[i+1][j-1] == '.':
                coords = x - 1, y + 1
                continue
            if j == m - 1:
                # Extend right boundary.
                for row in cave:
                    row.append('.')
                m += 1
                continue
            if cave[i+1][j+1] == '.':
                coords = x + 1, y + 1
                continue
            break  # Sand rests.
        x, y = coords
        i, j = y, x - x_min
        cave[i][j] = 'o'
        sand += 1
    return sand


def _d15_beacon_exclusion_zone(lines):
    sensor_beacons = list()
    sensor_prefix = 'Sensor at '
    beacon_prefix = 'closest beacon is at '
    for line in lines:
        sensor, beacon = line.split(': ')
        assert sensor.startswith(sensor_prefix)
        sxq, syq = sensor[len(sensor_prefix):].split(', ')
        sx, sy = tuple(map(int, map(operator.itemgetter(1), [q.split('=') for q in (sxq, syq)])))
        assert beacon.startswith(beacon_prefix)
        bxq, byq = beacon[len(beacon_prefix):].split(', ')
        bx, by = tuple(map(int, map(operator.itemgetter(1), [q.split('=') for q in (bxq, byq)])))
        sensor_beacons.append((sx, sy, bx, by))
    return sensor_beacons


def d15_1_beacon_exclusion_zone(lines):
    sensor_beacons = _d15_beacon_exclusion_zone(lines)
    y = 2_000_000  # 10 (example).
    x_ranges = list()
    for sx, sy, bx, by in sensor_beacons:
        dist = abs(sx - bx) + abs(sy - by)  # Manhattan.
        if sy < y:
            if y - sy > dist:
                continue
            radius = dist - (y - sy)
            xl, xr = sx - radius, sx + radius
        elif sy > y:
            if sy - y > dist:
                continue
            radius = dist - (sy - y)
            xl, xr = sx - radius, sx + radius
        else:
            xl, xr = sx - dist, sx + dist
        # Exclude beacons lying on the lateral.
        if by == y:
            if xl == bx and xr == bx:
                continue
            elif xl == bx:
                xl += 1
            elif xr == bx:
                xr -= 1
        x_ranges.append((xl, xr))
    # Merge overlapping ranges.
    x_ranges.sort()
    i = 1
    while i < len(x_ranges):
        (x1l, x1r), (x2l, x2r) = x_ranges[i-1], x_ranges[i]
        if x1r >= x2l:
            x_ranges = x_ranges[:i-1] + [(x1l, max(x1r, x2r))] + x_ranges[i+1:]
        else:
            i += 1
    return sum(xr - xl + 1 for xl, xr in x_ranges)


def d15_2_beacon_exclusion_zone(lines):
    sensor_beacons = _d15_beacon_exclusion_zone(lines)
    y_max = x_max = 4_000_000
    y_x_ranges = [list() for _ in range(y_max + 1)]
    for sx, sy, bx, by in sensor_beacons:
        dist = abs(sx - bx) + abs(sy - by)  # Manhattan.
        for y, x_ranges in enumerate(y_x_ranges):
            if sy < y:
                if y - sy > dist:
                    continue
                radius = dist - (y - sy)
                xl, xr = sx - radius, sx + radius
            elif sy > y:
                if sy - y > dist:
                    continue
                radius = dist - (sy - y)
                xl, xr = sx - radius, sx + radius
            else:
                xl, xr = sx - dist, sx + dist
            x_ranges.append((xl, xr))
    # Merge overlapping ranges.
    for y, x_ranges in enumerate(y_x_ranges):
        x_ranges.sort()
        i = 1
        while i < len(x_ranges):
            (x1l, x1r), (x2l, x2r) = x_ranges[i-1], x_ranges[i]
            if x1r >= x2l:
                x_ranges = x_ranges[:i-1] + [(x1l, max(x1r, x2r))] + x_ranges[i+1:]
            else:
                i += 1
        for xl, xr in x_ranges:
            if 0 <= xr <= x_max:
                return 4_000_000 * (xr + 1) + y
    raise ValueError()


def _d16_proboscidea_volcanium(lines):
    valve_to_flow = dict()
    valve_to_tunnels = dict()
    valve_prefix = 'Valve '
    valve_infix = ' has flow rate='
    for line in lines:
        s_valve_flow, s_tunnels = line.split('; ')
        s_valve, s_flow = s_valve_flow.split(valve_infix)
        assert s_valve.startswith(valve_prefix)
        valve = s_valve[len(valve_prefix):]
        assert valve not in valve_to_flow.keys()
        valve_to_flow[valve] = int(s_flow)
        tunnel0_prefixed, *tunnels_cdr = s_tunnels.split(', ')
        tunnel0 = tunnel0_prefixed[tunnel0_prefixed.rindex(' ')+1:]
        valve_to_tunnels[valve] = {tunnel0, *tunnels_cdr}
    valves = sorted(list(valve_to_flow.keys()))
    valve_to_i = {valve: i for i, valve in enumerate(valves)}
    flows = [valve_to_flow[valve] for valve in valves]
    g = [[-1 for _ in range(len(valves))] for _ in range(len(valves))]
    for valve, tunnels in valve_to_tunnels.items():
        i = valve_to_i[valve]
        for tunnel in tunnels:
            j = valve_to_i[tunnel]
            g[i][j] = 1
    return valves, flows, g


def d16_1_proboscidea_volcanium(lines, minutes_limit=30, workers=1):
    valves, flows, g = _d16_proboscidea_volcanium(lines)
    n = len(valves)
    states = {(0, tuple(valves.index('AA') for _ in range(workers)), tuple(-1 for _ in range(n)))}
    beam = n ** (workers+1)
    for minutes in range(minutes_limit):
        print(minutes, len(states))
        for w in range(workers):
            states_ = set()
            for flow, locations, open_times in states:
                location = locations[w]
                # Open the valve, if applicable.
                if open_times[location] == -1 and flows[location] > 0:
                    flow_ = flow + flows[location] * (minutes_limit - minutes - 1)
                    open_times_ = tuple(open_time if i != location else minutes
                                        for i, open_time in enumerate(open_times))
                    states_.add((flow_, locations, open_times_))
                # Move to neighboring valve.
                for j in range(len(g[location])):
                    if g[location][j] == -1:
                        continue
                    locations_ = tuple(location_ if w_ != w else j for w_, location_ in enumerate(locations))
                    states_.add((flow, locations_, open_times))
            states = states_
        # Reduce to beam size.
        states_sorted = sorted(list(states), key=operator.itemgetter(0), reverse=True)
        states = set(states_sorted[:beam])
    return max(states)[0]


def d16_2_proboscidea_volcanium(lines):
    return d16_1_proboscidea_volcanium(lines, minutes_limit=26, workers=2)


def _d17_pyroclastic_flow(lines):
    assert len(lines) == 1
    jets = lines[0]
    assert all(c == '<' or c == '>' for c in jets)
    rocks = [
        ['####'],
        ['.#.',
         '###',
         '.#.'],
        ['###',
         '..#',
         '..#'],
        ['#',
         '#',
         '#',
         '#'],
        ['##',
         '##']
    ]  # Diagrams are written "upside-down" to match chamber coordinates.
    for rock in rocks:
        assert all(len(rock[i]) == len(rock[0]) for i in range(1, len(rock)))
    return jets, rocks


def d17_1_pyroclastic_flow(lines, drops=2022):
    jets, rocks = _d17_pyroclastic_flow(lines)
    n = len(jets)
    jet_index = 0

    rock_lefts = [[rock[i].index('#') for i in range(len(rock))] for rock in rocks]
    rock_rights = [[rock[i].rindex('#') for i in range(len(rock))] for rock in rocks]
    rock_bottoms = list()
    for rock in rocks:
        rock_bottom = list()
        for j in range(len(rock[0])):
            if len(rock_bottom) > j:
                break
            for i in range(len(rock)):
                if rock[i][j] == '#':
                    rock_bottom.append(i)
                    break
        rock_bottoms.append(rock_bottom)
    rock_h_max = max(len(rock) for rock in rocks)

    chamber_width = 7
    chamber = list()

    def _get_chamber_top_air(_chamber):
        _i_top = len(_chamber) - 1
        while _i_top > -1 and '#' not in _chamber[_i_top]:
            _i_top -= 1
        if _i_top == -1:
            return tuple()
        _air = set()
        _frontier = list()
        for _j in range(len(_chamber[_i_top])):
            if _chamber[_i_top][_j] == '.':
                _air.add((_i_top, _j))
                _frontier.append((_i_top, _j))
        while len(_frontier) > 0:
            _i, _j = _frontier.pop(0)
            for _oi, _oj in ((_i+1, _j), (_i, _j+1), (_i-1, _j), (_i, _j-1)):
                if (_oi, _oj) in _air or\
                        _oi < 0 or _oi > _i_top or\
                        _oj < 0 or _oj >= len(_chamber[_oi]) or\
                        _chamber[_oi][_oj] != '.':
                    continue
                _air.add((_oi, _oj))
                _frontier.append((_oi, _oj))
        return tuple(sorted(list(_air)))

    jet_rock_lcm = (n * len(rocks)) // math.gcd(n, len(rocks))
    cycle_drop_start = -1
    cycle_drop_end = -1
    chamber_top_airs = [tuple()]
    chamber_top_air_diff_to_index = {tuple(): 0}

    drop = 0
    while drop < drops:
        # Add rows of air.
        while len(chamber) < rock_h_max or any('#' in row for row in chamber[-rock_h_max:]):
            chamber.append('.'*chamber_width)
        # Start drop.
        rock_index = drop % len(rocks)
        rock = rocks[rock_index]
        h, w = len(rock), len(rock[0])
        rock_left = rock_lefts[rock_index]
        rock_right = rock_rights[rock_index]
        rock_bottom = rock_bottoms[rock_index]
        i, j = len(chamber) - rock_h_max + 3, 2  # Bottom-left coordinates of rock in starting place.
        while True:
            # Jet.
            if jets[jet_index] == '<':
                if j > 0 and \
                        all(i + di >= len(chamber) or chamber[i+di][j+rock_left[di]-1] == '.'
                            for di in range(h)):
                    j -= 1
            else:
                if j + w < chamber_width and \
                        all(i + di >= len(chamber) or chamber[i+di][j+rock_right[di]+1] == '.'
                            for di in range(h)):
                    j += 1
            jet_index = (jet_index + 1) % n
            # Move down, if possible.
            if i > 0 and \
                    all(chamber[i+rock_bottom[dj]-1][j+dj] == '.'
                        for dj in range(w)):
                i -= 1
            else:
                break
        # End drop.
        for di in range(h):
            for dj in range(w):
                if rock[di][dj] == '#':
                    chamber[i+di] = chamber[i+di][:j+dj] + '#' + chamber[i+di][j+dj+1:]
        drop += 1
        # Handle cycles.
        if cycle_drop_end == -1 and drop % jet_rock_lcm == 0:
            chamber_top_air = _get_chamber_top_air(chamber)
            chamber_top_airs.append(chamber_top_air)
            chamber_top_i_max, _ = max(chamber_top_air)
            chamber_top_air_diff = tuple((chamber_top_i_max - air[0], air[1]) for air in chamber_top_air)
            if chamber_top_air_diff not in chamber_top_air_diff_to_index.keys():
                chamber_top_air_diff_to_index[chamber_top_air_diff] = len(chamber_top_airs) - 1
                # print(drop, chamber_top_air, chamber_top_air_diff)
            else:
                cycle_drop_start = chamber_top_air_diff_to_index[chamber_top_air_diff] * jet_rock_lcm
                cycle_drop_end = drop
                cycle_drops = cycle_drop_end - cycle_drop_start
                drop = ((drops - cycle_drop_start) // cycle_drops) * cycle_drops + cycle_drop_start

    if cycle_drop_end > -1:
        cycle_start_height = max(chamber_top_airs[cycle_drop_start//jet_rock_lcm])[0]
        cycle_end_height = max(chamber_top_airs[cycle_drop_end//jet_rock_lcm])[0]
        cycle_height = cycle_end_height - cycle_start_height
        cycle_drops = cycle_drop_end - cycle_drop_start
        cycles = (drops - cycle_drop_start) // cycle_drops
        height_before_cycles = cycle_start_height + 1
        height_during_cycles = cycle_height * cycles
        height_after_cycles = max(_get_chamber_top_air(chamber))[0] - cycle_end_height
        return height_before_cycles + height_during_cycles + height_after_cycles
    return max(_get_chamber_top_air(chamber))[0] + 1


def d17_2_pyroclastic_flow(lines):
    return d17_1_pyroclastic_flow(lines, drops=1000000000000)


def _d18_boiling_boulders(lines):
    cubes = [tuple(map(int, line.split(','))) for line in lines]
    assert all(len(cube) == 3 for cube in cubes)
    return tuple(cubes)


def d18_1_boiling_boulders(lines):
    cubes = _d18_boiling_boulders(lines)
    cube_set = set(cubes)
    surface = 0
    for x, y, z in cubes:
        for x_, y_, z_ in ((x-1, y, z), (x+1, y, z), (x, y-1, z), (x, y+1, z), (x, y, z-1), (x, y, z+1)):
            if (x_, y_, z_) not in cube_set:
                surface += 1
    return surface


def d18_2_boiling_boulders(lines):
    cubes = _d18_boiling_boulders(lines)
    x_min, y_min, z_min = cubes[0]
    x_max, y_max, z_max = cubes[0]
    for x, y, z in cubes[1:]:
        x_min = min(x_min, x)
        y_min = min(y_min, y)
        z_min = min(z_min, z)
        x_max = max(x_max, x)
        y_max = max(y_max, y)
        z_max = max(z_max, z)

    cube_set = set(cubes)
    surface = 0
    exterior = {(x_min-1, y_min-1, z_min-1)}
    frontier = [(x_min-1, y_min-1, z_min-1)]
    while len(frontier) > 0:
        x, y, z = frontier.pop(0)
        for x_, y_, z_ in ((x-1, y, z), (x+1, y, z), (x, y-1, z), (x, y+1, z), (x, y, z-1), (x, y, z+1)):
            if x_ < x_min-1 or x_ > x_max+1 or\
                    y_ < y_min-1 or y_ > y_max+1 or\
                    z_ < z_min-1 or z_ > z_max+1 or \
                    (x_, y_, z_) in exterior:
                continue
            if (x_, y_, z_) in cube_set:
                surface += 1
            else:
                exterior.add((x_, y_, z_))
                frontier.append((x_, y_, z_))
    return surface


def _d19_not_enough_minerals(lines):
    blueprints = list()
    cost_fixes = (
        ('Each ore robot costs ', ' ore'),
        ('Each clay robot costs ', ' ore'),
        ('Each obsidian robot costs ', ' ore and ', ' clay'),
        ('Each geode robot costs ', ' ore and ', ' obsidian.')
    )
    for i, line in enumerate(lines):
        blueprint_id, s_cost = line.split(': ')
        _id = int(blueprint_id.split(' ')[1])
        assert _id == i + 1
        s_costs = s_cost.split('. ')
        costs = list()
        for j, s_cost in enumerate(s_costs):
            fixes = cost_fixes[j]
            assert s_cost.startswith(fixes[0])
            assert s_cost.endswith(fixes[-1])
            s_cost = s_cost[len(fixes[0]):-len(fixes[-1])]
            if len(fixes) == 2:
                costs.append(int(s_cost))
            else:
                costs.append(tuple(map(int, s_cost.split(fixes[1]))))
        blueprints.append(((costs[0], 0, 0, 0),
                           (costs[1], 0, 0, 0),
                           (costs[2][0], costs[2][1], 0, 0),
                           (costs[3][0], 0, costs[3][1], 0)))
    return tuple(blueprints)


def d19_1_not_enough_minerals(lines, minutes=24, head=None, beam=2**16, return_quality_sum=True):
    blueprints = _d19_not_enough_minerals(lines)
    if head is not None:
        blueprints = blueprints[:head]

    def _cmp_robots_resources(_a, _b):
        _order = (
            (1, 3),  # Geode resource.
            (0, 3),  # Geode robot.
            (0, 2),  # Obsidian robot.
            (1, 2),  # Obsidian resource.
            (0, 1),  # Clay robot.
            (1, 1),  # Clay resource.
            (0, 0),  # Ore robot.
            (1, 0)   # Ore resource.
        )
        for _i, _j in _order:
            if _a[_i][_j] != _b[_i][_j]:
                return _a[_i][_j] - _b[_i][_j]
        return 0

    geodes_maxes = list()
    for _id, costs in enumerate(blueprints, start=1):
        print('id={:d}'.format(_id))
        robots_resources = {((1, 0, 0, 0), (0, 0, 0, 0))}
        for minute in range(minutes):
            print(minute, len(robots_resources), max(robots_resources, key=functools.cmp_to_key(_cmp_robots_resources)))
            robots_resources_ = set()
            for robots, resources in robots_resources:
                # Find all robots to build with these resources.
                factory_resources = [((0, 0, 0, 0), resources)]
                for b, cost in enumerate(costs):
                    if all(resources[s] >= cost[s] for s in range(len(cost))):
                        factory = tuple(1 if b == b_ else 0 for b_ in range(len(robots)))
                        resources_ = tuple(resources[s] - cost[s] for s in range(len(cost)))
                        factory_resources.append((factory, resources_))
                for factory, resources_ in factory_resources:
                    robots_ = tuple(robots[b] + factory[b] for b in range(len(robots)))
                    resources__ = tuple(resources_[s] + robots[s] for s in range(len(resources_)))
                    robots_resources_.add((robots_, resources__))
            robots_resources = robots_resources_
            robots_resources_sorted = sorted(list(robots_resources),
                                             key=functools.cmp_to_key(_cmp_robots_resources),
                                             reverse=True)
            robots_resources = set(robots_resources_sorted[:beam])
        geodes_max = max(map(lambda x: x[1][3], robots_resources))
        geodes_maxes.append(geodes_max)
        print('geodes={:d}'.format(geodes_max))

    if return_quality_sum:
        return sum(_id * geodes_max for _id, geodes_max in enumerate(geodes_maxes, start=1))

    product = 1
    for geodes_max in geodes_maxes:
        product *= geodes_max
    return product


def d19_2_not_enough_minerals(lines):
    return d19_1_not_enough_minerals(lines, minutes=32, head=3, beam=2**18, return_quality_sum=False)


def _d20_grove_positioning_system(lines):
    numbers = tuple(map(int, lines))
    zero_index = numbers.index(0)
    assert 0 not in numbers[zero_index+1:]
    return numbers, zero_index


def d20_1_grove_positioning_system(lines, decryption_key=1, mixes=1):
    numbers, zero_index = _d20_grove_positioning_system(lines)
    numbers = tuple(number * decryption_key for number in numbers)
    n = len(numbers)
    index_to_position = {i: i for i in range(n)}
    indices = list(range(n))
    for _ in range(mixes):
        for index, number in enumerate(numbers):
            position = index_to_position[index]
            position_ = (position + number) % (n - 1)
            if position == position_:
                continue
            if position < position_:
                indices_right = indices[position+1:position_+1]
                for index_ in indices_right:
                    index_to_position[index_] -= 1
                index_to_position[index] = position_
                indices = indices[:position] + indices_right + [index] + indices[position_+1:]
            else:
                indices_left = indices[position_:position]
                for index_ in indices_left:
                    index_to_position[index_] += 1
                index_to_position[index] = position_
                indices = indices[:position_] + [index] + indices_left + indices[position+1:]
    return sum(numbers[indices[(index_to_position[zero_index]+v) % n]] for v in (1000, 2000, 3000))


def d20_2_grove_positioning_system(lines):
    return d20_1_grove_positioning_system(lines, decryption_key=811589153, mixes=10)


def _d21_monkey_math(lines):
    monkey_to_expression = dict()
    for line in lines:
        monkey, expression = line.split(': ')
        try:
            number = int(expression)
            monkey_to_expression[monkey] = number
        except ValueError:
            operation = tuple(expression.split(' '))
            assert len(operation) == 3
            assert operation[1] in {'+', '-', '*', '/'}
            monkey_to_expression[monkey] = operation
    return monkey_to_expression


def d21_1_monkey_math(lines, return_root=True, ignore_key=None):
    monkey_to_expression = _d21_monkey_math(lines)
    if ignore_key is not None:
        monkey_to_expression[ignore_key] = ('ignore', 'this', 'key')

    monkey_to_resolutions = dict()
    q = list()
    for monkey, expression in monkey_to_expression.items():
        if type(expression) == int:
            q.append(monkey)
        else:
            op1, _, op2 = expression
            for op in (op1, op2):
                if op not in monkey_to_resolutions:
                    monkey_to_resolutions[op] = set()
                monkey_to_resolutions[op].add(monkey)
    while len(q) > 0:
        monkey = q.pop(0)
        if monkey not in monkey_to_resolutions.keys():
            continue
        resolutions = monkey_to_resolutions[monkey]
        solved = set()
        for monkey_ in resolutions:
            expression = monkey_to_expression[monkey_]
            if type(expression) == int:
                solved.add(monkey_)
                continue
            op1, symbol, op2 = expression
            x1 = monkey_to_expression[op1]
            x2 = monkey_to_expression[op2]
            if type(x1) != int or type(x2) != int:
                continue
            if symbol == '+':
                number = x1 + x2
            elif symbol == '-':
                number = x1 - x2
            elif symbol == '*':
                number = x1 * x2
            else:
                number = x1 // x2
            monkey_to_expression[monkey_] = number
            q.append(monkey_)
            solved.add(monkey_)
        for monkey_ in solved:
            resolutions.remove(monkey_)
        if len(resolutions) == 0:
            del monkey_to_resolutions[monkey]

    if return_root:
        return monkey_to_expression['root']

    return monkey_to_expression


def d21_2_monkey_math(lines):
    monkey_to_expression = d21_1_monkey_math(lines, return_root=False, ignore_key='humn')
    op1, _, op2 = monkey_to_expression['root']
    if type(monkey_to_expression[op1]) == int:
        equals = monkey_to_expression[op1]
        unknown = op2
    else:
        assert type(monkey_to_expression[op2]) == int
        equals = monkey_to_expression[op2]
        unknown = op1
    while unknown != 'humn':
        op1, symbol, op2 = monkey_to_expression[unknown]
        if type(monkey_to_expression[op1]) == int:
            if symbol == '+':
                equals -= monkey_to_expression[op1]
            elif symbol == '-':
                equals = monkey_to_expression[op1] - equals
            elif symbol == '*':
                equals //= monkey_to_expression[op1]
            else:
                equals = monkey_to_expression[op1] // equals
            unknown = op2
        else:
            assert type(monkey_to_expression[op2]) == int
            if symbol == '+':
                equals -= monkey_to_expression[op2]
            elif symbol == '-':
                equals += monkey_to_expression[op2]
            elif symbol == '*':
                equals //= monkey_to_expression[op2]
            else:
                equals *= monkey_to_expression[op2]
            unknown = op1
    return equals


def _d22_monkey_map(lines):
    map_lines = lines[:-2]
    m = max(len(line) for line in map_lines)
    map_ = tuple(line + ' '*(m-len(line)) for line in map_lines)
    assert lines[-2] == ''

    s_path = lines[-1]
    path = list()
    x = None
    for c in s_path:
        if c == 'R' or c == 'L':
            if x is not None:
                path.append(x)
            path.append(c)
            x = None
        else:
            if x is None:
                x = int(c)
            else:
                x = 10 * x + int(c)
    if x is not None:
        path.append(x)

    return map_, path, ((-1, 0), (0, 1), (1, 0), (0, -1))


def d22_1_monkey_map(lines=None, map_=None, path=None, directions=None, move_fn=None):
    if map_ is None and path is None and directions is None:
        map_, path, directions = _d22_monkey_map(lines)
    n, m = len(map_), len(map_[0])
    i, j = 0, 0
    while map_[i][j] == ' ':
        j += 1
    d = len(directions)
    direction_to_index = {direction: index for index, direction in enumerate(directions)}
    di, dj = directions[1]  # Facing right.

    def _move_wrap_default(_map, _n, _m, _steps, _i, _j, _di, _dj):
        while _steps > 0:
            _si, _sj = (_i + _di) % _n, (_j + _dj) % _m
            while map_[_si][_sj] == ' ':
                _si, _sj = (_si + _di) % _n, (_sj + _dj) % _m
            if map_[_si][_sj] == '#':
                break
            _i, _j = _si, _sj
            _steps -= 1
        return _i, _j, _di, _dj

    if move_fn is None:
        move_fn = _move_wrap_default

    for item in path:
        index = direction_to_index[(di, dj)]
        if item == 'R':
            di, dj = directions[(index+1) % d]
        elif item == 'L':
            di, dj = directions[(index-1) % d]
        else:
            i, j, di, dj = move_fn(map_, n, m, item, i, j, di, dj)

    row = 1000 * (i + 1)
    column = 4 * (j + 1)
    facing = direction_to_index[(di, dj)]
    return row + column + facing


def d22_2_monkey_map(lines):
    map_, path, directions = _d22_monkey_map(lines)
    n, m = len(map_), len(map_[0])
    h, w = 50, 50
    faces = list()
    for i in range(0, n, h):
        for j in range(0, m, w):
            if map_[i][j] != ' ':
                faces.append((i, j))
    assert len(faces) == 6

    d = len(directions)
    direction_to_edge = {direction: edge for edge, direction in enumerate(directions)}
    face_to_edge_to_face_edge = {face: [None for _ in range(d)] for face in faces}
    # Collect "easy" map-oriented face transitions.
    for face in faces:
        i, j = face
        for di, dj in directions:
            face_ = (i + di*h, j + dj*w)
            if face_ in faces:
                edge = direction_to_edge[(di, dj)]
                edge_ = direction_to_edge[(-di, -dj)]
                face_to_edge_to_face_edge[face][edge] = (face_, edge_)
    # Collect other face transitions by transitivity.
    for face_a in sorted(faces, key=lambda face: sum(face_to_edge_to_face_edge[face][edge] is None
                                                     for edge in range(d))):
        for edge_ab in range(d-1):
            if face_to_edge_to_face_edge[face_a][edge_ab] is None:
                continue
            face_b, edge_ba = face_to_edge_to_face_edge[face_a][edge_ab]
            for edge_ac in range(edge_ab+1, d):
                if edge_ac - edge_ab == 2:
                    continue  # Linear; i.e., not transitive.
                if face_to_edge_to_face_edge[face_a][edge_ac] is None:
                    continue
                face_c, edge_ca = face_to_edge_to_face_edge[face_a][edge_ac]
                edge_bc = (edge_ba - (edge_ac - edge_ab)) % d
                edge_cb = (edge_ca - (edge_ab - edge_ac)) % d
                if face_to_edge_to_face_edge[face_b][edge_bc] is not None:
                    assert face_to_edge_to_face_edge[face_b][edge_bc] == (face_c, edge_cb)
                else:
                    face_to_edge_to_face_edge[face_b][edge_bc] = (face_c, edge_cb)
                if face_to_edge_to_face_edge[face_c][edge_cb] is not None:
                    assert face_to_edge_to_face_edge[face_c][edge_cb] == (face_b, edge_bc)
                else:
                    face_to_edge_to_face_edge[face_c][edge_cb] = (face_b, edge_bc)
    assert all(all(face_to_edge_to_face_edge[face][edge] is not None
                   for edge in range(d))
               for face in faces)

    def _get_face_edges(_i, _j):
        _face = (_i // h) * h, (_j // w) * w
        _edges = list()
        if _i == _face[0]:
            _edges.append(0)
        if _j == _face[1] + w - 1:
            _edges.append(1)
        if _i == _face[0] + h - 1:
            _edges.append(2)
        if _j == _face[1]:
            _edges.append(3)
        return _face, _edges

    def _move_wrap_cube(_map, _n, _m, _steps, _i, _j, _di, _dj):
        while _steps > 0:
            _face_from, _edges_from = _get_face_edges(_i, _j)
            _edge_direction = direction_to_edge[(_di, _dj)]
            if _edge_direction in _edges_from:
                if _edge_direction == 0:
                    _k = _j - _face_from[1]
                elif _edge_direction == 1:
                    _k = _i - _face_from[0]
                elif _edge_direction == 2:
                    _k = _face_from[1] + w - 1 - _j
                else:  # _edge_direction == 3:
                    _k = _face_from[0] + h - 1 - _i
                _face_to, _edge_to = face_to_edge_to_face_edge[_face_from][_edge_direction]
                if _edge_to == 0:
                    _si = _face_to[0]
                    _sj = _face_to[1] + w - 1 - _k
                elif _edge_to == 1:
                    _si = _face_to[0] + h - 1 - _k
                    _sj = _face_to[1] + w - 1
                elif _edge_to == 2:
                    _si = _face_to[0] + h - 1
                    _sj = _face_to[1] + _k
                else:  # _edge_to == 3:
                    _si = _face_to[0] + _k
                    _sj = _face_to[1]
                _sdi, _sdj = directions[(_edge_to + 2) % d]
            else:
                _si, _sj = (_i + _di), (_j + _dj)
                _sdi, _sdj = _di, _dj
            if map_[_si][_sj] == '#':
                break
            _i, _j = _si, _sj
            _di, _dj = _sdi, _sdj
            _steps -= 1
        return _i, _j, _di, _dj

    return d22_1_monkey_map(lines=None, map_=map_, path=path, directions=directions, move_fn=_move_wrap_cube)


SOLVERS = {
    '1-1': d01_1_calorie_counting,
    '1-2': d01_2_calorie_counting,
    '2-1': d02_1_rock_paper_scissors,
    '2-2': d02_2_rock_paper_scissors,
    '3-1': d03_1_rucksack_reorganization,
    '3-2': d03_2_rucksack_reorganization,
    '4-1': d04_1_camp_cleanup,
    '4-2': d04_2_camp_cleanup,
    '5-1': d05_1_supply_stacks,
    '5-2': d05_2_supply_stacks,
    '6-1': d06_1_tuning_trouble,
    '6-2': d06_2_tuning_trouble,
    '7-1': d07_1_no_space_left_on_device,
    '7-2': d07_2_no_space_left_on_device,
    '8-1': d08_1_treetop_tree_house,
    '8-2': d08_2_treetop_tree_house,
    '9-1': d09_1_rope_bridge,
    '9-2': d09_2_rope_bridge,
    '10-1': d10_1_cathode_ray_tube,
    '10-2': d10_2_cathode_ray_tube,
    '11-1': d11_1_monkey_in_the_middle,
    '11-2': d11_2_monkey_in_the_middle,
    '12-1': d12_1_hill_climbing_algorithm,
    '12-2': d12_2_hill_climbing_algorithm,
    '13-1': d13_1_distress_signal,
    '13-2': d13_2_distress_signal,
    '14-1': d14_1_regolith_reservoir,
    '14-2': d14_2_regolith_reservoir,
    '15-1': d15_1_beacon_exclusion_zone,
    '15-2': d15_2_beacon_exclusion_zone,
    '16-1': d16_1_proboscidea_volcanium,
    '16-2': d16_2_proboscidea_volcanium,
    '17-1': d17_1_pyroclastic_flow,
    '17-2': d17_2_pyroclastic_flow,
    '18-1': d18_1_boiling_boulders,
    '18-2': d18_2_boiling_boulders,
    '19-1': d19_1_not_enough_minerals,
    '19-2': d19_2_not_enough_minerals,
    '20-1': d20_1_grove_positioning_system,
    '20-2': d20_2_grove_positioning_system,
    '21-1': d21_1_monkey_math,
    '21-2': d21_2_monkey_math,
    '22-1': d22_1_monkey_map,
    '22-2': d22_2_monkey_map,
}


if __name__ == '__main__':
    main()
