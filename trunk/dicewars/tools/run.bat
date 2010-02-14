set CLASSPATH=%CLASSPATH%;./dicewars.jar;./dicewars-client.jar;asm-3.1.jar;./players
set JPDA_TRANSPORT=dt_socket
set JPDA_ADDRESS=8000
set JPDA_SUSPEND=n
set JPDA_OPTS=-agentlib:jdwp=transport=%JPDA_TRANSPORT%,address=%JPDA_ADDRESS%,server=y,suspend=%JPDA_SUSPEND%

java %JPDA_OPTS% ru.icl.dicewars.DiceWars