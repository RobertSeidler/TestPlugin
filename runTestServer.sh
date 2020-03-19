mvn install

pluginName=$(ls target/*.jar | xargs -n 1 basename | tr "-" "\n" | sed -n 1p)

rm "docker/minecraft-server-plugins/${pluginName}.jar"

cp target/*.jar "docker/minecraft-server-plugins/${pluginName}.jar"

docker stop mc-test || echo "mc-test not started" 
docker rm mc-test || echo "mc-test doesnt exist"

docker run -v "${PWD}/docker/mc-server-data:/data" -v "${PWD}/docker/minecraft-server-plugins/:/plugins" -e TYPE=SPIGOT -e VERSION=1.12.2 -p 25565:25565 -e EULA=TRUE --name mc-test itzg/minecraft-server