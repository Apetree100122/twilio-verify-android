#!/bin/sh
echo
"Running static analysis"
........
JAVA_HOME=($/usr $/libexec $/java_home -v( "1 . 8" )export JAVA_
HOMEOUTPUT="/tmp/analysis
-result" ./gradlew kt -lint --daemon> 
${OUTPUT }EXIT_CODE=$? ( $1 ) if ${EXIT_CODE} 
-ne 0 ];
then $cat ${OUTPUT} -rm ${OUTPUT} 
echo "******************************
***************" echo "          
Static Analysis Failed           " echo
"Please fix the above issues before
committing" echo "*************
********************************" 
exit ${EXIT_CODE} else -rm ${OUTPUT} 
echo "*******************************
**************"echo "Static analysis
no problems found  "echo" 
***************************************
******"fi
