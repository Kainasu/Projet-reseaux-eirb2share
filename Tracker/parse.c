#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "parse.h"

char* itoa(int i, char b[]){
    char const digit[] = "0123456789";
    char* p = b;
    if(i<0){
        *p++ = '-';
        i *= -1;
    }
    int shifter = i;
    do{ //Move to where representation ends
        ++p;
        shifter = shifter/10;
    }while(shifter);
    *p = '\0';
    do{ //Move back, inserting digits as u go
        *--p = digit[i%10];
        i = i/10;
    }while(i);
    return b;
}


REQUEST_TYPE getRequestType(char *request){    
    char *req = strdup(request);    
    char *reqType;
    //printf("req:%s\n", req);
    if ((reqType = strsep(&req, " ")) != NULL) {
        //printf("%s\n", reqType);
        if (strncmp(reqType, "announce",strlen("announce")) == 0 || strncmp(reqType, "Announce" , strlen("Announce")) == 0){            
            return ANNOUNCE;
        }else if (strncmp(reqType, "look",strlen("look")) == 0 || strncmp(reqType, "Look",strlen("Look")) == 0){            
            return LOOK;
        }else if (strncmp(reqType, "update",strlen("update")) == 0 || strncmp(reqType, "Update",strlen("Update")) == 0){
            return UPDATE;
        }else if (strncmp(reqType, "getfile",strlen("getfile")) == 0 || strncmp(reqType, "Getfile",strlen("Getfile")) == 0){
            return GETFILE;
        }else if (strncmp(reqType, "interested",strlen("interested")) == 0 || strncmp(reqType, "Interested",strlen("Interested")) == 0){
            return INTERSTED;
        }else if (strncmp(reqType, "getpieces",strlen("getpieces")) == 0 || strncmp(reqType, "Getpieces",strlen("Getpieces")) == 0){
            return GETPIECES;
        }else if (strncmp(reqType, "have",strlen("have")) == 0 || strncmp(reqType, "Have",strlen("Have")) == 0){
            return HAVE;
        }else{
            return NO_TYPE;
        }
    }
    free(req);
    return NO_TYPE;
}


int checkInteger(char* str){
    for(int i = 0; i < strlen(str); i++){
        if (!isdigit(str[i]))
            return 0;
    }
    return 1;
}

int getAnnoucePresence(char *annoucement, Peer peer){
    char *annouce = strdup(annoucement);
    char *found;
    int counter = 0;
    int fc = 0;

    //char **filesInfo = ["files.dat" ,"902392309", "023823902832039", "1024", "fichier2.dat", "230820293", "1024", "09022323723H"];
    char * file_description[4];
    while((found = strsep(&annouce, " "))   != NULL ){ // on doit pas free found , car strsep n'alloue pas la memoire au contraire de strdup

        if (counter == 1 && strncmp(found, "listen", strlen("listen")) != 0){
            printf("INFO: je quite le getAnnounce counter ==1 \n");
            return 0;
        }
     
        if (counter == 2){
            if (!checkInteger(found))
                return 0;
            else{
                peer->port = atoi(found);
            }
        }

        if (counter == 3 && strncmp(found, "seed",strlen("seed")) != 0)
            return 0;                    
        
        if (counter == 4){
            found++;
            file_description[0]=found;
            fc++;
        }
        if ( counter> 4 ){ 
            if ( counter%4!=3 ){ // dans ce cas on n'est pas sur la key pour verifier si on est sur la dernier valeur qui contient ']' à la fin
                file_description[counter%4]=found;                
                fc++;
            }
            else{
                if ( found[strlen(found)-2] == ']'){
                    char *fileHash = strsep(&found, "]");                    
                    file_description[counter % 4] = fileHash;
                }else{
                    char *fileHash = found;
                    int len = strlen(fileHash);                    
                    if (fileHash[len - 1] == ']') {                        
                        fileHash[len - 1] = '\0';
                    }
                    file_description[counter % 4] = found;
                }
                fc++;
            }
        }

        if ( fc == 4 ){// dans ce cas on a parser tout le data du fichier il faut les stocker
            insertFileArray(peer->peerFiles,file_description[0],atoi(file_description[1]),atoi(file_description[2]),file_description[3]);
            fc=0;// new file 
        }
        //printf(" counter == %d et found %s \n",counter,found);
        //printf("end while fc=%d\n",fc);
        counter++;
    }   //   0       1      2    3     4          5    6      7          8         9     10     11                    
        //announce listen 2222 seed [file_a.dat 2097 1024 8905e92af file_b.dat 3145728 1536 330a57722]
    free(annouce);
    return 1;
}


int getLook(char *look, struct peerList peers, char **data){
    FILE *log = fopen("log/server.log", "a+");
    char *demande = strdup(look);
    char *found;
    int counter = 0;
    char * filename = NULL;    
    while((found = strsep(&demande, " "))   != NULL ){ // on doit pas free found , car strsep n'alloue pas la memoire au contraire de strdup        
        if ( counter == 1 ){            
            if (strncmp(found, "[filename=", 10) == 0) {
                found = found + 11;  // 11 pour enlever les guillemets
                // printf("1 - i found %s\n",found);
                filename = found;
                filename[strlen(filename) - 2] = '\0';
            } else {
                strcpy(*data, "> Error in the request format\n");
                return 0;
            }
        }
        counter++;
    }     
    if (filename == NULL){
        printf("le format de look n'est pas pris en charge \n");
        return -1;
    }
    

    fprintf(log,"INFO: start looking for %s \n",filename);

    
    Peer peer;
    //peer = peers.slh_first;
    //printf("$$$$ port  %s \n",peer->ip);
    int foundFile = 0;
    SLIST_FOREACH(peer, &peers,next_peer)
    {
        
        int used = peer->peerFiles->used;        
        for (int i = 0; i < used; i++) {
            fprintf(log, "INFO: i =%d  used= %d et  foreach : %s\n",i,used,peer->peerFiles->array[i]->file_name);
            //printf("filename : %s\n", filename);
            //printf("other : %s\n", peer->peerFiles->array[i]->file_name);
            if (strcmp(filename, peer->peerFiles->array[i]->file_name) == 0) {
                foundFile = 1;
                char response[2048];
                char length[20];
                char piece_size[20]; 
                char * key = peer->peerFiles->array[i]->key;
                fprintf(log,"INFO: found the file \n");
                itoa(peer->peerFiles->array[i]->length,length);
                itoa(peer->peerFiles->array[i]->piece_size,piece_size);
                
                strcpy(response ,"> list [");
                strcat(response,filename);
                strcat(response," ");
                strcat(response,length);
                strcat(response," ");
                strcat(response,piece_size);
                strcat(response," ");
                strcat(response,key);
                strcat(response,"]");
                strcat(response,"\n");
                //printf("====>response %s\n",response);
                strcpy(*data , response);
                //printf("====>data %s\n",*data);
                break;
            }
        }
        
    }
    if (!foundFile){                        
        strcpy(*data, "> file entered is not found\n");
    }
    //if ( counter >= 1 )
    //    printf("filename is %s\n",filename);
    //printf("end\n");
    //printf("%s\n", *data);
    free(demande);
    return 1;    
}

int getFile(char *buff, struct peerList peers, char **data){
    FILE *log = fopen("log/server.log", "a+");
    char *demande = strdup(buff);
    char *found;
    int counter = 0;
    char key_buffer[40];
    char * key = key_buffer;
    
    while((found = strsep(&demande, " "))   != NULL ){ // on doit pas free found , car strsep n'alloue pas la memoire au contraire de strdup
        if( counter == 1 ){            
            strncpy(key,found,strlen(found) + 1);                        
        }
        counter++;
    }
    
    if (key == NULL){
        printf("le format de getfile n'est pas pris en charge \n");
        return -1;
    }
    
    fprintf(log, "INFO: start looking for file with the key equal to %s \n",key);

    
    Peer peer;
    char response[200];
    int enter = 0;
    strcpy(response ,"> peers ");
    strcat(response,key);
    strcat(response," [");
    int foundKey = 0;
    SLIST_FOREACH(peer, &peers, next_peer)
    {
        int used = peer->peerFiles->used;        
        for (int i = 0; i < used; i++) {
            //printf(" i =%d  used= %d et  foreach : %s\n",i,used,peer->peerFiles->array[i]->key);
            //printf("%s\n", key);
            //printf("%s\n", peer->peerFiles->array[i]->key);
            //printf("%s == %s ? %d\n", key,  peer->peerFiles->array[i]->key, !strcmp(key, peer->peerFiles->array[i]->key));
            if (strcmp(key, peer->peerFiles->array[i]->key) == 0) {
                foundKey = 1;
                enter += 1;
                fprintf(log, "INFO: found the file \n");
                if (enter > 1)
                    strcat(response," ");
                char port[20];
                itoa(peer->port,port);
                strcat(response,peer->ip);
                strcat(response,":");
                strcat(response,port);
            }
        }        
    }
    if(!foundKey){            
            strcpy(*data, "> key is not found\n");
            free(demande);
            fclose(log);
            return 1;
        }
    strcat(response,"]");
    strcat(response,"\n");
    if ( enter >= 1 )
        strcpy(*data , response);
    //if ( counter >= 1 )
    //    printf("filename is %s\n",filename);
    //printf("end\n");
    //printf("%s\n", *data);
    free(demande);
    fclose(log);
    return 1;
}