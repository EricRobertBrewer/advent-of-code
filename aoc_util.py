import os
from urllib import request

INPUT_DIR = 'input'
ENV_VAR = 'AOC_SESSION'


def download_input_if_needed(year: str, day: int):
    year_dir = os.path.join(INPUT_DIR, year)
    day_path = os.path.join(year_dir, '{:0>2d}.txt'.format(day))
    if not os.path.exists(day_path):
        if ENV_VAR not in os.environ:
            raise KeyError('Set the environment variable `{}` as your AoC session token. '
                           '(Found in Dev Tools > Application > Cookies > \'session\')\n'
                           'export {}=<my_token>'.format(ENV_VAR, ENV_VAR))
        token = os.environ[ENV_VAR]

        os.makedirs(year_dir, exist_ok=True)
        url = 'https://adventofcode.com/{}/day/{:d}/input'.format(year, day)
        req = request.Request(url, headers={'cookie': 'session={}'.format(token)})
        with request.urlopen(req) as fp:
            text = fp.read().decode('utf-8')
        with open(day_path, 'w') as fd:
            fd.write(text)
    return day_path
