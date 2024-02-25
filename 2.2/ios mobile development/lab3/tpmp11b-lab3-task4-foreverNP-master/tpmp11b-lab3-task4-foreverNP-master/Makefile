CC = cc 
CFLAGS = 

TARGET = bin/program
OBJS = src/main.c src/military.c

all: $(TARGET)

build:
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJS)

run: build
	./$(TARGET)

clean:
	rm -f $(TARGET)
