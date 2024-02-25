CFLAGS = -Wall -Wextra -pthread
TARGET = bin/server
OBJS = obj/server.o obj/mmemory.o
SRC_DIR = src

all: $(TARGET)

$(TARGET): $(OBJS) | bin
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJS)

obj/server.o: $(SRC_DIR)/server.c $(SRC_DIR)/mmemory.h | obj
	$(CC) $(CFLAGS) -c $(SRC_DIR)/server.c -o obj/server.o

obj/mmemory.o: $(SRC_DIR)/mmemory.c $(SRC_DIR)/mmemory.h | obj
	$(CC) $(CFLAGS) -c $(SRC_DIR)/mmemory.c -o obj/mmemory.o

bin obj:
	mkdir -p $@

clean:
	rm -f $(TARGET) $(OBJS)
	rm -rf bin obj