from queue import PriorityQueue


def dijkstra_grid(n, m, si, sj, ei=None, ej=None, is_neighbor_fn=None, weight_fn=None):
    if is_neighbor_fn is None:
        is_neighbor_fn = lambda i_, j_, oi_, oj_: True
    if weight_fn is None:
        weight_fn = lambda i_, j_, oi_, oj_: 1

    d = [[-1 for _ in range(m)] for _ in range(n)]
    d[si][sj] = 0
    q = PriorityQueue(n * m)
    q.put((0, (si, sj)))
    while True:
        z, (i, j) = q.get()
        for oi, oj in ((i-1, j), (i, j+1), (i+1, j), (i, j-1)):
            if oi < 0 or oi >= n or oj < 0 or oj >= m:
                continue  # Out of bounds.
            if d[oi][oj] != -1:
                continue  # Already relaxed.
            if not is_neighbor_fn(i, j, oi, oj):
                continue
            oz = z + weight_fn(i, j, oi, oj)
            d[oi][oj] = oz
            q.put((oz, (oi, oj)))

        if ei is None and ej is None:
            if q.empty():
                break
        elif d[ei][ej] != -1:
            break

    if ei is None and ej is None:
        return d
    return d[ei][ej]
