#!/bin/zsh

# Make the space for the graphs
mkdir -p graphs/

# Generate the graphs in dot format
make run

# Use graphviz to turn them into SVG files
find graphs/ -type f -iname \*.dot | while read i; do
  dot -Tsvg $i > ${i:s/\.dot/.svg}
done

# Launch SVGs in Firefox
open -a Firefox.app graphs/*.svg
