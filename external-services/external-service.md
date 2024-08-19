# External Service

## How To Run

```bash
$ cd external-services/
$ java -jar external-services.jar
```
- It uses port `7070` by default.

## To change the port

```bash
$ java -jar external-services.jar --server.port=8080
```

## Swagger
```bash
$ curl http://localhost:7070/webjars/swagger-ui/index.html
```

## Netstat command to monitor the network connections
```bash
$ netstat -an| grep -w 127.0.0.1.7070
```

## To watch
```bash
$ watch 'netstat -an| grep -w 127.0.0.1.7070'
```