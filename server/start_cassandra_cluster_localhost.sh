docker pull cassandra:latest
docker run -d --name cassandra-node1 -d cassandra:latest
sleep 60
for i in `seq 2 5`; do
  echo "Starting node cassandra-node"$i
  docker run -d --name "cassandra-node"$i -e CASSANDRA_SEEDS=$(docker inspect -f '{{ .NetworkSettings.IPAddress }}' cassandra-node1) cassandra:latest
  sleep 60
done
echo "Done. All 5 nodes started."
