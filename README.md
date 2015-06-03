bloom-jclient
=============

Java client for [https://github.com/btnguyen2k/bloom-server](https://github.com/btnguyen2k/bloom-server).

## Release-notes ##

Latest release: `0.1.0`.

See [RELEASE-NOTES.md](RELEASE-NOTES.md).

## Usage ##

```java
// obtain an IBloomClient instance: REST client
IBloomClient bloomClient = new RestBloomClient().setBloomServerUrl("http://localhost:8080").init();
//or preferred way
IBloomClient bloomClient = RestBloomClientFactory.newBloomClient("http://localhost:8080");

// obtain an IBloomClient instance: Thrift client
IBloomClient bloomClient = new ThriftBloomClient().setBloomServerHostsAndPorts("localhost:9090,host2:9090,host3:9090").init();
//or preferred way
IBloomClient bloomClient = ThriftBloomClientFactory.newBloomClient("localhost:9090,host2:9090,host3:9090");
// Thrift client supports host fail-over!

// obtain an IBloomClient instance: Thrift-over-http client
IBloomClient bloomClient = new ThriftHttpBloomClient().setBloomServerUrls("http://localhost:8080/thrift,http://host2/thrift,http://host3/thrift").init();
//or preferred way
IBloomClient bloomClient = ThriftHttpBloomClientFactory.newBloomClient("http://localhost:8080/thrift,http://host2/thrift,http://host3/thrift");
// Thrift client supports host fail-over!
```

```java
// do some cool stuff

// initialize a bloom filter
BloomResponse response = bloomClient.initBloom("secret", "bloomName", 1000000, 1E-6, true, false, false);

// put an item to the default bloom filter
BloomResponse response = bloomClient.put("item1");

// put an item to a bloom filter
BloomResponse response = bloomClient.put("item2", "bloomName");

// test if an item has been put to the default bloom filter
BloomResponse response = bloomClient.mightContain("item1");

// test if an item has been put to a bloom filter
BloomResponse response = bloomClient.mightContain("item3", "bloomName");
```

## License ##

See [LICENSE.txt](LICENSE.txt) for details. Copyright (c) 2015 btnguyen2k.

Third party libraries are distributed under their own license(s).
