#!/bin/zsh

while [[ "$1" != "" ]]; do
    echo "Initing $1"

    cp template/Makefile $1/
    cp template/solve.kt $1/
    touch $1/input
    touch $1/sample_input

    shift
done
