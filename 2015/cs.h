#ifndef CS_H
#define CS_H

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
} CSDict;

CSDict *cs_dict_new();
void cs_dict_put(CSDict *dict, const char *key, void *value);
void *cs_dict_get(CSDict *dict, const char *key);
bool cs_dict_contains(CSDict *dict, const char *key);
bool cs_dict_remove(CSDict *dict, const char *key);
unsigned int cs_dict_size(CSDict *dict);

#endif
