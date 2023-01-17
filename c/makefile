all: solutions2015.x

solutions2015.x: solutions2015.o aoc.o cs.o
	gcc -g -o solutions2015.x solutions2015.o aoc.o cs.o -lcurl

solutions2015.o: solutions2015.h solutions2015.c
	gcc -g -c solutions2015.c

aoc.o: aoc.h aoc.c
	gcc -g -c aoc.c

cs.o: cs.h cs.c
	gcc -g -c cs.c

clean:
	rm -f *~ *# *.x *.o
