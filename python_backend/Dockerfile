FROM python:3.8-slim-buster

WORKDIR /app

ADD . /app

RUN pip install scikit-learn
RUN pip install numpy
RUN pip install scipy
RUN pip install Flask
RUN pip install matplotlib

EXPOSE 5000

CMD ["python", "app.py"]