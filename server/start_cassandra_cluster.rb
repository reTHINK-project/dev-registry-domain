puts "This tool starts a Cassandra DB cluster with predefined servers"

nodes_ips = []
remote_username = ARGV.first
raise ArgumentError, 'Minimum is two servers' if ARGV[1..-1].size < 2
ARGV[1..-1].each { |node| p node ; nodes_ips << node }

number_of_nodes = nodes_ips.size
number_of_nodes > 3 ? number_of_seeds = number_of_nodes / 2 : number_of_seeds = 1

#stop and removing existing containers
nodes_ips.each do |node|
  cmd = "ssh #{remote_username}@#{node} 'docker rm -f $(docker ps -a -q)'"
  system(cmd)
end

#init seed nodes
seeds = nodes_ips.sample(number_of_seeds)
seeds.each do |node|
  cmd = "ssh #{remote_username}@#{node} docker run --name node#{nodes_ips.index(node)} "\
        "-d -e CASSANDRA_BROADCAST_ADDRESS=#{node} -e CASSANDRA_ENDPOINT_SNITCH='GossipingPropertyFileSnitch' "\
        "-p 7000:7000 -p 9042:9042 cassandra:latest"
  system(cmd)
end

#init remaining nodes
(nodes_ips - seeds).each do |node|
  sleep(120)
  cmd = "ssh #{remote_username}@#{node} docker run --name node#{nodes_ips.index(node)} "\
    "-d -e CASSANDRA_BROADCAST_ADDRESS=#{node} -e CASSANDRA_ENDPOINT_SNITCH='GossipingPropertyFileSnitch' "\
    "-p 7000:7000 -p 9042:9042 -e CASSANDRA_SEEDS=#{seeds.join(",")} cassandra:latest"
  system(cmd)
end

sleep(100)
p "DONE all #{number_of_nodes} nodes up and running"
p "All done."
