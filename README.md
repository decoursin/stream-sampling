# stream-sampling

### Instructions

See [INSTRUCTIONS.pdf](INSTRUCTIONS.pdf)

### Assumptions

* I assumed computation is being carried out on a computer with an SSD disk, rather than a hard disk.
* I assume the random sample strategy can be done 'with replacement'.

### Run it

1) package it: `mvn package` 
2) run it: `dd if=/dev/urandom count=10 bs=1MB 2> /dev/null | base64 | java -jar ./target/stream-sampler.jar 8` OR `echo "" | java -jar ./target/stream-sampler.jar 8`. The latter works because it's coded to test for an empty STDIN to generate its own character; without the `echo "" | ..`, it just hangs waiting for STDIN.

### Testing

There's two different tests:
* `./test.e2e.sh`, which is just basic e2e testing.
* `mvn test`, which tests more accurately correctability.
