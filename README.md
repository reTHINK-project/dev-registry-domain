# dev-registry-domain

### [Documentation](https://github.com/reTHINK-project/dev-registry-domain/tree/master/docs)


### How to run
Expires global variable defines the maximum amount of time (in seconds) a Hyperty stays in the server ([soft state issue](https://github.com/reTHINK-project/dev-registry-domain/issues/7)).

``` 
docker build -t domain-registry .
docker run -e STORAGE_TYPE=RAM -e EXPIRES=3600 -p 4568:4567 domain-registry
```

### Integration tests
With ruby, rspec and Airborne (https://github.com/brooklynDev/airborne) installed.

``` 
HOST=server:port rspec integration_tests.rb
```


