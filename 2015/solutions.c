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
