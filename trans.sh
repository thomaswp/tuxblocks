export JAVA=/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar
export PY=~/.m2/repository/com/samskivert/pythagoras/1.3.2/pythagoras-1.3.2.jar
export PLAYN=~/.m2/repository/com/googlecode/playn/playn-core/1.7/playn-core-1.7.jar
export REACT=~/.m2/repository/com/threerings/react/1.3.1/react-1.3.1.jar
export OOO=~/.m2/repository/com/threerings/tripleplay/1.7/tripleplay-1.7.jar

find core/src/main/java -type d -exec sh -c "j2objc -d objc2 -use-arc -classpath \"$PLAYN:$PY:$OOO:$REACT\" -sourcepath core/src/main/java {}/*" \;