#!/usr/bin/env bash


NAME="smithy-translate"
HERE="$(pwd)"

if [ ! "$(basename "${HERE}")" = "${NAME}" ]; then echo "Run this script from the ${NAME} project root." && exit 1; fi

docker build -t "${NAME}" .
