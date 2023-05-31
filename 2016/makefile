all: solutions.x

solutions.x: solutions.o aoc.o
	g++ -g -o solutions.x solutions.o aoc.o -lcurl

solutions.o: solutions.cpp
	g++ -g -std=c++17 -c solutions.cpp

aoc.o: ../2015/aoc.h ../2015/aoc.c
	gcc -g -c ../2015/aoc.c

clean:
	rm -f *.o *.x
