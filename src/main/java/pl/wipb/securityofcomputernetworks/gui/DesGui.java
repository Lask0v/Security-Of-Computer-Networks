package pl.wipb.securityofcomputernetworks.gui;

// Java program to create a blank text field and set BOLD font type

import pl.wipb.securityofcomputernetworks.algorithms.des.Des;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesGui extends JFrame implements ActionListener {
    // JTextField
    static JTextField inputWord;
    static JTextField inputKey;

    // JFrame
    static JFrame frame;

    // JButton
    static JButton buttonEncrypt;
    static JButton buttonDecrypt;

    // label to display text
    static JTextField outputWord;

    static JLabel output;
    static JLabel inputMessageLabel;
    static JLabel inputKeyLabel;

    static boolean isEncryptedAlready = false;
    static boolean isDecryptedAlready = false;

    // default constructor
    public DesGui() {
    }

    // main class
    public void main() {
        // create a new frame to store text field and button
        frame = new JFrame("DES");

        // create a label to display text
//        output = new JLabel("nothing enered");
        inputMessageLabel = new JLabel("INPUT MESSAGE HERE");
        inputKeyLabel = new JLabel("INPUT KEY HERE");

        // create a new button
        buttonEncrypt = new JButton("ENCRYPT");
        buttonDecrypt = new JButton("DECRYPT");

        // create a object of the text class
        DesGui te = new DesGui();

        // addActionListener to button
        buttonEncrypt.addActionListener(te);
        buttonDecrypt.addActionListener(te);

        // create a object of JTextField with 16 columns
        inputWord = new JTextField(16);
        outputWord = new JTextField(16);
        inputKey = new JTextField(16);

        // create an object of font type
        Font fo = new Font("Serif", Font.BOLD, 15);

        // set the font of the textfield
        inputKeyLabel.setFont(fo);
        inputMessageLabel.setFont(fo);
        inputWord.setFont(fo);
        inputKey.setFont(fo);
        outputWord.setFont(fo);
        buttonDecrypt.setFont(fo);
        buttonEncrypt.setFont(fo);

        // create a panel to add buttons and textfield
        JPanel p = new JPanel();
        outputWord.setVisible(false);
        JPanel outputPanel = new JPanel();
        outputPanel.add(outputWord);
        p.add(outputPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonEncrypt);
        buttonPanel.add(buttonDecrypt);

        JPanel inputPanel = new JPanel();
        JPanel inputMessagePanel = new JPanel();
        JPanel inputKeyPanel = new JPanel();
        inputMessagePanel.add(inputMessageLabel);
        inputMessagePanel.add(inputWord);
        inputKeyPanel.add(inputKeyLabel);
        inputKeyPanel.add(inputKey);
        inputPanel.add(inputMessagePanel);
        inputPanel.add(inputKeyPanel);
        inputPanel.setLayout(new FlowLayout());

        p.add(inputPanel);
        p.add(buttonPanel);

        // add panel to frame
        frame.add(p);

        // set the size of frame
        frame.setSize(400, 400);
        frame.pack();

        frame.show();
    }

    // if the button is pressed
    public void actionPerformed(ActionEvent e) {
        if (validateInputData(inputWord) && validateInputData(inputKey))
            if (e.getActionCommand().equals("ENCRYPT")) {
                inputWord.setText(Des.encrypt(inputWord.getText(), inputKey.getText()));
                buttonEncrypt.setEnabled(false);
                buttonDecrypt.setEnabled(true);

            } else if (e.getActionCommand().equals("DECRYPT")) {
                inputWord.setText(Des.decrypt(inputWord.getText(), inputKey.getText()));
                buttonEncrypt.setEnabled(true);
                buttonDecrypt.setEnabled(false);
            }
    }

    private boolean validateInputData(JTextField inputWord) {
        if (inputWord.getText().length() == 0) {
            JOptionPane.showMessageDialog(frame, "Fill the form");
            return false;
        }
        return true;
    }

    private static char[] HEX_CHARACTERS = new char[] {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
}
