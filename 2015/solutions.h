#ifndef SOLUTIONS_H
#define SOLUTIONS_H

#include <math.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>

long solve(int day, int part, char *input_path);

long d01_not_quite_lisp(char *lines[], int line_count, int part);
long d02_i_was_told_there_would_be_no_math(char *lines[], int line_count, int part);
long d03_perfectly_spherical_houses_in_a_vacuum(char *lines[], int line_count, int part);
long d04_the_ideal_stocking_stuffer(char *lines[], int line_count, int part);
long d05_doesnt_he_have_intern_elves_for_this(char *lines[], int line_count, int part);

#endif
