from collections import defaultdict

import aoc


def get_answer(lines, part):
    blank_index = None
    for i, line in enumerate(lines):
        if line == '':
            if blank_index is not None:
                raise ValueError(f'Found multiple blank lines: {blank_index:d} and {i:d}')
            blank_index = i
    if blank_index is None:
        raise ValueError('Unable to find blank line.')

    before_to_afters = defaultdict(set)
    for line in lines[:blank_index]:
        before, after = map(int, line.split('|'))
        before_to_afters[before].add(after)

    updates = [list(map(int, line.split(',')))
               for line in lines[blank_index + 1:]]

    answer = 0

    for update in updates:
        ordered = True
        i = 0
        while i < len(update) - 1:
            for j in range(i + 1, len(update)):
                if update[j] in before_to_afters and update[i] in before_to_afters[update[j]]:
                    ordered = False
                    if part == 2:
                        t = update[i]
                        update[i] = update[j]
                        update[j] = t
                        i -= 1  # Redo from this point.
                    break
            if part == 1 and not ordered:
                break
            i += 1

        if (part == 1 and ordered) or (part == 2 and not ordered):
            if not len(update) % 2 == 1:
                raise ValueError(f'Even number of values in update: {update}')
            answer += update[(len(update) - 1) // 2]

    return answer


if __name__ == '__main__':
    aoc.solve(5, get_answer)
