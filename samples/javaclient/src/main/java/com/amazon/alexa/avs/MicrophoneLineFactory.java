/**
 * Copyright 2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * You may not use this file except in compliance with the License. A copy of the License is located the "LICENSE.txt"
 * file accompanying this source. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.amazon.alexa.avs;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneLineFactory {
    // get the system default microphone
    public TargetDataLine getMicrophone() {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        System.out.println("MIXER COUNT");
        System.out.println(mixers.length);
        for (Mixer.Info mixerInfo : mixers) {
            Mixer m = AudioSystem.getMixer(mixerInfo);
            try {
                m.open();
                m.close();
            } catch (Exception e) {
                System.out.println("MIXER THROUGH EXCEPTION");
                System.out.println(e);
                continue;
            }

            Line.Info[] lines = m.getTargetLineInfo();
            System.out.println("LINE COUNT");
            System.out.println(lines.length);
            for (Line.Info li : lines) {
                try {
                    TargetDataLine temp = (TargetDataLine) AudioSystem.getLine(li);
                    if (temp != null) {
                        return temp;
                    }
                } catch (Exception e) {
                    System.out.println("LINE EXCEPTION");
                }
            }
        }
        return null;
    }
}
