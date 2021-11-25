#!/bin/sh

curl -XPOST localhost:$1/values\?key\=$2\&value\=$3
