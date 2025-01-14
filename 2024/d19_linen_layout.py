import aoc


def get_answer(lines, part):
    patterns = set(lines[0].split(', '))
    designs = lines[2:]

    answer = 0
    if part == 1:
        for design in designs:
            if any(find_sequences(design, patterns)):
                answer += 1
    else:
        design_to_count = {'': 1}
        for design in designs:
            answer += get_design_count(design, patterns, design_to_count)
    return answer


def find_sequences(design, patterns):
    if len(design) == 0:
        yield True
    else:
        for pattern in patterns:
            if design.startswith(pattern):
                for q in find_sequences(design[len(pattern):], patterns):
                    yield q


def get_design_count(design, patterns, design_to_count):
    if design in design_to_count:
        return design_to_count[design]

    count = 0
    for pattern in patterns:
        if design.startswith(pattern):
            count += get_design_count(design[len(pattern):], patterns, design_to_count)

    design_to_count[design] = count
    return count


if __name__ == '__main__':
    aoc.solve(19, get_answer)
