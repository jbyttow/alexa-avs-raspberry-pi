/*
 * Copyright 1999-2004 Carnegie Mellon University.  
 * Portions Copyright 2004 Sun Microsystems, Inc.  
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.api;

import java.io.InputStream;
import javax.sound.sampled.*;

import com.amazon.alexa.avs.MicrophoneLineFactory;

/**
 * InputStream adapter
 */
public class Microphone {

    private TargetDataLine line;
    private InputStream inputStream;
    private AudioFormat audioFormat;
    private MicrophoneLineFactory microphoneLineFactory;

    public Microphone(
            float sampleRate,
            int sampleSize,
            boolean signed,
            boolean bigEndian) {
        audioFormat = new AudioFormat(
                            sampleRate,
                            sampleSize,
                            1,
                            signed,
                            bigEndian);
        microphoneLineFactory = new MicrophoneLineFactory();
    }

    public void openInputStream() {
        System.out.println("in output stream");
        try {
            line = AudioSystem.getTargetDataLine(audioFormat);
            System.out.println(line);
            // attempt to close the line before we open, this is due to resource contention
            stopRecording();
            line.open();
        } catch (LineUnavailableException e) {
            System.out.println("THROWING LINE NOT AVAIL INPUT STREAM");
            throw new IllegalStateException(e);
        }
        inputStream = new AudioInputStream(line);
        System.out.println("AUDIO STREAM OPEN");
    }

    public void startRecording() {
        System.out.println("START RECORDING MICROPHONE");
        System.out.println(line);
        line.start();
    }

    public void stopRecording() {
        line.stop();
        line.close();
        line = null;
    }

    public InputStream getStream() {
        return inputStream;
    }
}
