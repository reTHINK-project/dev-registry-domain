# dev-registry-domain

### [Documentation](https://github.com/reTHINK-project/dev-registry-domain/tree/master/docs)


### How to run
Expires global variable defines the maximum amount of time (in seconds) a Hyperty stays in the server ([soft state issue](https://github.com/reTHINK-project/dev-registry-domain/issues/7)).

``` 
$ docker build -t domain-registry .
$ docker run -e STORAGE_TYPE=RAM -e EXPIRES=3600 -p 4568:4567 domain-registry
```
The Domain Registry can be run with another configurations, such as a multi-host database cluster. See the [user manual](https://github.com/reTHINK-project/dev-registry-domain/blob/database-integration/docs/DomainRegistryUserManual.md) for more information.

### How to run with HTTPS connections

In order to use HTTPS connections a keystore file ([more info](https://www.sslshopper.com/article-how-to-create-a-self-signed-certificate-using-java-keytool.html)) and its password are required. Create a keystore.jks inside server/cert, change the ENV variables as needed and run the following commands:

``` 
$ docker build -t domain-registry .
$ docker run -e STORAGE_TYPE=RAM -e EXPIRES=3600 -e KEYSTORE_PASSWORD=password -e KEYSTORE=keystore.jks -p 4568:4567 domain-registry
```

### Integration tests
Inside the server/specs folder run the following docker commands.

``` 
$ docker build -t domain-specs .
$ docker run -e SERVER=my.domain.server -e PORT=port domain-specs
```

### Connector installation
Install the Registry Connector module by adding the following dependency to the corresponding *package.json* file:
```
 "dev-registry-domain": "rethink-project/dev-registry-domain#R0.3.0"
```
or by executing the following command:
```
npm install rethink-project/dev-registry-domain#R0.3.0 --save
```

Then is possible to require it in the code. Example:
```
const RegistryConnector = require('dev-registry-domain/connector');
const registry = new RegistryConnector(config.registry.url);
```




