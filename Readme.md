## How to build:

`mvn install`

The runnable jar can be found at at:

`target/server-1.0-SNAPSHOT-jar-with-dependencies.jar`

The Main file has two entry points, server and the stress generator. How to run:
```
java -jar server-1.0-SNAPSHOT-jar-with-dependencies.jar PORT N_THREADS server|stress_generator
```

## Example usage:
Run the server first, then run the load generator
```
java -jar target/server-1.0-SNAPSHOT-jar-with-dependencies.jar 8000 10 server
```

```
java -jar target/server-1.0-SNAPSHOT-jar-with-dependencies.jar 8000 20 stress_generator
```

The port must be the same for both the programs. `N_THREADS` for `stress_generator` should be ideally more than `server`

The server logs total request processed every minute

The stress generator logs everytime 100000 requests are processed.


## The example output

### Server:
```dtd
sulav@Sulavs-MBP java-challenge % java -jar target/server-1.0-SNAPSHOT-jar-with-dependencies.jar 8000 10 server
Oct 27, 2024 3:13:52 PM com.verve.challenge.Server runServer
INFO: Using 10 Threads
Oct 27, 2024 3:13:52 PM com.verve.challenge.Server runServer
INFO: Server is running on port 8,000
Oct 27, 2024 3:13:52 PM com.verve.challenge.StatCollector run
INFO: Starting collecting statistics...
Oct 27, 2024 3:14:52 PM com.verve.challenge.StatCollector run
INFO: Total request processed in cycle 1 : 403,005
Oct 27, 2024 3:15:52 PM com.verve.challenge.StatCollector run
INFO: Total request processed in cycle 2 : 1,657,802
Oct 27, 2024 3:16:52 PM com.verve.challenge.StatCollector run
INFO: Total request processed in cycle 3 : 1,745,308
Oct 27, 2024 3:17:52 PM com.verve.challenge.StatCollector run
INFO: Total request processed in cycle 4 : 1,742,772
Oct 27, 2024 3:18:52 PM com.verve.challenge.StatCollector run
INFO: Total request processed in cycle 5 : 1,750,783

```
### Stress Generator:
```dtd
sulav@Sulavs-MBP target % java -jar server-1.0-SNAPSHOT-jar-with-dependencies.jar 8000 20 stress_generator
Oct 27, 2024 3:14:39 PM com.verve.challenge.LoadGenerator$SyncRequestInvoker run
INFO: Processed:100,000 requests
Oct 27, 2024 3:14:43 PM com.verve.challenge.LoadGenerator$SyncRequestInvoker run
INFO: Processed:200,000 requests
Oct 27, 2024 3:14:47 PM com.verve.challenge.LoadGenerator$SyncRequestInvoker run
INFO: Processed:300,000 requests
Oct 27, 2024 3:14:52 PM com.verve.challenge.LoadGenerator$SyncRequestInvoker run
INFO: Processed:400,000 requests
Oct 27, 2024 3:14:56 PM com.verve.challenge.LoadGenerator$SyncRequestInvoker run
INFO: Processed:500,000 requests
Oct 27, 2024 3:14:59 PM com.verve.challenge.LoadGenerator$SyncRequestInvoker run
INFO: Processed:600,000 requests
Oct 27, 2024 3:15:03 PM com.verve.challenge.LoadGenerator$SyncRequestInvoker run
INFO: Processed:700,000 requests
Oct 27, 2024 3:15:07 PM com.verve.challenge.LoadGenerator$SyncRequestInvoker run
INFO: Processed:800,000 requests
Oct 27, 2024 3:15:11 PM com.verve.challenge.LoadGenerator$SyncRequestInvoker run
INFO: Processed:900,000 requests
```