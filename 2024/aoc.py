import os
import sys
import time
from urllib import request

INPUT_PATH = os.path.join('..', 'input')
ENV_VAR = 'AOC_SESSION'

YEAR = 2024


def solve(day, solver):
    if len(sys.argv) < 2 or len(sys.argv) > 3:
        print(f'Usage: python {sys.argv[0]} <part> [--example|-x]')
        exit(1)

    part = int(sys.argv[1])
    example = len(sys.argv) == 3 and (sys.argv[2] == '--example' or sys.argv[2] == '-x')

    if example:
        path = get_example_path(day)
    else:
        path = get_input_path(day)

    with open(path, 'r') as fd:
        lines = [line.strip('\n') for line in fd.readlines()]
    start = time.time()
    answer = solver(lines, part)
    duration = time.time() - start
    print(answer)
    print('Time: {:.3f} s'.format(duration))


def get_example_path(day):
    fname = get_fname(day, is_example=True)
    return os.path.join(INPUT_PATH, str(YEAR), fname)


def get_input_path(day):
    fname = get_fname(day)
    year_path = os.path.join(INPUT_PATH, str(YEAR))
    os.makedirs(year_path, exist_ok=True)
    path = os.path.join(year_path, fname)
    if not os.path.exists(path):
        if ENV_VAR not in os.environ:
            raise KeyError('Set the environment variable `{}` as your AoC session token. '
                           '(Found in Dev Tools > Application > Cookies > \'session\')\n'
                           'export {}=<my_token>'.format(ENV_VAR, ENV_VAR))
        token = os.environ[ENV_VAR]

        url = f'https://adventofcode.com/{YEAR:d}/day/{day:d}/input'
        req = request.Request(url, headers={'cookie': 'session={}'.format(token)})
        with request.urlopen(req) as fp:
            text = fp.read().decode('utf-8')
        with open(path, 'w') as fd:
            fd.write(text)
    return path


def get_fname(day, is_example=False):
    suffix = '-example' if is_example else ''
    return f'{day:0>2d}{suffix}.txt'