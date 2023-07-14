all: solutions.x

solutions.x: solutions.o aoc_c.o cs.o cs_c.o
	g++ -g -o solutions.x solutions.o aoc_c.o cs.o cs_c.o -lcurl

solutions.o: solutions.cpp
	g++ -g -std=c++17 -c solutions.cpp

aoc_c.o: ../2015/aoc.h ../2015/aoc.c
	gcc -g -c ../2015/aoc.c -o aoc_c.o

cs.o: cs.hpp cs.cpp
	g++ -g -std=c++11 -c cs.cpp

cs_c.o: ../2015/cs.h ../2015/cs.c
	gcc -g -c ../2015/cs.c -o cs_c.o

clean:
	rm -f *.o *.x
