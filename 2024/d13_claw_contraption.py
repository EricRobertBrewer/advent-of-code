import aoc


def get_answer(lines, part):
    l = 0
    machines = list()
    while l < len(lines):
        buttons = list()
        for line, prefix in zip((lines[l], lines[l + 1]), ('Button A: ', 'Button B: ')):
            if not line.startswith(prefix):
                raise ValueError(f'Unexpected line near `{l:d}`: {line}')
            xys = line[len(prefix):]
            xs, ys = xys.split(', ')
            if not xs.startswith('X+') or not ys.startswith('Y+'):
                raise ValueError(f'Unexpected button syntax near line `{l:d}`: {line}')
            buttons.append((int(xs[2:]), int(ys[2:])))

        line_prize = lines[l + 2]
        if not line_prize.startswith('Prize: '):
            raise ValueError(f'Unexpected line `{l + 2:d}`: {line_prize}')
        xs_prize, ys_prize = line_prize[7:].split(', ')
        if not xs_prize.startswith('X=') or not ys_prize.startswith('Y='):
            raise ValueError(f'Unexpected prize syntax on line `{l + 2:d}`: {line_prize}')
        prize = (int(xs_prize[2:]), int(ys_prize[2:]))

        machines.append((*buttons, prize))
        l += 4

    answer = 0
    for (xa, ya), (xb, yb), (xt, yt) in machines:
        if part == 2:
            xt, yt = xt + 10000000000000, yt + 10000000000000

        numer = xt * ya - yt * xa
        denom = xb * ya - yb * xa
        if numer < 0 and denom < 0:
            numer, denom = -numer, -denom
        if numer % denom == 0:
            n = numer // denom
            if (xt - n * xb) % xa == 0:
                m = (xt - n * xb) // xa
                answer += 3 * m + n
    return answer


if __name__ == '__main__':
    aoc.solve(13, get_answer)
