from typing import List

import aoc


def get_answer(lines: List[str], part: int):
    s = ''.join(lines)
    answer = 0

    enabled = True
    chunks = s.split('mul(')
    skipped_first = False
    for chunk in chunks:
        if not skipped_first:
            skipped_first = True  # Because the first chunk is not preceded by `mul(`.
        elif enabled:
            try:
                close_paren_index = chunk.index(')')
                xs, ys = chunk[:close_paren_index].split(',')
                if (1 <= len(xs) <= 3 and all(c.isdigit() for c in xs) and
                        1 <= len(ys) <= 3 and all(c.isdigit() for c in ys)):
                    answer += int(xs) * int(ys)
            except ValueError:
                pass

        if part == 2:
            rindex_do, rindex_dont = None, None
            try:
                rindex_do = chunk.rindex('do()')
            except ValueError:
                pass
            try:
                rindex_dont = chunk.rindex('don\'t()')
            except ValueError:
                pass
            if rindex_do is not None:
                if rindex_dont is not None:
                    enabled = rindex_do > rindex_dont
                else:
                    enabled = True
            elif rindex_dont is not None:
                enabled = False

    return answer


if __name__ == '__main__':
    aoc.solve(3, get_answer)
