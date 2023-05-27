#include "aoc.h"
#include "cs.h"

#include <math.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>

#define YEAR "2015"

long solve(int day, int part, char *input_path);

long d01_not_quite_lisp(char *lines[], int line_count, int part);
long d02_i_was_told_there_would_be_no_math(char *lines[], int line_count, int part);
long d03_perfectly_spherical_houses_in_a_vacuum(char *lines[], int line_count, int part);
long d04_the_ideal_stocking_stuffer(char *lines[], int line_count, int part);
long d05_doesnt_he_have_intern_elves_for_this(char *lines[], int line_count, int part);
long d06_probably_a_fire_hazard(char *lines[], int line_count, int part);
long d07_some_assembly_required(char *lines[], int line_count, int part);
long d08_matchsticks(char *lines[], int line_count, int part);
long d09_all_in_a_single_night(char *lines[], int line_count, int part);
long d10_elves_look_elves_say(char *lines[], int line_count, int part);
long d11_corporate_policy(char *lines[], int line_count, int part);
long d12_jsabacus_framework_io(char *lines[], int line_count, int part);
long d13_knights_of_the_dinner_table(char *lines[], int line_count, int part);
long d14_reindeer_olympics(char *lines[], int line_count, int part);
long d15_science_for_hungry_people(char *lines[], int line_count, int part);
long d16_aunt_sue(char *lines[], int line_count, int part);
long d17_no_such_thing_as_too_much(char *lines[], int line_count, int part);
long d18_like_a_gif_for_your_yard(char *lines[], int line_count, int part);
long d19_medicine_for_rudolph(char *lines[], int line_count, int part);
long d20_infinite_elves_and_infinite_houses(char *lines[], int line_count, int part);
long d21_rpg_simulator_20xx(char *lines[], int line_count, int part);
long d22_wizard_simulator_20xx(char *lines[], int line_count, int part);
long d23_opening_the_turing_lock(char *lines[], int line_count, int part);

int main(int argc, char *argv[]) {
    if (argc < 3) {
        fprintf(stderr, "Usage: %s <day> <part> [-x|--example]\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    int day = (int) strtol(argv[1], NULL, 10);
    if (day < 1 || day > 25) {
        fprintf(stderr, "Unexpected day: %d\n", day);
        exit(EXIT_FAILURE);
    }
    int part = (int) strtol(argv[2], NULL, 10);
    if (part != 1 && part != 2) {
        fprintf(stderr, "Unexpected part: %d\n", part);
        exit(EXIT_FAILURE);
    }
    bool example = false;
    if (argc > 3) {
        if (strcmp(argv[3], "-x") == 0 || strcmp(argv[3], "--example") == 0) {
            example = true;
        } else {
            fprintf(stderr, "Unexpected argument: %s\n", argv[3]);
            exit(EXIT_FAILURE);
        }
    }

    char *input_path;
    if (example) {
        input_path = aoc_example_path(YEAR, day);
    } else {
        input_path = aoc_download_input_if_needed(YEAR, day);
    }

    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC_RAW, &start);
    long answer = solve(day, part, input_path);
    clock_gettime(CLOCK_MONOTONIC_RAW, &end);

    fprintf(stdout, "%ld\n", answer);
    uint64_t duration_ms = (end.tv_sec - start.tv_sec) * 1000 + (end.tv_nsec - start.tv_nsec) / 1000000;
    fprintf(stdout, "Time: %llu ms\n", duration_ms);

    free(input_path);
    return 0;
}

long solve(int day, int part, char *input_path) {
    // Read lines from file.
    char *lines[2048];
    int line_count = 0;
    // https://man7.org/linux/man-pages/man3/getline.3.html
    FILE *fp = fopen(input_path, "r");
    char *line = NULL;
    size_t len = 0;
    ssize_t nread;
    while ((nread = getline(&line, &len, fp)) != -1) {
        // Remove newline.
        if (line[strlen(line) - 1] == '\n') {
            line[strlen(line) - 1] = '\0';
        }
        lines[line_count++] = line;
        line = NULL;
    }
    fclose(fp);

    long answer;
    long (*solution)(char *[], int, int); // Function pointer.
    if (day == 1) {
        solution = &d01_not_quite_lisp;
    } else if (day == 2) {
        solution = &d02_i_was_told_there_would_be_no_math;
    } else if (day == 3) {
        solution = &d03_perfectly_spherical_houses_in_a_vacuum;
    } else if (day == 4) {
        solution = &d04_the_ideal_stocking_stuffer;
    } else if (day == 5) {
        solution = &d05_doesnt_he_have_intern_elves_for_this;
    } else if (day == 6) {
        solution = &d06_probably_a_fire_hazard;
    } else if (day == 7) {
        solution = &d07_some_assembly_required;
    } else if (day == 8) {
        solution = &d08_matchsticks;
    } else if (day == 9) {
        solution = &d09_all_in_a_single_night;
    } else if (day == 10) {
        solution = &d10_elves_look_elves_say;
    } else if (day == 11) {
        solution = &d11_corporate_policy;
    } else if (day == 12) {
        solution = &d12_jsabacus_framework_io;
    } else if (day == 13) {
        solution = &d13_knights_of_the_dinner_table;
    } else if (day == 14) {
        solution = &d14_reindeer_olympics;
    } else if (day == 15) {
        solution = &d15_science_for_hungry_people;
    } else if (day == 16) {
        solution = &d16_aunt_sue;
    } else if (day == 17) {
        solution = &d17_no_such_thing_as_too_much;
    } else if (day == 18) {
        solution = &d18_like_a_gif_for_your_yard;
    } else if (day == 19) {
        solution = &d19_medicine_for_rudolph;
    } else if (day == 20) {
        solution = &d20_infinite_elves_and_infinite_houses;
    } else if (day == 21) {
        solution = &d21_rpg_simulator_20xx;
    } else if (day == 22) {
        solution = &d22_wizard_simulator_20xx;
    } else if (day == 23) {
        solution = &d23_opening_the_turing_lock;
    } else {
        fprintf(stderr, "No solution for day `%d` part `%d`.", day, part);
        exit(EXIT_FAILURE);
    }
    answer = (*solution)(lines, line_count, part);

    // Release lines.
    while (line_count) {
        free(lines[--line_count]);
    }

    return answer;
}

long d01_not_quite_lisp(char *lines[], int line_count, int part) {
    if (line_count != 1) {
        fprintf(stderr, "Unexpected lines: %d\n", line_count);
        exit(EXIT_FAILURE);
    }

    char *instructions = lines[0];
    int floor = 0;
    for (int i = 0; i < strlen(instructions); i++) {
        if (instructions[i] == '(') {
            floor++;
        } else if (instructions[i] == ')') {
            floor--;
            if (part == 2 && floor == -1) {
                return i + 1;
            }
        } else {
            fprintf(stderr, "Unexpected character: %c\n", instructions[i]);
            exit(EXIT_FAILURE);
        }
    }
    return floor;
}

long d02_i_was_told_there_would_be_no_math(char *lines[], int line_count, int part) {
    int x = 0;
    const int N_SIDES = 3;
    for (int i = 0; i < line_count; i++) {
        char *line = lines[i];
        int sides[N_SIDES];
        int s = 0;
        int v = 0;
        for (int j = 0; j < strlen(line)-1; j++) {
            if (line[j] == 'x') {
                sides[s] = v;
                s++;
                v = 0;
            } else {
                v = 10 * v + (line[j] - '0');
            }
        }
        sides[s] = v;

        int s_max = cs_imax(sides, N_SIDES);
        if (part == 1) {
            int area = 2*sides[0]*sides[1] + 2*sides[0]*sides[2] + 2*sides[1]*sides[2];
            int slack;
            if (s_max == 0) {
                slack = sides[1]*sides[2];
            } else if (s_max == 1) {
                slack = sides[0]*sides[2];
            } else {
                slack = sides[0]*sides[1];
            }
            x += area + slack;
        } else {
            int ribbon;
            if (s_max == 0) {
                ribbon = 2*sides[1] + 2*sides[2];
            } else if (s_max == 1) {
                ribbon = 2*sides[0] + 2*sides[2];
            } else {
                ribbon = 2*sides[0] + 2*sides[1];
            }
            int bow = sides[0]*sides[1]*sides[2];
            x += ribbon + bow;
        }
    }
    return x;
}

long d03_perfectly_spherical_houses_in_a_vacuum(char *lines[], int line_count, int part) {
    CS_Dict *dict = cs_dict_new(1024);
    cs_dict_put(dict, "++00000000", NULL);
    int x[2];
    x[0] = 0; x[1] = 0;
    int y[2];
    y[0] = 0; y[1] = 0;
    char code[11];
    char *moves = lines[0];
    for (int i = 0; i < strlen(moves); i++) {
        char move = moves[i];
        int robo = part == 1 || i % 2 == 0 ? 0 : 1;
        if (move == '^') {
            y[robo]++;
        } else if (move == 'v') {
            y[robo]--;
        } else if (move == '>') {
            x[robo]++;
        } else if (move == '<') {
            x[robo]--;
        } else {
            fprintf(stderr, "Unexpected character: %c\n", move);
            exit(EXIT_FAILURE);
        }
        code[0] = x[robo] < 0 ? '-' : '+';
        code[1] = y[robo] < 0 ? '-' : '+';
        sprintf(code + 2, "%04d%04d", abs(x[robo]), abs(y[robo]));
        cs_dict_put(dict, code, NULL);
    }
    unsigned int answer = cs_dict_size(dict);
    cs_dict_deinit(dict);
    return answer;
}

long d04_the_ideal_stocking_stuffer(char *lines[], int line_count, int part) {
    char *key = lines[0];

    int answer = 1;
    unsigned char digest[17];
    while (true) {
        int digits = (int) floor(log10(answer));
        char *message = malloc((strlen(key) + digits + 1) * sizeof(char));
        sprintf(message, "%s%d", key, answer);
        cs_md5(message, digest);
        free(message);
        if (part == 1 && digest[0] == 0x00 && digest[1] == 0x00 && (digest[2] & 0xf0) == 0x00) {
            break;
        } else if (part == 2 && digest[0] == 0x00 && digest[1] == 0x00 && digest[2] == 0x00) {
            break;
        }
        answer++;
    }
    printf("digest:");
    for (int i = 0; i < 16; i++) {
        if (i % 4 == 0) {
            printf(" ");
        }
        printf("%02x", digest[i]);
    }
    printf("\n");
    return answer;
}

long d05_doesnt_he_have_intern_elves_for_this(char *lines[], int line_count, int part) {
    int n = 0;
    for (int i = 0; i < line_count; i++) {
        char *s = lines[i];
        if (part == 1) {
            bool nice = true;
            int vowels = 0;
            bool double_letter = false;
            for (int j = 0; j < strlen(s) && nice; j++) {
                if (s[j] == 'a' || s[j] == 'e' || s[j] == 'i' || s[j] == 'o' || s[j] == 'u') {
                    vowels++;
                }
                if (j > 0) {
                    if (s[j] == s[j - 1]) {
                        double_letter = true;
                    }
                    if ((s[j - 1] == 'a' && s[j] == 'b') || (s[j - 1] == 'c' && s[j] == 'd') ||
                        (s[j - 1] == 'p' && s[j] == 'q') || (s[j - 1] == 'x' && s[j] == 'y')) {
                        nice = false;
                    }
                }
            }
            if (nice && vowels >= 3 && double_letter) {
                n++;
            }
        } else if (part == 2) {
            bool two_pair = false;
            CS_Dict *pair_to_index = cs_dict_new(8);
            char pair[3];
            char **index_strs = malloc((strlen(s) - 1) * sizeof(char *));
            for (int j = 1; j < strlen(s); j++) {
                index_strs[j - 1] = malloc(3 * sizeof(char));
            }
            bool between = false;
            for (int j = 1; j < strlen(s) && (!two_pair || !between); j++) {
                if (!two_pair) {
                    pair[0] = s[j - 1];
                    pair[1] = s[j];
                    if (cs_dict_contains(pair_to_index, pair)) {
                        int index = (int) strtol(cs_dict_get(pair_to_index, pair), NULL, 10);
                        if (j - index > 1) {
                            two_pair = true;
                        }
                    } else {
                        sprintf(index_strs[j - 1], "%d", j);
                        cs_dict_put(pair_to_index, pair, index_strs[j - 1]);
                    }
                }
                if (!between && j > 1 && s[j] == s[j - 2]) {
                    between = true;
                }
            }
            if (two_pair && between) {
                n++;
            }
            for (int j = 1; j < strlen(s); j++) {
                free(index_strs[j - 1]);
            }
            free(index_strs);
            cs_dict_deinit(pair_to_index);
        }
    }
    return n;
}

long d06_probably_a_fire_hazard(char *lines[], int line_count, int part) {
    const int N = 1000, M = 1000;
    unsigned char lights[N][M];
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < M; j++) {
            lights[i][j] = 0;
        }
    }

    for (int k = 0; k < line_count; k++) {
        char *line = lines[k];
        char *instruction, *a, *b;
        const char space[] = " ";
        int ia, ja, ib, jb;
        const char comma[] = ",";
        instruction = strtok(line, space);
        if (strcmp(instruction, "turn") == 0) {
            instruction = strtok(NULL, space);
        }
        a = strtok(NULL, space);
        strtok(NULL, space); // Skip 'through'.
        b = strtok(NULL, space);
        ia = (int) strtol(strtok(a, comma), NULL, 10);
        ja = (int) strtol(strtok(NULL, comma), NULL, 10);
        ib = (int) strtol(strtok(b, comma), NULL, 10);
        jb = (int) strtol(strtok(NULL, comma), NULL, 10);
        if (strcmp(instruction, "on") == 0) {
            for (int i = ia; i <= ib; i++) {
                for (int j = ja; j <= jb; j++) {
                    if (part == 1) {
                        lights[i][j] = 1;
                    } else {
                        lights[i][j] += 1;
                    }
                }
            }
        } else if (strcmp(instruction, "off") == 0) {
            for (int i = ia; i <= ib; i++) {
                for (int j = ja; j <= jb; j++) {
                    if (part == 1) {
                        lights[i][j] = 0;
                    } else if (lights[i][j] > 0) {
                        lights[i][j] -= 1;
                    }
                }
            }
        } else if (strcmp(instruction, "toggle") == 0) {
            for (int i = ia; i <= ib; i++) {
                for (int j = ja; j <= jb; j++) {
                    if (part == 1) {
                        lights[i][j] = lights[i][j] == 1 ? 0 : 1;
                    } else {
                        lights[i][j] += 2;
                    }
                }
            }
        } else {
            fprintf(stderr, "Unexpected instruction: %s\n", instruction);
            exit(EXIT_FAILURE);
        }
    }

    int n = 0;
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < M; j++) {
            n += lights[i][j];
        }
    }
    return n;
}

unsigned short _d07_circuit_value(CS_Dict *circuit, const char *wire, CS_Dict *memo) {
    if (!cs_dict_contains(circuit, wire)) { // Numeric literal.
        return (unsigned short) strtol(wire, NULL, 10);
    }
    if (cs_dict_contains(memo, wire)) { // Cached.
        return (unsigned short) strtol(cs_dict_get(memo, wire), NULL, 10);
    }
    unsigned short value;
    char *value_str;
    char *token, *a, *op, *b;
    unsigned short a_value, b_value;
    const char space[] = " ";
    char *input = cs_dict_get(circuit, wire);
    token = strtok(input, space);
    if (strcmp(token, "NOT") == 0) { // Unary operator NOT.
        a = strtok(NULL, space);
        a_value = _d07_circuit_value(circuit, a, memo);
        value = ~a_value;
    } else {
        a = token;
        op = strtok(NULL, space);
        if (op == NULL) { // Identity.
            value = _d07_circuit_value(circuit, a, memo);
        } else { // Binary operator.
            b = strtok(NULL, space);
            a_value = _d07_circuit_value(circuit, a, memo);
            b_value = _d07_circuit_value(circuit, b, memo);
            if (strcmp(op, "AND") == 0) {
                value = a_value & b_value;
            } else if (strcmp(op, "OR") == 0) {
                value = a_value | b_value;
            } else if (strcmp(op, "LSHIFT") == 0) {
                value = a_value << b_value;
            } else if (strcmp(op, "RSHIFT") == 0) {
                value = a_value >> b_value;
            } else {
                fprintf(stderr, "Unrecognized operator: %s\n", op);
                exit(EXIT_FAILURE);
            }
        }
    }
    value_str = malloc(((value > 0 ? (int) floor(log10(value)) : 1) + 1) * sizeof(char));
    sprintf(value_str, "%d", value);
    cs_dict_put(memo, wire, value_str);
    return value;
}

long d07_some_assembly_required(char *lines[], int line_count, int part) {
    CS_Dict *circuit = cs_dict_new(1024);
    for (int i = 0; i < line_count; i++) {
        char *line = lines[i];
        char *input, *wire;
        char *arrow = strstr(line, " -> ");
        input = malloc((arrow - line + 1) * sizeof(char));
        strncpy(input, line, arrow - line);
        wire = arrow + 4;
        cs_dict_put(circuit, wire, input);
    }
    if (part == 2) {
        unsigned short a_value = d07_some_assembly_required(lines, line_count, 1);
        printf("a_value: %d\n", a_value);
        char *a_value_str = malloc(((int) floor(log10(a_value)) + 1) * sizeof(char));
        sprintf(a_value_str, "%d", a_value);
        cs_dict_put(circuit, "b", a_value_str);
        printf("new b: %s\n", cs_dict_get(circuit, "b"));
    }
    CS_Dict *memo = cs_dict_new(1024);
    unsigned short answer = _d07_circuit_value(circuit, "a", memo);
    cs_dict_deinit(memo);
    cs_dict_deinit(circuit);
    return answer;
}

long d08_matchsticks(char *lines[], int line_count, int part) {
    int n = 0;
    for (int i = 0; i < line_count; i++) {
        char *line = lines[i];
        int code_len = strlen(line);
        if (line[0] != '"' || line[code_len - 1] != '"') {
            fprintf(stderr, "Unexpected string: %s\n", line);
            exit(EXIT_FAILURE);
        }
        if (part == 1) {
            int mem_len = 0;
            for (int j = 1; j < code_len - 1; j++) {
                if (line[j] != '\\') {
                    mem_len++;
                } else if (line[j + 1] == '\\' || line[j + 1] == '"') {
                    mem_len++;
                    j++;
                } else if (line[j + 1] == 'x') {
                    mem_len++;
                    j += 3;
                } else {
                    fprintf(stderr, "Unexpected string: %s\n", line);
                    exit(EXIT_FAILURE);
                }
            }
            n += code_len - mem_len;
        } else {
            int encoded_len = 2 + code_len;
            for (int j = 0; j < code_len; j++) {
                if (line[j] == '\\' || line[j] == '"') {
                    encoded_len++;
                }
            }
            n += encoded_len - code_len;
        }
    }
    return n;
}

long d09_all_in_a_single_night(char *lines[], int line_count, int part) {
    const int n = (int) ceil(sqrt(2 * line_count));
    if (n * (n - 1) / 2 != line_count) {
        fprintf(stderr, "Unexpected number of lines: %d\n", line_count);
        exit(EXIT_FAILURE);
    }

    // Collect distances in square matrix.
    int d[n][n];
    CS_Dict *locations = cs_dict_new(2 * n);
    char *index_strs[n];
    for (int i = 0; i < line_count; i++) {
        char *line = lines[i];
        char *a_str, *to, *b_str, *equal;
        int a, b, dist;
        to = strstr(line, " to ");
        equal = strstr(line, " = ");
        to[0] = '\0';
        equal[0] = '\0';
        a_str = line;
        if (!cs_dict_contains(locations, a_str)) {
            int index = cs_dict_size(locations);
            char *index_str = malloc(3 * sizeof(char));
            sprintf(index_str, "%02d", index);
            cs_dict_put(locations, a_str, index_str);
            index_strs[index] = index_str;
        }
        a = (int) strtol(cs_dict_get(locations, a_str), NULL, 10);
        b_str = to + 4;
        if (!cs_dict_contains(locations, b_str)) {
            int index = cs_dict_size(locations);
            char *index_str = malloc(3 * sizeof(char));
            sprintf(index_str, "%02d", index);
            cs_dict_put(locations, b_str, index_str);
            index_strs[index] = index_str;
        }
        b = (int) strtol(cs_dict_get(locations, b_str), NULL, 10);
        dist = (int) strtol(equal + 3, NULL, 10);
        d[a][b] = dist;
        d[b][a] = dist;
    }
    cs_dict_deinit(locations);

    // Try all permutations of locations.
    int polar_dist = -1;
    int len;
    char ***permutations = cs_permutations(index_strs, n, &len);
    for (int i = 0; i < len; i++) {
        char **permutation = permutations[i];
        // Calculate sum of distances between locations for this permutation.
        int dist = 0;
        int a, b;
        a = (int) strtol(permutation[0], NULL, 10);
        for (int j = 1; j < n; j++) {
            b = (int) strtol(permutation[j], NULL, 10);
            dist += d[a][b];
            a = b;
        }
        if (part == 1 && (polar_dist == -1 || dist < polar_dist)) {
            polar_dist = dist;
        } else if (part == 2 && (polar_dist == -1 || dist > polar_dist)) {
            polar_dist = dist;
        }
    }
    free(permutations);
    for (int i = 0; i < n; i++) {
        free(index_strs[i]);
    }
    return polar_dist;
}

long d10_elves_look_elves_say(char *lines[], int line_count, int part) {
    int n = strlen(lines[0]);
    char *seq = malloc((n + 1) * sizeof(char));
    seq[n] = '\0';
    strcpy(seq, lines[0]);
    const int rounds = part == 1 ? 40 : 50;
    for (int i = 0; i < rounds; i++) {
        n = strlen(seq);
        char *next = malloc(sizeof(char));
        next[0] = '\0';
        int len = 1;
        char buffer[8];
        int count = 1;
        for (int j = 1; j < n; j++) {
            if (seq[j] == seq[j - 1]) {
                count++;
            } else {
                sprintf(buffer, "%d%c", count, seq[j - 1]);
                const int b = strlen(buffer);
                next = realloc(next, len + b);
                strcpy(next + len - 1, buffer);
                len += b;
                count = 1;
            }
        }
        sprintf(buffer, "%d%c", count, seq[n - 1]);
        const int b = strlen(buffer);
        next = realloc(next, len + b);
        strcpy(next + len - 1, buffer);
        next[len + b] = '\0';
        free(seq);
        seq = next;
    }
    long answer = strlen(seq);
    free(seq);
    return answer;
}

long d11_corporate_policy(char *lines[], int line_count, int part) {
    const int len = strlen(lines[0]);
    char password[len + 1];
    strcpy(password, lines[0]);
    password[len] = '\0';
    while (part > 0) {
        // Increment password, wrapping 'z' to 'a'.
        int carry = len - 1;
        while (carry != -1) {
            if (password[carry] == 'z') {
                password[carry] = 'a';
                carry--;
            } else {
                password[carry]++;
                carry = -1;
            }
        }
        // Check conditions.
        bool straight = false;
        bool confusing = false;
        char first_pair_c = -1;
        bool two_pair = false;
        for (int i = 0; i < len; i++) {
            const char c = password[i];
            if (i >= 2 && !straight) {
                if (password[i - 2] == c - 2 && password[i - 1] == c - 1) {
                    straight = true;
                }
            }
            if (c == 'i' || c == 'o' || c == 'l') {
                confusing = true;
                break;
            }
            if (i >= 1 && !two_pair) {
                if (c == password[i - 1]) {
                    if (first_pair_c == -1) {
                        first_pair_c = c;
                    } else if (c != first_pair_c) {
                        two_pair = true;
                    }
                }
            }
        }
        if (straight && !confusing && two_pair) {
            printf("%s\n", password);
            part--;
        }
    }
    return 0;
}

long _d12_object_value(char *json, int *index) {
    if (json[*index] != '{') {
        fprintf(stderr, "Unexpected character at index %d: %c\n", *index, json[*index]);
        exit(EXIT_FAILURE);
    }
    int start = *index;
    (*index)++;
    long sum = 0;
    bool red = false;
    bool minus = false;
    int x = 0;
    while (true) {
        if (json[*index] == '{') {
            sum += _d12_object_value(json, index);
        } else if ((*index) - start >= 7 && strncmp(json + (*index) - 5, ":\"red\"", 6) == 0) {
            red = true;
        } else if (json[*index] == '-') {
            minus = true;
        } else if (json[*index] >= '0' && json[*index] <= '9') {
            x = 10 * x + (char) (json[*index] - '0');
        } else {
            if (x != 0) {
                sum += minus ? -x : x;
                minus = false;
                x = 0;
            }
            if (json[*index] == '}') {
                break;
            }
        }
        (*index)++;
    }
    return red ? 0 : sum;
}

long d12_jsabacus_framework_io(char *lines[], int line_count, int part) {
    char *json = lines[0];
    long sum = 0;
    if (part == 1) {
        bool minus = false;
        int x = 0;
        for (int i = 0; i < strlen(json); i++) {
            const char c = json[i];
            if (c == '-') {
                minus = true;
            } else if (c >= '0' && c <= '9') {
                x = 10 * x + (c - '0');
            } else if (x != 0) {
                sum += minus ? -x : x;
                minus = false;
                x = 0;
            }
        }
    } else {
        int index = 0;
        sum = _d12_object_value(json, &index);
    }
    return sum;
}

long d13_knights_of_the_dinner_table(char *lines[], int line_count, int part) {
    int n = (int) ceil(sqrt(line_count));
    if (n * (n - 1) != line_count) {
        fprintf(stderr, "Unexpected number of lines: %d\n", line_count);
        exit(EXIT_FAILURE);
    }
    if (part == 2) {
        n++; // Add self.
    }

    // Collect happiness units in square matrix.
    int d[n][n];
    CS_Dict *names = cs_dict_new(2 * n);
    char *index_strs[n];
    for (int i = 0; i < line_count; i++) {
        char *line = lines[i];
        const char space[] = " ";
        char *a_name, *polarity, *value_str, *b_name;
        int a, b, value;
        // Read.
        a_name = strtok(line, space); // Alice
        strtok(NULL, space); // would
        polarity = strtok(NULL, space); // gain/lose
        value_str = strtok(NULL, space); // #
        for (int j = 0; j < 6; j++) {
            strtok(NULL, space); // happiness units by sitting next to
        }
        b_name = strtok(NULL, space); // Bob.
        b_name[strlen(b_name) - 1] = '\0';
        // Process.
        if (!cs_dict_contains(names, a_name)) {
            int index = cs_dict_size(names);
            char *index_str = malloc(3 * sizeof(char));
            sprintf(index_str, "%02d", index);
            cs_dict_put(names, a_name, index_str);
            index_strs[index] = index_str;
        }
        a = (int) strtol(cs_dict_get(names, a_name), NULL, 10);
        value = (int) strtol(value_str, NULL, 10);
        if (!cs_dict_contains(names, b_name)) {
            int index = cs_dict_size(names);
            char *index_str = malloc(3 * sizeof(char));
            sprintf(index_str, "%02d", index);
            cs_dict_put(names, b_name, index_str);
            index_strs[index] = index_str;
        }
        b = (int) strtol(cs_dict_get(names, b_name), NULL, 10);
        if (strcmp(polarity, "gain") == 0) {
            d[a][b] = value;
        } else if (strcmp(polarity, "lose") == 0) {
            d[a][b] = -value;
        } else {
            fprintf(stderr, "Unexpected polarity: %s\n", polarity);
            exit(EXIT_FAILURE);
        }
    }
    cs_dict_deinit(names);

    if (part == 2) {
        int index = n - 1;
        char *index_str = malloc(3 * sizeof(char));
        sprintf(index_str, "%02d", index);
        index_strs[index] = index_str;
        for (int i = 0; i < n - 1; i++) {
            d[index][i] = 0;
            d[i][index] = 0;
        }
    }

    // Try all permutations of names.
    int max_value = -1;
    int len;
    char ***permutations = cs_permutations(index_strs, n, &len);
    for (int i = 0; i < len; i++) {
        char **permutation = permutations[i];
        // Calculate sum of happiness units between names for this permutation.
        int value = 0;
        int a, b;
        a = (int) strtol(permutation[0], NULL, 10);
        for (int j = 1; j < n; j++) {
            b = (int) strtol(permutation[j], NULL, 10);
            value += d[a][b];
            value += d[b][a];
            a = b;
        }
        a = (int) strtol(permutation[0], NULL, 10);
        value += d[a][b];
        value += d[b][a];
        if (max_value == -1 || value > max_value) {
            max_value = value;
        }
    }
    free(permutations);
    for (int i = 0; i < n; i++) {
        free(index_strs[i]);
    }
    return max_value;
}

long d14_reindeer_olympics(char *lines[], int line_count, int part) {
    const int wall = 2503;
    const int n = line_count;
    int speeds[n], times[n], rests[n];
    for (int i = 0; i < n; i++) {
        char *line = lines[i];
        const char space[] = " ";
        char *speed_str, *time_str, *rest_str;
        strtok(line, space); // Comet
        strtok(NULL, space); // can
        strtok(NULL, space); // fly
        speed_str = strtok(NULL, space); // #
        strtok(NULL, space); // km/s
        strtok(NULL, space); // for
        time_str = strtok(NULL, space); // #
        for (int j = 0; j < 6; j++) {
            strtok(NULL, space); // seconds, but then must rest for
        }
        rest_str = strtok(NULL, space); // #
        // strtok(NULL, space); // seconds.
        speeds[i] = (int) strtol(speed_str, NULL, 10);
        times[i] = (int) strtol(time_str, NULL, 10);
        rests[i] = (int) strtol(rest_str, NULL, 10);
    }

    if (part == 1) {
        int max_dist = -1;
        for (int i = 0; i < n; i++) {
            int speed = speeds[i], time = times[i], rest = rests[i], dist;
            dist = (wall / (time + rest)) * (speed * time) + cs_min(time, (wall % (time + rest))) * speed;
            if (max_dist == -1 || dist > max_dist) {
                max_dist = dist;
            }
        }
        return max_dist;
    }

    int dists[n], points[n];
    for (int i = 0; i < n; i++) {
        dists[i] = 0;
        points[i] = 0;
    }
    for (int t = 0; t < wall; t++) {
        for (int i = 0; i < n; i++) {
            int speed = speeds[i], time = times[i], rest = rests[i];
            if (t % (time + rest) < time) {
                dists[i] += speed;
            }
        }
        points[cs_imax(dists, n)]++;
    }
    return points[cs_imax(points, n)];
}

long d15_science_for_hungry_people(char *lines[], int line_count, int part) {
    const int tsp = 100;
    const int n = line_count;
    int caps[n], durs[n], flas[n], texs[n], cals[n];
    for (int i = 0; i < n; i++) {
        char *line = lines[i];
        // Read.
        const char space[] = " ";
        char *cap_str, *dur_str, *fla_str, *tex_str, *cal_str;
        strtok(line, space); // Ingredient:
        strtok(NULL, space); // capacity
        cap_str = strtok(NULL, space); // #,
        strtok(NULL, space); // durability
        dur_str = strtok(NULL, space); // #,
        strtok(NULL, space); // flavor
        fla_str = strtok(NULL, space); // #,
        strtok(NULL, space); // texture
        tex_str = strtok(NULL, space); // #,
        strtok(NULL, space); // calories
        cal_str = strtok(NULL, space); // #
        // Process.
        cap_str[strlen(cap_str) - 1] = '\0';
        caps[i] = (int) strtol(cap_str, NULL, 10);
        dur_str[strlen(dur_str) - 1] = '\0';
        durs[i] = (int) strtol(dur_str, NULL, 10);
        fla_str[strlen(fla_str) - 1] = '\0';
        flas[i] = (int) strtol(fla_str, NULL, 10);
        tex_str[strlen(tex_str) - 1] = '\0';
        texs[i] = (int) strtol(tex_str, NULL, 10);
        cals[i] = (int) strtol(cal_str, NULL, 10);
    }

    int len;
    unsigned short **bucket_permutations = cs_bucket_permutations(tsp, n, &len);
    int max_score = -1;
    for (int i = 0; i < len; i++) {
        unsigned short *bucket_permutation = bucket_permutations[i];
        int score, cap = 0, dur = 0, fla = 0, tex = 0, cal = 0;
        for (int j = 0; j < n; j++) {
            cap += bucket_permutation[j] * caps[j];
            dur += bucket_permutation[j] * durs[j];
            fla += bucket_permutation[j] * flas[j];
            tex += bucket_permutation[j] * texs[j];
            cal += bucket_permutation[j] * cals[j];
        }
        if (part == 2 && cal != 500) {
            continue;
        }
        score = cs_max(0, cap) * cs_max(0, dur) * cs_max(0, fla) * cs_max(0, tex);
        if (max_score == -1 || score > max_score) {
            max_score = score;
        }
    }
    return max_score;
}

long d16_aunt_sue(char *lines[], int line_count, int part) {
    const int n = line_count;
    CS_Dict *aunt_item_to_count[n];
    for (int i = 0; i < n; i++) {
        char *line = lines[i];
        CS_Dict *item_to_count = cs_dict_new(8);
        const char space[] = " ";
        char *item, *count_str;
        strtok(line, space); // Sue
        strtok(NULL, space); // #:
        while (true) {
            bool end = false;
            item = strtok(NULL, space); // cats:
            item[strlen(item) - 1] = '\0';
            count_str = strtok(NULL, space); // #[,]
            if (count_str[strlen(count_str) - 1] == ',') {
                count_str[strlen(count_str) - 1] = '\0';
            } else {
                end = true;
            }
            cs_dict_put(item_to_count, item, count_str);
            if (end) {
                break;
            }
        }
        aunt_item_to_count[i] = item_to_count;
    }

    long answer = -1;
    CS_Dict *ticker_to_count = cs_dict_new(16);
    const char *ticker_keys[] = {
            "children", "cats", "samoyeds", "pomeranians", "akitas", "vizslas", "goldfish", "trees", "cars", "perfumes"
    };
    const char *ticker_values[] = {
            "3", "7", "2", "3", "0", "0", "5", "3", "2", "1"
    };
    for (int i = 0; i < 10; i++) {
        cs_dict_put(ticker_to_count, ticker_keys[i], (void *) ticker_values[i]);
    }
    for (int i = 0; i < n; i++) {
        CS_Dict *item_to_count = aunt_item_to_count[i];
        const int k = cs_dict_size(item_to_count);
        char *keys[k];
        cs_dict_keys(item_to_count, keys);
        bool match = true;
        for (int j = 0; j < k && match; j++) {
            char *key = keys[j];
            int count = (int) strtol(cs_dict_get(item_to_count, key), NULL, 10);
            int ticker_count = (int) strtol(cs_dict_get(ticker_to_count, key), NULL, 10);
            if (part == 1) {
                if (count != ticker_count) {
                    match = false;
                }
            } else {
                if (strcmp(key, "cats") == 0 || strcmp(key, "trees") == 0) {
                    if (count <= ticker_count) {
                        match = false;
                    }
                } else if (strcmp(key, "pomeranians") == 0 || strcmp(key, "goldfish") == 0) {
                    if (count >= ticker_count) {
                        match = false;
                    }
                } else if (count != ticker_count) {
                    match = false;
                }
            }
        }
        if (match) {
            answer = i + 1;
            break;
        }
    }
    cs_dict_deinit(ticker_to_count);
    for (int i = 0; i < n; i++) {
        cs_dict_deinit(aunt_item_to_count[i]);
    }
    return answer;
}

long d17_no_such_thing_as_too_much(char *lines[], int line_count, int part) {
    const int n = line_count;
    int containers[n];
    for (int i = 0; i < n; i++) {
        containers[i] = (int) strtol(lines[i], NULL, 10);
    }

    const int liters = 150;

    if (part == 1) {
        int volumes[liters + 1];
        volumes[0] = 1;
        for (int j = 1; j <= liters; j++) {
            volumes[j] = 0;
        }
        for (int i = 0; i < n; i++) {
            for (int j = liters; j >= containers[i]; j--) {
                volumes[j] += volumes[j - containers[i]];
            }
        }
        return volumes[liters];
    }

    int cardinality[liters + 1][n + 1];
    for (int j = 0; j <= liters; j++) {
        for (int i = 0; i <= n; i++) {
            cardinality[j][i] = 0;
        }
    }
    cardinality[0][0] = 1;
    for (int i = 0; i < n; i++) {
        for (int j = liters; j >= containers[i]; j--) {
            for (int k = 0; k <= n; k++) {
                cardinality[j][k + 1] += cardinality[j - containers[i]][k];
            }
        }
    }
    for (int i = 1; i <= n; i++) {
        if (cardinality[liters][i] > 0) {
            return cardinality[liters][i];
        }
    }
    return -1;
}

long d18_like_a_gif_for_your_yard(char *lines[], int line_count, int part) {
    const int n = line_count, m = strlen(lines[0]);
    char lights[n][m];
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            lights[i][j] = lines[i][j];
        }
    }

    const int rounds = 100;
    for (int round = 0; round < rounds; round++) {
        char next_lights[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (part == 2) {
                    if ((i == 0 && j == 0) || (i == 0 && j == m - 1) ||
                            (i == n - 1 && j == 0) || (i == n - 1 && j == m - 1)) {
                        next_lights[i][j] = '#';
                        continue;
                    }
                }
                int on = 0;
                for (int di = -1; di <= 1; di++) {
                    if (i + di < 0 || i + di >= n) {
                        continue;
                    }
                    for (int dj = -1; dj <= 1; dj++) {
                        if (di == 0 && dj == 0) {
                            continue;
                        }
                        if (j + dj < 0 || j + dj >= m) {
                            continue;
                        }
                        if (lights[i + di][j + dj] == '#') {
                            on++;
                        }
                    }
                }
                if (lights[i][j] == '#') {
                    next_lights[i][j] = on == 2 || on == 3 ? '#' : '.';
                } else {
                    next_lights[i][j] = on == 3 ? '#' : '.';
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                lights[i][j] = next_lights[i][j];
            }
        }
    }
    long on = 0;
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            if (lights[i][j] == '#') {
                on++;
            }
        }
    }
    return on;
}

long d19_medicine_for_rudolph(char *lines[], int line_count, int part) {
    const int n = line_count - 2;
    char *medicine = lines[line_count - 1];
    char *replacements[n][2];
    for (int i = 0; i < n; i++) {
        char *line = lines[i];
        const char space[] = " ";
        replacements[i][0] = strtok(line, space); // Al
        strtok(NULL, space); // =>
        replacements[i][1] = strtok(NULL, space); // ThF
    }

    long answer = -1;
    CS_Dict *molecules = cs_dict_new(2);
    cs_dict_put(molecules, medicine, NULL);
    const int beam = 1 << 12;
    int step = 1;
    while (answer == -1) {
        const int m = cs_dict_size(molecules);
        char *keys[m];
        cs_dict_keys(molecules, keys);
        // Reduce to beam size according to smallest length.
        int *lens = malloc(m * sizeof(int));
        int *indices = malloc(m * sizeof(int));
        for (int k = 0; k < m; k++) {
            lens[k] = strlen(keys[k]);
            indices[k] = k;
        }
        cs_isort(lens, indices, m, true);
        printf("step: %d; m: %d; keys[indices[0]]: %s\n", step, m, keys[indices[0]]);
        // Construct next population.
        CS_Dict *next_molecules = cs_dict_new(beam << 6);
        for (int k = 0; k < cs_min(beam, m) && answer == -1; k++) {
            char *key = keys[indices[k]];
            const int mlen = strlen(key);
            for (int i = 0; i < n && answer == -1; i++) {
                char *pattern = replacements[i][part == 1 ? 0 : 1];
                const int plen = strlen(pattern);
                char *sub = replacements[i][part == 1 ? 1 : 0];
                const int slen = strlen(sub);
                for (int index = 0; index < mlen - plen + 1 && answer == -1; index++) {
                    if (strncmp(pattern, key + index, plen) == 0) {
                        char *molecule = malloc((mlen - plen + slen + 1) * sizeof(char));
                        strncpy(molecule, key, index);
                        strncpy(molecule + index, sub, slen);
                        strncpy(molecule + index + slen, key + index + plen, mlen - index - plen);
                        molecule[mlen - plen + slen] = '\0';
                        if (part == 2 && strcmp(molecule, "e") == 0) {
                            answer = step;
                        }
                        cs_dict_put(next_molecules, molecule, NULL);
                        free(molecule);
                    }
                }
            }
        }
        if (part == 1) {
            answer = cs_dict_size(next_molecules);
        }
        cs_dict_deinit(molecules);
        molecules = next_molecules;
        free(indices);
        free(lens);
        step++;
    }
    cs_dict_deinit(molecules);
    return answer;
}

long d20_infinite_elves_and_infinite_houses(char *lines[], int line_count, int part) {
    const int presents = (int) strtol(lines[0], NULL, 10);
    const int n = presents;
    unsigned int *houses = malloc(n * sizeof(unsigned int));
    for (int i = 0; i < n; i++) {
        houses[i] = 0;
    }
    const int factor = part == 1 ? 10 : 11;
    int x = 1;
    while (x < n) {
        for (int z = x; z < n && (part == 1 || z <= 50 * x); z += x) {
            houses[z] += factor * x;
        }
        if (houses[x] >= presents) {
            break;
        }
        x++;
    }
    free(houses);
    return x;
}

long d21_rpg_simulator_20xx(char *lines[], int line_count, int part) {
    int boss_hp_base, boss_atk, boss_def;
    const char space[] = " ";
    strtok(lines[0], space); // Hit
    strtok(NULL, space); // Points:
    boss_hp_base = (int) strtol(strtok(NULL, space), NULL, 10);
    strtok(lines[1], space); // Damage:
    boss_atk = (int) strtol(strtok(NULL, space), NULL, 10);
    strtok(lines[2], space); // Armor:
    boss_def = (int) strtol(strtok(NULL, space), NULL, 10);

    const int i_cost = 0, i_atk = 1, i_def = 2;
    const int weapons[][3] = {
            {8, 4, 0}, {10, 5, 0}, {25, 6, 0}, {40, 7, 0}, {74, 8, 0}
    };
    const int weapons_n = 5;
    const int armors[][3] = {
            {0, 0, 0}, // No armor.
            {13, 0, 1}, {31, 0, 2}, {53, 0, 3}, {75, 0, 4}, {102, 0, 5}
    };
    const int armors_n = 6;
    const int rings[][3] = {
            {0, 0, 0}, {0, 0, 0}, // May buy zero to two rings.
            {25, 1, 0}, {50, 2, 0}, {100, 3, 0}, {20, 0, 1}, {40, 0, 2}, {80, 0, 3}
    };
    const int rings_n = 8;

    const int hero_hp_base = 100, hero_atk_base = 0, hero_def_base = 0;
    int polar_cost = -1;
    for (int w = 0; w < weapons_n; w++) {
        for (int a = 0; a < armors_n; a++) {
            for (int r0 = 0; r0 < rings_n - 1; r0++) {
                for (int r1 = r0 + 1; r1 < rings_n; r1++) {
                    // Equip.
                    const int *equipped[] = {weapons[w], armors[a], rings[r0], rings[r1]};
                    int cost = 0;
                    int hero_atk = hero_atk_base;
                    int hero_def = hero_def_base;
                    for (int i = 0; i < 4; i++) {
                        cost += equipped[i][i_cost];
                        hero_atk += equipped[i][i_atk];
                        hero_def += equipped[i][i_def];
                    }
                    // Fight.
                    int boss_hp = boss_hp_base;
                    int hero_hp = hero_hp_base;
                    while (boss_hp > 0 && hero_hp > 0) {
                        boss_hp -= cs_max(1, hero_atk - boss_def);
                        if (boss_hp <= 0) {
                            break;
                        }
                        hero_hp -= cs_max(1, boss_atk - hero_def);
                    }
                    if (part == 1) {
                        if (boss_hp <= 0 && (polar_cost == -1 || cost < polar_cost)) {
                            polar_cost = cost;
                        }
                    } else {
                        if (hero_hp <= 0 && (polar_cost == -1 || cost > polar_cost)) {
                            polar_cost = cost;
                        }
                    }
                }
            }
        }
    }
    return polar_cost;
}

long d22_wizard_simulator_20xx(char *lines[], int line_count, int part) {
    int boss_hp_base, boss_atk;
    const char space[] = " ";
    strtok(lines[0], space); // Hit
    strtok(NULL, space); // Points:
    boss_hp_base = (int) strtol(strtok(NULL, space), NULL, 10);
    strtok(lines[1], space); // Damage:
    boss_atk = (int) strtol(strtok(NULL, space), NULL, 10);
    printf("hp: %d; atk: %d\n", boss_hp_base, boss_atk);

    const int hero_hp_base = 50, hero_def_base = 0, mana_base = 500;
    const int shield_def = 7, poison_damage = 3, recharge_mana_gain = 101;
    const int j_mana = 0, j_damage = 1, j_heal = 2, j_shield_timer = 3, j_poison_timer = 4, j_recharge_timer = 5;
    const int spells[][6] = {
            { 53,  4,  0,  0,  0,  0}, // Magic Missile.
            { 73,  2,  2,  0,  0,  0}, // Drain.
            {113,  0,  0,  6,  0,  0}, // Shield.
            {173,  0,  0,  0,  6,  0}, // Poison.
            {229,  0,  0,  0,  0,  5}  // Recharge.
    };
    const int spells_n = 5;

    int states_size = 4;
    CS_Dict **step_states = malloc(states_size * sizeof(CS_Dict *));
    step_states[0] = cs_dict_new(1);
    const char bar[] = "|";
    char initial_state[32];
    // hero_hp|mana|mana_spent|boss_hp|shield_timer|poison_timer|recharge_timer|s_prev
    sprintf(initial_state, "%d|%d|%d|%d|%d|%d|%d|%d", hero_hp_base, mana_base, 0, boss_hp_base, 0, 0, 0, -1);
    cs_dict_put(step_states[0], initial_state, NULL);
    bool hero_turn = true;
    int step = 1;
    long x_spent = -1;
    int x_s = -1;
    int x_step = -1;
    char *x_state = NULL;
    while (cs_dict_size(step_states[step - 1]) > 0) {
        CS_Dict *states = step_states[step - 1];
        const int n = cs_dict_size(states);
        char *keys[n];
        cs_dict_keys(states, keys);
        CS_Dict *_states = cs_dict_new(n << 2);
        for (int k = 0; k < n; k++) {
            char *state = keys[k];
            char *state_tokens = strdup(state);
            int hero_hp = (int) strtol(strtok(state_tokens, bar), NULL, 10);
            int mana = (int) strtol(strtok(NULL, bar), NULL, 10);
            int mana_spent = (int) strtol(strtok(NULL, bar), NULL, 10);
            int boss_hp = (int) strtol(strtok(NULL, bar), NULL, 10);
            int shield_timer = (int) strtol(strtok(NULL, bar), NULL, 10);
            int poison_timer = (int) strtol(strtok(NULL, bar), NULL, 10);
            int recharge_timer = (int) strtol(strtok(NULL, bar), NULL, 10);
            int s_prev = (int) strtol(strtok(NULL, bar), NULL, 10);
            free(state_tokens);
            // Hard mode.
            if (part == 2 && hero_turn) {
                if (hero_hp == 1) {
                    continue;
                }
                hero_hp--;
            }
            // Effects.
            if (shield_timer > 0) {
                shield_timer--;
            }
            if (poison_timer > 0) {
                boss_hp -= poison_damage;
                if (boss_hp <= 0) {
                    if (x_spent == -1 || mana_spent < x_spent) {
                        x_spent = mana_spent;
                        x_s = -1;
                        x_step = step;
                        x_state = state;
                    }
                    continue;
                }
                poison_timer--;
            }
            if (recharge_timer > 0) {
                mana += recharge_mana_gain;
                recharge_timer--;
            }
            if (hero_turn) {
                // Cast spell.
                for (int s = 0; s < spells_n; s++) {
                    const int *spell = spells[s];
                    // Check mana cost.
                    if (spell[j_mana] > mana) {
                        continue;
                    }
                    // Disallow stacking the same effect.
                    if ((spell[j_shield_timer] > 0 && shield_timer > 0) ||
                            (spell[j_poison_timer] > 0 && poison_timer > 0) ||
                            (spell[j_recharge_timer] > 0 && recharge_timer > 0)) {
                        continue;
                    }
                    int _hero_hp = hero_hp + spell[j_heal];
                    int _mana = mana - spell[j_mana];
                    int _mana_spent = mana_spent + spell[j_mana];
                    int _boss_hp = boss_hp - spell[j_damage];
                    if (_boss_hp <= 0) {
                        if (x_spent == -1 || mana_spent < x_spent) {
                            x_spent = _mana_spent;
                            x_s = s;
                            x_step = step;
                            x_state = state;
                        }
                        continue;
                    }
                    int _shield_timer = shield_timer + spell[j_shield_timer];
                    int _poison_timer = poison_timer + spell[j_poison_timer];
                    int _recharge_timer = recharge_timer + spell[j_recharge_timer];
                    char _state[32];
                    sprintf(_state, "%d|%d|%d|%d|%d|%d|%d|%d",
                            _hero_hp, _mana, _mana_spent, _boss_hp, _shield_timer, _poison_timer, _recharge_timer, s);
                    cs_dict_put(_states, _state, state);
                }
            } else {
                // Boss attack.
                int _hero_hp = hero_hp - cs_max(1, boss_atk - hero_def_base - (shield_timer > 0 ? shield_def : 0));
                if (_hero_hp > 0) {
                    char _state[32];
                    sprintf(_state, "%d|%d|%d|%d|%d|%d|%d|%d",
                            _hero_hp, mana, mana_spent, boss_hp, shield_timer, poison_timer, recharge_timer, s_prev);
                    cs_dict_put(_states, _state, state);
                }
            }
        }
        if (step == states_size) {
            states_size <<= 1;
            step_states = realloc(step_states, states_size * sizeof(CS_Dict *));
        }
        step_states[step] = _states;
        hero_turn = !hero_turn;
        step++;
    }

    // Backtrack.
    printf("spent: %ld; s: %d; step: %d; state: %s\n", x_spent, x_s, x_step, x_state);
    char *x_states[x_step];
    x_states[x_step - 1] = x_state;
    int back_step = x_step - 1;
    while (back_step > 0) {
        x_states[back_step - 1] = cs_dict_get(step_states[back_step], x_states[back_step]);
        back_step--;
    }

    // Replay.
    printf("REPLAY\n");
    for (int i = 0; i < x_step; i++) {
        printf("Turn %02d - %s: %s\n", i + 1, i % 2 == 0 ? "HERO" : "BOSS", x_states[i]);
    }

    for (int i = 0; i < step; i++) {
        cs_dict_deinit(step_states[i]);
    }
    return x_spent;
}

long d23_opening_the_turing_lock(char *lines[], int line_count, int part) {
    int i = 0;
    unsigned int r[2];
    r[0] = part == 1 ? 0 : 1; // a
    r[1] = 0; // b
    while (i < line_count) {
        char *line = lines[i];
        if (strncmp(line, "hlf", 3) == 0) {
            char j = line[4] - 'a';
            r[j] /= 2;
            i++;
        } else if (strncmp(line, "tpl", 3) == 0) {
            char j = line[4] - 'a';
            r[j] *= 3;
            i++;
        } else if (strncmp(line, "inc", 3) == 0) {
            char j = line[4] - 'a';
            r[j] += 1;
            i++;
        } else if (strncmp(line, "jmp", 3) == 0) {
            i += (int) strtol(line + 4, NULL, 10);
        } else if (strncmp(line, "jie", 3) == 0) {
            char j = line[4] - 'a';
            if (r[j] % 2 == 0) {
                i += (int) strtol(line + 7, NULL, 10);
            } else {
                i++;
            }
        } else if (strncmp(line, "jio", 3) == 0) {
            char j = line[4] - 'a';
            if (r[j] == 1) {
                i += (int) strtol(line + 7, NULL, 10);
            } else {
                i++;
            }
        } else {
            printf("Unexpected instruction: %s\n", line);
            break;
        }
    }
    return r[1];
}
