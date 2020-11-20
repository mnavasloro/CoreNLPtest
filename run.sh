#!/bin/sh
sudo /usr/bin/java -jar /ixasrl/target/ixasrl-1.0.jar --server.port=8092 --server.use-forward-headers=true --server.servlet.context-path=/ixasrl
