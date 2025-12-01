#compdef init.sh

# Completions for the init script.

# Add completions if a particular year has been chosen.
compyear() {
    matching_year=$1

    if [[ -e $matching_year ]]; then
        local last_word=$words[$#words]
        find $matching_year -maxdepth 1 -type d | grep '^.*\/day\(\d\+.\d\+\)$' | sed -E 's/^.*day([0-9]+)\.[0-9]+$/\1/' | while read i; do
            # We only init the first part of the day's puzzle so ditch the ".2" suffix.
            # Part2 will be handled by the p2.sh script.
            compadd $matching_year/day$i.1
            if (( i < 25 )) ; then
                compadd $matching_year/day$((i + 1)).1
            fi
        done
    else
        compadd $matching_year/day1.1
    fi
}

if [[ "$PWD" == "$AOC_PATH" ]]; then
    # In the root path we match the years we might be doing.
    local -a years
    find . -maxdepth 1 -type d | grep '^\.\/aoc\(\d\+\)$' | sed -E 's/^.*aoc([0-9]+).*$/\1/' | while read i; do
        years+=aoc$i
        # Include +1 because we might be starting a new year. Init.sh will create any dirs it needs.
        years+=aoc$((i + 1))
    done

    if [[ $#words -eq 2 ]]; then
        # Check if the `word` starts with something from `years`
        local matching_year=""
        for year in $years; do
            local word=$words[2]
            if [[ word[(i)$year] -eq 1 ]]; then
                matching_year=$year
            fi
        done

        if [[ $matching_year == "" ]]; then
            # We haven't selected a year yet so offer the years.
            compadd $years
        else
            # We have selected a year so offer its days.
            compyear $matching_year
        fi
    else
        _path_files
    fi
elif [[ "$(realpath "$PWD"/..)" == "$AOC_PATH" ]]; then
    # PWD implies the year is selected.
    compyear $(basename $PWD)
else
fi
