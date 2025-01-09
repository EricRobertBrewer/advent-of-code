import aoc


def get_answer(lines, part):
    disk = lines[0]

    if part == 2:
        return get_answer_2(disk)

    id_ = 0
    i = 0
    memory = list()
    while i < len(disk):
        size = int(disk[i])
        if size == 0:
            raise ValueError(f'0-sized file at position: {i:d}')
        for _ in range(size):
            memory.append(id_)

        if i + 1 < len(disk):
            free = int(disk[i + 1])
            for _ in range(free):
                memory.append(-1)

        id_ += 1
        i += 2

    i_free = 0
    while i_free < len(memory) and memory[i_free] != -1:
        i_free += 1
    i_file = len(memory) - 1
    while i_file >= 0 and memory[i_file] == -1:
        i_file -= 1
    while i_free < i_file:
        memory[i_free] = memory[i_file]
        memory[i_file] = -1
        while i_free < len(memory) and memory[i_free] != -1:
            i_free += 1
        while i_file >= 0 and memory[i_file] == -1:
            i_file -= 1

    return sum(i * id_ for i, id_ in enumerate(memory[:i_free]))


def get_answer_2(disk):
    id_ = 0
    i = 0
    system = list()
    address = 0
    while i < len(disk):
        size = int(disk[i])
        system.append((id_, address, address + size))
        address += size

        if i + 1 < len(disk):
            free = int(disk[i + 1])
            system.append((-1, address, address + free))
            address += free

        id_ += 1
        i += 2

    p = len(system) - 1
    while p > 0:
        id_, start, end = system[p]
        if id_ != -1:
            size = end - start
            p_free = 0
            while p_free < p:
                id_free, start_free, end_free = system[p_free]
                if id_free == -1:
                    free = end_free - start_free
                    if free == size:
                        system[p_free] = (id_, start_free, end_free)
                        system[p] = (-1, start, end)
                        break
                    elif free > size:
                        system.insert(p_free, (id_, start_free, start_free + size))
                        system[p_free + 1] = (-1, start_free + size, end_free)
                        system[p + 1] = (-1, start, end)
                        p += 1
                        break
                p_free += 1
        p -= 1

    answer = 0
    for id_, start, end in system:
        if id_ == -1:
            continue
        for address in range(start, end):
            answer += address * id_
    return answer


if __name__ == '__main__':
    aoc.solve(9, get_answer)
