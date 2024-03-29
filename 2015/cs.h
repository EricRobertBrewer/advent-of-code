#ifndef CS_H
#define CS_H

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

int cs_min(int x, int y);
int cs_max(int x, int y);
int cs_imin(int *a, int n);
int cs_imax(int *a, int n);
int cs_sum(int *a, int n);

void cs_sort(int *a, int n, bool asc);
void cs_isort(int *a, int *indices, int n, bool asc);

// $ Write a dictionary (hash map) implementation in the C programming language.

typedef struct _BucketNode {
    char *key;
    void *value;
    struct _BucketNode *next;
} BucketNode;

typedef struct {
    BucketNode **buckets;
    unsigned int capacity; // Number of buckets.
    unsigned int size; // Total number of values stored.
} CS_Dict;

CS_Dict *cs_dict_new(unsigned int capacity);
void cs_dict_put(CS_Dict *dict, const char *key, void *value);
void *cs_dict_get(CS_Dict *dict, const char *key);
bool cs_dict_contains(CS_Dict *dict, const char *key);
bool cs_dict_remove(CS_Dict *dict, const char *key);
unsigned int cs_dict_size(CS_Dict *dict);
void cs_dict_keys(CS_Dict *dict, char *keys[]);
void cs_dict_deinit(CS_Dict *dict);

void cs_md5(const char *key, unsigned char *digest);

char ***cs_permutations(char **a, int a_len, int *len);
unsigned short **cs_bucket_permutations(unsigned short volume, unsigned short buckets, int *len);

unsigned short **cs_combinations(unsigned short n, unsigned short r, int *len);

#endif
