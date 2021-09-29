// BitOfFunPhone
//
// Brushing the dust off my Java
// Simple GUI app to mimic a phone - dialpad that plays 
// sounds when you press the keys
// All sound from https://freesound.org/ 
// V1 Sep 2021 - Mastered on GitHub

//package FunJava;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;

// @@@ Clean these out once working
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class BitOfFunPhone{
    ///////////////////////////////////////////////////
    // Global class variables

    ///////////////////////////////////////////////////
    // DialPadButton class
    // We will have 10 very similar dial pad buttons
    // so create a class, so we can listen to
    // each button separately
    // """ Note to self - could potentially extend JButton, rather than
    // having it as a property
    ///////////////////////////////////////////////////
    class DialPadButton extends JButton implements  ActionListener, LineListener{
        private Integer mButtonDigit;
        private Clip mAudioClip;
        boolean playCompleted;
        
        public DialPadButton(Integer newButtonDigit) {
            super(Integer.toString(newButtonDigit));
            mButtonDigit = newButtonDigit;
            
            // Load the audio clip.  We assume these
            // will be short/small, so pre-load for speed
            // of response, rather than streaming from file 
            // each time
            setupAudio();
            addActionListener(this);            
        }

        // Called when something happens to this button
        public void actionPerformed(ActionEvent e) {
            System.out.println("Something happened");
            // Make sure we always play from the start
            mAudioClip.setFramePosition(0);
            mAudioClip.start();
             
            while (!playCompleted) {
                // wait for the playback completes
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
             
           // Don't close until finished @@ for now.  How to close on shutdown??
//           mAudioClip.close();
        }
        
        // Method to load the audio clip for this button
        private void setupAudio() {
            String[] fileNames;
            String wavFileName;
            // This should be defined globally, for easier editing
            String soundDirName = "Sounds-numbers";
                
            // Find a *.wav file with a <thisDigit>_ prefix
            System.out.println("@@ setupAudio");
            File soundDirectory = new File("Sounds-numbers");        
            String namePrefix = Integer.toString(mButtonDigit) + "_";
            FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File f, String name) {
                        return (name.endsWith(".wav") && (name.startsWith(namePrefix)));
                    }
                };

            // There should 1, and only 1, audio file with the appropriate prefix.  
            // Ideally put more error handling in here, since we're dealing with external files
            fileNames = soundDirectory.list(filter);
            wavFileName = fileNames[0];
            System.out.println(wavFileName);
            
            // Windows-specific.  This should be updated so it's OS-independent
            File audioFile = new File(soundDirName + "/" + wavFileName);
     
            // @@@ testing.  For moment play on creation.  Later play on button press
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);     
                mAudioClip = (Clip) AudioSystem.getLine(info);
     
     // @@@ currently don't care when audio finishes
                mAudioClip.addLineListener(this);
                mAudioClip.open(audioStream);
                 
                System.out.println("Ready to start clip");
                 
            } catch (UnsupportedAudioFileException ex) {
                System.out.println("The specified audio file is not supported.");
                ex.printStackTrace();
            } catch (LineUnavailableException ex) {
                System.out.println("Audio line for playing back is unavailable.");
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println("Error playing the audio file.");
                ex.printStackTrace();
            }
        }

        /**
         * Listens to the START and STOP events of the audio line.
         // @@@ May move to inner class as part of button press
         */
        @Override
        public void update(LineEvent event) {
            LineEvent.Type type = event.getType();
             
            if (type == LineEvent.Type.START) {
                System.out.println("Playback started.");
                 
            } else if (type == LineEvent.Type.STOP) {
                playCompleted = true;
                System.out.println("Playback completed.");
            } 
        }
    }


    public JPanel createContentPane (){

        // We create a bottom JPanel to place everything on.
        JPanel totalPhonePanel = new JPanel();
        totalPhonePanel.setLayout(null);

        // Set up the dialpad with a similar layout to the 
        // dialpad on my phone, so 1->9 
        // in a grid and 0 at the bottom middle
        JPanel dialPadPanel = new JPanel();
        dialPadPanel.setLayout(new GridLayout(4,3,10,10));
        dialPadPanel.setSize(500,500);
        
        for (int ii = 1; ii <= 9; ii++){
            dialPadPanel.add(new DialPadButton(ii));
        }
        
        dialPadPanel.add(new DialPadButton(0));
        
        totalPhonePanel.add(dialPadPanel);
        totalPhonePanel.setOpaque(true);
        return totalPhonePanel;
    }


    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Bit Of Fun Phone");

        //Create and set up the content pane.
        BitOfFunPhone myPhone = new BitOfFunPhone();
        frame.setContentPane(myPhone.createContentPane());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(250, 300);
        frame.setVisible(true);
    }

    public static void main(String[] args) {        
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}