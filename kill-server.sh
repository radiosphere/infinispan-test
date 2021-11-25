#!/bin/sh

kill `lsof -ti tcp:$1`
