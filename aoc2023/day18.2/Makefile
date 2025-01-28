build:
	kotlinc -cp ../../libs/junit-platform-console-standalone-1.11.4.jar:../../libs/kotlin-test-2.0.21.jar test.kt solve.kt ../../utils/*.kt -d bin

run:
	kotlin -cp bin SolveKt < input

sample:
	kotlin -cp bin SolveKt < sample_input

test: build run-test

run-test:
	kotlin -cp bin:../../libs/junit-platform-console-standalone-1.11.4.jar:../../libs/kotlin-test-2.0.21.jar org.junit.platform.console.ConsoleLauncher execute --scan-classpath
