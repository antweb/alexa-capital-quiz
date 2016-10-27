package com.antweb.alexacapitalquiz;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

public class CapitalQuizSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<String>();
    static {
        supportedApplicationIds.add("amzn1.ask.skill.e2151429-dc0e-4b67-a812-6cbf4b3e9d4b");
    }

    public CapitalQuizSpeechletRequestStreamHandler() {
        super(new CapitalQuizSpeechlet(), supportedApplicationIds);
    }
}
