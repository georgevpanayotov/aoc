all:
	kotlinc test.kt ../utils/*.kt -d bin

run:
	kotlin -cp bin TestKt < input

runStop:
	kotlin -cp bin TestKt stop < input_stop

runLimit:
	kotlin -cp bin TestKt limit 5 < input_stop
