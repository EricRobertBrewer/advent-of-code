extern "C" {
    #include "../2015/aoc.h"
}

#include <cstdlib>
#include <ctime>
#include <fstream>
#include <iostream>
#include <set>
#include <string>
#include <tuple>
#include <vector>

#define YEAR "2016"

long solve(int day, int part, std::string input_path);

long d01_no_time_for_a_taxicab(std::vector<std::string> lines, int part);

int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " <day> <part> [-x|--example]" << std::endl;
        exit(EXIT_FAILURE);
    }

    const int day = std::stoi(argv[1]);
    if (day < 1 || day > 25) {
        std::cerr << "Day must be within 1 and 25: " << day << std::endl;
        exit(EXIT_FAILURE);
    }
    const int part = std::stoi(argv[2]);
    if (part != 1 && part != 2) {
        std::cerr << "Part must be 1 or 2: " << part << std::endl;
        exit(EXIT_FAILURE);
    }
    bool example = false;
    if (argc > 3) {
        if ((strcmp(argv[3], "-x") == 0 || strcmp(argv[3], "--example") == 0)) {
            example = true;
        } else {
            std::cerr << "Unexpected argument: " << argv[3] << std::endl;
            exit(EXIT_FAILURE);
        }
    }

    char input_path_p[256];
    if (example) {
        aoc_example_path(input_path_p, YEAR, day);
    } else {
        aoc_download_input_if_needed(input_path_p, YEAR, day);
    }
    std::string input_path = input_path_p;

    std::time_t start = std::time(NULL);
    long answer = solve(day, part, input_path);
    std::time_t end = std::time(NULL);

    std::cout << answer << std::endl;
    std::cout << "Time: " << (end - start) << " ms" << std::endl;
    return 0;
}

long solve(int day, int part, std::string input_path) {
    std::vector<std::string> lines;
    std::string line;
    std::ifstream fin(input_path);
    while (std::getline(fin, line)) {
        lines.push_back(line);
    }

    long (*solution)(std::vector<std::string>, int);
    if (day == 1) {
        solution = &d01_no_time_for_a_taxicab;
    } else {
        std::cerr << "No solution for day: " << day << std::endl;
        exit(EXIT_FAILURE);
    }
    return (*solution)(lines, part);
}

long d01_no_time_for_a_taxicab(std::vector<std::string> lines, int part) {
    std::string line = lines[0];
    std::vector<std::string> instructions;
    std::size_t start = 0, end;
    std::string sep = ", ";
    while ((end = line.find(sep, start)) != std::string::npos) {
        instructions.push_back(line.substr(start, end - start));
        start = end + sep.length();
    }
    instructions.push_back(line.substr(start));

    int x = 0, y = 0;
    int directions[][2] = {
        {0, 1}, {1, 0}, {0, -1}, {-1, 0}
    };
    int direction = 0; // North.
    std::set<std::tuple<int, int>> locations;
    locations.insert(std::tuple<int, int>(x, y));
    for (int i = 0; i < instructions.size(); i++) {
        std::string instruction = instructions[i];
        char turn = instruction[0];
        if (turn == 'L') {
            direction = (direction + 3) % 4;
        } else if (turn == 'R') {
            direction = (direction + 1) % 4;
        } else {
            std::cerr << "Unexpected turn: " << turn << std::endl;
            exit(EXIT_FAILURE);
        }
        const int steps = std::stoi(instruction.substr(1));
        for (int j = 0; j < steps; j++) {
            x += directions[direction][0];
            y += directions[direction][1];
            std::tuple<int, int> location(x, y);
            if (part == 2 && locations.find(location) != locations.end()) {
                return std::abs(x) + std::abs(y);
            } else {
                locations.insert(location);
            }
        }
    }
    return std::abs(x) + std::abs(y);
}
