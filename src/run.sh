javac -d ../bin *.java
#ulimit -t unlimited && nice -n 19 java -cp ../bin CuckooTabRandTest
#ulimit -t unlimited && nice -n 19 java -cp ../bin CuckooMultShiftRandTest
#ulimit -t unlimited && nice -n 19 java -cp ../bin LPModRandTest
#ulimit -t unlimited && nice -n 19 java -cp ../bin LPMultShiftRandTest
#ulimit -t unlimited && nice -n 19 java -cp ../bin LPTabRandTest
#ulimit -t unlimited && nice -n 19 java -cp ../bin LPMultShiftSeqTest
ulimit -t unlimited && nice -n 19 java -cp ../bin LPTabSeqTest
