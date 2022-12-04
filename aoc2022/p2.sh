#!/bin/zsh

curr=$(basename $PWD)
curr[$#curr]=2

cp ./input ../$curr/
cp ./solve.kt ../$curr/
