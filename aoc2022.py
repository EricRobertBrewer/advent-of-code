import argparse

import aoc


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

    args = parser.parse_args()
    day = args.day
    part = args.part
    solve(day, part)


def solve(day: int, part: int):
    day_path = aoc.download_input(YEAR, day)
    with open(day_path, 'r') as fd:
        lines = [line.strip() for line in fd.readlines()]

    key = str(day) + '-' + str(part)
    solver = SOLVERS[key]
    if solver is None:
        raise ValueError('No solution for day `{:d}` part `{:d}`'.format(day, part))
    answer = solver(lines)
    print(answer)


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


SOLVERS = {
    '1-1': d01_1_calorie_counting,
    '1-2': d01_2_calorie_counting,
    '2-1': d02_1_rock_paper_scissors,
    '2-2': d02_2_rock_paper_scissors,
}


if __name__ == '__main__':
    main()
