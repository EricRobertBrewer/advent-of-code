import aoc


def get_answer(lines, part):
    reports = [list(map(int, line.split(' '))) for line in lines]

    n_safe = 0
    if part == 1:
        for report in reports:
            if is_safe(report):
                n_safe += 1
    else:
        for report in reports:
            reports_skip = [[x for j, x in enumerate(report) if j != i]
                            for i in range(len(report))]
            if any(is_safe(report_skip) for report_skip in reports_skip):
                n_safe += 1
    return n_safe


def is_safe(report):
    diffs = [report[i] - report[i - 1] for i in range(1, len(report))]
    return all(1 <= diff <= 3 for diff in diffs) or all(-3 <= diff <= -1 for diff in diffs)


if __name__ == '__main__':
    aoc.solve(2, get_answer)
