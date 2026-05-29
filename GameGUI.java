import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class GameGUI extends JFrame {

    private static final String WINDOW_TITLE  = "Life & Finance Simulator";
    private static final int    WINDOW_WIDTH  = 1150; 
    private static final int    WINDOW_HEIGHT = 750;
    private static final String SHEET_PATH    = "assets/sheet.png"; 
    
    private static final Color BG_DARK = new Color(24, 26, 31);
    private static final Color PANEL_DARK = new Color(33, 37, 43);
    private static final Color COLOR_GOOD = new Color(152, 195, 121);
    private static final Color COLOR_BAD = new Color(224, 108, 117);
    private static final Color COLOR_NEUTRAL = new Color(97, 175, 239);

    private JLabel labelAge;
    private JLabel labelCashFlow; 
    private JLabel labelNetWorthText; 
    private JLabel labelHappinessText; 
    private JLabel labelCareerText; 
    private JLabel labelAssetText; // NEW: Shows Education, House, Car
    private JPanel actionPanel;
    private JPanel timeControlPanel; 
    private JTextPane lifeFeed; 
    private ModernProgressBar nwProgressBar;
    private ModernProgressBar hapProgressBar;

    private Person player;
    private BufferedImage masterSheet;

    private JPanel glassPane;
    private List<FloatingText> animations = new CopyOnWriteArrayList<>();
    private Timer animTimer;

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

        setupAnimationEngine(); 
        buildFrame();
        updateActionPanel(player.getAge());
        
        logEvent("Simulation Started", "Welcome to the world, " + player.getName() + "!", null, COLOR_NEUTRAL);
        setVisible(true);
    }

    private void setupAnimationEngine() {
        glassPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("SansSerif", Font.BOLD, 26));

                for (FloatingText ft : animations) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ft.alpha));
                    g2.setColor(ft.color);
                    g2.drawString(ft.text, ft.x, ft.y);
                }
            }
        };
        glassPane.setOpaque(false);
        setGlassPane(glassPane);
        glassPane.setVisible(true);

        animTimer = new Timer(16, e -> {
            boolean needsRepaint = false;
            for (FloatingText ft : animations) {
                ft.y -= 2; 
                ft.alpha -= 0.02f; 
                if (ft.alpha <= 0) animations.remove(ft);
                needsRepaint = true;
            }
            if (needsRepaint) glassPane.repaint();
        });
        animTimer.start();
    }

    private void spawnAnimation(String text, Color c) {
        int randX = 300 + (int)(Math.random() * 80 - 40);
        int randY = 300 + (int)(Math.random() * 40 - 20);
        animations.add(new FloatingText(text, randX, randY, c));
    }

    class FloatingText {
        String text; int x, y; float alpha = 1.0f; Color color;
        public FloatingText(String t, int x, int y, Color c) {
            this.text = t; this.x = x; this.y = y; this.color = c;
        }
    }

    private void buildFrame() {
        setLayout(new BorderLayout(15, 15));
        
        JPanel rootPanel = new JPanel(new BorderLayout(15, 15));
        rootPanel.setBackground(BG_DARK);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel leftPanel = new JPanel(new BorderLayout(15, 15));
        leftPanel.setOpaque(false);
        leftPanel.add(buildStatsPanel(), BorderLayout.NORTH);
        leftPanel.add(buildActionPanelShell(), BorderLayout.CENTER);
        
        timeControlPanel = buildTimeControlPanel();
        leftPanel.add(timeControlPanel, BorderLayout.SOUTH);
        
        rootPanel.add(leftPanel, BorderLayout.CENTER);
        rootPanel.add(buildFeedPanel(), BorderLayout.EAST); 
        
        add(rootPanel);
    }

    private JPanel buildFeedPanel() {
        JPanel feedContainer = new JPanel(new BorderLayout());
        TitledBorder feedBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(171, 178, 191)), "Life Timeline"
        );
        feedBorder.setTitleColor(Color.WHITE);
        feedContainer.setBorder(feedBorder);
        feedContainer.setBackground(PANEL_DARK);
        feedContainer.setPreferredSize(new Dimension(350, 0)); 

        lifeFeed = new JTextPane();
        lifeFeed.setEditable(false);
        lifeFeed.setBackground(BG_DARK); 
        lifeFeed.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(lifeFeed);
        scrollPane.setBorder(null); 
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        feedContainer.add(scrollPane, BorderLayout.CENTER);
        return feedContainer;
    }

    private void logEvent(String title, String message, ImageIcon icon, Color headerColor) {
        try {
            StyledDocument doc = lifeFeed.getStyledDocument();
            SimpleAttributeSet titleStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(titleStyle, headerColor);
            StyleConstants.setBold(titleStyle, true);
            StyleConstants.setFontFamily(titleStyle, "SansSerif");
            StyleConstants.setFontSize(titleStyle, 16);
            doc.insertString(doc.getLength(), title + "\n", titleStyle);
            
            if (icon != null) {
                Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                lifeFeed.setCaretPosition(doc.getLength());
                lifeFeed.insertIcon(new ImageIcon(img));
                doc.insertString(doc.getLength(), "  ", null); 
            }
            
            SimpleAttributeSet textStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(textStyle, new Color(171, 178, 191));
            StyleConstants.setFontFamily(textStyle, "SansSerif");
            StyleConstants.setFontSize(textStyle, 14);
            doc.insertString(doc.getLength(), message + "\n\n", textStyle);
            
            lifeFeed.setCaretPosition(doc.getLength());
        } catch (Exception e) { System.out.println("Error writing to life feed."); }
    }

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5)); // 5 Rows now
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

        JPanel careerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        careerRow.setOpaque(false);
        labelCareerText = makeStatLabel(getCareerDisplay());
        labelCareerText.setForeground(COLOR_NEUTRAL);
        careerRow.add(labelCareerText);
        
        JPanel assetRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        assetRow.setOpaque(false);
        labelAssetText = makeStatLabel(getAssetDisplay());
        labelAssetText.setForeground(new Color(198, 120, 221)); // Purple
        assetRow.add(labelAssetText);

        JPanel nwRow = new JPanel(new BorderLayout(15, 0));
        nwRow.setOpaque(false);
        labelNetWorthText = makeStatLabel("Net Worth: $" + (int)player.getNetWorth());
        nwProgressBar = new ModernProgressBar(0, 10000, COLOR_GOOD); 
        nwProgressBar.setValue((int)player.getNetWorth());
        nwProgressBar.setPreferredSize(new Dimension(400, 20));
        nwRow.add(labelNetWorthText, BorderLayout.WEST);
        nwRow.add(nwProgressBar, BorderLayout.CENTER);

        JPanel hapRow = new JPanel(new BorderLayout(15, 0));
        hapRow.setOpaque(false);
        labelHappinessText = makeStatLabel("Happiness: " + player.getHappiness() + " / 100");
        hapProgressBar = new ModernProgressBar(0, 100, new Color(241, 196, 15));
        hapProgressBar.setValue(player.getHappiness());
        hapProgressBar.setPreferredSize(new Dimension(400, 20));
        hapRow.add(labelHappinessText, BorderLayout.WEST);
        hapRow.add(hapProgressBar, BorderLayout.CENTER);

        panel.add(textRow);
        panel.add(careerRow);
        panel.add(assetRow);
        panel.add(nwRow);
        panel.add(hapRow);

        return panel;
    }
    
    private String getCareerDisplay() {
        if (player.getCareerTier() == 0) return "Career: Unemployed";
        if (player.getCareerTier() == 1) return "Career: Part-Time Job";
        
        String tierString = "Entry-Level";
        if (player.getCareerTier() == 3) tierString = "Mid-Level";
        if (player.getCareerTier() >= 4) tierString = "Executive";
        
        return "Career: " + tierString + " in " + player.getCareerPath();
    }
    
    private String getAssetDisplay() {
        String ed = "HS Diploma";
        if (player.isEnrolled()) ed = "In College";
        else if (player.getEducationLevel() == 1) ed = "College Dropout";
        else if (player.getEducationLevel() == 2) ed = "Bachelor's";
        else if (player.getEducationLevel() == 3) ed = "Master's";
        else if (player.getEducationLevel() >= 4) ed = "PhD";
        
        String car = "No Car";
        if (player.getCarTier() == 1) car = "Beater";
        else if (player.getCarTier() == 2) car = "Sedan";
        else if (player.getCarTier() == 3) car = "SUV";
        else if (player.getCarTier() >= 4) car = "Supercar";
        
        String house = "Renting";
        if (player.getHouseTier() == 1) house = "Starter Home";
        else if (player.getHouseTier() == 2) house = "Suburban House";
        else if (player.getHouseTier() >= 3) house = "Mansion";
        
        return "[" + ed + "]  |  [" + car + "]  |  [" + house + "]";
    }

    private JLabel makeStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
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

        JButton btnSim1  = makeIconButton("Simulate 1 Year",  COLOR_NEUTRAL);
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
            spawnAnimation("+$ WINDFALL!", COLOR_GOOD);
        } else {
            if (msg.contains("market") || msg.contains("correction")) { row = 1; col = 4; } 
            else if (msg.contains("career") || msg.contains("downsizing")) { row = 0; col = 2; } 
            spawnAnimation("-$ EMERGENCY!", COLOR_BAD);
        }

        String title = isGoodNews ? "WINDFALL EVENT" : "CRITICAL EVENT";
        Color headerColor = isGoodNews ? COLOR_GOOD : COLOR_BAD;
        
        logEvent(title, msg, sliceIconFromSheet(row, col), headerColor);
    }

    public void updateActionPanel(int age) {
        actionPanel.removeAll();
        
        JPanel buttonRow = new JPanel(new GridLayout(0, 3, 15, 15));
        buttonRow.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buttonRow.setOpaque(false);

        // ALWAYS SHOW ASSET UPGRADES BASED ON TIER
        addDynamicAssetButtons(buttonRow);

        // EDUCATION LOOP
        if (player.isEnrolled()) {
            JButton btnStudy = makeIconButton("Study Hard", COLOR_NEUTRAL);
            btnStudy.addActionListener(e -> { spawnAnimation("+ Grades", COLOR_GOOD); triggerAction("Study Hard", 0, 1, "Hitting the books."); });
            
            JButton btnParty = makeIconButton("Party", new Color(198, 120, 221));
            btnParty.addActionListener(e -> { spawnAnimation("- Grades", COLOR_BAD); triggerAction("Party", 1, 1, "Going out with friends."); });
            
            JButton btnDrop = makeIconButton("Drop Out", COLOR_BAD);
            btnDrop.addActionListener(e -> { spawnAnimation("- Education Stopped", COLOR_BAD); triggerAction("Drop Out", 1, 3, "Left school early."); });
            
            buttonRow.add(btnStudy);
            buttonRow.add(btnParty);
            buttonRow.add(btnDrop);
            
        } else {
            // APPLY TO SCHOOL BUTTONS
            if (age >= 18 && player.getEducationLevel() == 0) {
                JButton btnApply = makeIconButton("Apply to College", COLOR_NEUTRAL);
                btnApply.addActionListener(e -> showCollegeApplicationPopup());
                buttonRow.add(btnApply);
            } else if (player.getEducationLevel() == 2) {
                JButton btnApply = makeIconButton("Apply for Master's", COLOR_NEUTRAL);
                btnApply.addActionListener(e -> showCollegeApplicationPopup());
                buttonRow.add(btnApply);
            } else if (player.getEducationLevel() == 3) {
                JButton btnApply = makeIconButton("Apply for PhD", COLOR_NEUTRAL);
                btnApply.addActionListener(e -> showCollegeApplicationPopup());
                buttonRow.add(btnApply);
            }

            // CAREER BUTTONS
            if (age >= 22 && player.getCareerTier() == 0) {
                JButton btnEmergencyJob = makeIconButton("Find Career", new Color(229, 192, 123));
                btnEmergencyJob.addActionListener(e -> showCareerSelectionPopup());
                buttonRow.add(btnEmergencyJob);
            } else if (player.hasCareer() && player.getCareerTier() < 4) {
                JButton btnPromote = makeIconButton("Seek Promotion", COLOR_GOOD);
                btnPromote.addActionListener(e -> handlePromotionAttempt());
                buttonRow.add(btnPromote);
            }

            // GENERAL BUTTONS
            JButton btnInvest = makeIconButton("Invest 50%", COLOR_GOOD);
            btnInvest.addActionListener(e -> { spawnAnimation("-50% Cash Invested", COLOR_GOOD); triggerAction("Invest 50%", 0, 4, "Aggressive early saving."); });
            buttonRow.add(btnInvest);
            
            if (!player.hasKids() && age >= 25) {
                JButton btnKids = makeIconButton("Have Kids", new Color(198, 120, 221));
                btnKids.addActionListener(e -> { spawnAnimation("- Cash, + Family", new Color(198, 120, 221)); triggerAction("Have Kids", 1, 3, "Your expenses skyrocketed."); });
                buttonRow.add(btnKids);
            }
        }

        JLabel phaseLabel = new JLabel("Grades: " + player.getGrades() + "/100", SwingConstants.CENTER);
        phaseLabel.setForeground(new Color(171, 178, 191));
        phaseLabel.setFont(new Font("Serif", Font.ITALIC, 18));

        actionPanel.add(phaseLabel, BorderLayout.NORTH);
        actionPanel.add(buttonRow, BorderLayout.CENTER);
        actionPanel.revalidate();
        actionPanel.repaint();
    }
    
    private void addDynamicAssetButtons(JPanel buttonRow) {
        // Dynamic Car Button
        if (player.getCarTier() == 0) {
            JButton btn = makeIconButton("Buy Beater ($3k)", COLOR_BAD);
            btn.addActionListener(e -> handleAssetPurchase(3000, "Buy Beater Car", 1, true));
            buttonRow.add(btn);
        } else if (player.getCarTier() == 1) {
            JButton btn = makeIconButton("Upgrade Sedan ($15k)", COLOR_NEUTRAL);
            btn.addActionListener(e -> handleAssetPurchase(15000, "Upgrade Sedan", 2, true));
            buttonRow.add(btn);
        } else if (player.getCarTier() == 2) {
            JButton btn = makeIconButton("Upgrade SUV ($60k)", COLOR_NEUTRAL);
            btn.addActionListener(e -> handleAssetPurchase(60000, "Upgrade SUV", 3, true));
            buttonRow.add(btn);
        } else if (player.getCarTier() == 3) {
            JButton btn = makeIconButton("Supercar ($250k)", new Color(229, 192, 123));
            btn.addActionListener(e -> handleAssetPurchase(250000, "Buy Supercar", 4, true));
            buttonRow.add(btn);
        }
        
        // Dynamic House Button
        if (player.getHouseTier() == 0) {
            JButton btn = makeIconButton("Starter Home ($50k)", new Color(229, 192, 123));
            btn.addActionListener(e -> handleAssetPurchase(50000, "Buy Starter Home", 1, false));
            buttonRow.add(btn);
        } else if (player.getHouseTier() == 1) {
            JButton btn = makeIconButton("Suburban House ($150k)", new Color(229, 192, 123));
            btn.addActionListener(e -> handleAssetPurchase(150000, "Buy Suburban House", 2, false));
            buttonRow.add(btn);
        } else if (player.getHouseTier() == 2) {
            JButton btn = makeIconButton("Mansion ($1M)", new Color(229, 192, 123));
            btn.addActionListener(e -> handleAssetPurchase(1000000, "Buy Mansion", 3, false));
            buttonRow.add(btn);
        }
    }
    
    private void handleAssetPurchase(int cost, String name, int tier, boolean isCar) {
        if (player.getCash() >= cost) {
            player.addCash(-cost);
            if(isCar) player.setCarTier(tier); else player.setHouseTier(tier);
            player.modifyHappiness(20);
            spawnAnimation("-$" + cost, COLOR_BAD);
            logEvent("Asset Upgraded", "You bought a " + name + "!", sliceIconFromSheet(1, 4), COLOR_GOOD);
        } else {
            spawnAnimation("DECLINED!", COLOR_BAD);
            logEvent("TRANSACTION FAILED", "Insufficient funds for: " + name, sliceIconFromSheet(1, 3), COLOR_BAD);
        }
        refreshAll();
    }

    private void showCollegeApplicationPopup() {
        String[] paths = {"Community College (100%)", "State University (65%)", "Private University (25%)", "Elite Ivy League (5%)"};
        String choice = (String) JOptionPane.showInputDialog(this, 
            "Select where to apply (Acceptance based on Grades):", "College Application", 
            JOptionPane.QUESTION_MESSAGE, null, paths, paths[0]);
            
        if (choice != null) {
            int grades = player.getGrades();
            boolean accepted = false;
            String tierName = "";
            
            if (choice.contains("Community")) { accepted = true; tierName = "Community"; }
            else if (choice.contains("State") && grades >= 60) { accepted = (Math.random() < 0.65); tierName = "State"; }
            else if (choice.contains("Private") && grades >= 80) { accepted = (Math.random() < 0.25); tierName = "Private"; }
            else if (choice.contains("Elite") && grades >= 95) { accepted = (Math.random() < 0.05); tierName = "Elite"; }
            
            if (accepted) {
                player.setEnrolled(true);
                player.setUniversityTier(tierName);
                spawnAnimation("ACCEPTED!", COLOR_GOOD);
                logEvent("Admissions Decision", "Congratulations! You were accepted into " + tierName + " tier.", sliceIconFromSheet(0, 1), COLOR_GOOD);
            } else {
                spawnAnimation("REJECTED", COLOR_BAD);
                logEvent("Admissions Decision", "We regret to inform you that you were rejected. Improve your grades.", sliceIconFromSheet(1, 3), COLOR_BAD);
            }
            refreshAll();
        }
    }

    private void showCareerSelectionPopup() {
        String[] paths = {"Technology", "Healthcare", "Finance", "Trades"};
        String choice = (String) JOptionPane.showInputDialog(this, 
            "Select your professional industry:", "Career Path Selection", 
            JOptionPane.QUESTION_MESSAGE, null, paths, paths[0]);
            
        if (choice != null) {
            player.setCareerPath(choice);
            player.setCareerTier(2); 
            spawnAnimation("+ Salary Unlocked", COLOR_GOOD);
            logEvent("Career Started", "You accepted an Entry-Level position in " + choice + ".", sliceIconFromSheet(0, 2), COLOR_NEUTRAL);
            refreshAll();
        }
    }

    private void handlePromotionAttempt() {
        boolean success = GameEngine.takeInstantAction(player, "Seek Promotion");
        if (success) {
            spawnAnimation("+ PROMOTED!", COLOR_GOOD);
            logEvent("Career Advancement", "You were promoted! Salary increased.", sliceIconFromSheet(0, 2), COLOR_GOOD);
        } else {
            // Check if it failed because of Education
            if (player.getCareerTier() == 2 && player.getEducationLevel() < 2) {
                spawnAnimation("- Needs Bachelor's", COLOR_BAD);
                logEvent("Promotion Denied", "HR requires a Bachelor's Degree for Mid-Level roles.", sliceIconFromSheet(1, 3), COLOR_BAD);
            } else if (player.getCareerTier() == 3 && player.getEducationLevel() < 3) {
                spawnAnimation("- Needs Master's", COLOR_BAD);
                logEvent("Promotion Denied", "HR requires a Master's Degree for Executive roles.", sliceIconFromSheet(1, 3), COLOR_BAD);
            } else {
                spawnAnimation("- Passed Over", COLOR_BAD);
                logEvent("Promotion Denied", "Your boss passed you over. Keep grinding.", sliceIconFromSheet(1, 3), COLOR_BAD);
            }
        }
        refreshAll();
    }

    private void triggerAction(String actionName, int row, int col, String message) {
        boolean success = GameEngine.takeInstantAction(player, actionName);
        if (success) {
            logEvent("Action Taken: " + actionName, message, sliceIconFromSheet(row, col), COLOR_NEUTRAL);
        } else {
            spawnAnimation("DECLINED!", COLOR_BAD);
            logEvent("TRANSACTION FAILED", "Action Failed: " + actionName, sliceIconFromSheet(1, 3), COLOR_BAD);
        }
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
        summary.add(makeGameOverLabel(String.format("Final Net Worth: $%,d", (int)nw), COLOR_GOOD, 22));
        summary.add(Box.createVerticalStrut(10));
        summary.add(makeGameOverLabel("Retirement Rank: " + title, COLOR_NEUTRAL, 18));
        
        actionPanel.add(summary, BorderLayout.CENTER);
        actionPanel.revalidate();
        actionPanel.repaint();
        
        logEvent("RETIREMENT REACHED", "Final Rank: " + title + " | Net Worth: $" + (int)nw, null, COLOR_GOOD);
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
        labelCareerText.setText(getCareerDisplay());
        labelAssetText.setText(getAssetDisplay());
        
        int currentNW = (int)player.getNetWorth();
        int nextMilestone = 10000;
        if (currentNW > 10000) nextMilestone = 100000;
        if (currentNW > 100000) nextMilestone = 1000000;
        if (currentNW > 1000000) nextMilestone = 5000000;
        if (currentNW > 5000000) nextMilestone = 25000000; 
        
        nwProgressBar.setMaximum(nextMilestone);
        nwProgressBar.setValue(Math.max(0, currentNW)); 
        labelNetWorthText.setText(String.format("Net Worth: $%,d / $%,d", currentNW, nextMilestone));
        
        hapProgressBar.setValue(player.getHappiness());
        labelHappinessText.setText("Happiness: " + player.getHappiness() + " / 100");
        
        if (player.getAge() >= 65) showGameOverScreen();
        else updateActionPanel(player.getAge());
    }

    private JButton makeIconButton(String label, Color bgColor) {
        JButton button = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-4, 15, 15);
                
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