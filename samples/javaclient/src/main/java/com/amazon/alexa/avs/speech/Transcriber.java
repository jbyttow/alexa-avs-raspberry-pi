package com.amazon.alexa.avs.speech;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

import javax.sound.sampled.TargetDataLine;

import com.amazon.alexa.avs.AVSController;
import com.amazon.alexa.avs.MicrophoneLineFactory;

public class Transcriber {

    Configuration configuration;
    LiveSpeechRecognizer recognizer;
    private final ClassLoader resLoader;
    private TranscriberListener transcriberListener;
    private boolean transcriberEnabled = true;

    private static final String ACOUSTIC_MODEL = "res/en-us/";
    private static final String DICTIONARY_PATH = "res/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH = "res/dialog/";
    private static final String LANGUAGE_MODEL = "res/en-us.lm.bin";

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
        configuration.setGrammarName("start");

        recognizer = new LiveSpeechRecognizer(configuration);
    }

    public void startRecognition() throws Exception {
        System.out.println("transcriber: start recognition");
        System.out.println(recognizer);
        this.transcriberEnabled = true;

        //MicrophoneLineFactory microphoneLineFactory = new MicrophoneLineFactory();
        //TargetDataLine microphoneLine = microphoneLineFactory.getMicrophone();
        //microphoneLine.open();
        /*
        
        System.out.println("LINE STATUS");
        System.out.println(microphoneLine.isRunning());
        
        System.out.println(microphoneLine.isRunning());
        microphoneLine.close();

        TargetDataLine microphoneLine2 = microphoneLineFactory.getMicrophone();
        System.out.println("LINE STATUS 2");
        System.out.println(microphoneLine2.isRunning());
        */

        recognizer.startRecognition(true);

    
        // this needs to become it's own thread
        while (this.transcriberEnabled) {
            String utterance = recognizer.getResult().getHypothesis();
            System.out.println(utterance);
            if (utterance.equals("robot") || utterance.equals("meow")) {
                this.transcriberListener.onSuccessfulTrigger();
            }
        }
    }

    public void stopRecognition() {
        System.out.println("STOPPING RECOGNITION IN FUNCTION");
        this.transcriberEnabled = false;
        recognizer.stopRecognition();
        closeRecognitionLine();
    }

    // this is a hack workaround Sphinx4 not letting go of input lines
    // it has been addressed by the authors and fix is assumed to be coming
    // until then we force close out the mic inputs
    private void closeRecognitionLine() {
        System.out.println("CLOSING LINE");
        MicrophoneLineFactory microphoneLineFactory = new MicrophoneLineFactory();
        TargetDataLine microphoneLine = microphoneLineFactory.getMicrophone();
        microphoneLine.stop();
        microphoneLine.close();
    }
}
