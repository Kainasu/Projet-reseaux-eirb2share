#ifndef FILE_H
#define FILE_H

typedef struct {
    char* file_name;
    int length;
    int piece_size;
    char* key;
} File;

#endif