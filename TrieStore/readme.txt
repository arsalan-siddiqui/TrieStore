Pre requirements
1. Java8
2. Maven

Instructions
1. open cmd
2. Go into the project directory where pom.xml file exist.
3. run command "mvn install compile"
4. Then paste: mvn exec:java -Dexec.mainClass=com.wirestorm.triestore.TrieStore -Dexec.args="full path of your file that contains commands"

like:
mvn exec:java -Dexec.mainClass=com.wirestorm.triestore.TrieStore -Dexec.args="dir\fileName.txt"

sample command:
mvn exec:java -Dexec.mainClass=com.wirestorm.triestore.TrieStore -Dexec.args="command.txt"
