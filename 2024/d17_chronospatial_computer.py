import aoc


def get_answer(lines, part):
    r = list()
    prefixes = [f'Register {c}: ' for c in ('A', 'B', 'C')]
    for i, prefix in enumerate(prefixes):
        if not lines[i].startswith(prefix):
            raise ValueError(f'Bad line syntax: {lines[i]}')
        r.append(int(lines[i][len(prefix):]))
    if not lines[4].startswith('Program: '):
        raise ValueError(f'Bad program line: {lines[4]}')
    program = list(map(int, lines[4][9:].split(',')))

    if part == 1:
        outputs = run_program(program, r)
        return ','.join(map(str, outputs))

    # Ensure viability for this specific case.
    ip_adv = None  # Ensure only one.
    v = None  # Must be 4 or 8.
    ip_out = None  # Only one.
    ip_jnz = None  # Only one.
    has_bst = False  # Has to come before other b operations.
    has_cdv = False  # Has to come before other c operations.
    ip = 0
    while ip < len(program):
        opcode = program[ip]
        if opcode == 0:
            if ip_adv is not None:
                raise ValueError(f'Multiple `adv`: {ip_adv:d} and {ip:d}')
            ip_adv = ip
            operand = program[ip + 1]
            if not (2 <= operand <= 3):
                raise ValueError(f'Unable to perform `adv` on operand: {operand:d}')
            v = 2 ** operand
        elif opcode == 1:
            if not has_bst:
                raise ValueError('Unable to perform `bxl` before `bst`.')
        elif opcode == 2:
            has_bst = True
        elif opcode == 3:
            if ip_jnz is not None:
                raise ValueError(f'Multiple `jnz`: {ip_jnz:d} and {ip:d}')
            ip_jnz = ip
            if program[ip + 1] != 0:
                raise ValueError(f'Unable to perform `jnz` with non-zero ip target: {program[ip + 1]:d}')
        elif opcode == 4:
            if not has_bst or not has_cdv:
                raise ValueError(f'Unable to perform `bxc` before `bst` and `cdv`.')
        elif opcode == 5:
            if ip_out is not None:
                raise ValueError(f'Multiple `out`: {ip_out:d} and {ip:d}')
            ip_out = ip
        elif opcode == 6:
            if not has_bst:
                raise ValueError('Unable to perform `bdv` before `bst`.')
        elif opcode == 7:
            if program[ip + 1] == 6:
                raise ValueError('Unable to perform `cdv` dependent on `c`.')
            if program[ip + 1] == 5 and not has_bst:
                raise ValueError('Unable to perform `cdv` dependent on a previous loop\'s `b`')
            has_cdv = True
        ip += 2
    if ip_adv is None or v is None:
        raise ValueError('Missing `adv`.')
    if ip_out is None:
        raise ValueError('Missing `out`.')
    if ip_out != len(program) - 4:
        raise ValueError(f'Expected `out` at ip={len(program) - 4:d}: {ip_out:d}')
    if ip_jnz is None:
        raise ValueError('Missing `jnz`.')
    if ip_jnz != len(program) - 2:
        raise ValueError(f'Expected `jnz` at ip={len(program) - 2:d}: {ip_jnz:d}')

    # Because the above assumptions are satisfied, the program fits a particular pattern where
    #   the `out` operation is performed n times and the `jnz` operation jumps to the beginning
    #   n-1 times, where n is the length of the program. In addition, `adv` is performed
    #   exactly once in the loop, dividing `a` by a constant factor `v`. Thus, since `a` must
    #   be zero in order to fall through the final `jnz` operation, the previous value of `a`
    #   must have been constrained to the finite range [1,v-1]. Any values of `a` in this range
    #   which produce the correct last output from `out` becomes a candidate for `a` during the
    #   previous loop. All candidates for `a` in the range [a*v,(a+1)*v-1] can then be tested
    #   for viability in the previous loop (by checking the output `out` against the
    #   second-to-last program output), and so on until the beginning of the program.
    loop = len(program)
    a_candidates = {0}
    while loop > 0:
        a_candidates_next = set()
        for a_candidate in a_candidates:
            for a in range(max(1, a_candidate * v), (a_candidate + 1) * v):
                outputs = run_program(program, [a, 0, 0], exit_ips={ip_jnz})
                if outputs[0] == program[loop - 1]:
                    a_candidates_next.add(a)
        a_candidates = a_candidates_next
        loop -= 1
    return min(a_candidates)


def run_program(program, r, exit_ips=None):
    if exit_ips is None:
        exit_ips = set()

    r = [_ for _ in r]  # Copy.

    ip = 0
    outputs = list()
    while ip < len(program) and ip not in exit_ips:
        opcode, operand = program[ip], program[ip + 1]
        # print(f'ip={ip:d}  opcode={opcode:d}  operand={operand:d}  r={r}')
        if opcode == 0:  # adv
            combo = get_combo(operand, r)
            r[0] = r[0] // (2 ** combo)
        elif opcode == 1:  # bxl
            r[1] = r[1] ^ operand
        elif opcode == 2:  # bst
            combo = get_combo(operand, r)
            r[1] = combo % 8
        elif opcode == 3:  # jnz
            if r[0] != 0:
                ip = operand
                continue  # Don't naturally increment `ip`.
        elif opcode == 4:  # bxc
            r[1] = r[1] ^ r[2]
        elif opcode == 5:  # out
            combo = get_combo(operand, r)
            output = combo % 8
            outputs.append(output)
        elif opcode == 6:  # bdv
            combo = get_combo(operand, r)
            r[1] = r[0] // (2 ** combo)
        elif opcode == 7:  # cdv
            combo = get_combo(operand, r)
            r[2] = r[0] // (2 ** combo)
        ip += 2

    return outputs


def get_combo(operand, r):
    if operand == 7:
        raise ValueError(f'Invalid combo operand `{operand:d}`')
    return operand if 0 <= operand < 4 else r[operand - 4]


if __name__ == '__main__':
    aoc.solve(17, get_answer)
