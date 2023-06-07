extern "C" {
    #include "../2015/aoc.h"
    #include "../2015/cs.h"
}
#include "cs.hpp"

#include <algorithm>
#include <chrono>
#include <cstdlib>
#include <deque>
#include <fstream>
#include <iostream>
#include <map>
#include <set>
#include <string>
#include <tuple>
#include <utility>
#include <vector>

#define YEAR "2016"

uint64_t current_time_ms();

long solve(int day, int part, std::string input_path);

long d01_no_time_for_a_taxicab(std::vector<std::string> lines, int part);
long d02_bathroom_security(std::vector<std::string> lines, int part);
long d03_squares_with_three_sides(std::vector<std::string> lines, int part);
long d04_security_through_obscurity(std::vector<std::string> lines, int part);
long d05_how_about_a_nice_game_of_chess(std::vector<std::string> lines, int part);
long d06_signals_and_noise(std::vector<std::string> lines, int part);
long d07_internet_protocol_version_7(std::vector<std::string> lines, int part);
long d08_two_factor_authentication(std::vector<std::string> lines, int part);
long d09_explosives_in_cyberspace(std::vector<std::string> lines, int part);
long d10_balance_bots(std::vector<std::string> lines, int part);

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

    uint64_t start = current_time_ms();
    long answer = solve(day, part, input_path);
    uint64_t end = current_time_ms();

    std::cout << answer << std::endl;
    std::cout << "Time: " << (end - start) << " ms" << std::endl;
    return 0;
}

uint64_t current_time_ms() {
    // https://stackoverflow.com/a/56107709/1559071
    using namespace std::chrono;
    return duration_cast<milliseconds>(system_clock::now().time_since_epoch()).count();
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
    } else if (day == 2) {
        solution = &d02_bathroom_security;
    } else if (day == 3) {
        solution = &d03_squares_with_three_sides;
    } else if (day == 4) {
        solution = &d04_security_through_obscurity;
    } else if (day == 5) {
        solution = &d05_how_about_a_nice_game_of_chess;
    } else if (day == 6) {
        solution = &d06_signals_and_noise;
    } else if (day == 7) {
        solution = &d07_internet_protocol_version_7;
    } else if (day == 8) {
        solution = &d08_two_factor_authentication;
    } else if (day == 9) {
        solution = &d09_explosives_in_cyberspace;
    } else if (day == 10) {
        solution = &d10_balance_bots;
    } else {
        std::cerr << "No solution for day: " << day << std::endl;
        exit(EXIT_FAILURE);
    }
    return (*solution)(lines, part);
}

long d01_no_time_for_a_taxicab(std::vector<std::string> lines, int part) {
    std::vector<std::string> instructions = cs::string_split(lines[0], ", ");

    int x = 0, y = 0;
    int directions[][2] = {
        {0, 1}, {1, 0}, {0, -1}, {-1, 0}
    };
    int direction = 0; // North.
    std::set<std::tuple<int, int>> locations;
    locations.insert(std::tuple<int, int>(x, y));
    for (std::string instruction : instructions) {
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

long d02_bathroom_security(std::vector<std::string> lines, int part) {
    int x, y;
    const int xlim = 5, ylim = 5;
    std::string pad[ylim];
    if (part == 1) {
        x = 1;
        y = 1;
        pad[0] = "123--";
        pad[1] = "456--";
        pad[2] = "789--";
        pad[3] = "-----";
        pad[4] = "-----";
    } else {
        x = 0;
        y = 2;
        pad[0] = "--1--";
        pad[1] = "-234-";
        pad[2] = "56789";
        pad[3] = "-ABC-";
        pad[4] = "--D--";
    }
    std::string code = "";
    for (std::string line : lines) {
        for (char c : line) {
            if (c == 'U') {
                if (y > 0 && pad[y - 1][x] != '-') {
                    y--;
                }
            } else if (c == 'D') {
                if (y < ylim - 1 && pad[y + 1][x] != '-') {
                    y++;
                }
            } else if (c == 'L') {
                if (x > 0 && pad[y][x - 1] != '-') {
                    x--;
                }
            } else if (c == 'R') {
                if (x < xlim - 1 && pad[y][x + 1] != '-') {
                    x++;
                }
            } else {
                std::cerr << "Unexpected instruction: " << c << std::endl;
                exit(EXIT_FAILURE);
            }
        }
        code += pad[y][x];
    }
    std::cout << code << std::endl;
    return 0;
}

long d03_squares_with_three_sides(std::vector<std::string> lines, int part) {
    long n = 0;
    if (part == 1) {
        for (std::string line : lines) {
            const int a = std::stoi(line.substr(0, 5));
            const int b = std::stoi(line.substr(5, 10));
            const int c = std::stoi(line.substr(10, 15));
            if (a + b > c && a + c > b && b + c > a) {
                n++;
            }
        }
    } else {
        for (int i = 0; i < lines.size(); i += 3) {
            for (int j = 0; j < 3; j++) {
                const int a = std::stoi(lines[i].substr(5 * j, 5 * (j + 1)));
                const int b = std::stoi(lines[i + 1].substr(5 * j, 5 * (j + 1)));
                const int c = std::stoi(lines[i + 2].substr(5 * j, 5 * (j + 1)));
                if (a + b > c && a + c > b && b + c > a) {
                    n++;
                }
            }
        }
    }
    return n;
}

long d04_security_through_obscurity(std::vector<std::string> lines, int part) {
    long answer = 0;
    for (std::string line : lines) {
        std::map<char, int> c_to_count;
        std::vector<std::string> parts = cs::string_split(line, "-");
        std::string sector_checksum = parts[parts.size() - 1];
        std::size_t bracket = sector_checksum.find("[");
        int sector = std::stoi(sector_checksum.substr(0, bracket));
        std::string checksum = sector_checksum.substr(bracket + 1, 5);
        if (part == 1) {
            // Count characters.
            for (int i = 0; i < parts.size() - 1; i++) {
                for (char c : parts[i]) {
                    if (c_to_count.find(c) == c_to_count.end()) {
                        c_to_count[c] = 0;
                    }
                    c_to_count[c] = c_to_count[c] + 1;
                }
            }
            // Order by descending frequency, then alphabetically.
            std::vector<std::pair<int, char>> count_cs;
            for (const auto &c_count : c_to_count) {
                count_cs.push_back(std::pair(c_count.second, c_count.first));
            }
            std::sort(count_cs.begin(), count_cs.end(), [](const std::pair<int, char> &a, const std::pair<int, char> &b){
                if (a.first != b.first) {
                    return a.first > b.first;
                }
                return a.second < b.second;
            });
            // Verify checksum.
            bool room = true;
            for (int i = 0; i < 5 && room; i++) {
                if (count_cs[i].second != checksum[i]) {
                    room = false;
                }
            }
            if (room) {
                answer += sector;
            }
        } else {
            for (int i = 0; i < parts.size() - 1; i++) {
                for (char c : parts[i]) {
                    std::cout << (char) ((c - 'a' + sector) % 26 + 'a');
                }
                std::cout << " ";
            }
            std::cout << sector << std::endl;
        }
    }
    return answer;
}

long d05_how_about_a_nice_game_of_chess(std::vector<std::string> lines, int part) {
    std::string door_id = lines[0];
    std::string password = part == 1 ? "" : "zzzzzzzz";
    unsigned char digest[17];
    for (long i = 0; (part == 1 && password.length() < 8) || (part == 2 && password.find("z") != std::string::npos); i++) {
        char message[32];
        std::sprintf(message, "%s%ld", door_id.c_str(), i);
        cs_md5(message, digest);
        if (digest[0] == 0x00 && digest[1] == 0x00 && (digest[2] & 0xf0) == 0x00) {
            unsigned char sixth = digest[2] & 0x0f;
            if (part == 1) {
                password += (char) (sixth + (sixth <= 9 ? '0' : 'a' - 10));
            } else {
                int position = (int) sixth;
                if (position < password.length() && password[position] == 'z') {
                    unsigned char seventh = digest[3] >> 4;
                    password[position] = (char) (seventh + (seventh <= 9 ? '0' : 'a' - 10));
                }
            }
        }
    }
    std::cout << password << std::endl;
    return 0;
}

long d06_signals_and_noise(std::vector<std::string> lines, int part) {
    std::string answer = "";
    for (int j = 0; j < lines[0].length(); j++) {
        std::map<char, int> c_to_count;
        for (int i = 0; i < lines.size(); i++) {
            char c = lines[i][j];
            if (c_to_count.find(c) == c_to_count.end()) {
                c_to_count[c] = 0;
            }
            c_to_count[c] = c_to_count[c] + 1;
        }
        int polar_count = -1;
        char polar_c = '\0';
        for (const auto &c_count : c_to_count) {
            if (polar_count == -1 || (part == 1 && c_count.second > polar_count) || (part == 2 && c_count.second < polar_count)) {
                polar_count = c_count.second;
                polar_c = c_count.first;
            }
        }
        answer += polar_c;
    }
    std::cout << answer << std::endl;
    return 0;
}

long d07_internet_protocol_version_7(std::vector<std::string> lines, int part) {
    long tls = 0, ssl = 0;
    for (std::string line : lines) {
        bool inside_brackets = false;
        bool abba_outside = false;
        bool abba_inside = false;
        std::set<std::string> aba_outsides;
        std::set<std::string> bab_insides;
        for (int j = 0; j < line.length(); j++) {
            if (line[j] == '[') {
                inside_brackets = true;
                continue;
            } else if (line[j] == ']') {
                inside_brackets = false;
                continue;
            }
            if (j >= 2 && j < line.length() - 1 &&
                    line[j - 1] == line[j] && line[j - 2] == line[j + 1] && line[j] != line[j + 1]) {
                if (inside_brackets) {
                    abba_inside = true;
                } else {
                    abba_outside = true;
                }
            }
            if (j >= 1 && j < line.length() - 1 &&
                    line[j - 1] == line[j + 1] && line[j] != line[j + 1]) {
                std::string aba = line.substr(j - 1, 3);
                if (inside_brackets) {
                    bab_insides.insert(aba);
                } else {
                    aba_outsides.insert(aba);
                }
            }
        }
        if (abba_outside && !abba_inside) {
            tls++;
        }
        for (const std::string &aba : aba_outsides) {
            std::string bab = aba.substr(1, 2) + aba.substr(1, 1);
            if (bab_insides.find(bab) != bab_insides.end()) {
                ssl++;
                break;
            }
        }
    }
    return part == 1 ? tls : ssl;
}

long d08_two_factor_authentication(std::vector<std::string> lines, int part) {
    const int H = 6, W = 50;
    char pixels[H][W];
    for (int y = 0; y < H; y++) {
        for (int x = 0; x < W; x++) {
            pixels[y][x] = '.';
        }
    }
    for (std::string line : lines) {
        std::vector<std::string> parts = cs::string_split(line, " ");
        if (parts[0] == "rect") {
            std::vector<std::string> w_h = cs::string_split(parts[1], "x");
            const int w = std::stoi(w_h[0]);
            const int h = std::stoi(w_h[1]);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    pixels[y][x] = '#';
                }
            }
        } else if (parts[1] == "row") {
            const int y = std::stoi(parts[2].substr(2));
            const int by = std::stoi(parts[4]);
            char row[W];
            for (int x = 0; x < W; x++) {
                row[x] = pixels[y][x];
            }
            for (int x = 0; x < W; x++) {
                pixels[y][x] = row[(x + W - by) % W];
            }
        } else if (parts[1] == "column") {
            const int x = std::stoi(parts[2].substr(2));
            const int by = std::stoi(parts[4]);
            char column[H];
            for (int y = 0; y < H; y++) {
                column[y] = pixels[y][x];
            }
            for (int y = 0; y < H; y++) {
                pixels[y][x] = column[(y + H - by) % H];
            }
        }
    }

    long on = 0;
    for (int y = 0; y < H; y++) {
        for (int x = 0; x < W; x++) {
            if (pixels[y][x] == '#') {
                on++;
            }
            std::cout << pixels[y][x];
        }
        std::cout << std::endl;
    }
    return on;
}

long _d09_decompressed_length(std::string s, int version) {
    long length = 0;
    bool parentheses = false, read_repeat = false;
    int subsequent = 0, repeat = 0;
    for (int i = 0; i < s.length(); i++) {
        char c = s[i];
        if (c == '(') {
            if (parentheses) {
                std::cerr << "Unexpected opening parenthesis." << std::endl;
                exit(EXIT_FAILURE);
            }
            parentheses = true;
        } else if (c == ')') {
            if (!parentheses) {
                std::cerr << "Unexpected closing parenthesis." << std::endl;
                exit(EXIT_FAILURE);
            }
            std::string sub = s.substr(i + 1, subsequent);
            if (version == 1) {
                length += sub.length() * repeat;
            } else {
                length += _d09_decompressed_length(sub, version) * repeat;
            }
            i += subsequent;
            parentheses = false;
            read_repeat = false;
            subsequent = 0;
            repeat = 0;
        } else if (parentheses && !read_repeat && c != 'x') {
            subsequent = 10 * subsequent + (c - '0');
        } else if (parentheses && !read_repeat && c == 'x') {
            read_repeat = true;
        } else if (parentheses && read_repeat) {
            repeat = 10 * repeat + (c - '0');
        } else {
            length++;
        }
    }
    return length;
}

long d09_explosives_in_cyberspace(std::vector<std::string> lines, int part) {
    std::string file = lines[0];
    return _d09_decompressed_length(file, part);
}

long d10_balance_bots(std::vector<std::string> lines, int part) {
    std::map<std::string, std::string> bot_to_low;
    std::map<std::string, std::string> bot_to_high;
    std::map<std::string, std::vector<int>> entity_to_values;
    std::deque<std::string> bots_actionable;
    for (std::string line : lines) {
        std::vector<std::string> parts = cs::string_split(line, " ");
        if (parts[0] == "value") {
            int value = std::stoi(parts[1]);
            if (parts[4] != "bot") {
                std::cerr << "Unexpected value to non-bot: " << parts[4] << std::endl;
                exit(EXIT_FAILURE);
            }
            std::string bot = parts[4] + parts[5];
            if (entity_to_values.find(bot) == entity_to_values.end()) {
                entity_to_values[bot] = std::vector<int>();
            }
            entity_to_values[bot].push_back(value);
            if (entity_to_values[bot].size() == 2) {
                bots_actionable.push_back(bot);
            }
        } else if (parts[0] == "bot") {
            std::string bot = parts[0] + parts[1];
            if (bot_to_low.find(bot) != bot_to_low.end()) {
                std::cerr << "Unexpected repeated instruction: " << line << std::endl;
                exit(EXIT_FAILURE);
            }
            std::string low = parts[5] + parts[6];
            bot_to_low[bot] = low;
            if (bot_to_high.find(bot) != bot_to_high.end()) {
                std::cerr << "Unexpected repeated instruction: " << line << std::endl;
                exit(EXIT_FAILURE);
            }
            std::string high = parts[10] + parts[11];
            bot_to_high[bot] = high;
        } else {
            std::cerr << "Unexpected instruction: " << line << std::endl;
            exit(EXIT_FAILURE);
        }
    }

    // Process the queue.
    while (!bots_actionable.empty()) {
        std::string bot = bots_actionable.front();
        bots_actionable.pop_front();
        int value_low = entity_to_values[bot][0];
        int value_high = entity_to_values[bot][1];
        entity_to_values[bot].clear();
        if (value_low > value_high) {
            int temp = value_low;
            value_low = value_high;
            value_high = temp;
        }
        if (part == 1 && value_low == 17 && value_high == 61) {
            return std::stoi(bot.substr(3));
        }
        std::string low = bot_to_low[bot];
        if (entity_to_values.find(low) == entity_to_values.end()) {
            entity_to_values[low] = std::vector<int>();
        }
        entity_to_values[low].push_back(value_low);
        if (low.length() >= 3 && low.substr(0, 3) == "bot" && entity_to_values[low].size() == 2) {
            bots_actionable.push_back(low);
        }
        std::string high = bot_to_high[bot];
        if (entity_to_values.find(high) == entity_to_values.end()) {
            entity_to_values[high] = std::vector<int>();
        }
        entity_to_values[high].push_back(value_high);
        if (high.length() >= 3 && high.substr(0, 3) == "bot" && entity_to_values[high].size() == 2) {
            bots_actionable.push_back(high);
        }
    }
    long answer = 1;
    const std::string outputs[] = {"output0", "output1", "output2"};
    for (std::string output : outputs) {
        answer *= entity_to_values[output][0];
    }
    return answer;
}
