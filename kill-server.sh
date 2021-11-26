#!/bin/sh

kill -9 `lsof -ti tcp:$1`
