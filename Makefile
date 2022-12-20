PEER_DIR = Peer
TRACKER_DIR= Tracker
BUILD_DIR = build
SRC_DIR = Pair
TEST_DIR = tst
TRACKER_TST = tst
CC=gcc
CFLAGS= -Wall -I${TRACKER_DIR}
SRC_FILES = $$(find ${SRC_DIR} -name '*.java')

TEST_FILES = $$(find ${TEST_DIR} -name '*.java')
TEST_EXEC = $$(find ${BUILD_DIR} -name 'Test*.class' -exec basename {} \; | cut -d. -f1 | grep -v -e "Abstrait" -e "Arret" -e "Montee")

PACKAGE_NAME = Pair

all: compil

install_dir:
	mkdir -p ${BUILD_DIR}
	mkdir -p ${BUILD_DIR}/${TRACKER_DIR}

alltest:
	javac -d ${BUILD_DIR} ${SRC_FILES}
	javac -d ${BUILD_DIR} -cp ${BUILD_DIR} ${TEST_FILES}
	for e in ${TEST_EXEC} ; do \
	java -ea -cp ${BUILD_DIR} ${PACKAGE_NAME}.$$e ; \
	done


%.o: ${TRACKER_DIR}/%.c ${TRACKER_DIR}/%.h
	${CC} ${CFLAGS} -c $< -o ${BUILD_DIR}/${TRACKER_DIR}/$@ 

client:
	mkdir -p ${BUILD_DIR}
	javac -d ${BUILD_DIR} ${SRC_FILES}
	java -cp ${BUILD_DIR} ${PACKAGE_NAME}.Main

server: server.o parse.o fileArray.o peerList.o
	@${CC} ${CFLAGS} ${BUILD_DIR}/${TRACKER_DIR}/*.o -o $@ -pthread
	./$@

compil: install_dir server

testTracker:
	mkdir -p ${BUILD_DIR}/${TRACKER_DIR}
	@$(CC) $(CFLAGS) ${TRACKER_DIR}/fileArray.c ${TRACKER_DIR}/parse.c ${TRACKER_DIR}/peerList.c ${TRACKER_DIR}/${TRACKER_TST}/fileArraytest.c -o ${BUILD_DIR}/${TRACKER_DIR}/fileArraytest
	@$(CC) $(CFLAGS) ${TRACKER_DIR}/fileArray.c ${TRACKER_DIR}/parse.c ${TRACKER_DIR}/peerList.c ${TRACKER_DIR}/${TRACKER_TST}/parseTest.c -o ${BUILD_DIR}/${TRACKER_DIR}/parseTest
	@./${BUILD_DIR}/${TRACKER_DIR}/fileArraytest
	@./${BUILD_DIR}/${TRACKER_DIR}/parseTest



clean:
	rm -rf ${BUILD_DIR}/* server log/*

cleanFiles:
	rm fichier_test*.txt
