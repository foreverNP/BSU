CXX = g++
CXXFLAGS = -Wall -g
SRC = main.cpp
OBJ = $(SRC:.cpp=.o)
BUILD_DIR = build
BIN_DIR = bin
EXEC = $(BIN_DIR)/app

$(EXEC): $(BUILD_DIR)/main.o | $(BIN_DIR)
	$(CXX) $(CXXFLAGS) -o $@ $^

$(BUILD_DIR)/%.o: %.cpp | $(BUILD_DIR)
	$(CXX) $(CXXFLAGS) -c $< -o $@

$(BUILD_DIR) $(BIN_DIR):
	mkdir -p $@

clean:
	rm -rf $(BUILD_DIR) $(BIN_DIR)

run: $(EXEC)
	./$(EXEC)

build: $(EXEC)

.PHONY: all clean run build