#include "cs.h"

int cs_min(int *a, int n) {
    int x = a[0];
    int j = 0;
    for (int i = 1; i < n; i++) {
        if (a[i] < x) {
            x = a[i];
            j = i;
        }
    }
    return j;
}

int cs_max(int *a, int n) {
    int x = a[0];
    int j = 0;
    for (int i = 1; i < n; i++) {
        if (a[i] > x) {
            x = a[i];
            j = i;
        }
    }
    return j;
}

// hash function using the djb2 algorithm
unsigned long cs_hash(const char *str) {
    unsigned long hash = 5381;
    int c;

    while ((c = *str++) != '\0') {
        hash = ((hash << 5) + hash) + c; // hash * 33 + c
    }

    return hash;
}

// create a new bucket node with the specified key and value
BucketNode *cs_dict_bucket_new(const char *key, void *value) {
    BucketNode *node = (BucketNode *)malloc(sizeof(BucketNode));
    if (node == NULL) {
        fprintf(stderr, "Error: Unable to allocate memory.\n");
        exit(EXIT_FAILURE);
    }
    node->key = strdup(key);
    node->value = value;
    node->next = NULL;
    return node;
}

// create a new dictionary
CSDict *cs_dict_new() {
    CSDict *dict = (CSDict *)malloc(sizeof(CSDict));
    if (dict == NULL) {
        fprintf(stderr, "Error: Unable to allocate memory.\n");
        exit(EXIT_FAILURE);
    }
    memset(dict->buckets, 0, sizeof(dict->buckets));
    dict->size = 0;
    return dict;
}

// set a key-value pair in the dictionary
void cs_dict_put(CSDict *dict, const char *key, void *value) {
    unsigned long index = cs_hash(key) % MAX_BUCKETS;
    BucketNode *node = dict->buckets[index];

    // check if the key is already in the dictionary
    while (node != NULL) {
        if (strcmp(node->key, key) == 0) {
            node->value = value;
            return;
        }
        node = node->next;
    }

    // add the key-value pair to the dictionary
    BucketNode *new_node = cs_dict_bucket_new(key, value);
    new_node->next = dict->buckets[index];
    dict->buckets[index] = new_node;
    dict->size++;
}

// get a value for a key in the dictionary
void *cs_dict_get(CSDict *dict, const char *key) {
    unsigned long index = cs_hash(key) % MAX_BUCKETS;
    BucketNode *node = dict->buckets[index];

    while (node != NULL) {
        if (strcmp(node->key, key) == 0) {
            return node->value;
        }
        node = node->next;
    }

    return NULL;
}

// check if a key is in the dictionary
bool cs_dict_contains(CSDict *dict, const char *key) {
    unsigned long index = cs_hash(key) % MAX_BUCKETS;
    BucketNode *node = dict->buckets[index];

    while (node != NULL) {
        if (strcmp(node->key, key) == 0) {
            return true;
        }
        node = node->next;
    }

    return false;
}

// remove a key-value pair from the dictionary
bool cs_dict_remove(CSDict *dict, const char *key) {
    unsigned long index = cs_hash(key) % MAX_BUCKETS;
    BucketNode *prev = NULL;
    BucketNode *node = dict->buckets[index];

    while (node != NULL) {
        if (strcmp(node->key, key) == 0) {
            if (prev == NULL) {
                dict->buckets[index] = node->next;
            } else {
                prev->next = node->next;
            }
            free(node->key);
            free(node);
            dict->size--;
            return true;
        }
        prev = node;
        node = node->next;
    }

    return false;
}

unsigned int cs_dict_size(CSDict *dict) {
    return dict->size;
}
