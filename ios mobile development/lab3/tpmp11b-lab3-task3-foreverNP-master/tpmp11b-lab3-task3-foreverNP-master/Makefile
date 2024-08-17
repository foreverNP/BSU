CC = cc 
CFLAGS = 

TARGET = bin/program
OBJS = src/main.c src/note.c

all: $(TARGET)

build:
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJS)

run: build
	./$(TARGET)

test:
	$(CC) $(CFLAGS) -o bin/test src/test.c src/note.c
	./bin/test

clean:
	rm -f $(TARGET)
