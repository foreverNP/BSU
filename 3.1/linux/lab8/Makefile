all: test bench run

test:
	python fs/tests.py

bench:
	python fs/benchmarks.py

run_main:
	python fs/main.py

run: install
	python fs/server.py

install:
	pip install -r requirements.txt

.PHONY: all test bench run run_main
