/*
 * Lukas Krampitz
 * Apr 4, 2021
 * 
 */
package Audio;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Tacitor
 */
public class AudioRef {
    
    private static boolean playTurnBeep = true;

    /**
     * Play some audio from a URL file path
     */
    public static void playTurnBeep() {
        try {
            InputStream TURN_BEEP = (AudioRef.class.getResourceAsStream("turnBeep.wav"));

            InputStream bufferedStream; //add a buffer to the stream
            AudioInputStream stream; //the stream best used for playing audio

            //decorate it with a buffer
            bufferedStream = new BufferedInputStream(TURN_BEEP);

            //load it in
            stream = AudioSystem.getAudioInputStream(bufferedStream);

            Clip clip = AudioSystem.getClip();
            clip.open(stream);

            clip.start();

            //debug the turn idecator
            //System.out.println("boop");
        } catch (FileNotFoundException ex) {
            System.out.println("Sound File not found\n" + ex);
        } catch (IOException ex) {
            System.out.println("Sound File error\n" + ex);
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("Sound File is of wrong type\n" + ex);
        } catch (LineUnavailableException ex) {
            System.out.println("Sound File error: \n" + ex);
        }
    }
}
