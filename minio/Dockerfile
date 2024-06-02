FROM minio/minio:latest

EXPOSE 9000
EXPOSE 9001

VOLUME minio-storage:/data

CMD ["server", "--console-address", ":9001",  "/data/"]