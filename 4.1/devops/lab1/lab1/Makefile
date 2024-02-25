build:
	docker build -t pedropascal1/hello-app:1.0 .

start:
	docker run -d -p 8081:80 --name mypage pedropascal1/hello-app:1.0

push:
	docker push pedropascal1/hello-app:1.0

.PHONY: build start