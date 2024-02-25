
# 1. Скачиваем образы и создаём контейнеры с нужными именами
docker run -d --name mynginxlast -p 8081:80 nginx:latest
docker run -d --name mynginxalpine -p 8082:80 nginx:alpine
docker run -d --name mynginx1-26 -p 8083:80 nginx:1.26

# 2. Демонстрация запуска контейнера mynginxlast в неинтерактивном режиме
docker stop mynginxlast
docker rm mynginxlast
docker run -d --name mynginxlast nginx:latest

# Подключение к контейнеру через exec -it
docker exec -it mynginxlast /bin/bash

# 3. Демонстрация запуска контейнера mynginxlast в интерактивном режиме
docker stop mynginxlast
docker rm mynginxlast
docker run -it --name mynginxlast nginx:latest /bin/bash

# 4. Монтирование каталога v как volume в mynginxalpine
docker stop mynginxalpine
docker rm mynginxalpine
docker run -d --name mynginxalpine -v .\task1:/usr/share/nginx/html -p 8080:80 nginx:alpine