from collections import defaultdict

import aoc


def get_answer(lines, part):
    network = defaultdict(set)
    for line in lines:
        comp_a, comp_b = line.split('-')
        network[comp_a].add(comp_b)
        network[comp_b].add(comp_a)

    if part == 1:
        threes_t = set()
        for comp_a, neighbors_a in network.items():
            if comp_a[0] != 't':
                continue
            for comp_b in neighbors_a:
                for comp_c in network[comp_b].intersection(neighbors_a):
                    threes_t.add(tuple(sorted([comp_a, comp_b, comp_c])))
        return len(threes_t)

    while True:
        size_to_comps = defaultdict(set)
        for comp, neighbors in network.items():
            size_to_comps[len(neighbors)].add(comp)
        if len(size_to_comps.keys()) == 1:
            clique_max = None
            for comp_a, neighbors_a in network.items():
                clique = {comp_a}
                for comp_b in neighbors_a:
                    if all(comp in network[comp_b] for comp in clique):
                        clique.add(comp_b)
                if clique_max is None or len(clique) > len(clique_max):
                    clique_max = clique
            return ','.join(sorted(clique_max))

        # Below is part of general solution, but doesn't apply AT ALL to this specific input.
        size_min = min(size_to_comps.keys())
        comps_min = size_to_comps[size_min]
        for comp_min in comps_min:
            for comp in network[comp_min]:
                network[comp].remove(comp_min)
            del network[comp_min]


if __name__ == '__main__':
    aoc.solve(23, get_answer)
