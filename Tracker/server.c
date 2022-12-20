#include "server.h"

#include <arpa/inet.h>
#include <fcntl.h>  // for open
#include <netdb.h>
#include <netinet/in.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>
#include "peerList.h"
#include "parse.h"

struct args{
    Peer peer;
    int fd;
};

void* func(void* clientfd);

struct peerList peers;

int main() {
    FILE* log = fopen("log/server.log", "a+");
    int sockfd, clientfd, *new_sock;
    Peer peer;

    struct sockaddr_in server, client;

    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd == -1) {
        printf("socket  creation failed \n");
        exit(0);
    } else
        printf("Socket creation succed... \n");

    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;  // une adresse ip de la machine courante
    server.sin_port = htons(PORT);

    if (bind(sockfd, (SA*)&server, sizeof(server)) != 0) {
        printf("Socket bind failed \n");
        exit(0);
    } else {
        printf("Socket bind succeed \n");
    }

    // now server start listening

    if (listen(sockfd, 5) != 0) {  // 5 valeur max de la file d'attente
        printf("Listening Failed\n");
        exit(0);
    } else {
        printf("Server Listening \n");
    }

    // Accept the data from the client
    socklen_t len;
    len = sizeof(client);    
    while (1) {        
        clientfd = accept(sockfd, (SA*)&client, &len);  // accepte une connexion sur une socket
        if (clientfd < 0) {
            printf(" Server accept failed ...\n");
            exit(0);
        } else {
            peer = peer_init(&peers, inet_ntoa(client.sin_addr) , ntohs(client.sin_port));            
            printf("Server accept the client fd = %d \n ", clientfd);
            fprintf(log, "INFO: Server accept the client fd = %d \n ", clientfd);            
            printf("Connection accepted from %s:%d\n", inet_ntoa(client.sin_addr), ntohs(client.sin_port));
            fprintf(log, "INFO: Connection accepted from %s:%d\n", inet_ntoa(client.sin_addr), ntohs(client.sin_port));            
        }

        pthread_t sniffer_thread;
        new_sock = malloc(sizeof(int));
        *new_sock = clientfd;
        struct args arguments;
        arguments.peer = peer;
        arguments.fd = clientfd;

        if (pthread_create(&sniffer_thread, NULL, func, (void*)&arguments) < 0) {
            perror("could not create thread");
            fprintf(log, "WARNING: could not create thread\n");
            return 1;
        }
        
        fprintf(log, "INFO: Handler assigned\n");
        printf("Handler assigned\n");        
    }
    
    close(sockfd);    
    if (!SLIST_EMPTY(&peers)) {
        Peer p1, p2;
        p1 = SLIST_FIRST(&peers);        
        while (p1 != NULL) {            
            p2 = SLIST_NEXT(p1, next_peer);
            free_peer(p1);
            p1 = p2;
        }
    }
    fclose(log);
}

void* func(void* arg) {
    FILE* log = fopen("log/server.log", "a+");

    struct args* arguments = arg;
    int clientfd = arguments->fd;
    Peer peer = arguments->peer;
    char buff[MAX];
    char response[MAX];
    bzero(response, MAX);
    char* data = response;    
    // int n;
    for (;;) {        
        //strcpy(data, "> ok\n");
        // Emptying the buffer        
        bzero(buff, MAX);
        // read the message from the client and copy it in the buffer        
        read(clientfd, buff, MAX);
        printf("%s\n", buff);
        
        int entered = 0;

        REQUEST_TYPE req_type = getRequestType(buff);        
        switch (req_type) {
            case ANNOUNCE:                
                if (getAnnoucePresence(buff, peer) != 0) { 
                    //printf("> ok\n");
                    strcpy(data, "> ok\n");
                    entered = 1;
                }
                break;
            case LOOK:
                getLook(buff, peers, &data);                
                break;
            case GETFILE:
                getFile(buff, peers ,&data);
                //printf(" case getFile : %s",data);
                break;
            case NO_TYPE:
                strcpy(data, "> Cette commande n'est pas prise en compte\n");
            default:
                break;
        };

        // ===>print the file description to check if the parse is well done
        if (entered){            
            files_display(peer->peerFiles);
        }
        /*    
        else
            files_display(peer->peerFiles);
            */
        //===>

        // print buffer which contains the client contents
        // printf("From client: %s", buff);

        bzero(buff, MAX);
        
        //
        // and send that buffer to client
        // printf(" server will send to client %s sizeof = %ld \n",data,sizeof(data));

        printf("%s", data);
        write(clientfd, data, MAX);
        printf("< ");
        bzero(response, MAX);

        // printf("%s", data);

        // if msg contains "Exit" then server exit and chat ended.
        if (strncmp("EXIT", buff, (size_t)4) == 0) {
            printf("Server Exit...\n");
            fprintf(log, "INFO: Server Exit...\n");            
            break;
        }

    }
    fclose(log);
    return NULL;
}
