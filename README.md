# javaProbe
Simple service (HTTP, ..) checker in Java.

Usefull in Docker or K8s healthcheck usage.
Specificaly if distro-less Images are used.


## Sample usage

### In Docker

```
docker run --name somaApp -d \
  --cpus=1 -m 312m --memory-reservation=312m \
  -p 14080:8080 \
  --add-host=cachesrv01:$LENKOTR_BCACHEHOST01 \
  --restart=unless-stopped \
  --health-cmd='java -jar /opt/javaProbe.jar -u http://localhost:8080' \
  --health-timeout=3s \
  --health-interval=7s \
  some.repo.com/app/webapp:1.7.2
```
Caution:
o> Image needs to have (some) Shell installed;


### In Docker-Compose

```
..
  lenkoTrWebApp:
    image: some.repo.com/app/webapp:${VER_BASE}
    container_name: xWebApp
    ports:
      - "14080:8080"
    networks:
      - lenkotr-network
    hostname: "xWebApp"
    extra_hosts:
      - "${HOSTNAME_CACHE01}:${HOST_CACHE01}"
    depends_on:
      - cachesrv01
    healthcheck:
      test: [ "CMD", "java", "-jar", "/opt/javaProbe.jar", "-u", "http://localhost:8080/" ]
      start_period: 60s
      interval: 10s
      timeout: 10s
      retries: 3
    deploy:
      resources:
        limits:
          cpus: '0.53'
          memory: 312M
        reservations:
          cpus: '0.31'
          memory: 302M
..
```


## Reference

o> https://github.com/jgrumboe/distroless-java-healthchecks
o> https://www.naiyerasif.com/post/2021/03/01/java-based-health-check-for-docker/