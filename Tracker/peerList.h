#ifndef __PEER_LIST_H__
#define __PEER_LIST_H__

#include <sys/queue.h>
#include "fileArray.h"



struct Peer{
    int port;
    char* ip;
    FileArray *peerFiles;
    SLIST_ENTRY(Peer) next_peer;
};

typedef struct Peer* Peer;


SLIST_HEAD(peerList, Peer); 

Peer peer_init(struct peerList * peer_list, char* ip, unsigned int port);

void free_peer(Peer p);

#endif