import javax.swing.*;
import java.awt.*;

public class GameGUI {
    // 1. Create your player globally so the buttons can see them!
    static Person player = new Person("Roen", 22, 50000.0);
    static int year = 1;

    public static void main(String[] args) {
        
        // 2. The Window (JFrame)
        JFrame window = new JFrame("LIFE: Finance Simulator");
        window.setSize(400, 300);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // We use a GridLayout to neatly stack 4 rows and 1 column
        window.setLayout(new GridLayout(4, 1)); 

        // 3. The Text (Labels)
        JLabel yearText = new JLabel("=== YEAR " + year + " ===", SwingConstants.CENTER);
        JLabel statsText = new JLabel("Net Worth: $" + player.getSavings(), SwingConstants.CENTER);

        // 4. The Buttons
        JButton saveButton = new JButton("Put Money in Savings");
        JButton investButton = new JButton("Invest in Stock Market");

        // 5. THE MAGIC: What happens when you click "Save"
        saveButton.addActionListener(e -> {
            if (year <= 10) {
                // Add fake money for now (Partner A will fix this math later!)
                player.updateSavings(5000.0); 
                year++;
                
                // Update the text on the screen
                yearText.setText("=== YEAR " + year + " ===");
                statsText.setText("Net Worth: $" + player.getSavings());
            } else {
                yearText.setText("🎉 GAME OVER 🎉");
                saveButton.setEnabled(false);
                investButton.setEnabled(false);
            }
        });

        // 6. Glue everything to the window
        window.add(yearText);
        window.add(statsText);
        window.add(saveButton);
        window.add(investButton);

        // Turn the lights on!
        window.setVisible(true);
    }
}