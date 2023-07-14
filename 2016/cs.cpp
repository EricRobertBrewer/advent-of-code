#include "cs.hpp"

std::vector<std::string> cs::string_split(std::string s) {
    return cs::string_split(s, " ");
}

std::vector<std::string> cs::string_split(std::string s, std::string sep) {
    std::vector<std::string> tokens;
    std::size_t start = 0, end;
    while ((end = s.find(sep, start)) != std::string::npos) {
        tokens.push_back(s.substr(start, end - start));
        start = end + sep.length();
    }
    tokens.push_back(s.substr(start));
    return tokens;
}

std::vector<std::pair<int, int>> cs::dijkstra_grid_path(
        int m, int n, int si, int sj, int ei, int ej, std::function<bool(int, int)> is_neighbor
) {
    int d[m][n];
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            d[i][j] = -1;
        }
    }
    d[si][sj] = 0;
    auto compare = [&d](const std::pair<int, int> &a, const std::pair<int, int> &b) {
        return d[a.first][a.second] > d[b.first][b.second]; // Ascending (min queue).
    };
    std::priority_queue<std::pair<int, int>, std::vector<std::pair<int, int>>, decltype(compare)> q(compare);
    q.push(std::pair<int, int>(si, sj));
    std::map<std::pair<int, int>, std::pair<int, int>> back_trace;

    while (!q.empty() && d[ei][ej] == -1) {
        std::pair<int, int> node = q.top();
        q.pop();
        const int deltas[4][2] = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int k = 0; k < 4; k++) {
            int i = node.first + deltas[k][0];
            int j = node.second + deltas[k][1];
            if (i >= 0 && i < m && j >= 0 && j < n && is_neighbor(i, j) && d[i][j] == -1) {
                d[i][j] = d[node.first][node.second] + 1;
                std::pair<int, int> neighbor(i, j);
                q.push(neighbor);
                back_trace[neighbor] = node;
            }
        }
    }

    std::vector<std::pair<int, int>> path;
    if (d[ei][ej] != -1) {
        std::pair<int, int> node(ei, ej);
        while (back_trace.find(node) != back_trace.end()) {
            path.push_back(node);
            node = back_trace[node];
        }
        std::reverse(path.begin(), path.end());
    }
    return path;
}
