#ifndef SOLUTIONS_H
#define SOLUTIONS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>

long solve(int day, int part, char *input_path);

long d01_not_quite_lisp(int line_count, char *lines[], int part);
long d02_i_was_told_there_would_be_no_math(int line_count, char *lines[], int part);
long d03_perfectly_spherical_houses_in_a_vacuum(int line_count, char *lines[], int part);

#endif
