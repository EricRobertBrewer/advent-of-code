#include "solutions.h"
#include "aoc.h"
#include "cs.h"

#define YEAR "2015"

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
    if (day == 1) {
        answer = d01_not_quite_lisp(lines, line_count, part);
    } else if (day == 2) {
        answer = d02_i_was_told_there_would_be_no_math(lines, line_count, part);
    } else if (day == 3) {
        answer = d03_perfectly_spherical_houses_in_a_vacuum(lines, line_count, part);
    } else if (day == 4) {
        answer = d04_the_ideal_stocking_stuffer(lines, line_count, part);
    } else if (day == 5) {
        answer = d05_doesnt_he_have_intern_elves_for_this(lines, line_count, part);
    } else if (day == 6) {
        answer = d06_probably_a_fire_hazard(lines, line_count, part);
    } else if (day == 7) {
        answer = d07_some_assembly_required(lines, line_count, part);
    } else if (day == 8) {
        answer = d08_matchsticks(lines, line_count, part);
    } else if (day == 9) {
        answer = d09_all_in_a_single_night(lines, line_count, part);
    } else if (day == 10) {
        answer = d10_elves_look_elves_say(lines, line_count, part);
    } else if (day == 11) {
        answer = d11_corporate_policy(lines, line_count, part);
    } else {
        fprintf(stderr, "No solution for day `%d` part `%d`.", day, part);
        exit(EXIT_FAILURE);
    }

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

        int s_max = cs_max(sides, N_SIDES);
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
    CS_Dict *dict = cs_dict_new();
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
    free(dict);
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
            CS_Dict *pair_to_index = cs_dict_new();
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
            free(pair_to_index);
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
    CS_Dict *circuit = cs_dict_new();
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
    CS_Dict *memo = cs_dict_new();
    unsigned short answer = _d07_circuit_value(circuit, "a", memo);
    free(memo);
    free(circuit);
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
    CS_Dict *locations = cs_dict_new();
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
    free(locations);

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