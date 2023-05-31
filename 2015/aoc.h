#ifndef ADVENT_OF_CODE_H
#define ADVENT_OF_CODE_H

#include <curl/curl.h> // Download input.
#include <errno.h> // Error checking.
#include <stdio.h>
#include <stdlib.h>  // Retrieve environment variable (`getenv`).
#include <string.h> // Readable error message.
#include <sys/stat.h> // Create directory (`mkdir`).
#include <unistd.h> // Check if file exists (`access`).

void aoc_download_input_if_needed(char *day_path, const char *year, int day);
void aoc_example_path(char *example_path, const char *year, int day);

#endif
