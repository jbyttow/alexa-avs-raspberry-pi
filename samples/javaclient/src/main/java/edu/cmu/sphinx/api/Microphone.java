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
        try {
            stopRecording();
            line = AudioSystem.getTargetDataLine(audioFormat);
            // attempt to close the line before we open, this is due to resource contention            
            line.open();
        } catch (LineUnavailableException e) {
            throw new IllegalStateException(e);
        }
        inputStream = new AudioInputStream(line);
    }

    public void startRecording() {
        line.start();
    }

    public void stopRecording() {
        if (line == null) {
            return;
        }
        line.stop();
        line.close();
    }

    public InputStream getStream() {
        return inputStream;
    }
}
