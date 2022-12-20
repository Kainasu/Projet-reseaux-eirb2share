#include "fileArray.h"


void initFileArray(FileArray *a, size_t initialSize) {
    a->array = malloc(initialSize * sizeof(File));
    a->used = 0;
    a->size = initialSize;
}

void insertFileArray(FileArray *a, char *name, int length, int piece_size, char *key) {
    // a->used is the number of used entries, because a->array[a->used++] updates a->used only *after* the FileArray has been accessed.
    // Therefore a->used can go up to a->size
    if (a->used == a->size) {
        a->size *= 2;
        a->array = realloc(a->array, a->size * sizeof(File));
    }

    File *element = malloc(sizeof(File));
    element->file_name = malloc(sizeof(char) * (strlen(name) + 1));
    strcpy(element->file_name, name);
    element->length = length;
    element->piece_size = piece_size;
    element->key = malloc(sizeof(char) * (strlen(key) + 1));
    strcpy(element->key, key);

    a->array[a->used++] = element;
}

void file_display_infos(File *f) {
    FILE *log = fopen("log/server.log", "a+");
    fprintf(log, "%s %d %d %s \n", f->file_name, f->length, f->piece_size, f->key);
    fclose(log);
}

void files_display(FileArray *a) {
    FILE *log = fopen("log/server.log", "a+");
    for (int i = 0; i < a->used; i++)
        file_display_infos(a->array[i]);    
    fprintf(log, "Taille utilisée = %ld \n", a->used);
    fprintf(log, "Capacité = %ld \n", a->size);
    fclose(log);
}

void freeFileArray(FileArray *a) {
    for (int i = 0; i < a->used; i++) {
        free(a->array[i]->file_name);
        free(a->array[i]->key);
        free(a->array[i]);
    }
    free(a->array);
    a->array = NULL;
    a->used = a->size = 0;
}
