#include "cs.h"

int cs_min(int x, int y) {
    return x < y ? x : y;
}

int cs_max(int x, int y) {
    return x > y ? x : y;
}

int cs_imin(int *a, int n) {
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

int cs_imax(int *a, int n) {
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

int cs_sum(int *a, int n) {
    int sum = 0;
    for (int i = 0; i < n; i++) {
        sum += a[i];
    }
    return sum;
}

void cs_sort(int *a, int n, bool asc) {
    if (n <= 1) {
        return;
    }
    const int pivot = n / 2;
    cs_sort(a, pivot, asc);
    cs_sort(a + pivot, n - pivot, asc);
    int *b = malloc(n * sizeof(int));
    int i = 0, j = pivot, k = 0;
    while (i < pivot || j < n) {
        if (i == pivot) {
            b[k++] = a[j++];
        } else if (j == n || (asc && a[i] < a[j]) || (!asc && a[i] > a[j])) {
            b[k++] = a[i++];
        } else {
            b[k++] = a[j++];
        }
    }
    for (k = 0; k < n; k++) {
        a[k] = b[k];
    }
    free(b);
}

void _cs_isort(int *a, int *x, int n, bool asc) {
    if (n <= 1) {
        return;
    }
    const int pivot = n / 2;
    _cs_isort(a, x, pivot, asc);
    _cs_isort(a, x + pivot, n - pivot, asc);
    int *y = malloc(n * sizeof(int));
    int i = 0, j = pivot, k = 0;
    while (i < pivot || j < n) {
        if (i == pivot) {
            y[k++] = x[j++];
        } else if (j == n || (asc && a[x[i]] < a[x[j]]) || (!asc && a[x[i]] > a[x[j]])) {
            y[k++] = x[i++];
        } else {
            y[k++] = x[j++];
        }
    }
    for (k = 0; k < n; k++) {
        x[k] = y[k];
    }
    free(y);
}

// `indices` must be allocated first.
// This function will populate `indices` with the correct ordering.
void cs_isort(int *a, int *indices, int n, bool asc) {
    for (int i = 0; i < n; i++) {
        indices[i] = i;
    }
    _cs_isort(a, indices, n, asc);
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
BucketNode *_cs_dict_bucket_new(const char *key, void *value) {
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
CS_Dict *cs_dict_new(unsigned int capacity) {
    CS_Dict *dict = (CS_Dict *) malloc(sizeof(CS_Dict));
    if (dict == NULL) {
        fprintf(stderr, "Error: Unable to allocate memory.\n");
        exit(EXIT_FAILURE);
    }
    dict->buckets = (BucketNode **) malloc(capacity * sizeof(BucketNode *));
    for (int i = 0; i < capacity; i++) {
        dict->buckets[i] = NULL;
    }
    dict->capacity = capacity;
    dict->size = 0;
    return dict;
}

// set a key-value pair in the dictionary
void cs_dict_put(CS_Dict *dict, const char *key, void *value) {
    unsigned long index = cs_hash(key) % dict->capacity;
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
    BucketNode *new_node = _cs_dict_bucket_new(key, value);
    new_node->next = dict->buckets[index];
    dict->buckets[index] = new_node;
    dict->size++;
}

// get a value for a key in the dictionary
void *cs_dict_get(CS_Dict *dict, const char *key) {
    unsigned long index = cs_hash(key) % dict->capacity;
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
bool cs_dict_contains(CS_Dict *dict, const char *key) {
    unsigned long index = cs_hash(key) % dict->capacity;
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
bool cs_dict_remove(CS_Dict *dict, const char *key) {
    unsigned long index = cs_hash(key) % dict->capacity;
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

unsigned int cs_dict_size(CS_Dict *dict) {
    return dict->size;
}

void cs_dict_keys(CS_Dict *dict, char *keys[]) {
    int k = 0;
    for (int i = 0; i < dict->capacity; i++) {
        BucketNode *node = dict->buckets[i];
        while (node != NULL) {
            keys[k++] = node->key;
            node = node->next;
        }
    }
}

void _cs_dict_deinit_bucket(BucketNode *node) {
    if (node != NULL) {
        _cs_dict_deinit_bucket(node->next);
        free(node->key);
        free(node);
    }
}

void cs_dict_deinit(CS_Dict *dict) {
    for (int i = 0; i < dict->capacity; i++) {
        _cs_dict_deinit_bucket(dict->buckets[i]);
    }
    free(dict->buckets);
    free(dict);
}

// https://en.wikipedia.org/wiki/MD5#Algorithm
static unsigned int CS_MD5_K[] = {
    0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
    0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
    0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be,
    0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
    0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
    0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
    0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed,
    0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
    0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c,
    0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
    0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05,
    0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
    0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039,
    0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
    0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
    0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
};

static int CS_MD5_SHIFT[] = {
    7, 12, 17, 22,  7, 12, 17, 22,  7, 12, 17, 22,  7, 12, 17, 22,
    5,  9, 14, 20,  5,  9, 14, 20,  5,  9, 14, 20,  5,  9, 14, 20,
    4, 11, 16, 23,  4, 11, 16, 23,  4, 11, 16, 23,  4, 11, 16, 23,
    6, 10, 15, 21,  6, 10, 15, 21,  6, 10, 15, 21,  6, 10, 15, 21
};

void cs_md5(const char *message, unsigned char *digest) {
    unsigned long len = (unsigned long) strlen(message);
    unsigned int len_pad = ((int) ceil((len + 9) / 64.0)) * 64;
    unsigned char *message_pad = malloc((len_pad + 1) * sizeof(unsigned char));
    // Insert original message at beginning.
    for (int i = 0; i < len; i++) {
        message_pad[i] = (unsigned char) message[i];
    }
    message_pad[len] = 0x80; // "1" bit
    // Pad with zeros, if needed.
    if ((len + 9) % 64 != 0) {
        for (int i = 0; i < len_pad - len - 1; i++) {
            message_pad[len + 1 + i] = 0x00; // "0" bits.
        }
    }
    // Add 64-bit length of original message in little endian bits.
    const unsigned long len_bits = len * 8;
    for (int i = 0; i < 8; i++) {
        message_pad[len_pad - 8 + i] = (len_bits >> (8 * i)) & 0xff;
    }

    unsigned int m[16];
    unsigned int a0 = 0x67452301; // Little endian.
    unsigned int b0 = 0xefcdab89;
    unsigned int c0 = 0x98badcfe;
    unsigned int d0 = 0x10325476;

    unsigned char *mp;
    for (mp = message_pad; mp < message_pad + len_pad; mp += 64) {
        for (int j = 0; j < 16; j++) {
            m[j] = (*(mp + (4 * j) + 3) << 24) +
                    (*(mp + (4 * j) + 2) << 16) +
                    (*(mp + (4 * j) + 1) << 8) +
                    *(mp + (4 * j) + 0); // Little endian.
        }
        unsigned int a = a0;
        unsigned int b = b0;
        unsigned int c = c0;
        unsigned int d = d0;
        for (int i = 0; i < 64; i++) {
            unsigned int f, g;
            if (i < 16) {
                f = (b & c) | (~b & d);
                g = i;
            } else if (i < 32) {
                f = (d & b) | (~d & c);
                g = (5 * i + 1) % 16;
            } else if (i < 48) {
                f = (b ^ c) ^ d;
                g = (3 * i + 5) % 16;
            } else {
                f = c ^ (b | ~d);
                g = (7 * i) % 16;
            }
            f += a + CS_MD5_K[i] + m[g];
            a = d;
            d = c;
            c = b;
            b += (f << CS_MD5_SHIFT[i]) | (f >> (32 - CS_MD5_SHIFT[i])); // Left-rotate.
        }
        a0 += a;
        b0 += b;
        c0 += c;
        d0 += d;
    }

    free(message_pad);

    for (int i = 0; i < 4; i++) {
        digest[i] = (a0 >> (8 * i)) & 0xff; // Little endian.
        digest[4 + i] = (b0 >> (8 * i)) & 0xff;
        digest[8 + i] = (c0 >> (8 * i)) & 0xff;
        digest[12 + i] = (d0 >> (8 * i)) & 0xff;
    }
}

char ***cs_permutations(char **a, int a_len, int *len) {
    char ***permutations = NULL;
    int n = 0;
    if (a_len == 1) {
        // Return atomic sequence.
        permutations = malloc(sizeof(char **));
        permutations[0] = a;
        n = 1;
    } else {
        // TODO: Add set (dict) here to exclude repetitions (should implement table doubling/halving in CS_Dict).
        for (int i = 0; i < a_len; i++) {
            // Recursively get permutations of tail (elements excluding a[i]).
            int tail_len = a_len - 1;
            char **tail = malloc(tail_len * sizeof(char *));
            memcpy(tail, a, i * sizeof(char *));
            memcpy(tail + i, a + i + 1, (a_len - i - 1) * sizeof(char *));
            int tail_permutations_len;
            char ***tail_permutations = cs_permutations(tail, tail_len, &tail_permutations_len);
            // Concatenate a[i] with all tail permutations and append to answer.
            permutations = realloc(permutations, (n + tail_permutations_len) * sizeof(char **));
            for (int j = 0; j < tail_permutations_len; j++) {
                char **tail_permutation = tail_permutations[j];
                char **permutation = malloc(a_len * sizeof(char *));
                permutation[0] = a[i];
                memcpy(permutation + 1, tail_permutation, tail_len * sizeof(char *));
                permutations[n] = permutation;
                n++;
            }
            free(tail_permutations);
            free(tail);
        }
    }

    *len = n;
    return permutations;
}

unsigned short **cs_bucket_permutations(unsigned short volume, unsigned short buckets, int *len) {
    unsigned short **bucket_permutations = NULL;
    int n = 0;
    if (buckets == 1) {
        bucket_permutations = malloc(sizeof(unsigned short *));
        unsigned short *permutation = malloc(sizeof(unsigned short));
        *permutation = volume;
        *bucket_permutations = permutation;
        n = 1;
    } else {
        for (unsigned short x = 0; x <= volume; x++) {
            int tail_permutations_len;
            unsigned short **tail_permutations = cs_bucket_permutations(volume - x, buckets - 1, &tail_permutations_len);
            bucket_permutations = realloc(bucket_permutations, (n + tail_permutations_len) * sizeof(unsigned short *));
            for (int i = 0; i < tail_permutations_len; i++) {
                unsigned short *tail_permutation = tail_permutations[i];
                unsigned short *permutation = malloc(buckets * sizeof(unsigned short));
                permutation[0] = x;
                for (int j = 0; j < buckets - 1; j++) {
                    permutation[j + 1] = tail_permutation[j];
                }
                bucket_permutations[n] = permutation;
                n++;
                free(tail_permutation);
            }
            free(tail_permutations);
        }
    }

    *len = n;
    return bucket_permutations;
}

unsigned short **_cs_combinations(unsigned short n, unsigned short r, int *len, int k) {
    unsigned short **combinations = NULL;
    int size = 0;
    if (r == 1) {
        size = n - k;
        combinations = malloc(size * sizeof(unsigned short *));
        for (int i = k; i < n; i++) {
            unsigned short *combination = malloc(sizeof(unsigned short));
            *combination = i;
            combinations[i - k] = combination;
        }
    } else {
        for (int i = k; i < n - r + 1; i++) {
            int tail_len;
            unsigned short **tail_combinations = _cs_combinations(n, r - 1, &tail_len, i + 1);
            combinations = realloc(combinations, (size + tail_len) * sizeof(unsigned short *));
            for (int j = 0; j < tail_len; j++) {
                unsigned short *tail_combination = tail_combinations[j];
                unsigned short *combination = malloc(r * sizeof(unsigned short));
                combination[0] = i;
                for (int t = 0; t < r - 1; t++) {
                    combination[t + 1] = tail_combination[t];
                }
                combinations[size] = combination;
                size++;
                free(tail_combination);
            }
            free(tail_combinations);
        }
    }
    *len = size;
    return combinations;
}

unsigned short **cs_combinations(unsigned short n, unsigned short r, int *len) {
    if (r < 1 || r > n) {
        fprintf(stderr, "Unexpected input: n=%hu; r=%hu\n", n, r);
        exit(EXIT_FAILURE);
    }
    return _cs_combinations(n, r, len, 0);
}
