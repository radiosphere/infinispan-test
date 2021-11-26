#!/bin/sh

kill -15 `lsof -ti tcp:$1`
