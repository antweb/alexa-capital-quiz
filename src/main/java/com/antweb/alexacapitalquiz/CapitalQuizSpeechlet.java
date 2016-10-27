package com.antweb.alexacapitalquiz;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CapitalQuizSpeechlet implements Speechlet {

    private final int NUM_COUNTRIES = 9;

    private static final String[] countries = {
            "Albania",
            "Andorra",
            "Armenia",
            "Austria",
            "Azerbaijan",
            "Belarus",
            "Belgium",
            "Bosnia and Herzegovina",
            "Bulgaria"
    };

    private static final Map<String, String> capitals = new HashMap<String, String>();
    static {
        capitals.put("Albania", "Tirana");
        capitals.put("Andorra", "Andorra la Vella");
        capitals.put("Armenia", "Yerevan");
        capitals.put("Austria", "Vienna");
        capitals.put("Azerbaijan", "Baku");
        capitals.put("Belarus", "Minsk");
        capitals.put("Belgium", "Brussels");
        capitals.put("Bosnia and Herzegovina", "Sarajevo");
        capitals.put("Bulgaria", "Sofia");
    }

    @Override
    public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {
    }

    @Override
    public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
        String text = "During this quiz, I will name a series of countries" +
                        "for which you will have to guess the capital city. " +
                        "Whenever you're ready, say start.";
        PlainTextOutputSpeech response = new PlainTextOutputSpeech();
        response.setText(text);

        // Reprompt
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Just say start when you are ready");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(response, reprompt);
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
        Intent intent = intentRequest.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("StartIntent".equals(intentName)) {
            return getStartResponse(intentRequest, session);
        } else if ("AnswerIntent".equals(intentName)) {
            return getAnswerResponse(intentRequest, session);
        } else {
            PlainTextOutputSpeech response = new PlainTextOutputSpeech();
            response.setText("Could you repeat that?");

            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(response);

            return SpeechletResponse.newAskResponse(response, reprompt);
        }
    }

    private SpeechletResponse getStartResponse(IntentRequest intentRequest, Session session) {
        String quesion = getQuestion(session);

        String ssml = "<speak>Okay. <break strength=\"strong\"/>";
        ssml += quesion + "</speak>";

        SsmlOutputSpeech response = new SsmlOutputSpeech();
        response.setSsml(ssml);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(response);

        return SpeechletResponse.newAskResponse(response, reprompt);
    }

    private SpeechletResponse getAnswerResponse(IntentRequest intentRequest, Session session) {
        String currentContry = (String)session.getAttribute("currentCountry");
        if (currentContry == null) {
            PlainTextOutputSpeech response = new PlainTextOutputSpeech();
            response.setText("You have to start a new game first");
            return SpeechletResponse.newTellResponse(response);
        }

        String capital = capitals.get(currentContry);

        System.out.println("currentCountry " + currentContry);
        System.out.println("capital " + capital);
        String answer = intentRequest.getIntent().getSlot("City").getValue();

        SsmlOutputSpeech response = new SsmlOutputSpeech();
        if (answer.equals(capital)) {
            incrementScore(session);

            String question = getQuestion(session);

            String ssml = "<speak>Correct. The capital of " + currentContry + " is " + capital + ".";
            ssml += "<break strength=\"medium\"/> Next question. " + question + "</speak>";
            response.setSsml(ssml);

            SsmlOutputSpeech repromptOutput = new SsmlOutputSpeech();
            repromptOutput.setSsml("<speak>" + question + "</speak>");
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptOutput);

            return SpeechletResponse.newAskResponse(response, reprompt);
        } else {
            String ssml = "<speak>That's not right. The capital of " + currentContry + " is " + capital + ", " +
                    "not " + answer + ". Your score was " + getScore(session) + ".</speak>";
            response.setSsml(ssml);
            return SpeechletResponse.newTellResponse(response);
        }
    }

    private String getQuestion(Session session) {
        Random rng = new Random();
        int random = rng.nextInt(NUM_COUNTRIES);
        String country = countries[random];
        String capital = capitals.get(country);

        session.setAttribute("currentCountry", country);
        String ssml = "What is the capital of " + country + "?";
        return ssml;
    }

    private void incrementScore(Session session) {
        Object oldScore = session.getAttribute("score");
        if (oldScore == null) {
            session.setAttribute("score", 1);
        } else {
            session.setAttribute("score", ((Integer) oldScore) + 1);
        }
    }

    private String getScore(Session session) {
        Object score = session.getAttribute("score");
        if (score == null) {
            return "0";
        } else {
            return score.toString();
        }
    }
}
