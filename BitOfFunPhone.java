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
        private Integer buttonDigit;
        private Clip audioClip;
        boolean playCompleted;
        
        public DialPadButton(Integer newButtonDigit) {
            super(Integer.toString(newButtonDigit));
            buttonDigit = newButtonDigit;
            
            // Load the audio clip.  We assume these
            // will be short/small, so pre-load for speed
            // of response, rather than streaming from file 
            // each time
            setupAudio();
            addActionListener(this);
            
        }

        // Called when something happens to a button, but No Op at the moment
        public void actionPerformed(ActionEvent e) {
            System.out.println("Something happened");
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
            String namePrefix = Integer.toString(buttonDigit) + "_";
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
     
                Clip audioClip = (Clip) AudioSystem.getLine(info);
     
     // @@@ currently don't care when audio finishes
                audioClip.addLineListener(this);
     
                audioClip.open(audioStream);
                 
                System.out.println("Ready to start clip");
                audioClip.start();
                 
                while (!playCompleted) {
                    // wait for the playback completes
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                 
               // Don't close until finished @@ for now
               audioClip.close();
                 
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

    // Definition of global values and items that are part of the GUI.
    // int redScoreAmount = 0;
    // int blueScoreAmount = 0;

    // JPanel titlePanel, scorePanel, buttonPanel;
    // JLabel redLabel, blueLabel, redScore, blueScore;
    // JButton redButton, blueButton, resetButton;

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
        
        for (int ii = 1; ii <= 2; ii++){
            dialPadPanel.add(new DialPadButton(ii));
        }
        
        dialPadPanel.add(new DialPadButton(0));
        
        totalPhonePanel.add(dialPadPanel);

        
        // buttonPanel = new JPanel();
        // buttonPanel.setLayout(null);
        // buttonPanel.setLocation(10, 80);
        // buttonPanel.setSize(260, 70);
        // totalGUI.add(buttonPanel);

        // // We create a button and manipulate it using the syntax we have
        // // used before. Now each button has an ActionListener which posts 
        // // its action out when the button is pressed.
        // redButton = new JButton("Red Score!");
        // redButton.setLocation(0, 0);
        // redButton.setSize(120, 30);
        // redButton.addActionListener(this);
        // buttonPanel.add(redButton);

        
        totalPhonePanel.setOpaque(true);
        return totalPhonePanel;
    }

    // Called when somethig happens to a button, but No Op at the moment
    public void actionPerformed(ActionEvent e) {
    }
    // // This is the new ActionPerformed Method.
    // // It catches any events with an ActionListener attached.
    // // Using an if statement, we can determine which button was pressed
    // // and change the appropriate values in our GUI.
    // public void actionPerformed(ActionEvent e) {
        // if(e.getSource() == redButton)
        // {
            // redScoreAmount = redScoreAmount + 1;
            // redScore.setText(""+redScoreAmount);
        // }
        // else if(e.getSource() == blueButton)
        // {
            // blueScoreAmount = blueScoreAmount + 1;
            // blueScore.setText(""+blueScoreAmount);
        // }
        // else if(e.getSource() == resetButton)
        // {
            // redScoreAmount = 0;
            // blueScoreAmount = 0;
            // redScore.setText(""+redScoreAmount);
            // blueScore.setText(""+blueScoreAmount);
        // }
    // }

    private static void createAndShowGUI() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Bit Of Fun Phone");

        //Create and set up the content pane.
        BitOfFunPhone myPhone = new BitOfFunPhone();
        frame.setContentPane(myPhone.createContentPane());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(280, 190);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // @@@ temp testing
        // String[] pathnames;
        // File soundDirectory = new File("Sounds-numbers");        
        // String sample = "2_";
        // FilenameFilter filter = new FilenameFilter() {
                // @Override
                // public boolean accept(File f, String name) {
// //                    return (name.endsWith(".wav") && (name.startsWith("1_")));
                    // return (name.endsWith(".wav") && (name.startsWith(sample)));
                // }
            // };

        // // This is how to apply the filter
        // pathnames = soundDirectory.list(filter);

        // // For each pathname in the pathnames array
        // for (String pathname : pathnames) {
            // // Print the names of files and directories
            // System.out.println(pathname);        
        // }
        
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}