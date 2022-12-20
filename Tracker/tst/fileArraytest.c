#include "../fileArray.h"
#include <assert.h>

int main(){
    FileArray a ;
    initFileArray(&a,3);  
    assert(a.used == 0 && a.size == 3);
    insertFileArray(&a, "fichier1", 2097152, 1024, "8905e92afeb80fc7722ec89eb0bf0966");
    assert(a.used == 1 && a.size == 3);
    insertFileArray(&a, "fichier2", 2097153, 1024, "330a57722ec8b0bf09669a2b35f88e9e");
    assert(a.used == 2 && a.size == 3);
    insertFileArray(&a, "fichier3", 2097154, 1024, "a2b35f88e9e8905e92afeb80fc77");
    assert(a.used == 3 && a.size == 3);
    insertFileArray(&a, "fichier4", 2097155, 1024, "a5a515aaaa222228852r22z22");
    assert(a.used == 4 && a.size == 6);

    files_display(&a);
    freeFileArray(&a);
    printf("SUCCESS\n");
    
}