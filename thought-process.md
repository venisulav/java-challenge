# Thought Process

There are many network level factors like the location of client and server
that can impact the performance of the service. In my simulated scenario,
the http server has `n` threads and I generate load using another
program that has `m` threads to try to throttle the web server.
All the threads in this application continuously sends a request
to the web server. In my experiment where I sent the request with only
`id` parameter, the web server could process approximately
1,8 million records per minute. But this depends on the kind of machine that you use.
Therefore, I have made number of threads as a parameter to the running program. I had used 5 server threads and 10 threads to create stress. I'm running on a mac with 8 cores.

I have used a http server that is already a part of the
java standard library in the implementation as we just need
one endpoint and didn't want to use any frameworks for simplicity.

I have created a `StatCollector` class to collect statistics.
Internally it stores an integer counter and a set of ids. Every minute, the total number of processed
request is logged and it reinitializes the statistics. It is possible to have a window during reinitialization where the server threads might be working with the older statistics instance. This is a tradeoff and is possible to happen only during reinitialization which we do once a minute. Synchronizing is a bit of a hassle here because it could create a performance bottleneck. I do not think this is an issue at least in our application, because the deduplication logic lasts only a minute. Clearing the set and resetting the counter would work as well, but this comes with a huge syncronization overhead because this would mean you'd need an exclusive lock to control the modification to the counter and the set together.

I replaced the GET with a POST for the endpoint. I used
a plain text as the message format instead of json for simplification.
I didn't want to include external libraries like jackson or gson for handling JSON. I tested this by sending a request to a python flask application that accepts post.

To simulate multiple instances of Server running, I
made the StatCollector a Singleton which can be used by more than one
instance of the server. I also made sure to decouple the
statistics from the http server. Ideally it's possible to
run the StatCollector as a separate program and define a contract
with the http server.

I was not very clear about sending the logs to the distributed streaming service and wanted my program to run easily as a standalone. I could have created a docker componse with kafka and my application, but it felt too heavy for the task.




