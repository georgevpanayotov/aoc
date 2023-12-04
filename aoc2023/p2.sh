#!/bin/zsh

curr=$(basename $PWD)
curr[$#curr]=2

cp_it() {
    echo "copying $1"
    cp ./$1 ../$curr/
}

cp_it ./input
cp_it ./sample_input
cp_it ./solve.kt

git add ../$curr
