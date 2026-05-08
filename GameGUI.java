import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameGUI {
    static Person player;
    static int year = 1;
    static Random rand = new Random();

    public static void main(String[] args) {
        // 1. CHARACTER CREATION POPUPS
        String playerName = JOptionPane.showInputDialog(null, "Enter your character's name:", "Welcome to LIFE", JOptionPane.QUESTION_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) playerName = "Player 1"; 

        String[] genderOptions = {"Male", "Female"};
        int genderChoice = JOptionPane.showOptionDialog(null, "Select your gender:", "Character Creation",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, genderOptions, genderOptions[0]);

        String imageFileName = (genderChoice == 0) ? "male.png" : "female.png";
        player = new Person(playerName, 22, 50000.0);

        // 2. WINDOW SETUP
        JFrame window = new JFrame("LIFE: Finance Simulator");
        window.setSize(450, 600); 
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout(10, 10)); 
        window.getContentPane().setBackground(Color.DARK_GRAY);

        // 3. IMAGE LOADING
        JLabel pictureLabel = new JLabel("", SwingConstants.CENTER);
        try {
            // Absolute path lookup to help Mac find the file
            String currentFolder = System.getProperty("user.dir");
            String fullPath = currentFolder + "/" + imageFileName;
            ImageIcon profilePic = new ImageIcon(fullPath);
            Image img = profilePic.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            pictureLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            pictureLabel.setText("Image Not Found");
            pictureLabel.setForeground(Color.LIGHT_GRAY);
        }

        // 4. CENTER STATS PANEL
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setOpaque(false);
        
        JLabel yearText = new JLabel("=== " + player.getName() + " - YEAR " + year + " ===", SwingConstants.CENTER);
        yearText.setForeground(Color.WHITE); 
        yearText.setFont(new Font("SansSerif", Font.BOLD, 22)); 

        JLabel statsText = new JLabel("Net Worth: $" + (int)player.getSavings(), SwingConstants.CENTER);
        statsText.setForeground(Color.GREEN); 
        statsText.setFont(new Font("SansSerif", Font.PLAIN, 20));
        
        centerPanel.add(yearText);
        centerPanel.add(statsText);

        // 5. BOTTOM BUTTON PANEL
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setOpaque(false);
        JButton saveButton = new JButton("Save (+$5k)");
        JButton investButton = new JButton("Invest (Gamble)");
        buttonPanel.add(saveButton);
        buttonPanel.add(investButton);

        // BUTTON ACTIONS
        saveButton.addActionListener(e -> runYear(window, yearText, statsText, saveButton, investButton, 5000.0, false));
        
        investButton.addActionListener(e -> {
            double change;
            // True 50/50 Gamble
            if (Math.random() < 0.5) {
                change = -((Math.random() * 20000) + 5000); // Loss
            } else {
                change = (Math.random() * 40000) + 5000; // Win
            }
            runYear(window, yearText, statsText, saveButton, investButton, change, true);
        });

        window.add(pictureLabel, BorderLayout.NORTH);
        window.add(centerPanel, BorderLayout.CENTER);
        window.add(buttonPanel, BorderLayout.SOUTH);
        window.setVisible(true);
    }

    // THIS METHOD HANDLES THE GAME LOGIC
    public static void runYear(JFrame win, JLabel yT, JLabel sT, JButton b1, JButton b2, double moneyChange, boolean isInvest) {
        if (year < 10) {
            player.updateSavings(moneyChange);
            
            // Show market result popup
            if (isInvest) {
                String msg = (moneyChange > 0) ? "📈 Market Win! Gained $" + (int)moneyChange : "📉 Market Crash! Lost $" + (int)Math.abs(moneyChange);
                JOptionPane.showMessageDialog(win, msg);
            }

            // --- RANDOM LIFE EVENT POOL (25% Chance) ---
            if (Math.random() < 0.25) {
                int eventPicker = rand.nextInt(4) + 1; 
                double impact = 0;
                String eventMsg = "";

                switch (eventPicker) {
                    case 1:
                        impact = -((Math.random() * 4000) + 1000);
                        eventMsg = "🚨 UNEXPECTED EVENT: Medical Bill! Cost: $" + (int)Math.abs(impact);
                        break;
                    case 2:
                        impact = ((Math.random() * 5000) + 2000);
                        eventMsg = "💰 UNEXPECTED EVENT: Tax Refund! You gained: $" + (int)impact;
                        break;
                    case 3:
                        impact = -((Math.random() * 2500) + 500);
                        eventMsg = "🚗 UNEXPECTED EVENT: Car Repair! Cost: $" + (int)Math.abs(impact);
                        break;
                    case 4:
                        impact = ((Math.random() * 1500) + 500);
                        eventMsg = "🎁 UNEXPECTED EVENT: Birthday Gift! You gained: $" + (int)impact;
                        break;
                }
                player.updateSavings(impact);
                JOptionPane.showMessageDialog(win, eventMsg);
            }

            year++;
            yT.setText("=== " + player.getName() + " - YEAR " + year + " ===");
            sT.setText("Net Worth: $" + (int)player.getSavings());

        } else {
            // END GAME STATE
            yT.setText("🎉 SIMULATION COMPLETE 🎉");
            sT.setText("Final Net Worth: $" + (int)player.getSavings());
            b1.setEnabled(false);
            b2.setEnabled(false);
            JOptionPane.showMessageDialog(win, "Game Over, " + player.getName() + "! Your final score: $" + (int)player.getSavings());
        }
    }
}