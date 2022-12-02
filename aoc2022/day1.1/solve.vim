" Add a blank line at the front of the input
normal gg
normal O

" Insert a `0` line before each elf's section.
g/^$/normal o0

" Add a blank line at the end. This helps the } command to make sure it fully selects each block.
normal G
normal o

" Add a `+` to the front of each snack's line (omit the leading 0).
g/^0$/normal }kI +D

" Join each section (including the leading 0) into a single line so 0 + cal + cal + cal
g/^$/normal j0i0}kJ

" Pipe each non empty line to bc to calculate the sum.
v/^$/.!bc -ql

" Sort input, reverse, and grab first item.
%!sort -n | tac | head -n 1
