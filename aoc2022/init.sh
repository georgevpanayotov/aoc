#!/bin/zsh

while [[ "$1" != "" ]]; do
    cp template/Makefile $1/
    cp template/solve.kt $1/

    shift
done
