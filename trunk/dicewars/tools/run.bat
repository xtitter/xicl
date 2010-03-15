set CLASSPATH=%CLASSPATH%;./dicewars.jar;./dicewars-client.jar;./asm-3.1.jar;./classmexer.jar
set JPDA_TRANSPORT=dt_socket
set JPDA_ADDRESS=8000
set JPDA_SUSPEND=n
set JPDA_OPTS=-agentlib:jdwp=transport=%JPDA_TRANSPORT%,address=%JPDA_ADDRESS%,server=y,suspend=%JPDA_SUSPEND%

set JAVA_OPTS=-Xmx256m -Xms64m -javaagent:classmexer.jar

%JAVA_HOME%\bin\java %JAVA_OPTS% %JPDA_OPTS% ru.icl.dicewars.MainJFrame