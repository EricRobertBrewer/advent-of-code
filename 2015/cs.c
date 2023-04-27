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