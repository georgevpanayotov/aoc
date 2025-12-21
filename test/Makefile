build:
	kotlinc -cp ../libs/junit-platform-console-standalone-1.11.4.jar:../libs/kotlin-test-2.0.21.jar test.kt BoundedShapeTest.kt ../utils/*.kt -d bin

run:
	kotlin -cp bin TestKt < input

runStop:
	kotlin -cp bin TestKt stop < input_stop

runLimit:
	kotlin -cp bin TestKt limit 5 < input_stop

test: build run-test

run-test:
	kotlin -cp bin:../libs/junit-platform-console-standalone-1.11.4.jar:../libs/kotlin-test-2.0.21.jar org.junit.platform.console.ConsoleLauncher execute --scan-classpath
