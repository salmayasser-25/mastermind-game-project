package com.mycompany.mastermindgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MasterMindGame {
    public static void main(String[] args) {
        new GameInterface();
    }
}

class Participant {
    private List<String> attemptsHistory;

    public Participant() {
        attemptsHistory = new ArrayList<>();
    }

    public void recordAttempt(String attempt) {
        attemptsHistory.add(attempt);
    }

    public List<String> getAttemptsHistory() {
        return attemptsHistory;
    }
}

class Hint {
    private int exactMatches;
    private int partialMatches;
    private List<Character> positionHints;

    public Hint(int exactMatches, int partialMatches, List<Character> positionHints) {
        this.exactMatches = exactMatches;
        this.partialMatches = partialMatches;
        this.positionHints = positionHints;
    }

    public int getExactMatches() {
        return exactMatches;
    }

    public int getPartialMatches() {
        return partialMatches;
    }

    public List<Character> getPositionHints() {
        return positionHints;
    }

    @Override
    public String toString() {
        return exactMatches + " exact matches, " + partialMatches + " partial matches. Position hints: " + positionHints;
    }
}

class GameInterface extends JFrame {
    private static final char[] COLOR_OPTIONS = {'R', 'G', 'B', 'Y', 'O', 'P'};
    private char[] hiddenCode;
    private int remainingTries;
    private JTextField userInput;
    private JTextArea resultDisplay;
    private Participant participant;

    public GameInterface() {
        setTitle("Mastermind Game");
        setSize(500, 400); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        hiddenCode = generateHiddenCode();
        remainingTries = 6;
        participant = new Participant();

        
        JLabel promptLabel = new JLabel("Enter your guess (4 colors): R, G, B, Y, O, P");
        promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 16));  

        
        JPanel inputPanel = new JPanel();
        userInput = new JTextField(4);
        userInput.setFont(new Font("Arial", Font.PLAIN, 16));  
        JButton submitButton = new JButton("Submit Guess");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 16));  
        inputPanel.add(userInput);
        inputPanel.add(submitButton);

        
        resultDisplay = new JTextArea(10, 30);
        resultDisplay.setEditable(false);
        resultDisplay.setFont(new Font("Arial", Font.PLAIN, 16)); 
        JScrollPane scrollPane = new JScrollPane(resultDisplay);

        
        add(promptLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = userInput.getText().toUpperCase();

                if (input.length() != 4) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter exactly 4 colors.");
                    return;
                }

                for (char color : input.toCharArray()) {
                    boolean isValidColor = false;
                    for (char validColor : COLOR_OPTIONS) {
                        if (color == validColor) {
                            isValidColor = true;
                            break;
                        }
                    }
                    if (!isValidColor) {
                        JOptionPane.showMessageDialog(null, "Invalid color entered. Please use only: R, G, B, Y, O, P.");
                        return;
                    }
                }

                char[] guess = input.toCharArray();
                Hint hint = evaluateGuess(guess);
                resultDisplay.append("Guess: " + input + " -> " + hint + "\n");
                participant.recordAttempt(input);

                if (hint.getExactMatches() == 4) {
                    JOptionPane.showMessageDialog(null, "Congratulations! You guessed the correct code.");
                    resetGame();
                    return;
                }

                remainingTries--;
                if (remainingTries <= 0) {
                    JOptionPane.showMessageDialog(null, "Game over! The secret code was: " + new String(hiddenCode));
                    resetGame();
                }
            }
        });

        setVisible(true);
    }

    private char[] generateHiddenCode() {
        Random random = new Random();
        char[] code = new char[4];
        for (int i = 0; i < 4; i++) {
            code[i] = COLOR_OPTIONS[random.nextInt(COLOR_OPTIONS.length)];
        }
        return code;
    }

    private Hint evaluateGuess(char[] guess) {
        int exactMatches = 0;
        int partialMatches = 0;
        List<Character> positionHints = new ArrayList<>();

        boolean[] codeUsed = new boolean[4];
        boolean[] guessUsed = new boolean[4];

        for (int i = 0; i < 4; i++) {
            if (guess[i] == hiddenCode[i]) {
                exactMatches++;
                positionHints.add(guess[i]);
                codeUsed[i] = true;
                guessUsed[i] = true;
            } else {
                positionHints.add('-');
            }
        }

        for (int i = 0; i < 4; i++) {
            if (!guessUsed[i]) {
                for (int j = 0; j < 4; j++) {
                    if (!codeUsed[j] && guess[i] == hiddenCode[j]) {
                        partialMatches++;
                        codeUsed[j] = true;
                        break;
                    }
                }
            }
        }

        return new Hint(exactMatches, partialMatches, positionHints);
    }

    private void resetGame() {
        hiddenCode = generateHiddenCode();
        remainingTries = 6;
        participant = new Participant();
        resultDisplay.setText("");
        userInput.setText("");
    }
}
