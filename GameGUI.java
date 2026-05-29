import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class GameGUI extends JFrame {

    private static final String WINDOW_TITLE  = "Life & Finance Simulator";
    private static final int    WINDOW_WIDTH  = 900;
    private static final int    WINDOW_HEIGHT = 650;
    private static final String SHEET_PATH    = "assets/sheet.png"; 
    
    // Modern Dark Theme Palette
    private static final Color BG_DARK = new Color(24, 26, 31);
    private static final Color PANEL_DARK = new Color(33, 37, 43);

    private JLabel labelAge;
    private JLabel labelCashFlow; 
    private JLabel labelNetWorthText; 
    private JLabel labelHappinessText; 
    private JPanel actionPanel;
    private JPanel timeControlPanel; 
    
    private ModernProgressBar nwProgressBar;
    private ModernProgressBar hapProgressBar;

    private Person player;
    private BufferedImage masterSheet;

    public GameGUI(String playerName) {
        this.player = new Person(playerName);
        try {
            File file = new File(SHEET_PATH);
            if (file.exists()) masterSheet = ImageIO.read(file);
        } catch (Exception e) { System.out.println("Warning: Missing sheet.png"); }

        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        getContentPane().setBackground(BG_DARK);

        buildFrame();
        updateActionPanel(player.getAge());
        setVisible(true);
    }

    private void buildFrame() {
        setLayout(new BorderLayout(15, 15));
        
        JPanel rootPanel = new JPanel(new BorderLayout(15, 15));
        rootPanel.setBackground(BG_DARK);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        rootPanel.add(buildStatsPanel(), BorderLayout.NORTH);
        rootPanel.add(buildActionPanelShell(), BorderLayout.CENTER);
        
        timeControlPanel = buildTimeControlPanel();
        rootPanel.add(timeControlPanel, BorderLayout.SOUTH);
        
        add(rootPanel);
    }

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 10));
        TitledBorder dashBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(171, 178, 191)), "Player Dashboard: " + player.getName()
        );
        dashBorder.setTitleColor(Color.WHITE);
        panel.setBorder(dashBorder);
        panel.setBackground(PANEL_DARK);

        JPanel textRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        textRow.setOpaque(false);
        labelAge      = makeStatLabel("Age: " + player.getAge());
        labelCashFlow = makeStatLabel("Cash Flow: $" + (int)GameEngine.getAnnualCashFlow(player) + "/yr");
        textRow.add(labelAge);
        textRow.add(labelCashFlow);

        JPanel nwRow = new JPanel(new BorderLayout(15, 0));
        nwRow.setOpaque(false);
        labelNetWorthText = makeStatLabel("Net Worth: $" + (int)player.getNetWorth());
        
        nwProgressBar = new ModernProgressBar(0, 10000, new Color(46, 204, 113)); 
        nwProgressBar.setValue((int)player.getNetWorth());
        nwProgressBar.setPreferredSize(new Dimension(500, 25));
        nwRow.add(labelNetWorthText, BorderLayout.WEST);
        nwRow.add(nwProgressBar, BorderLayout.CENTER);

        JPanel hapRow = new JPanel(new BorderLayout(15, 0));
        hapRow.setOpaque(false);
        labelHappinessText = makeStatLabel("Happiness: " + player.getHappiness() + " / 100");
        
        hapProgressBar = new ModernProgressBar(0, 100, new Color(241, 196, 15));
        hapProgressBar.setValue(player.getHappiness());
        hapProgressBar.setPreferredSize(new Dimension(500, 25));
        hapRow.add(labelHappinessText, BorderLayout.WEST);
        hapRow.add(hapProgressBar, BorderLayout.CENTER);

        panel.add(textRow);
        panel.add(nwRow);
        panel.add(hapRow);

        return panel;
    }

    private JLabel makeStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        return label;
    }

    private JPanel buildActionPanelShell() {
        actionPanel = new JPanel(new BorderLayout()); 
        TitledBorder actionBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(171, 178, 191)), "Actions & Choices"
        );
        actionBorder.setTitleColor(Color.WHITE);
        actionPanel.setBorder(actionBorder);
        actionPanel.setBackground(PANEL_DARK);
        return actionPanel;
    }

    private JPanel buildTimeControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        TitledBorder timeBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(171, 178, 191)), "Time Engine"
        );
        timeBorder.setTitleColor(Color.WHITE);
        panel.setBorder(timeBorder);
        panel.setBackground(PANEL_DARK);

        JButton btnSim1  = makeIconButton("Simulate 1 Year",  new Color(97, 175, 239));
        JButton btnSim10 = makeIconButton("Simulate 10 Years", new Color(198, 120, 221));

        btnSim1.addActionListener(e -> {
            handleTimelineEvent(GameEngine.simulateOneYear(player));
            refreshAll();
        });

        btnSim10.addActionListener(e -> {
            handleTimelineEvent(GameEngine.simulateTenYears(player));
            refreshAll();
        });

        panel.add(btnSim1);
        panel.add(btnSim10);
        return panel;
    }

    private void handleTimelineEvent(String msg) {
        if (msg == null) return;
        int row = 0, col = 3; 
        boolean isGoodNews = false; 

        if (msg.contains("WINDFALL")) {
            isGoodNews = true;
            if (msg.contains("bonus")) { row = 0; col = 0; } 
            else if (msg.contains("Market") || msg.contains("Portfolio")) { row = 0; col = 4; } 
        } else {
            if (msg.contains("market") || msg.contains("correction")) { row = 1; col = 4; } 
            else if (msg.contains("career") || msg.contains("downsizing")) { row = 0; col = 2; } 
        }

        showCustomPopup(isGoodNews ? "GOOD NEWS!" : "CRITICAL ALERT", msg, sliceIconFromSheet(row, col), isGoodNews);
    }

    public void updateActionPanel(int age) {
        actionPanel.removeAll();
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonRow.setOpaque(false);

        if (age >= 22 && !player.hasCareer()) {
            JButton btnEmergencyJob = makeIconButton("Apply for New Job", new Color(229, 192, 123));
            btnEmergencyJob.addActionListener(e -> triggerAction("Start Career", 0, 2, "Back on track!"));
            buttonRow.add(btnEmergencyJob);
        }

        // STAGE 1: HIGH SCHOOL (16-18)
        if (age >= 16 && age <= 18) {
            if (!player.hasPartTimeJob()) {
                JButton btnJob = makeIconButton("Get Part-Time Job", new Color(152, 195, 121));
                btnJob.addActionListener(e -> triggerAction("Get Part-Time Job", 0, 0, "Flipping burgers for minimum wage."));
                buttonRow.add(btnJob);
            }
            JButton btnCar = makeIconButton("Buy Beater Car", new Color(224, 108, 117));
            btnCar.addActionListener(e -> triggerAction("Buy Beater Car", 1, 4, "It barely runs, but it's yours!"));
            buttonRow.add(btnCar);
            
            JButton btnInvestEarly = makeIconButton("Invest $1,000", new Color(97, 175, 239));
            btnInvestEarly.addActionListener(e -> triggerAction("Invest $1,000", 0, 4, "Planting the seeds for compound interest."));
            buttonRow.add(btnInvestEarly);

        // STAGE 2: COLLEGE / EARLY ADULT (19-22)
        } else if (age >= 19 && age <= 22) {
            JButton btnLoan = makeIconButton("Take Student Loans", new Color(224, 108, 117));
            btnLoan.addActionListener(e -> triggerAction("Take Student Loans", 0, 3, "Debt acquired to pay for textbooks."));
            
            JButton btnIntern = makeIconButton("Paid Internship", new Color(152, 195, 121));
            btnIntern.addActionListener(e -> triggerAction("Paid Internship", 0, 2, "Looks great on a resume."));
            
            JButton btnInvestEarly = makeIconButton("Invest $1,000", new Color(97, 175, 239));
            btnInvestEarly.addActionListener(e -> triggerAction("Invest $1,000", 0, 4, "Every dollar matters early on."));
            
            buttonRow.add(btnLoan);
            buttonRow.add(btnIntern);
            buttonRow.add(btnInvestEarly);

        // STAGE 3: YOUNG PROFESSIONAL (23-29)
        } else if (age >= 23 && age <= 29) {
            if (!player.hasCareer()) { 
                JButton btnCareer = makeIconButton("Start Career", new Color(97, 175, 239));
                btnCareer.addActionListener(e -> triggerAction("Start Career", 0, 2, "Welcome to the corporate grind."));
                buttonRow.add(btnCareer);
            }
            JButton btnInvest = makeIconButton("Invest 50%", new Color(152, 195, 121));
            btnInvest.addActionListener(e -> triggerAction("Invest 50%", 0, 4, "Aggressive early saving."));
            buttonRow.add(btnInvest);
            
            JButton btnHouse = makeIconButton("Buy House", new Color(229, 192, 123));
            btnHouse.addActionListener(e -> triggerAction("Buy House", 1, 2, "You are a homeowner!"));
            buttonRow.add(btnHouse);

        // STAGE 4: ADULTHOOD (30-50)
        } else if (age >= 30 && age <= 50) {
            JButton btnInvest = makeIconButton("Invest 50%", new Color(152, 195, 121));
            btnInvest.addActionListener(e -> triggerAction("Invest 50%", 0, 4, "Accelerating investments."));
            
            JButton btnKids = makeIconButton("Have Kids", new Color(198, 120, 221));
            btnKids.addActionListener(e -> triggerAction("Have Kids", 1, 3, "Your expenses just skyrocketed."));
            
            buttonRow.add(btnInvest);
            buttonRow.add(btnKids);

        // STAGE 5: PRE-RETIREMENT (51-64)
        } else if (age >= 51 && age < 65) {
            JButton btnRetire = makeIconButton("Max Retirement", new Color(97, 175, 239));
            btnRetire.addActionListener(e -> triggerAction("Max Retirement", 1, 4, "Catch-up contributions."));
            
            JButton btnBoat = makeIconButton("Buy Boat", new Color(224, 108, 117));
            btnBoat.addActionListener(e -> triggerAction("Buy Boat", 2, 1, "The two happiest days..."));
            
            buttonRow.add(btnRetire);
            buttonRow.add(btnBoat);
        }

        JLabel ageLabel = new JLabel(getLifeStageName(age), SwingConstants.CENTER);
        ageLabel.setForeground(new Color(171, 178, 191));
        ageLabel.setFont(new Font("Serif", Font.ITALIC, 18));

        actionPanel.add(ageLabel, BorderLayout.NORTH);
        actionPanel.add(buttonRow, BorderLayout.CENTER);
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private void triggerAction(String actionName, int row, int col, String message) {
        GameEngine.takeInstantAction(player, actionName);
        showCustomPopup(actionName, message, sliceIconFromSheet(row, col), true);
        refreshAll();
    }

    private ImageIcon sliceIconFromSheet(int row, int col) {
        if (masterSheet == null) return null;
        try {
            int boxW = masterSheet.getWidth() / 5, boxH = masterSheet.getHeight() / 3;
            BufferedImage cropped = masterSheet.getSubimage(col * boxW, row * boxH, boxW, boxH);
            return new ImageIcon(cropped.getScaledInstance(120, 120, Image.SCALE_SMOOTH));
        } catch (Exception ex) { return null; }
    }

    private void showCustomPopup(String title, String message, ImageIcon icon, boolean isGoodNews) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(PANEL_DARK); 

        JLabel imageLabel = new JLabel(icon);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        String colorHex = isGoodNews ? "#98c379" : "#e06c75"; 
        String htmlText = "<html><div style='text-align: center; width: 250px; font-family: sans-serif;'>"
                        + "<h2 style='color: " + colorHex + ";'>" + title + "</h2>"
                        + "<p style='color: white; font-size: 14px;'>" + message + "</p></div></html>";
        
        JLabel textLabel = new JLabel(htmlText, SwingConstants.CENTER);
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JButton closeBtn = makeIconButton("Continue", new Color(97, 175, 239));
        closeBtn.addActionListener(e -> dialog.dispose()); 

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(PANEL_DARK);
        buttonPanel.add(closeBtn);

        dialog.add(imageLabel, BorderLayout.NORTH);
        dialog.add(textLabel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this); 
        dialog.setVisible(true); 
    }

    private String getLifeStageName(int age) {
        if      (age <= 18) return "Life Stage: High School";
        else if (age <= 22) return "Life Stage: College / Trade";
        else if (age <= 29) return "Life Stage: Young Professional";
        else if (age <= 50) return "Life Stage: Adulthood";
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

        summary.add(Box.createVerticalStrut(20));
        summary.add(makeGameOverLabel("Happy 65th Birthday, " + player.getName() + "!", Color.WHITE, 24));
        summary.add(Box.createVerticalStrut(20));
        summary.add(makeGameOverLabel(String.format("Final Net Worth: $%,d", (int)nw), new Color(152, 195, 121), 22));
        summary.add(Box.createVerticalStrut(10));
        summary.add(makeGameOverLabel("Retirement Rank: " + title, new Color(97, 175, 239), 18));
        
        actionPanel.add(summary, BorderLayout.CENTER);
        actionPanel.revalidate();
        actionPanel.repaint();
    }
    
    private JLabel makeGameOverLabel(String text, Color c, int size) {
        JLabel l = new JLabel(text);
        l.setForeground(c);
        l.setFont(new Font("SansSerif", Font.BOLD, size));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    public void refreshAll() {
        labelAge.setText("Age: " + player.getAge());
        labelCashFlow.setText("Cash Flow: $" + (int)GameEngine.getAnnualCashFlow(player) + "/yr");
        
        int currentNW = (int)player.getNetWorth();
        
        // DYNAMIC MILESTONE MATH! (Fixes the $2M limit)
        int nextMilestone = 10000;
        if (currentNW > 10000) nextMilestone = 100000;
        if (currentNW > 100000) nextMilestone = 1000000;
        if (currentNW > 1000000) nextMilestone = 5000000;
        if (currentNW > 5000000) nextMilestone = 25000000; // Super wealthy!
        
        nwProgressBar.setMaximum(nextMilestone);
        // Force the progress bar to drop to 0 if they go into debt
        nwProgressBar.setValue(Math.max(0, currentNW)); 
        
        labelNetWorthText.setText(String.format("Net Worth: $%,d / $%,d", currentNW, nextMilestone));
        
        hapProgressBar.setValue(player.getHappiness());
        labelHappinessText.setText("Happiness: " + player.getHappiness() + " / 100");
        
        if (player.getAge() >= 65) showGameOverScreen();
        else updateActionPanel(player.getAge());
    }

    // --- ADVANCED GRAPHICS: CUSTOM DROP-SHADOW BUTTONS ---
    private JButton makeIconButton(String label, Color bgColor) {
        JButton button = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw Drop Shadow
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-4, 15, 15);
                
                // Draw Actual Button
                if (getModel().isRollover()) g2.setColor(bgColor.brighter());
                else g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 15, 15);
                
                super.paintComponent(g); 
                g2.dispose();
            }
        }; 
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(Color.BLACK); 
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); 
        button.setPreferredSize(new Dimension(170, 50)); 
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    // --- ADVANCED GRAPHICS: MODERN PROGRESS BAR ---
    class ModernProgressBar extends JProgressBar {
        private Color barColor;
        public ModernProgressBar(int min, int max, Color color) {
            super(min, max);
            this.barColor = color;
            setOpaque(false);
            setBorderPainted(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background Track
            g2.setColor(new Color(15, 15, 25)); 
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
            int fillWidth = (int) (getWidth() * getPercentComplete());
            if (fillWidth > 0) {
                GradientPaint gp = new GradientPaint(0, 0, barColor.darker(), fillWidth, 0, barColor.brighter().brighter());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, fillWidth, getHeight(), 20, 20);
            }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        String name = JOptionPane.showInputDialog(null, "Enter your name:");
        if(name == null || name.trim().isEmpty()) name = "Player";
        String finalName = name;
        SwingUtilities.invokeLater(() -> new GameGUI(finalName));
    }
}