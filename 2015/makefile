all: solutions.x

solutions.x: solutions.o aoc.o cs.o
	gcc -g -o solutions.x solutions.o aoc.o cs.o -lcurl

solutions.o: solutions.h solutions.c
	gcc -g -c solutions.c

aoc.o: aoc.h aoc.c
	gcc -g -c aoc.c

cs.o: cs.h cs.c
	gcc -g -c cs.c

clean:
	rm -f *~ *# *.x *.o
