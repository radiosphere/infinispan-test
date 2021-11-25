# infinispan-test Project

To test the concept, start infinispan locally:
```
docker run -it -p 11222:11222 -e USER="admin" -e PASS="password" quay.io/infinispan/server:13.0
```

Then start two instances of this project using `./gradlew quarkusDev` and `./gradlew quarkusDev -Dquarkus.http.port=8081` (to make sure it uses a different port than the first instance).

You can now call
```
curl -XPOST localhost:8080/values\?key\=hello\&value\=world
```
and 
```
curl localhost:8081/values/hello
```
