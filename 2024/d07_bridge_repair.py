import aoc


def get_answer(lines, part):
    values = list()
    equation_operands = list()
    for line in lines:
        value_operands = line.split(': ')
        values.append(int(value_operands[0]))
        equation_operands.append(list(map(int, value_operands[1].split(' '))))

    answer = 0
    for i, value in enumerate(values):
        operands = equation_operands[i]
        if any(result == value for result in permute_operators(operands, concat=part == 2)):
            answer += value
    return answer


def permute_operators(operands, concat=False):
    if len(operands) == 1:
        yield operands[0]
    else:
        a, b = operands[:2]
        for result in permute_operators([a + b, *operands[2:]], concat=concat):
            yield result
        for result in permute_operators([a * b, *operands[2:]], concat=concat):
            yield result
        if concat:
            for result in permute_operators([int(str(a) + str(b)), *operands[2:]], concat=concat):
                yield result


if __name__ == '__main__':
    aoc.solve(7, get_answer)
