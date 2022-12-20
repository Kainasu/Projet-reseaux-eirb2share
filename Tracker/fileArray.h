#ifndef FILE_ARRAY_H
#define FILE_ARRAY_H
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "file.h"

typedef struct {
    File **array;
    size_t used;
    size_t size;
} FileArray;

void initFileArray(FileArray *a, size_t initialSize);

void insertFileArray(FileArray *a, char *name, int length, int piece_size, char *key);

void files_display(FileArray *a);

void freeFileArray(FileArray *a);

#endif