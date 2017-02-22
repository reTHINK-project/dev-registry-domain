# Registry Connector Manual

## Introduction

### Motivation
This document describes the manual for the reThink architecture component named
Registry Connector. The Registry Connector is the component that ensures the
communication between the Messaging Node and the Domain Registry.

### Context
One of the components of the reThink project architecture is the Registry
service.  The Registry service is similar to a directory service, that
facilitates management and lookup of the Hyperty instances being run on users’
devices.  The Registry service is sub-divided in two components: the Global
Registry, built on top of a Distributed Hash Table, and the Domain Registry,
which uses the client-server model and provides a REST API.  The Registry
service must be a replicated, load balanced, fault tolerant distributed system
with worldwide availability.  The communication between the user's device
runtime and the Domain Registry, is done through the Messaging Node. Therefore
a component named Registry Connector is required to bridge the Messaging Node
and the Domain Registry. This component, the Registry Connector, run on the
Messaging Node, being responsible for the communication with the REST API
provided by the [Domain
Registry](https://github.com/reTHINK-project/dev-registry-domain/blob/master/docs/DomainRegistryDeveloperManual.md).

### Functionalities
The Registry Connector is implemented in Javascript so it is possible to reuse
the component in the several Messaging Node implementations: it could be used
in the [Matrix](http://matrix.org/), [Node.js](https://nodejs.org) and
[Vert.x](http://vertx.io/) (Java Virtual Machine) implementations.  The
Registry Connector implements the CRUD operations (Create, Read, Update and
Delete) provided by the Domain Registry REST API.

### Architecture
In Figure 1 an architecture overview is shown. Two main subcomponents are
identified: Registry Connector API and HTTP Request Shim layer.

![Registry Connector overview](registry-connector-overview.png)
**Figure 1:** Registry Connector architecture overview.

In the Registry Connector API the main CRUD functionalities provided by the
Registry Connector are implemented.
The HTTP Request Shim layer deals with the different Javascript engines that
the Registry Connector may run under. For example, in the Vert.x
implementation, since it runs in the Java Virtual Machine (JVM), the [Nashorn
Javascript Engine](http://openjdk.java.net/projects/nashorn/) doesn’t implement
an API to provide client functionality for transferring data between a client
and a server (like XMLHTTPRequest). As such, it is necessary to implement this
functionality calling Java methods from Javascript, which is accomplished by
the use of a shim layer. This is also applicable to the Node.js runtime, where
a Node.js HTTP client library ([request](https://github.com/request/request)) is used.

The Registry Connector can be required as a module through [NPM](www.npmjs.com).

### Implementation
In this Section, the specific Vert.x Messaging Node implementation is detailed.
An architecture overview is depicted in Figure 2.

![Registry Connector architecture](registry-connector-architecture.png)
**Figure 2:** Registry Connector architecture.

The Registry Connector implementation in Vert.x takes advantage of the defined
Verticle concept. A Verticle is a chunk of code that can be deployed and run by
Vert.x. An application in Vert.x is ideally composed by several verticles that
communicate with each other by sending messages through the Event Bus. The
Verticles could be written in any of the languages supported by Vert.x
(Javascript, Java, Ruby, etc), which technically are the languages that have
JVM implementations.

Since the Registry Connector is written in Javascript, a Javascript verticle
was written as to be possible to interact with the Registry Connector code.
The Registry Connector verticle is more precisely, a worker verticle, i.e a
verticle that is executed using a thread from the Vert.x worker thread pool.
The communication with the Registry Connector verticle is done through the
Event Bus using a publish-subscribe messaging pattern.  When the user runtime
wants to contact the Domain Registry, it sends a message with the address
domain://registry.\<sp-domain\> in the from field, to the Messaging Node.  The
main verticle in the Messaging Node, when receiving this message, will forward
it to the address mn:/registry-connector. During the deployment process, the
Register Connector verticle will register a handler to process messages sent to
this address, and call the respective Registry Connector API functions.

#### Message Format
The Messages received by the Registry Connector are described in [Registration Messages](https://github.com/reTHINK-project/specs/blob/master/messages/registration-messages.md).

### Code Structure

```
├── index.js
├── src
│   ├── RegistryConnector.js
│   ├── dataObject.js
│   ├── hyperty.js
│   ├── java-request.js
│   ├── js-request.js
│   └── request.js
```
**Registry Connector code files structure.**

#### RegistryConnector file
In the RegistryConnector file the main messages processing logic is defined.
The method `processMessage` will inspect the message type field, and call the apropriate method to deal with the request:
 - `readOperation` - if message field `type: 'read'`
 - `createOperation` - if message field `type: 'create'`
 - `updateOperation` - if message field `type: 'update'` 
 - `deleteOperation` - if message field `type: 'delete'`
 
Each of these methods will verify if the request is an hyperty or data object request, and will call the respective hyperty/data object methods to deal with the request. All the methods receive a callback as an argument, which will be called
with the response message as an argument.

##### Request file
This file provides a wrapper for the request methods.
When initializing an Request object, the constructor will check the Javascript engine, and load the corresponding HTTP request shim library.

##### hyperty and dataObject file
Each of these files implement the methods described in the Request file, that provide CRUD operations to hyperties and dataobjects:

#### HTTP/HTTPS request shim files
As mentioned before, even thought the code is able to run in different
Javascript engines, due to the lack of a common API for executing HTTP/HTTPS requests, is necessary to provide shim functions for executing the requests accordingly with the underlying Javascript engine.
Right now, there is an implementation for the Nashorn engine and Node.js.

In the case of Nashorn, it uses a Java wrapper class to execute the requests to the Domain Registry API, the class *HTTPRequest* provided by the [Vertx Message Node](https://github.com/reTHINK-project/dev-msg-node-vertx). In node.js it uses the [request](https://github.com/request/request) library.

Both shims provide the same methods:

 - **get** - make a GET request to the provided URL;
 - **put** - make a PUT request to the provided URL with the provided JSON data;
 - **del** - make a DELETE request to the provided URL;

A callback function should be provided, which will receive an error object,
response body and status code, as arguments.

### Connector installation
Install the Registry Connector module by adding the following dependency to the corresponding *package.json* file:
```
 "dev-registry-domain": "rethink-project/dev-registry-domain#R0.8.0"
```
or by executing the following command:
```
npm install rethink-project/dev-registry-domain#R0.8.0 --save
```

Then is possible to require it in the code. Example:
```
const RegistryConnector = require('dev-registry-domain/connector');
const registry = new RegistryConnector(config);
```
### Config options

**HTTP Domain Registry** 
```
const config = {
  url: 'http://citysdk.tecnico.ulisboa.pt',
  retries: 2
  ssl: {
    enabled: false
  }
};

const registry = new RegistryConnector(config);
```

**HTTPS Domain Registry** 

More information about how to generate the necessary CA infrastructure and certificates is in the [Certification Manual](https://github.com/reTHINK-project/dev-registry-domain/blob/master/docs/CertificationManual.md).
```
# vertx config
const config = {
  url: 'https://citysdk.tecnico.ulisboa.pt',
  retries: 2
  ssl: {
    enabled: true,
    trustStore: 'domain.jks',
    trustStorePass: 'rethink',
    keyStore: 'connector.jks',
    keyStorePass: 'rethink',
    keyPassphrase: 'rethink'
  }
};

# node.js config
const config = {
  url: 'https://citysdk.tecnico.ulisboa.pt',
  retries: 2
  ssl: {
    enabled: true,
    cert: 'connector.cert.pem',
    key: 'connector.key.pem',
    keyPassphrase: 'rethink',
    ca: 'ca-bundle.pem'
  }
};

const registry = new RegistryConnector(config);
```










