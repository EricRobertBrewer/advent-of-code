all: solutions2015.x

solutions2015.x: solutions2015.o aoc.o
	gcc -g -o solutions2015.x solutions2015.o aoc.o -lcurl

solutions2015.o: solutions2015.h solutions2015.c aoc.h
	gcc -g -c solutions2015.c

aoc.o: aoc.h aoc.c
	gcc -g -c aoc.c

clean:
	rm -f *~ *# *.x *.o
