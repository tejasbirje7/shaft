FROM elasticsearch:8.4.3

COPY elasticsearch.yml /usr/share/elasticsearch/config/

RUN bin/elasticsearch-plugin install analysis-icu
