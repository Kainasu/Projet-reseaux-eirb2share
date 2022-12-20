CC=gcc
CFLAGS=-std=c99 -Wall -pthread
BUILD_FOLDER=build

all:
	mkdir -p $(BUILD_FOLDER)
	$(CC) $(CFLAGS) server.c fileArray.c parse.c peerList.c -o $(BUILD_FOLDER)/server.out
	$(CC) $(CFLAGS) client.c fileArray.c parse.c -o $(BUILD_FOLDER)/client.out

test:

clean:
	rm -rf $(BUILD_FOLDER)
