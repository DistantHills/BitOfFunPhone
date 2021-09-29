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
    // DialPadButton() inner class
    // We'll create 1 for each dial pad button, so
    // we can keep the per-button processing together
    ///////////////////////////////////////////////////
    class DialPadButton extends JButton implements  ActionListener{
        private Integer mButtonDigit;
        private Clip mAudioClip;
        
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

        ///////////////////////////////////////////////////
        // setupAudio()
        // Internal method to access the audio file and 
        // get ready to play it
        // Returns - nothing
        ///////////////////////////////////////////////////
        private void setupAudio() {
            String[] fileNames;
            String wavFileName;
            // This should be defined globally, for easier editing
            String soundDirName = "Sounds-numbers";
                
            // Find a *.wav file with a <thisDigit>_ prefix
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
            
            // Windows-specific.  This should be updated so it's OS-independent
            File audioFile = new File(soundDirName + "/" + wavFileName);
     
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);     
                mAudioClip = (Clip) AudioSystem.getLine(info);     
                mAudioClip.open(audioStream);
                 
                System.out.println("Button " + mButtonDigit + " ready to play - " + wavFileName);
                 
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

        ///////////////////////////////////////////////////
        // actionPerformed()
        // For the EventListener interface, so we know 
        // when this button is pressed
        ///////////////////////////////////////////////////
        public void actionPerformed(ActionEvent e) {
            // Make sure we always play from the start
            mAudioClip.setFramePosition(0);
            
            // Note that this will create a new thread to 
            // play the audio, so it won't block processing on the
            // main thread, and multiple audio clips can play in parallel
            mAudioClip.start();
        }
        
        // @@@ may still want this - not clear if we need to clip.close() everytime we 
        // stop playing, or just at end of day
        // /**
         // * Listens to the START and STOP events of the audio line.
         // // @@@ May move to inner class as part of button press
         // */
        // @Override
        // public void update(LineEvent event) {
            // LineEvent.Type type = event.getType();
             
            // if (type == LineEvent.Type.START) {
                // System.out.println("Playback started.");
                 
            // } else if (type == LineEvent.Type.STOP) {
                // playCompleted = true;
                // System.out.println("Playback completed.");
            // } 
        // }

        ///////////////////////////////////////////////////
        // finalize() - has been deprecated.  So, we can't explicitly 
        // close the audio Clip, but will assume garbage collection will do so
        ///////////////////////////////////////////////////
    }


    ///////////////////////////////////////////////////
    // createContentPane()
    // Sets up the DialPad panel with buttons
    // Could potentially expand in future, e.g. with a 'screen'
    // and dial button, so put the dialpad on its own panel
    ///////////////////////////////////////////////////
    public JPanel createContentPane(){

        // We create a bottom JPanel to place everything on.
        JPanel totalPhonePanel = new JPanel();
        // Use GridLayout so the content expands to fill the space
        totalPhonePanel.setLayout(new GridLayout(1,1,10,10));

        // Set up the dialpad with a similar layout to the 
        // dialpad on my phone, so 1->9 
        // in a grid and 0 at the bottom middle
        JPanel dialPadPanel = new JPanel();
        // dialPadPanel.setLayout(new GridBagLayout());
        // GridBagConstraints cons = new GridBagConstraints();
        
        dialPadPanel.setLayout(new GridLayout(4,3,10,10));
        dialPadPanel.setSize(500,600);
        
        for (int ii = 1; ii <= 9; ii++){
            // cons.gridx = ii-1;
            // cons.gridy = 0;
            // cons.fill = GridBagConstraints.BOTH;
            // dialPadPanel.add(new DialPadButton(ii), cons);
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
        frame.setSize(500, 600);
        frame.pack();
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