set CLASSPATH=%CLASSPATH%;./dicewars.jar;./dicewars-client.jar;

set JAVA_OPTS=-Xmx256m -Xms64m 

"%JAVA_HOME%"\bin\java %JAVA_OPTS% ru.icl.dicewars.ConcatReplays %1 %2 %3 %4 %5 %6 %7 %8 %9