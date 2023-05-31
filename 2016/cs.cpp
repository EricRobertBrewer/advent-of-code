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
