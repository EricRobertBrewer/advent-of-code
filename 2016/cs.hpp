#ifndef CS_HPP
#define CS_HPP

#include <functional>
#include <map>
#include <queue>
#include <string>
#include <utility>
#include <vector>

namespace cs {

    std::vector<std::string> string_split(std::string s);
    std::vector<std::string> string_split(std::string s, std::string sep);

    std::vector<std::pair<int, int>> dijkstra_grid_path(
            int m, int n, int si, int sj, int ei, int ej, std::function<bool(int, int)> is_neighbor
    );
}

#endif
