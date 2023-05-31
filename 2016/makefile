all: solutions.x

solutions.x: solutions.o aoc.o cs.o
	g++ -g -o solutions.x solutions.o aoc.o cs.o -lcurl

solutions.o: solutions.cpp
	g++ -g -std=c++17 -c solutions.cpp

aoc.o: ../2015/aoc.h ../2015/aoc.c
	gcc -g -c ../2015/aoc.c

cs.o: cs.hpp cs.cpp
	c++ -g -c cs.cpp

clean:
	rm -f *.o *.x
