FROM nginx:1.17.8-alpine
WORKDIR /usr/share/nginx/html
COPY . /usr/share/nginx/html
RUN mv /usr/share/nginx/html/matrix-backend.conf /etc/nginx/conf.d
EXPOSE 4000
