domain_is_running=$(docker inspect --format="{{ .State.Running }}" domain 2> /dev/null)

if [ "$domain_is_running" ] ; then 
  docker rm -f domain
  echo 'Domain container removed.'
fi

docker build -t domain-registry .
docker run --name domain -d -e STORAGE_TYPE=RAM -e EXPIRES=3600 -e DOMAIN_ENV=DEVELOPMENT -p 4568:4567 domain-registry
docker logs -f domain

