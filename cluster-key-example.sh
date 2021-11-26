#!/bin/sh

./kill-server.sh 8080 
./kill-server.sh 8081
./kill-server.sh 8082

./start-server.sh 8080 & > /dev/null
./start-server.sh 8081 & > /dev/null
./start-server.sh 8082 & > /dev/null

./set-key.sh 8080 hello 1
./set-key.sh 8080 hello 2
./set-key.sh 8081 hello 3
./set-key.sh 8082 hello 4

sleep(1)
echo "Now got hello set to `./get-key.sh 8082 hello`"

./kill-server.sh 8080
sleep(1)

./set-key.sh 8082 hello 4
echo "Now got hello set to `./get-key.sh 8082 hello`"
./kill-server.sh 8082
sleep(1)

./set-key.sh 8081 hello 5
echo "Now got hello set to `./get-key.sh 8081 hello`"
