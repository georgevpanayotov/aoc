#!/bin/zsh

curr=$(basename $PWD)

if [[ "$curr[$#curr]" != "1" ]]; then
    echo "Must be called from the part 1 dir."
    exit
fi

curr[$#curr]=2

cp_it() {
    echo "copying $1"
    if [[ ! -e ../$curr/$1 ]]; then
        cp ./$1 ../$curr/
    fi
}

mkdir -p ../$curr

cp_it ./input
cp_it ./sample_input
cp_it ./solve.kt
cp_it ./Makefile

git add ../$curr/solve.kt
git add ../$curr/Makefile
