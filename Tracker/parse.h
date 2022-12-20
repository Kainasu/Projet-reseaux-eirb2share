#ifndef __PARSE_H__
#define __PARSE_H__

#include "server.h"
#include "peerList.h"

typedef enum REQUEST_TYPE {
    ANNOUNCE,
    LOOK,
    UPDATE,
    GETFILE,
    INTERSTED,
    GETPIECES,
    HAVE,
    NO_TYPE = -1
} REQUEST_TYPE;

char *itoa(int i, char b[]);

REQUEST_TYPE getRequestType(char *request);

int checkInteger(char *str);
int getAnnoucePresence(char *annoucement, Peer peer);

int getLook(char *look, struct peerList peers, char **data);

int getFile(char *buff, struct peerList peers, char **data);

#endif