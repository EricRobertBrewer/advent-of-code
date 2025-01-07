import aoc


def get_answer(lines, part):
    if not all(len(line) == len(lines[0]) for line in lines[1:]):
        print('Non-square input.')
        return -1
    n, m = len(lines), len(lines[0])

    answer = 0

    if part == 1:
        target = 'XMAS'
        deltas = ((0, 1), (1, 1), (1, 0), (1, -1),
                  (0, -1), (-1, -1), (-1, 0), (-1, 1))
        for i in range(n):
            for j in range(m):
                for di, dj in deltas:
                    match = True
                    k = 0
                    while match and k < len(target):
                        i_ = i + k * di
                        j_ = j + k * dj
                        if not (0 <= i_ < n) or not (0 <= j_ < m) or lines[i_][j_] != target[k]:
                            match = False
                        k += 1
                    if match:
                        answer += 1
    else:
        for i in range(1, n - 1):
            for j in range(1, m - 1):
                if lines[i][j] != 'A':
                    continue
                if not ((lines[i - 1][j - 1] == 'M' and lines[i + 1][j + 1] == 'S') or
                        (lines[i - 1][j - 1] == 'S' and lines[i + 1][j + 1] == 'M')):
                    continue
                if not ((lines[i + 1][j - 1] == 'M' and lines[i - 1][j + 1] == 'S') or
                        (lines[i + 1][j - 1] == 'S' and lines[i - 1][j + 1] == 'M')):
                    continue
                answer += 1

    return answer


if __name__ == '__main__':
    aoc.solve(4, get_answer)
