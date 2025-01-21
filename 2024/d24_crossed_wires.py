import os
from collections import defaultdict

import aoc


def get_answer(lines, part):
    l_blank = None
    for l, line in enumerate(lines):
        if line == '':
            if l_blank is not None:
                raise ValueError(f'Multiple blank lines: {l_blank:d} and {l}')
            l_blank = l
    if l_blank is None:
        raise ValueError('Unable to find blank line.')

    wire_to_value = dict()
    for line in lines[:l_blank]:
        wire_value = line.split(': ')
        wire = wire_value[0]
        value = wire_value[1] == '1'
        wire_to_value[wire] = value

    wire_to_other_to_op_outputs = defaultdict(lambda: defaultdict(list))
    for line in lines[l_blank + 1:]:
        wire_op_other, output = line.split(' -> ')
        wire, op, other = wire_op_other.split(' ')
        wire_to_other_to_op_outputs[wire][other].append((op, output))
        wire_to_other_to_op_outputs[other][wire].append((op, output))

    if part == 1:
        q = list()
        for wire in wire_to_value.keys():
            q.append(wire)

        while len(q) > 0:
            wire = q.pop()
            for other, op_outputs in wire_to_other_to_op_outputs[wire].items():
                if other not in wire_to_value.keys():
                    continue
                for op, output in op_outputs:
                    if output in wire_to_value.keys():
                        continue
                    v_wire, v_other = wire_to_value[wire], wire_to_value[other]
                    if op == 'AND':
                        value = v_wire and v_other
                    elif op == 'OR':
                        value = v_wire or v_other
                    elif op == 'XOR':
                        value = v_wire ^ v_other
                    else:
                        raise ValueError(f'Unknown operation between {wire} and {other}: {op}')
                    wire_to_value[output] = value
                    q.append(output)

        answer = 0
        for wire_z in sorted([wire for wire in wire_to_value.keys() if wire.startswith('z')], reverse=True):
            answer *= 2
            if wire_to_value[wire_z]:
                answer += 1
        return answer

    # Methodically find the outputs to swap in the generated `-output.txt` file.
    # Makes changes in `-swap.txt` as lines of `[OUTPUT] [SWAP]`, e.g., `z04 abc`.
    output_to_swap = dict()
    with open(os.path.join(aoc.INPUT_PATH, '2024', '24-swap.txt'), 'r') as fd:
        for line in fd.readlines():
            output, swap = line.strip().split(' ')
            output_to_swap[output] = swap
            output_to_swap[swap] = output

    output_to_op_wires = dict()
    for wire, other_to_op_outputs in wire_to_other_to_op_outputs.items():
        for other, op_outputs in other_to_op_outputs.items():
            for op, output in op_outputs:
                if output in output_to_swap.keys():
                    output = output_to_swap[output]
                output_to_op_wires[output] = (op, *sorted([wire, other]))

    with open(os.path.join(aoc.INPUT_PATH, '2024', '24-output.txt'), 'w') as fd:
        i = 0
        while True:
            output = f'z{i:0>2d}'
            if output not in output_to_op_wires.keys():
                break
            inputs = get_inputs(output, output_to_op_wires)
            fd.write(f'{output}={inputs}\n')
            i += 1

    return ','.join(sorted(list(output_to_swap.keys())))


def get_inputs(output, output_to_op_wires):
    op, wire, other = output_to_op_wires[output]
    if wire[0] != 'x' and wire[0] != 'y':
        wire, depth_wire = get_inputs(wire, output_to_op_wires)
        wire = '(' + wire + ')'
        depth_wire += 1
    else:
        depth_wire = 0
    if other[0] != 'x' and other[0] != 'y':
        other, depth_other = get_inputs(other, output_to_op_wires)
        other = '(' + other + ')'
        depth_other += 1
    else:
        depth_other = 0

    if depth_other < depth_wire:
        t = wire
        wire = other
        other = t
    return f'{wire} {op}[{output}] {other}', max(depth_wire, depth_other)


if __name__ == '__main__':
    aoc.solve(24, get_answer)
