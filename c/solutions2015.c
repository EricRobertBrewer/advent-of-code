#include "solutions2015.h"
#include "aoc.h"

#define YEAR "2015"

int main(int argc, char *argv[]) {
    if (argc < 3) {
        fprintf(stderr, "Usage: %s <day> <part> [-x|--example]\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    int day = (int) strtol(argv[1], NULL, 10);
    int part = (int) strtol(argv[2], NULL, 10);
    int example = 0;
    if (argc > 3) {
        if (strcmp(argv[3], "-x") == 0 || strcmp(argv[3], "--example") == 0) {
            example = 1;
        }
    }
    printf("day: %d; part: %d; example: %d\n", day, part, example);

    char *input_path;
    if (example) {
        printf("Calling `aoc_example_path(%s, %d)`...\n", YEAR, day);
        input_path = aoc_example_path(YEAR, day);
    } else {
        printf("Calling `aoc_download_input_if_needed(%s, %d)`...\n", YEAR, day);
        input_path = aoc_download_input_if_needed(YEAR, day);
    }

    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC_RAW, &start);
    long answer = solve(day, part, input_path);
    clock_gettime(CLOCK_MONOTONIC_RAW, &end);
    uint64_t duration_ms = (end.tv_sec - start.tv_sec) * 1000 + (end.tv_nsec - start.tv_nsec) / 1000000;
    fprintf(stdout, "Time: %llu ms\n", duration_ms);

    fprintf(stdout, "%ld\n", answer);

    free(input_path);
    return 0;
}

long solve(int day, int part, char *input_path) {
    char **lines;
    FILE *file = fopen(input_path, "r");
    fclose(file);

    if (day == 1 && part == 1) {
        return d_01_1_not_quite_lisp(lines);
    }

    fprintf(stderr, "No solution for day `%d` part `%d`.", day, part);
    exit(EXIT_FAILURE);
}

long d_01_1_not_quite_lisp(char **lines) {
    return -1L;
}
