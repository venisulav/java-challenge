How to build:

`mvn install`

The runnable jar can be found at at:

`target/server-1.0-SNAPSHOT-jar-with-dependencies.jar`

The Main file has two entry points, server and the stress generator. How to run:
```
java -jar server-1.0-SNAPSHOT-jar-with-dependencies.jar PORT N_THREADS server|stress_generator
```

Example usage:
Run the server first, then run the load generator
```
java -jar target/server-1.0-SNAPSHOT-jar-with-dependencies.jar 8000 10 server
```

```
java -jar target/server-1.0-SNAPSHOT-jar-with-dependencies.jar 8000 20 stress_generator
```

The port must be the same for both the programs. `N_THREADS` for `stress_generator` should be ideally more than `server`

The server logs total request processed every minute

The stress generator logs everytime 100000 requests are processed