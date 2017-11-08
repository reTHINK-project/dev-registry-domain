# dev-registry-domain

[![Build Status](https://travis-ci.org/reTHINK-project/dev-registry-domain.svg?branch=develop)](https://travis-ci.org/reTHINK-project/dev-registry-domain)

### [Documentation](https://github.com/reTHINK-project/dev-registry-domain/tree/master/docs)


### How to run
Expires global variable defines the maximum amount of time (in seconds) a Hyperty stays in the server ([soft state issue](https://github.com/reTHINK-project/dev-registry-domain/issues/7)).

```
$ docker build -t domain-registry .
$ docker run -e STORAGE_TYPE=RAM -e EXPIRES=3600 -e DOMAIN_ENV=DEVELOPMENT -p 4568:4567 domain-registry
```
A development environment will provide an overview of the Hyperties stored on the status page (/live).
The Domain Registry can be run with another configurations, such as a with multi-host database cluster or with mutual authentication. See the [user manual](https://github.com/reTHINK-project/dev-registry-domain/blob/master/docs/DomainRegistryUserManual.md) for more information.


### Integration tests
Inside the server/specs folder run the following docker commands.

```
$ docker build -t domain-specs .
$ docker run -e HOST=my.domain.server:port domain-specs
```

### Connector installation
Install the Registry Connector module by adding the following dependency to the corresponding *package.json* file:
```
 "dev-registry-domain": "rethink-project/dev-registry-domain#R1.0.0"
```
or by executing the following command:
```
npm install rethink-project/dev-registry-domain#R1.0.0 --save
```

Then is possible to require it in the code. Example:
```
const RegistryConnector = require('dev-registry-domain/connector');
const registry = new RegistryConnector(config);
```

#### Config options

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

**Notifications**

In order to receive hyperty/data objects notifications from Domain Registry, is necessary to pass an additional callback.
```
const notify = (err, msg) => {

  Object.keys(msg.updated).forEach(function (key) {
    // Process notification here
    console.log(`${msg.updated[key]} changed to status -> ${msg.updated[key].status}`)
  });

};

var registry = new RegistryConnector(config, notify);
```
