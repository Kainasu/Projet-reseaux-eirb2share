#include "../parse.h"
#include "assert.h"
#include "../peerList.h"
#define BUFFERSIZE 256

int main(){
    printf("Parse.c tests : \n");
    printf("Testing itoa : ");
    char buffer[BUFFERSIZE];
    assert(strcmp("10", itoa(10, buffer)) == 0);
    assert(strcmp("99", itoa(99, buffer)) == 0);
    assert(strcmp("-101", itoa(-101, buffer)) == 0);
    assert(strcmp("-301", itoa(-301, buffer)) == 0);
    printf("PASS\n");

    printf("Testing getRequestType : ");
    assert(getRequestType("getfile something") == GETFILE);
    assert(getRequestType("look something") == LOOK);
    assert(getRequestType("update something") == UPDATE);
    assert(getRequestType("interested in something") == INTERSTED);
    assert(getRequestType("getpieces in something") == GETPIECES);
    assert(getRequestType("interested in something") == INTERSTED);    
    printf("PASS\n");

    printf("Testing checkInteger : ");
    assert(checkInteger("10") == 1);
    assert(checkInteger("400") == 1);
    assert(checkInteger("Abc") == 0);
    printf("PASS\n");

    printf("Testing getAnnoucePresence :");
    
    FileArray a ;
    initFileArray(&a,3);
    struct peerList * peer_list = malloc(sizeof(struct peerList));
    Peer p1 = peer_init(peer_list, 0, 0);
    Peer p2 = peer_init(peer_list,0 ,0);
    
    //invalid request
    char * annoucement2 = malloc(150 * sizeof(char)); 
    annoucement2 = "announce listen 39733 "; 
    
    assert(getAnnoucePresence(annoucement2,p2) == 0);
    printf(" PASS\n");
    // correct request
    char * annoucement1 = malloc(150 * sizeof(char));
    annoucement1 = "announce listen 39733 seed [file2.txt 21 1024 ac0ee8c094236494f66db433f6c6d2ca] leech []";
    
    assert(getAnnoucePresence(annoucement1,p1) == 1);
    assert(p1->port == 39733);    
    
    //Check File Values in the Peer;
    assert(p1->peerFiles->used == 1);
    assert(strcmp(p1->peerFiles->array[0]->key ,"ac0ee8c094236494f66db433f6c6d2ca") == 0);
    assert(p1->peerFiles->array[0]->length == 21);
    assert(p1->peerFiles->array[0]->piece_size == 1024);
    assert(strcmp(p1->peerFiles->array[0]->file_name ,"file2.txt") == 0);


    printf("Testing getLook :");



    free_peer(p1);
    free_peer(p2);
    free(peer_list);
    freeFileArray(&a);
    
    printf(" PASS\n");
    printf("All tests passed\n");


}