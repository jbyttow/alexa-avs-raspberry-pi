package com.amazon.alexa.avs.speech;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

import javax.sound.sampled.TargetDataLine;

import com.amazon.alexa.avs.AVSController;
import com.amazon.alexa.avs.MicrophoneLineFactory;

public class Transcriber extends Thread {

    private Configuration configuration;
    private LiveSpeechRecognizer recognizer;
    private final ClassLoader resLoader;
    private TranscriberListener transcriberListener;
    private boolean transcriberEnabled = false;
    private List<String> triggerWords;
    private MicrophoneLineFactory microphoneLineFactory;

    private static final String ACOUSTIC_MODEL = "res/en-us/";
    private static final String DICTIONARY_PATH = "res/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH = "res/dialog/";
    private static final String LANGUAGE_MODEL = "res/en-us.lm.bin";
    private static final String GRAMMAR_NAME = "start";
    

    public Transcriber(final TranscriberListener listener) throws Exception {   
        this.transcriberListener = listener;

        configuration = new Configuration();
        resLoader = Thread.currentThread().getContextClassLoader();

        URL url = resLoader.getResource(GRAMMAR_PATH);
    
        configuration.setAcousticModelPath(resLoader.getResource(ACOUSTIC_MODEL).toString());
        configuration.setDictionaryPath(resLoader.getResource(DICTIONARY_PATH).toString());
        configuration.setLanguageModelPath(resLoader.getResource(LANGUAGE_MODEL).toString());
        configuration.setGrammarPath(resLoader.getResource(GRAMMAR_PATH).toString());
        configuration.setUseGrammar(true);
        configuration.setGrammarName(GRAMMAR_NAME);

        this.microphoneLineFactory = new MicrophoneLineFactory();
        recognizer = new LiveSpeechRecognizer(configuration);

        this.triggerWords = Arrays.asList("robot", "meow");
    }

    public void startRecognition() {
        System.out.println("starting recognition");
        this.transcriberEnabled = true;
        recognizer.startRecognition(true);
    }

    @Override
    public void run() {
        super.run();
        try {
            while (this.transcriberEnabled) {
                String utterance = recognizer.getResult().getHypothesis();
                System.out.println(utterance);
                for (String triggerWord : triggerWords) {
                    if (utterance.equals(triggerWord)) {
                        System.out.println(triggerWord);
                        this.transcriberListener.onSuccessfulTrigger();
                    }
                }
                Thread.sleep(1);
            }
        } catch (InterruptedException ex) {
            System.out.println(ex);
            // handle exception
        }
    }

    public void stopRecognition() {
        this.transcriberEnabled = false;
        recognizer.stopRecognition();
    }
}
