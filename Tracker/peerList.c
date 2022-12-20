#include "peerList.h"

Peer peer_init(struct peerList * peer_list, char* ip, unsigned int port){
    
    Peer peer = malloc (sizeof(struct Peer));
    peer->ip = ip;
    peer->port = port;
    peer->peerFiles = malloc(sizeof(FileArray));
    initFileArray(peer->peerFiles,5);
    SLIST_INSERT_HEAD(peer_list, peer, next_peer);
    return peer;
}

void free_peer(Peer p){
    freeFileArray(p->peerFiles);
    free(p->peerFiles);
    free(p);
}