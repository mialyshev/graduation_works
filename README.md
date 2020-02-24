1)git clone
2)mvn compile
3)mvn exec:java -Dexec.mainClass=programs.Main -Dexec.args="type here your repository link"
for example:
mvn exec:java -Dexec.mainClass=programs.Main -Dexec.args="https://github.com/Maratjimbo/webservice"