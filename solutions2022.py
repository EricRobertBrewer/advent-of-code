import argparse
import functools
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
            if type(v) == int:
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
}


if __name__ == '__main__':
    main()
