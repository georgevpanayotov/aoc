#!/bin/zsh

do_init() {
    mkdir -p $1
    cp template/Makefile $1/
    cp template/solve.kt $1/
    touch $1/input
    touch $1/sample_input
}

if [[ "$1" == "" ]]; then
    echo "No argument"
    return 1
fi

echo "Initing and tracking $1"
do_init $1
git add $1/*
shift

while [[ "$1" != "" ]]; do
    echo "Initing $1"
    do_init $1

    shift
done
