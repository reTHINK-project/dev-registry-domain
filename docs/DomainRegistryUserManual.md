# Domain Registry User Manual

## Introduction

This document provides instructions on the use of a reThink’s architecture component named Domain Registry. Together with the Global Registry, it forms the Registry Service. The Global Registry provides a mapping from a User’s Unique Id (GUID) to the several services it uses. Each Communication Service Provider (CSP)
runs a Domain Registry service that resolves domain-dependent user identifiers to
the actual information about this user’s Hyperty Instances (a Hyperty used by a user in one device).

A Domain Registry stores, for each user identifier, the list of Hyperty Instances the user runs on his devices. It also stores, for each Hyperty Instance, the data that enables other applications to contact it, by providing a mapping between the identifier for each Hyperty Instance and the data that characterizes it.

The Domain Registry is a critical service as it stands in the critical path for call establishment. As it will be used very often, it must provide a low access time, high availability and be capable of fast updates (e.g. for when a device changes IP address). It is based on the client-server model and handles high-speed and high- frequency data.

The remaining of this document comprises the following sections: How to deploy, comprising instructions on how to use Docker and
the command line, a definition of the REST API and respective available endpoints, and finally, some usage examples.

## How to deploy

The Domain Registry is deployed using Docker. All commands must be executed  inside the _server_ folder.

### How to deploy using docker

A Dockerfile is provided, so is possible to run the Domain Registry through a Docker container. Since several ways of storing requests are available, there are three possible ways to run the Domain Registry.

1. Storing requests in-memory;
2. Storing requests in a multi-host Cassandra database cluster.
3. Storing requests in a single Cassandra node;

#### Requests saved in-memory

Similarly to the last Domain registry version, requests may be saved in-memory. It is the simplest way to deploy the server. The commands are the following:

```
$ docker build -t domain-registry .
$ docker run -e STORAGE_TYPE=RAM -e EXPIRES=3600 -p 4568:4567 domain-registry
```
Expires global variable defines the maximum amount of time (in seconds) a Hyperty stays in the server (see [soft state issue](https://github.com/reTHINK-project/dev-registry-domain/issues/7)). Note that the published port 4568 may be changed to another port that better suits your needs. Running the server with this configuration will work exactly as the last version.

#### Requests saved in a multi-host Cassandra cluster

Starting the database cluster in separate machines (ie, two VMs on a cloud service provider), requires that every Cassandra node advertises an IP address to the other nodes because the address of the container is behind the docker bridge. A script, written in ruby, is provided to setup all this configuration.

```
$ ruby start_cassandra_cluster.rb username 82.196.2.146 128.199.35.237 178.62.207.90 128.199.33.57
```
This script takes as arguments the IP addresses of the servers in which the docker containers will run. After a few minutes the cluster should be running. Use SSH to connect to one of the four servers and follow the next steps. The previous script assumes that Docker is installed on the servers and SSH public key authentication is enabled (login without password). Otherwise, it will not work. Before running the next command, execute _docker ps_ to ensure that the container is indeed running.

* Connect to the cluster using cqlsh (Cassandra query language interactive terminal).

```
$ docker run -it --link <container name>:cassandra --rm cassandra sh -c 'exec cqlsh "$CASSANDRA_PORT_9042_TCP_ADDR"'
```
You should see something like:

```
[cqlsh 5.0.1 | Cassandra 2.2.0 | CQL spec 3.3.0 | Native protocol v4]
Use HELP for help.
cqlsh>
```

* Execute Domain registry data model configuration in cqlsh

Paste the following configuration into your cqlsh prompt to create a keyspace, and two hyperties's tables:

```
CREATE KEYSPACE rethinkeyspace WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};

use rethinkeyspace;

CREATE TABLE hyperties_by_id (
    hypertyid text,
    user text,
    descriptor text,
    startingTime text,
    lastModified text,
    expires int,
    PRIMARY KEY(hypertyid)
);

CREATE TABLE hyperties_by_user (
    hypertyid text,
    user text,
    descriptor text,
    startingTime text,
    lastModified text,
    expires int,
    PRIMARY KEY(user, hypertyid)
);

SELECT * FROM hyperties_by_id;
```
An empty hyperties table should appear. Again, you may change the replication\_factor to another value. With this configuration (4 nodes with a replication factor of 3), we can tolerate the loss of 2 nodes. 

The following command provides information about the cluster, such as the state (Normal/Leaving/Joining/Moving), load, and IDs.

```
$ docker exec <container-name> nodetool status rethinkeyspace
```
Observe that "rethinkeyspace" is the name of the keyspace defined previously.

Something like this should appear:

```
Datacenter: datacenter1

Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--  Address       Load       Tokens       Owns (effective)  Host ID                               Rack
UN  172.17.2.131  167.54 KB  256          60.0%             04111fc6-4dfa-496c-b904-0defde6d3b92  rack1
UN  172.17.2.133  298.41 KB  256          61.4%             b5b98cd9-dea0-4413-9387-1050dd3dafc3  rack1
UN  172.17.2.132  270.25 KB  256          66.1%             f771448e-2a93-4726-af0a-21d1577ca3d9  rack1
UN  172.17.2.135  258.53 KB  256          58.0%             4e6a07c9-dd3b-43e2-9cc0-32e39122a82d  rack1
UN  172.17.2.134  283.42 KB  256          54.5%             ec5bf2ea-37f8-4751-a578-dd09fd073a3f  rack1
```

With the database cluster running we can start the Domain Registry with the following commands:

```
$ docker build -t domain-registry .
$ docker run -e STORAGE_TYPE=CASSANDRA -e CONTACT_POINTS_IPS=ip1,ip2,ip3 -e EXPIRES=3600 -p 4568:4567 domain-registry
```
The environment variable CONTACT\_POINTS\_IPS comprises a set of IP addresses belonging to some database nodes. The Domain Registry server will use these IP's to discover and establish a connection with the database. The server will only use one IP, but providing the client more IPs will increase the chance for the client to continue to work with the database in case of node failures.

When executing the _docker run_ command, if something like this appear, it means that the client successfully connected with the database cluster.

```
Connected to cluster: Test Cluster
Datacenter: datacenter1; Host: /172.17.2.131; Rack: rack1
Datacenter: datacenter1; Host: /172.17.2.132; Rack: rack1
Datacenter: datacenter1; Host: /172.17.2.135; Rack: rack1
Datacenter: datacenter1; Host: /172.17.2.134; Rack: rack1
```

Finally, the /live page could be used to verify up and down Cassandra nodes. A GET /live should return the following JSON object:

```
{
  "Database cluster size": "5",
  "Hyperties stored": "0",
  "Database up nodes": "5",
  "Database connection": "up",
  "Storage type": "Cassandra",
  "status": "up"
}
```
#### Storing requests in a single Cassandra node

Starting the Domain Registry backed with a single database node is quite simple:

```
$ docker run --name cassandra-node -d cassandra:latest
```

Next, follow the same steps as before. Establish a connection to the cluster with cqlsh, execute the data model into the cqlsh prompt, verify that everything is working as expected, and finally start the Domain Registry.

```
$ docker build -t domain-registry .
$ docker run -e STORAGE_TYPE=CASSANDRA -e CONTACT_POINTS_IPS=ip -e EXPIRES=3600 -p 4568:4567 domain-registry
```
## Rest API definition and available endpoints

The Domain Registry is a REST server that allows to create, update and remove data (users and Hyperty Instances in this case). Next, are described the three available API endpoints.

* GET /hyperty/user/:user_id
* PUT /hyperty/user/:user_id/:hyperty_instance_id
* DELETE /hyperty/user/:user_id/:hyperty_instance_id

Possible HTTP status codes returned: 200 OK indicating that the request has succeeded and 404 Not Found indicating that the server has not found anything matching the request URI (users or hyperties). In both cases, a message is returned on the response: “hyperty created”, “user not found” or “data not found”.

Since the users and hyperties URLs contain characters outside the ASCII set, URLs need to be converted to a valid ASCII format. The character “%” followed by two
hexadecimal digits replace the unsafe characters. As an example, the enconded
version of user://inesc-id.pt/ruijose is user%3A%2F%2Finesc-id.pt%2Fruijose.

### GET /hyperty/user/:user_id

Retrieves all Hyperties instances from a user indicated by the user_id parameter.

#### Parameters

**user_id**: The ID of the user for whom to return results for.

**Example_value**: user://inesc-id.pt/ruijose

#### Example request

GET /hyperty/user/user%3A%2F%2Finesc-id.pt%2Fruijose

#### Example result

```
{
  "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4327-238jhdq83d8": {
    "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
    "startingTime": "2016-02-08T13:40:26Z",
    "lastModified": "2016-02-08T13:41:27Z,
    "expires" : 3600
  }
  "hyperty://inesc-id.pt/b7b3rs4-3245-42gn-4127-238jhdq83d8": {
    "descriptor": "hyperty-catalogue://localhost/HelloHyperty",
    "startingTime": "2016-02-08T13:42:00Z",
    "lastModified": "2016-02-08T13:42:53Z",
    "expires" : 30
  }
}
```

The descriptor is a link to the Catalogue from where the descriptor of the instance can be retrieved and, the startingTime and lastModified refer to the date the instance is first registered and the last time the instance was modified. Both dates format are compliant with ISO8601 format. In the future, more information about the hyperty instances will be provided.

In this example, two hyperty instances are returned for the requested user id. Note that the requested URLs are encoded.

If the server could not find what was requested, along with the HTTP status codes, a “user not found” or a “data not found” message is returned to the user.

#### Error messages Examples

```
{
  “message” : “user not found”
}
```

```
{
  “message” : “data not found”
}
```

### PUT /hyperty/user/:user_id/:hyperty_instance_id

Creates or updates a Hyperty Instance. It also creates a user if it doesn’t exists already.

#### Parameters

**user_id**: The ID of the user for whom to return results for.

**Example_value**: user://inesc-id.pt/ruijose

**hyperty_id**: The ID of the Hyperty to be created.

**Example_value**: hyperty://ua.pt/428bee1b-887a8ee8cb32

#### Example request

PUT /hyperty/user/user%3A%2F%2Finesc-id.pt%2Fruijose/hyperty%3A%2F%2Fua. pt %2F428bee1b-887a8ee8cb32

#### Example result

```
{
  “message” : “hyperty created”
}
```

Note that the requested URL’s are encoded.

### DELETE /hyperty/user/:user_id/:hyperty_instance_id

Deletes a Hyperty Instance from a user indicated by the user_id parameter.

#### Parameters

**user_id**: The ID of the user for whom to return results for.

**Example_value**: user://inesc-id.pt/ruijose

**hyperty_id**: The ID of the Hyperty to be created.

**Example_value**: hyperty://ua.pt/428bee1b-887a8ee8cb32

#### Example request

DELETE /hyperty/user/user%3A%2F%2Finesc-id.pt%2Fruijose/hyperty %3A %2F%2Fua.pt%2F428bee1b-887a8ee8cb32

#### Example result

```
{
  “message” : “hyperty deleted”
}
```

Note that the requested URL’s are encoded.

## Future functionalities

The current version is missing any authentication mechanisms. Currently, it is assumed that the Message Node is the only one capable of interacting with the Local Registry, with the former being trusted by the latter to verify the user’s authorization to perform the requests. This model will have to be replaced with a secure mechanism where either the identity of the Message Node or of the user is verified.

A load balancer will also be added to distribute network traffic across the Domain Registry servers. Thereby, we hope to increase capacity (concurrent users) and application’s reliability.
