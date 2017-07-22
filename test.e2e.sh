#! /bin/bash

set -u

jar_file=${JAR_FILE:-"./target/stream-sampler.jar"}

## test 1
size=200
echo "test 1: should print expected sample size: $size"
result=$(dd if=/dev/urandom count=10 bs=1MB 2> /dev/null | base64 | java -jar "$jar_file" $size | wc -m)
if (( size == result )); then
    echo "pass."
else
    echo "FAIL! Actual result is: $result"
fi

echo

## test 2
size=0
echo "test 2: should print expected sample size: $size"
result=$(dd if=/dev/urandom count=10 bs=1MB 2> /dev/null | base64 | java -jar "$jar_file" $size | wc -m)
if (( size == result )); then
    echo 'pass.'
else
    echo "FAIL! Actual result is: $result"
fi

echo

## test 3
echo "test 3: STDIN is empty string, doesn't error."
result=$(echo "" | java -jar "$jar_file" 100 | wc -m)
if (( $? == 0 )); then
    echo "pass."
else
    echo "FAIL! Actual result is: $result"
fi

echo

test 4
size=999999
echo "test 4: should handle big STDIN (13508775 bytes) and big sample size: $size"
result=$(dd if=/dev/urandom count=100 bs=1MB 2> /dev/null | base64 | java -jar "$jar_file" $size | wc -m)
if (( size == result )); then
    echo 'pass.'
else
    echo "FAIL! Actual result is: $result"
fi

echo