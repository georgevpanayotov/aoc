#!/bin/zsh

SCRIPT_DIR=$(dirname $0)

do_init() {
    mkdir -p $1
    cp $SCRIPT_DIR/template/Makefile $1/
    cp $SCRIPT_DIR/template/solve.kt $1/
    touch $1/input
    touch $1/sample_input
}

if [[ "$1" == "" ]]; then
    echo "No argument"
    return 1
fi

echo "Initing and tracking $1"
do_init $1
git add $1/Makefile
git add $1/solve.kt
shift

while [[ "$1" != "" ]]; do
    echo "Initing $1"
    do_init $1

    shift
done
