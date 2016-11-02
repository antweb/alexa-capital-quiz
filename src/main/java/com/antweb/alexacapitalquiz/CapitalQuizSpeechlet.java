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

    private final int NUM_COUNTRIES = 51;

    private static final String[] countries = {
            "Albania",
            "Andorra",
            "Armenia",
            "Austria",
            "Azerbaijan",
            "Belarus",
            "Belgium",
            "Bosnia and Herzegovina",
            "Bulgaria",
            "Croatia",
            "Cyprus",
            "Czech Republic",
            "Denmark",
            "Estonia",
            "Finland",
            "France",
            "Georgia",
            "Germany",
            "Greece",
            "Hungary",
            "Iceland",
            "Ireland",
            "Italy",
            "Kazakhstan",
            "Kosovo",
            "Latvia",
            "Liechtenstein",
            "Lithuania",
            "Luxembourg",
            "Macedonia",
            "Malta",
            "Moldova",
            "Monaco",
            "Montenegro",
            "Netherlands",
            "Norway",
            "Poland",
            "Portugal",
            "Romania",
            "Russia",
            "San Marino",
            "Serbia",
            "Slovakia",
            "Slovenia",
            "Spain",
            "Sweden",
            "Switzerland",
            "Turkey",
            "Ukraine",
            "United Kingdom",
            "Vatican City"
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
        capitals.put("Croatia", "Zagreb");
        capitals.put("Cyprus", "Nicosia");
        capitals.put("Czech Republic", "Prague");
        capitals.put("Denmark", "Copenhagen");
        capitals.put("Estonia", "Tallinn");
        capitals.put("Finland", "Helsinki");
        capitals.put("France", "Paris");
        capitals.put("Georgia", "Tbilisi");
        capitals.put("Germany", "Berlin");
        capitals.put("Greece", "Athens");
        capitals.put("Hungary", "Budapest");
        capitals.put("Iceland", "Reykjavik");
        capitals.put("Ireland", "Dublin");
        capitals.put("Italy", "Rome");
        capitals.put("Kazakhstan", "Astana");
        capitals.put("Kosovo", "Pristina");
        capitals.put("Latvia", "Riga");
        capitals.put("Liechtenstein", "Vaduz");
        capitals.put("Lithuania", "Vilnius");
        capitals.put("Luxembourg", "Luxembourg");
        capitals.put("Macedonia", "Skopje");
        capitals.put("Malta", "Valletta");
        capitals.put("Moldova", "Chisinau");
        capitals.put("Monaco", "Monaco");
        capitals.put("Montenegro", "Podgorica");
        capitals.put("Netherlands", "Amsterdam");
        capitals.put("Norway", "Oslo");
        capitals.put("Poland", "Warsaw");
        capitals.put("Portugal", "Lisbon");
        capitals.put("Romania", "Bucharest");
        capitals.put("Russia", "Moscow");
        capitals.put("San Marino", "San Marino");
        capitals.put("Serbia", "Belgrade");
        capitals.put("Slovakia", "Bratislava");
        capitals.put("Slovenia", "Ljubljana");
        capitals.put("Spain", "Madrid");
        capitals.put("Sweden", "Stockholm");
        capitals.put("Switzerland", "Bern");
        capitals.put("Turkey", "Ankara");
        capitals.put("Ukraine", "Kyiv");
        capitals.put("United Kingdom", "London");
        capitals.put("Vatican City", "Vatican City");
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
                        "Ready when you are.";
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
        } else if ("AMAZON.StopIntent".equals(intentName)) {
            return getStopResponse();
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
            return getStopResponse();
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
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

    private SpeechletResponse getStopResponse() {
        PlainTextOutputSpeech response = new PlainTextOutputSpeech();
        response.setText("Okay, bye.");
        return SpeechletResponse.newTellResponse(response);
    }

    private SpeechletResponse getHelpResponse() {
        String text = "This is a quiz, in which I will name a series of countries" +
                "for which you will have to guess the capital city. " +
                "Whenever you're ready, just ask me to start a new game.";

        PlainTextOutputSpeech response = new PlainTextOutputSpeech();
        response.setText(text);

        String repromptText = "Whenever you're ready, just ask me to start a new game.";
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText(repromptText);
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(response, reprompt);
    }
}
