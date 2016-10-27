# Build
You can build the project using maven:

```
mvn assembly:assembly -DdescriptorId=jar-with-dependencies
```

This will generate a JAR file that can be directly uploaded to AWS Lambda. Keep in mind that the SpeechletRequestStreamHandler requires an application ID for validation.

