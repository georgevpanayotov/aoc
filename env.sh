#!/bin/zsh

if [[ ! -e init.sh ]]; then
    echo Must be called from the root dir.
    return
fi

if [[ $ZSH_EVAL_CONTEXT == 'toplevel' ]]; then
    echo Must be called via '`source env.sh`'
    exit
fi

OLD_PATH=$PATH
PATH=$PATH:$PWD

unenv() {
    PATH=$OLD_PATH
    unset OLD_PATH
    unfunction unenv
}
