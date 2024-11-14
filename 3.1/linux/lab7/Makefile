# Makefile

CC = gcc
CFLAGS = -Wall -Wextra -std=c11 -g
AR = ar
ARFLAGS = rcs

# Определяем, использовать ли отладку
ifdef DEBUG
    CFLAGS += -DDEBUG
endif

# Имя библиотеки
LIB = bin/libmmemory.a

# Объектные файлы
OBJS = obj/mmemory.o

# Тестовый исполняемый файл
TEST = bin/test_mmemory

all: create_dirs $(LIB) $(TEST)

create_dirs:
	mkdir -p bin obj

$(LIB): $(OBJS)
	$(AR) $(ARFLAGS) $@ $^

obj/mmemory.o: src/mmemory.c src/mmemory.h
	$(CC) $(CFLAGS) -c src/mmemory.c -o $@

$(TEST): obj/test_mmemory.o $(LIB)
	$(CC) $(CFLAGS) -o $@ obj/test_mmemory.o -Lbin -lmmemory

obj/test_mmemory.o: src/test_mmemory.c src/mmemory.h
	$(CC) $(CFLAGS) -c src/test_mmemory.c -o $@

clean:
	rm -f obj/*.o bin/$(LIB) bin/$(TEST)
	rm -rf bin obj

build: all

run: $(TEST)
	./$(TEST)

.PHONY: all clean create_dirs build run