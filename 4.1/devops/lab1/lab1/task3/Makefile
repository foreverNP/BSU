build_all:
	docker build -t simple_python_app:v1 -f ./simple_python_app/Dockerfile ./simple_python_app
	docker build -t simple_python_app:v2 -f ./simple_python_app/Dockerfile.v2 ./simple_python_app
	docker build -t simple_python_app:v3 -f ./simple_python_app/Dockerfile.v3 ./simple_python_app
	docker build -t simple_python_app:v4 -f ./simple_python_app/Dockerfile.v4 ./simple_python_app
	docker build -t simple_python_app:v5 -f ./simple_python_app/Dockerfile.v5 ./simple_python_app
	docker build -t simple_python_app:v5.alpine -f ./simple_python_app/Dockerfile.v5.alpine ./simple_python_app
	docker build -t simple_python_app:v6 -f ./simple_python_app/Dockerfile.v6 ./simple_python_app
	docker image ls simple_python_app