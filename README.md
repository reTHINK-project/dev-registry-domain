# dev-registry-domain

### [Documentation](https://github.com/reTHINK-project/dev-registry-domain/tree/master/docs)


### How to run

``` 
docker build -t domain-registry .
docker run -e STORAGE_TYPE=RAM -e EXPIRES=3600 -p 4568:4567 domain-registry
```

### Integration tests
With ruby, rspec and Airborne (https://github.com/brooklynDev/airborne) installed.

``` 
rspec integration_tests.rb
```


