# jira-issue-crawler
A simple jira issue crawler which given the issue key and jira backend url, the crawler will try to 
get issue details and export it as a CSV output.

# Build Instructions
To build and run this project, you need to have JDK 1.8 and maven installed 
on your computer.

Simply execute: 

`mvn clean package`

in a command line to build the artifact.

After building the artifact successfully, go to target directory and
run:

`java -jar jira-issue-tracker-0.0.1-jar-with-dependencies.jar {jira/backend/url}`

Note that if you don't specify any jira backend url, a default
url will be used which is: https://issues.apache.org/jira


