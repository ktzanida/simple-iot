# Simple Kafka App in Clojure using Jackdaw and Integrant

This is an example implementation of Kafka in Clojure using the
Jackdaw Streams API.

## Setting up

You may need to download and install the Confluent CLI from
[https://www.confluent.io/download/](https://www.confluent.io/download/).

I am developing and experimenting with it using the following docker image:
[https://hub.docker.com/r/landoop/fast-data-dev](https://hub.docker.com/r/landoop/fast-data-dev).

## Send some messages

Fire up a REPL and hit:
```
(dev)
```

This app uses integrant at its core to orchestrate things. The
configuration used in this app can be found in:

```
resources/config.edn
```

Then in REPL type:

```
(go)
```

The app is now running.

You can publish messages through the `client` namespace and function
`publish-temperature!`.
