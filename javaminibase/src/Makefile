#This Makefile is used to create assignments.

#the following macros should be updated according to
#the assignment to be generated

JARFILES=bufmgr/*.class diskmgr/*.class global/*.class iterator/*.class\
         heap/*.class chainexception/*.class  btree/*.class index/*.class tests/*.class

JDKPATH = /usr/java/jdk1.3.1_02
LIBPATH = .:..
CLASSPATH = $(LIBPATH)
BINPATH = $(JDKPATH)/bin
JAVAC = $(JDKPATH)/bin/javac -classpath $(CLASSPATH)
JAVA  = $(JDKPATH)/bin/java  -classpath $(CLASSPATH)

DOCFILES=bufmgr diskmgr global chainexception heap btree iterator index

##############  update the above for each assignment in making

ASSIGN=/home/candan/Courses/CSE510spr02/minjava/javaminibase/src
LIBDIR=$(ASSIGN)/lib
KEY=$(ASSIGN)/key
SRC=$(ASSIGN)/src

IMAGELINK=$(PACKAGEINDEX)/images
PACKAGEINDEX=$(ASSIGN)/javadoc

JAVADOC=javadoc -public -d $(PACKAGEINDEX)

git_modify = bufmgr/FIFO.java bufmgr/LIFO.java bufmgr/LRUK.java bufmgr/BufMgr.java tests/BMTest2020.java
### Generate jar and javadoc files.  Apply to most assignments.
db:
	make -C global
	make -C chainexception
	make -C btree
	make -C bufmgr
	make -C diskmgr
	make -C heap
	make -C index
	make -C iterator

doc:
	$(JAVADOC) $(DOCFILES)

test:
	cd tests; whoami; make bmtest2020;

clean:
	\rm -f $(CLASSPATH)/*.class *~ \#* core $(JARFILES) TRACE
git_a:
	git add $(git_modify)
