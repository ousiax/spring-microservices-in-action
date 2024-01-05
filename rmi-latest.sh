#!/bin/sh

artifact_id=$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
images=$(docker images | grep $artifact_id | grep latest | awk '{print $1":"$2}')
for image in $images; do
  docker rmi -f $image
done
