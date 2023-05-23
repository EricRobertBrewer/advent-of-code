#ifndef CS_H
#define CS_H

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

#define MAX_BUCKETS 1024

int cs_min(int *a, int n);
int cs_max(int *a, int n);

// $ Write a dictionary (hash map) implementation in the C programming language.

typedef struct _BucketNode {
    char *key;
    void *value;
    struct _BucketNode *next;
} BucketNode;

typedef struct {
    BucketNode *buckets[MAX_BUCKETS];
    unsigned int size;
} CS_Dict;

CS_Dict *cs_dict_new();
void cs_dict_put(CS_Dict *dict, const char *key, void *value);
void *cs_dict_get(CS_Dict *dict, const char *key);
bool cs_dict_contains(CS_Dict *dict, const char *key);
bool cs_dict_remove(CS_Dict *dict, const char *key);
unsigned int cs_dict_size(CS_Dict *dict);

void cs_md5(const char *key, unsigned char *digest);

char ***cs_permutations(char **a, int a_len, int *len);

#endif
