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
long d11_radioisotope_thermoelectric_generators(std::vector<std::string> lines, int part);
long d12_leonardos_monorail(std::vector<std::string> lines, int part);
long d13_a_maze_of_twisty_little_cubicles(std::vector<std::string> lines, int part);
long d14_one_time_pad(std::vector<std::string> lines, int part);
long d15_timing_is_everything(std::vector<std::string> lines, int part);
long d16_dragon_checksum(std::vector<std::string> lines, int part);

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
    } else if (day == 11) {
        solution = &d11_radioisotope_thermoelectric_generators;
    } else if (day == 12) {
        solution = &d12_leonardos_monorail;
    } else if (day == 13) {
        solution = &d13_a_maze_of_twisty_little_cubicles;
    } else if (day == 14) {
        solution = &d14_one_time_pad;
    } else if (day == 15) {
        solution = &d15_timing_is_everything;
    } else if (day == 16) {
        solution = &d16_dragon_checksum;
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

bool _d11_is_floor_valid(std::string floor) {
    int comma = floor.find(',');
    if (comma == 0) { // No generators.
        return true;
    }
    // A state is unstable when a generator is present with a microchip without its corresponding generator.
    std::string generators = floor.substr(0, comma);
    std::string microchips = floor.substr(comma + 1);
    for (char m : microchips) {
        if (generators.find(m) == -1) {
            return false;
        }
    }
    return true;
}

std::string _d11_create_state_next(
        std::string floor_next,
        std::string floor_adjacent_next,
        int elevator,
        int elevator_adjacent,
        std::vector<std::string> floor_generators,
        std::vector<std::string> floor_microchips
) {
    std::string state_next = "";
    state_next += (char) (elevator_adjacent + '0');
    for (int i = 0; i < floor_generators.size(); i++) {
        std::string floor;
        if (i == elevator) {
            floor = floor_next;
        } else if (i == elevator_adjacent) {
            floor = floor_adjacent_next;
        } else {
            floor = floor_generators[i] + "," + floor_microchips[i];
        }
        state_next += "|" + floor;
    }
    return state_next;
}

long d11_radioisotope_thermoelectric_generators(std::vector<std::string> lines, int part) {
    const int N = lines.size();
    std::vector<std::string> floors_initial;
    for (int i = 0; i < N; i++) {
        floors_initial.push_back("");
    }
    for (std::string line : lines) {
        std::vector<std::string> tokens = cs::string_split(line, " ");
        int i;
        if (tokens[1] == "first") {
            i = 0;
        } else if (tokens[1] == "second") {
            i = 1;
        } else if (tokens[1] == "third") {
            i = 2;
        } else { // if (tokens[1] == "fourth") {
            i = 3;
        }
        std::string generators = "";
        std::string microchips = "";
        if (tokens[4] == "nothing") {
        } else {
            int j = 4;
            while (true) {
                if (tokens[j] == "and") {
                    j++;
                }
                std::string element = tokens[j + 1];
                char element_c = element[0] - 'a' + 'A';
                std::string item = tokens[j + 2];
                char item_c = item[0];
                if (item_c == 'g') {
                    generators += element_c;
                } else if (item_c == 'm') {
                    microchips += element_c;
                } else {
                    std::cerr << "Unexpected item: " << item << std::endl;
                    exit(EXIT_FAILURE);
                }
                if (item[item.length() - 1] == '.') {
                    break;
                }
                j += 3;
            }
        }
        if (part == 2 && i == 0) {
            generators += "DE";
            microchips += "DE";
        }
        std::sort(generators.begin(), generators.end());
        std::sort(microchips.begin(), microchips.end());
        floors_initial[i] = generators + "," + microchips;
    }
    std::string state_initial = "0";
    for (std::string floor : floors_initial) {
        state_initial += "|" + floor;
    }
    std::cout << "state_initial: " << state_initial << std::endl;

    // Construct ending state prefix with (N - 1) empty floors.
    std::string state_final_prefix = "";
    state_final_prefix += (char) (N - 1 + '0');
    for (int i = 0; i < N - 1; i++) {
        state_final_prefix += "|,";
    }
    state_final_prefix += "|";

    std::set<std::string> states_prev; // Prevent immediate backtracking.
    std::set<std::string> states;
    states.insert(state_initial);
    long steps = 0;
    while (!states.empty()) {
        // Check termination condition.
        bool found = false;
        for (std::string state : states) {
            if (state.substr(0, state_final_prefix.length()) == state_final_prefix) {
                found = true;
                break;
            }
        }
        if (found) {
            break;
        }
        // Build next population of states.
        std::cout << "steps: " << steps << "; states: " << states.size() << std::endl;
        std::set<std::string> states_next;
        for (std::string state : states) {
            // De-serialize state.
            std::vector<std::string> floor_generators;
            std::vector<std::string> floor_microchips;
            std::vector<std::string> tokens = cs::string_split(state, "|");
            int elevator = std::stoi(tokens[0]);
            for (int i = 1; i < tokens.size(); i++) {
                std::string floor = tokens[i];
                int comma = floor.find(',');
                floor_generators.push_back(floor.substr(0, comma));
                floor_microchips.push_back(floor.substr(comma + 1));
            }
            std::string generators = floor_generators[elevator];
            std::string microchips = floor_microchips[elevator];
            int elevator_adjacents[] = {elevator - 1, elevator + 1}; // Up or down one level.
            for (int elevator_adjacent : elevator_adjacents) {
                // Skip floors out of bounds.
                if (elevator_adjacent < 0 || elevator_adjacent >= N) {
                    continue;
                }
                // Move a single item up or down.
                for (int j = 0; j < generators.length() + microchips.length(); j++) {
                    std::string generators_next = generators;
                    std::string microchips_next = microchips;
                    std::string generators_adjacent_next = floor_generators[elevator_adjacent];
                    std::string microchips_adjacent_next = floor_microchips[elevator_adjacent];
                    if (j < generators.length()) {
                        generators_next = generators.substr(0, j) + generators.substr(j + 1);
                        generators_adjacent_next = generators_adjacent_next + generators.substr(j, 1);
                    } else {
                        int jm = j - generators.length();
                        microchips_next = microchips.substr(0, jm) + microchips.substr(jm + 1);
                        microchips_adjacent_next = microchips_adjacent_next + microchips.substr(jm, 1);
                    }
                    std::sort(generators_next.begin(), generators_next.end());
                    std::sort(microchips_next.begin(), microchips_next.end());
                    std::sort(generators_adjacent_next.begin(), generators_adjacent_next.end());
                    std::sort(microchips_adjacent_next.begin(), microchips_adjacent_next.end());
                    std::string floor_next = generators_next + "," + microchips_next;
                    std::string floor_adjacent_next = generators_adjacent_next + "," + microchips_adjacent_next;
                    if (_d11_is_floor_valid(floor_next) && _d11_is_floor_valid(floor_adjacent_next)) {
                        std::string state_next = _d11_create_state_next(
                                floor_next, floor_adjacent_next,
                                elevator, elevator_adjacent,
                                floor_generators, floor_microchips);
                        if (states_prev.find(state_next) == states_prev.end()) {
                            states_next.insert(state_next);
                        }
                    }
                }
                // Move a pair of items.
                if (generators.length() + microchips.length() < 2) {
                    continue;
                }
                int len;
                unsigned short **item_indices = cs_combinations(generators.length() + microchips.length(), 2, &len);
                for (int k = 0; k < len; k++) {
                    std::string generators_next = generators;
                    std::string microchips_next = microchips;
                    std::string generators_adjacent_next = floor_generators[elevator_adjacent];
                    std::string microchips_adjacent_next = floor_microchips[elevator_adjacent];
                    // Indices returned from `cs_combinations` are in ascending order, so process last index first.
                    for (int q = 1; q >= 0; q--) {
                        unsigned short j = item_indices[k][q];
                        if (j < generators.length()) {
                            generators_next = generators_next.substr(0, j) + generators_next.substr(j + 1);
                            generators_adjacent_next = generators_adjacent_next + generators.substr(j, 1);
                        } else {
                            short jm = j - generators.length();
                            microchips_next = microchips_next.substr(0, jm) + microchips_next.substr(jm + 1);
                            microchips_adjacent_next = microchips_adjacent_next + microchips.substr(jm, 1);
                        }
                    }
                    std::sort(generators_next.begin(), generators_next.end());
                    std::sort(microchips_next.begin(), microchips_next.end());
                    std::sort(generators_adjacent_next.begin(), generators_adjacent_next.end());
                    std::sort(microchips_adjacent_next.begin(), microchips_adjacent_next.end());
                    std::string floor_next = generators_next + "," + microchips_next;
                    std::string floor_adjacent_next = generators_adjacent_next + "," + microchips_adjacent_next;
                    if (_d11_is_floor_valid(floor_next) && _d11_is_floor_valid(floor_adjacent_next)) {
                        std::string state_next = _d11_create_state_next(
                                floor_next, floor_adjacent_next,
                                elevator, elevator_adjacent,
                                floor_generators, floor_microchips);
                        if (states_prev.find(state_next) == states_prev.end()) {
                            states_next.insert(state_next);
                        }
                    }
                }
            }
        }
        states_prev = states;
        states = states_next;
        steps++;
    }
    return steps;
}

long d12_leonardos_monorail(std::vector<std::string> lines, int part) {
    int r[4];
    for (int i = 0; i < std::size(r); i++) {
        r[i] = 0;
    }
    if (part == 2) {
        r[2] = 1;
    }
    int i = 0;
    while (i < lines.size()) {
        std::string line = lines[i];
        std::vector<std::string> tokens = cs::string_split(line);
        std::string xs = tokens[1];
        if (tokens[0] == "cpy") {
            int x;
            if (xs.length() == 1 && xs[0] >= 'a' && xs[0] <= 'd') {
                x = r[xs[0] - 'a'];
            } else {
                x = (int) std::stoi(xs);
            }
            r[tokens[2][0] - 'a'] = x;
        } else if (tokens[0] == "inc") {
            r[xs[0] - 'a']++;
        } else if (tokens[0] == "dec") {
            r[xs[0] - 'a']--;
        } else if (tokens[0] == "jnz") {
            int x;
            if (xs.length() == 1 && xs[0] >= 'a' && xs[0] <= 'd') {
                x = r[xs[0] - 'a'];
            } else {
                x = (int) std::stoi(xs);
            }
            if (x != 0) {
                i += (int) std::stoi(tokens[2]);
                continue;
            }
        } else {
            std::cerr << "Unexpected instruction: " << tokens[0] << std::endl;
            exit(EXIT_FAILURE);
        }
        i++;
    }
    return r[0];
}

bool _d13_is_wall(int x, int y, int favorite) {
    unsigned long sum = (x * x) + (3 * x) + (2 * x * y) + y + (y * y) + favorite;
    int ones = 0;
    while (sum > 0) {
        if (sum % 2 == 1) {
            ones++;
        }
        sum >>= 1;
    }
    return ones % 2 == 1;
}

long d13_a_maze_of_twisty_little_cubicles(std::vector<std::string> lines, int part) {
    int favorite = std::stoi(lines[0]);
    std::map<std::pair<int, int>, int> point_to_dist;
    std::deque<std::pair<int, int>> frontier;
    std::pair<int, int> start(1, 1);
    point_to_dist[start] = 0;
    frontier.push_back(start);
    long answer = -1;
    while (frontier.size() > 0 && answer == -1) {
        std::pair<int, int> point = frontier.front();
        frontier.pop_front();
        if (part == 2 && point_to_dist[point] == 50) {
            answer = point_to_dist.size();
            break;
        }
        int x = point.first, y = point.second;
        std::pair<int, int> neighbors[] = {
                std::pair(x - 1, y), std::pair(x, y - 1), std::pair(x + 1, y), std::pair(x, y + 1)
        };
        for (std::pair<int, int> neighbor : neighbors) {
            int xd = neighbor.first, yd = neighbor.second;
            if (point_to_dist.find(neighbor) != point_to_dist.end() || xd < 0 || yd < 0 || _d13_is_wall(xd, yd, favorite)) {
                continue;
            }
            point_to_dist[neighbor] = point_to_dist[point] + 1;
            frontier.push_back(neighbor);
            if (part == 1 && xd == 31 && yd == 39) {
                answer = point_to_dist[neighbor];
                break;
            }
        }
    }
    return answer;
}

long d14_one_time_pad(std::vector<std::string> lines, int part) {
    std::string salt = lines[0];
    const int NEXT = 1000;
    const std::string digits = "0123456789abcdef";
    char message_c[32]; // Fed to MD5.
    unsigned char digest_c[17]; // Output from MD5.
    char hex_c[33]; // Hexadecimal representation of digest.
    std::deque<std::string> hexes;
    std::map<char, std::deque<int>> five_to_indices;
    for (char c : digits) {
        five_to_indices[c] = std::deque<int>();
    }
    int keys = 0;
    int index = 0;
    while (keys < 64) {
        // Trim outdated five-hashes.
        for (char c : digits) {
            while (five_to_indices[c].size() > 0 && five_to_indices[c].front() <= index - NEXT) {
                five_to_indices[c].pop_front();
            }
        }
        // Check for valid three-keys.
        if (hexes.size() >= NEXT) {
            std::string hex = hexes.front();
            hexes.pop_front();
            for (int j = 2; j < 32; j++) {
                char c = hex[j];
                if (hex[j - 1] == c && hex[j - 2] == c) {
                    if (five_to_indices[c].size() > 0) {
                        keys++;
                        std::cout << "index: " << index << "; c: " << c << "; keys: " << keys << std::endl;
                    }
                    break; // "Only consider the first such triplet in a hash."
                }
            }
        }
        // Generate hash `NEXT` steps away.
        std::sprintf(message_c, "%s%d", salt.c_str(), index);
        cs_md5(message_c, digest_c);
        for (int j = 0; j < 16; j++) {
            sprintf(hex_c + 2 * j, "%02x", digest_c[j]);
        }
        if (part == 2) {
            // Stretch key.
            for (int i = 0; i < 2016; i++) {
                cs_md5(hex_c, digest_c);
                for (int j = 0; j < 16; j++) {
                    sprintf(hex_c + 2 * j, "%02x", digest_c[j]);
                }
            }
        }
        hexes.push_back(std::string(hex_c));
        for (int j = 4; j < 32; j++) {
            char c = hex_c[j];
            if (hex_c[j - 1] == c && hex_c[j - 2] == c && hex_c[j - 3] == c && hex_c[j - 4] == c) {
                five_to_indices[c].push_back(index);
            }
        }
        index++;
    }
    return index - NEXT - 1;
}

long d15_timing_is_everything(std::vector<std::string> lines, int part) {
    const int N = part == 2 ? lines.size() + 1 : lines.size();
    int mods[N];
    int positions[N];
    for (std::string line : lines) {
        std::vector<std::string> tokens = cs::string_split(line, " ");
        int i = (int) std::stoi(tokens[1].substr(1)) - 1;
        mods[i] = (int) std::stoi(tokens[3]);
        positions[i] = (int) std::stoi(tokens[11].substr(0, tokens[11].length() - 1));
    }
    if (part == 2) {
        mods[N - 1] = 11;
        positions[N - 1] = 0;
    }
    long t = 0;
    while (true) {
        bool capture = true;
        for (int i = 0; i < N && capture; i++) {
            if ((positions[i] + t + i + 1) % mods[i] != 0) {
                capture = false;
            }
        }
        if (capture) {
            break;
        }
        t++;
    }
    return t;
}

long d16_dragon_checksum(std::vector<std::string> lines, int part) {
    std::string data = lines[0];
    const int disk = part == 2 ? 35651584 : 272;
    while (data.length() < disk) {
        std::string b = "";
        for (int i = data.length() - 1; i >= 0; i--) {
            b += data[i] == '1' ? "0" : "1";
        }
        data = data + "0" + b;
    }
    std::string checksum = data.substr(0, disk);
    while (checksum.length() % 2 == 0) {
        std::string checksum_next = "";
        for (int i = 0; i < checksum.length() / 2; i++) {
            if (checksum[2 * i] == checksum[2 * i + 1]) {
                checksum_next += "1";
            } else {
                checksum_next += "0";
            }
        }
        checksum = checksum_next;
    }
    std::cout << checksum << std::endl;
    return 0;
}
