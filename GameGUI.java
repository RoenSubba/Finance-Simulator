import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class GameGUI extends JFrame {

    private static final String WINDOW_TITLE  = "Life & Finance Simulator";
    private static final int    WINDOW_WIDTH  = 900;
    private static final int    WINDOW_HEIGHT = 600;
    private static final String SHEET_PATH    = "assets/sheet.png"; // The single master image

    private JLabel labelName;
    private JLabel labelAge;
    private JLabel labelNetWorth;
    private JLabel labelCashFlow; 
    private JPanel actionPanel;
    private JPanel timeControlPanel; 

    private Person player;
    private BufferedImage masterSheet;

    public GameGUI(String playerName) {
        this.player = new Person(playerName);

        // Pre-load the master icon sheet safely
        try {
            File file = new File(SHEET_PATH);
            if (file.exists()) {
                masterSheet = ImageIO.read(file);
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load master sheet.png from assets/ folder.");
        }

        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        buildFrame();
        updateActionPanel(player.getAge());
        setVisible(true);
    }

    private void buildFrame() {
        setLayout(new BorderLayout(10, 10));
        add(buildStatsPanel(),       BorderLayout.NORTH);
        add(buildActionPanelShell(), BorderLayout.CENTER);
        
        timeControlPanel = buildTimeControlPanel();
        add(timeControlPanel, BorderLayout.SOUTH);
    }

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Player Stats"));
        panel.setBackground(new Color(30, 30, 50));

        labelName     = makeStatLabel("Name: "      + player.getName());
        labelAge      = makeStatLabel("Age: "       + player.getAge());
        labelNetWorth = makeStatLabel("Net Worth: $" + (int)player.getNetWorth());
        labelCashFlow = makeStatLabel("Cash Flow: $" + (int)GameEngine.getAnnualCashFlow(player) + "/yr");

        panel.add(labelName);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(labelAge);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(labelNetWorth);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(labelCashFlow);

        return panel;
    }

    private JLabel makeStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.BOLD, 15));
        return label;
    }

    private JPanel buildActionPanelShell() {
        actionPanel = new JPanel(new GridBagLayout()); 
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionPanel.setBackground(new Color(20, 20, 40));
        return actionPanel;
    }

    private JPanel buildTimeControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Time Controls"));
        panel.setBackground(new Color(30, 30, 50));

        JButton btnSim1  = makeIconButton("Simulate 1 Year",  new Color(150, 200, 150));
        JButton btnSim10 = makeIconButton("Simulate 10 Years", new Color(150, 170, 220));

        btnSim1.addActionListener(e -> { GameEngine.simulateOneYear(player); refreshAll(); });
        btnSim10.addActionListener(e -> { GameEngine.simulateTenYears(player); refreshAll(); });

        panel.add(btnSim1);
        panel.add(btnSim10);
        return panel;
    }

    public void updateActionPanel(int age) {
        actionPanel.removeAll();
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonRow.setOpaque(false);

        // Row/Col maps directly to where the icon lives on the grid sheet image
        if (age >= 16 && age <= 21) {
            JButton btnJob = makeIconButton("Get Part-Time Job", new Color(220, 180, 100));
            JButton btnPC  = makeIconButton("Buy Gaming PC", new Color(200, 100, 100)); 

            btnJob.addActionListener(e -> { triggerAction("Get Part-Time Job", 0, 0, "You got hired! Time to earn some cash flow."); });
            btnPC.addActionListener(e -> { triggerAction("Buy PC", 0, 1, "Specs are clean! +15 Happiness."); });
            
            buttonRow.add(btnJob);
            buttonRow.add(btnPC);

        } else if (age >= 22 && age <= 29) {
            JButton btnCareer  = makeIconButton("Start Career", new Color(130, 180, 220));
            JButton btnLoans   = makeIconButton("Pay Loans", new Color(220, 130, 130));
            JButton btnInvest  = makeIconButton("Invest 50%", new Color(130, 200, 150));
            JButton btnJewelry = makeIconButton("Buy Jewelry", new Color(200, 100, 100)); 

            btnCareer.addActionListener(e -> { triggerAction("Start Career", 0, 2, "Welcome to adulthood. Salary unlocked!"); });
            btnLoans.addActionListener(e -> { triggerAction("Pay Student Loans", 0, 3, "Paid a lump sum toward your education debt."); });
            btnInvest.addActionListener(e -> { triggerAction("Invest 50%", 0, 4, "Allocating 50% of your cash into volatile assets."); });
            btnJewelry.addActionListener(e -> { triggerAction("Buy Jewelry", 1, 1, "Drip acquired. +20 Happiness."); });

            buttonRow.add(btnCareer);
            buttonRow.add(btnLoans);
            buttonRow.add(btnInvest);
            buttonRow.add(btnJewelry);

        } else if (age >= 30 && age <= 50) {
            JButton btnHouse  = makeIconButton("Buy House", new Color(200, 160, 100));
            JButton btnKids   = makeIconButton("Have Kids", new Color(220, 140, 180));
            JButton btnInvest = makeIconButton("Invest 50%", new Color(130, 200, 150));
            JButton btnCar    = makeIconButton("Buy Sports Car", new Color(200, 100, 100)); 

            btnHouse.addActionListener(e -> { triggerAction("Buy House", 1, 2, "Down payment sent. You are officially a homeowner!"); });
            btnKids.addActionListener(e -> { triggerAction("Have Kids", 1, 3, "Congratulations! Life just got much more expensive."); });
            btnInvest.addActionListener(e -> { triggerAction("Invest 50%", 0, 4, "Allocating 50% of your cash into volatile assets."); });
            btnCar.addActionListener(e -> { triggerAction("Buy Sports Car", 1, 4, "Vroom. Mid-life crisis managed. +40 Happiness."); });

            buttonRow.add(btnHouse);
            buttonRow.add(btnKids);
            buttonRow.add(btnInvest);
            buttonRow.add(btnCar);

        } else {
            JButton btnRetire = makeIconButton("Max Retirement", new Color(180, 140, 220));
            JButton btnBoat   = makeIconButton("Buy Boat", new Color(200, 100, 100)); 
            
            btnRetire.addActionListener(e -> { triggerAction("Max Retirement", 1, 4, "Shoveling maximum capital into senior accounts."); });
            btnBoat.addActionListener(e -> { triggerAction("Buy Boat", 2, 1, "You bought a boat! +30 Happiness."); });
            
            buttonRow.add(btnRetire);
            buttonRow.add(btnBoat);
        }

        JLabel ageLabel = new JLabel(getLifeStageName(age), SwingConstants.CENTER);
        ageLabel.setForeground(new Color(180, 180, 220));
        ageLabel.setFont(new Font("Serif", Font.ITALIC, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10,0,4,0);
        actionPanel.add(ageLabel, gbc);
        gbc.gridy = 1;
        actionPanel.add(buttonRow, gbc);

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    /**
     * NATIVE JAVA CROPPING LOGIC
     * Dynamically slices out a sub-image from sheet.png based on row and column indexes.
     */
    private void triggerAction(String actionName, int row, int col, String message) {
        GameEngine.takeInstantAction(player, actionName);

        ImageIcon popupIcon = null;
        if (masterSheet != null) {
            try {
                int totalCols = 5;
                int totalRows = 3;
                
                int boxW = masterSheet.getWidth() / totalCols;
                int boxH = masterSheet.getHeight() / totalRows;
                
                // Add minor structural crop padding to avoid the black label borders inside the sheet
                int padX = (int)(boxW * 0.12);
                int padY = (int)(boxH * 0.12);
                
                // Slice the sub-section out of the primary buffer array natively
                BufferedImage cropped = masterSheet.getSubimage(
                    (col * boxW) + padX, 
                    (row * boxH) + padY, 
                    boxW - (2 * padX), 
                    boxH - (2 * padY)
                );
                
                // Scale smooth for visual display popup layout
                Image scaled = cropped.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                popupIcon = new ImageIcon(scaled);
            } catch (Exception ex) {
                System.out.println("Error processing subimage grid slicing for: " + actionName);
            }
        }

        // Trigger standard pop-up window block
        JOptionPane.showMessageDialog(this, message, actionName, JOptionPane.INFORMATION_MESSAGE, popupIcon);
        refreshAll();
    }

    private String getLifeStageName(int age) {
        if      (age <= 21) return "Life Stage: Teen / Young Adult";
        else if (age <= 29) return "Life Stage: Young Professional";
        else if (age <= 50) return "Life Stage: Mid-Life";
        else                return "Life Stage: Pre-Retirement";
    }

    private void showGameOverScreen() {
        actionPanel.removeAll();
        timeControlPanel.setVisible(false); 

        double nw = player.getNetWorth();
        String title = "In Debt";
        if (nw > 0) title = "Getting By";
        if (nw > 100000) title = "Financially Secure";
        if (nw > 1000000) title = "Financially Independent";
        if (nw > 5000000) title = "Generational Wealth";

        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setOpaque(false);

        JLabel titleLabel = new JLabel("Happy 65th Birthday, " + player.getName() + "!");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nwLabel = new JLabel("Final Net Worth: $" + (int)nw);
        nwLabel.setForeground(Color.GREEN);
        nwLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nwLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rankLabel = new JLabel("Retirement Rank: " + title);
        rankLabel.setForeground(Color.CYAN);
        rankLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        rankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hapLabel = new JLabel("Final Happiness: " + player.getHappiness() + "/100");
        hapLabel.setForeground(Color.YELLOW);
        hapLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        hapLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        summary.add(Box.createVerticalStrut(20));
        summary.add(titleLabel);
        summary.add(Box.createVerticalStrut(20));
        summary.add(nwLabel);
        summary.add(Box.createVerticalStrut(10));
        summary.add(rankLabel);
        summary.add(Box.createVerticalStrut(10));
        summary.add(hapLabel);

        actionPanel.add(summary);
        actionPanel.revalidate();
        actionPanel.repaint();

        player.printHistory();
    }

    public void refreshAll() {
        labelName.setText("Name: " + player.getName());
        labelAge.setText("Age: " + player.getAge());
        labelNetWorth.setText("Net Worth: $" + (int)player.getNetWorth());
        labelCashFlow.setText("Cash Flow: $" + (int)GameEngine.getAnnualCashFlow(player) + "/yr");
        
        if (player.getAge() >= 65) {
            showGameOverScreen();
        } else {
            updateActionPanel(player.getAge());
        }
    }

    private JButton makeIconButton(String label, Color bgColor) {
        JButton button = new JButton(label); 
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); 
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); 
        button.setFocusPainted(false);
        button.setOpaque(true); 
        button.setBorderPainted(true);
        button.setPreferredSize(new Dimension(170, 60)); 
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        String name = JOptionPane.showInputDialog(null, "Enter your name to begin your life:");
        if(name == null || name.trim().isEmpty()) name = "Player";
        
        String finalName = name;
        SwingUtilities.invokeLater(() -> new GameGUI(finalName));
    }
}