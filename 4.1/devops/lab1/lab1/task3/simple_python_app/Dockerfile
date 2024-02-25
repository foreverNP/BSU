FROM python:3.11

COPY . /app/

RUN apt-get update && apt-get install -y gcc

RUN pip install -r /app/requirements.txt

WORKDIR /app

CMD ["uvicorn", "app:app", "--host", "0.0.0.0"]