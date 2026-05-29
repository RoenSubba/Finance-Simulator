import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class GameGUI extends JFrame {

    private static final String WINDOW_TITLE  = "Life & Finance Simulator";
    private static final int    WINDOW_WIDTH  = 900;
    private static final int    WINDOW_HEIGHT = 600;
    private static final String SHEET_PATH    = "assets/sheet.png"; 

    private JLabel labelAge;
    private JLabel labelCashFlow; 
    private JLabel labelNetWorthText; 
    private JLabel labelHappinessText; 
    private JPanel actionPanel;
    private JPanel timeControlPanel; 
    
    private JProgressBar nwProgressBar;
    private JProgressBar hapProgressBar;

    private Person player;
    private BufferedImage masterSheet;

    public GameGUI(String playerName) {
        this.player = new Person(playerName);

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
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        TitledBorder dashBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Player Dashboard: " + player.getName()
        );
        dashBorder.setTitleColor(Color.WHITE);
        panel.setBorder(dashBorder);
        panel.setBackground(new Color(30, 30, 50));

        // 1. Top Row: Age & Cash Flow
        JPanel textRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        textRow.setOpaque(false);
        labelAge      = makeStatLabel("Age: " + player.getAge());
        labelCashFlow = makeStatLabel("Cash Flow: $" + (int)GameEngine.getAnnualCashFlow(player) + "/yr");
        textRow.add(labelAge);
        textRow.add(labelCashFlow);

        // 2. Middle Row: Net Worth Visualizer (Custom Gradient Bar)
        JPanel nwRow = new JPanel(new BorderLayout(10, 0));
        nwRow.setOpaque(false);
        labelNetWorthText = makeStatLabel("Net Worth: $" + (int)player.getNetWorth() + " / $2,000,000");
        
        ModernProgressBar nwBar = new ModernProgressBar(0, 2000000, new Color(46, 204, 113)); 
        nwBar.setValue((int)player.getNetWorth());
        nwBar.setPreferredSize(new Dimension(400, 20));
        
        nwRow.add(labelNetWorthText, BorderLayout.WEST);
        nwRow.add(nwBar, BorderLayout.CENTER);
        this.nwProgressBar = nwBar;   

        // 3. Bottom Row: Happiness Visualizer (Custom Gradient Bar)
        JPanel hapRow = new JPanel(new BorderLayout(10, 0));
        hapRow.setOpaque(false);
        labelHappinessText = makeStatLabel("Happiness: " + player.getHappiness() + " / 100");
        
        ModernProgressBar hapBar = new ModernProgressBar(0, 100, new Color(241, 196, 15));
        hapBar.setValue(player.getHappiness());
        hapBar.setPreferredSize(new Dimension(400, 20));
        
        hapRow.add(labelHappinessText, BorderLayout.WEST);
        hapRow.add(hapBar, BorderLayout.CENTER);
        this.hapProgressBar = hapBar; 

        panel.add(textRow);
        panel.add(nwRow);
        panel.add(hapRow);

        return panel;
    }

    private JLabel makeStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.BOLD, 15));
        return label;
    }

    private JPanel buildActionPanelShell() {
        actionPanel = new JPanel(new BorderLayout()); 
        
        TitledBorder actionBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Actions"
        );
        actionBorder.setTitleColor(Color.WHITE);
        actionPanel.setBorder(actionBorder);
        
        actionPanel.setBackground(new Color(20, 20, 40));
        return actionPanel;
    }

    private JPanel buildTimeControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        TitledBorder timeBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Time Controls"
        );
        timeBorder.setTitleColor(Color.WHITE);
        panel.setBorder(timeBorder);
        
        panel.setBackground(new Color(30, 30, 50));

        JButton btnSim1  = makeIconButton("Simulate 1 Year",  new Color(150, 200, 150));
        JButton btnSim10 = makeIconButton("Simulate 10 Years", new Color(150, 170, 220));

        btnSim1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String eventMsg = GameEngine.simulateOneYear(player);
                handleTimelineEvent(eventMsg);
                refreshAll();
            }
        });

        btnSim10.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String eventMsg = GameEngine.simulateTenYears(player);
                handleTimelineEvent(eventMsg);
                refreshAll();
            }
        });

        panel.add(btnSim1);
        panel.add(btnSim10);
        return panel;
    }

    private void handleTimelineEvent(String msg) {
        if (msg == null) return;
        
        int row = 0;
        int col = 3; 
        boolean isGoodNews = false; 

        if (msg.contains("WINDFALL")) {
            isGoodNews = true;
            if (msg.contains("bonus")) { row = 0; col = 0; } 
            else if (msg.contains("Market") || msg.contains("Portfolio")) { row = 0; col = 4; } 
            else { row = 0; col = 3; } 
        } else {
            if (msg.contains("market") || msg.contains("correction")) { row = 1; col = 4; } 
            else if (msg.contains("career") || msg.contains("downsizing")) { row = 0; col = 2; } 
        }

        ImageIcon icon = sliceIconFromSheet(row, col);
        String popupTitle = isGoodNews ? "GOOD NEWS!" : "CRITICAL ALERT";
        showCustomPopup(popupTitle, msg, icon, isGoodNews);
    }

    public void updateActionPanel(int age) {
        actionPanel.removeAll();
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonRow.setOpaque(false);

        if (age >= 22 && player.hasCareer() == false) {
            JButton btnEmergencyJob = makeIconButton("Apply for New Job", new Color(255, 200, 100));
            btnEmergencyJob.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Start Career", 0, 2, "Interviews passed! You successfully replaced your career income stream.");
                }
            });
            buttonRow.add(btnEmergencyJob);
        }

        if (age >= 16 && age <= 21) {
            if (player.hasPartTimeJob() == false) {
                JButton btnJob = makeIconButton("Get Part-Time Job", new Color(220, 180, 100));
                btnJob.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        triggerAction("Get Part-Time Job", 0, 0, "You got hired! Time to earn some cash flow.");
                    }
                });
                buttonRow.add(btnJob);
            }
            JButton btnPC  = makeIconButton("Buy Gaming PC", new Color(200, 100, 100)); 
            btnPC.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Buy PC", 0, 1, "Specs are clean! +15 Happiness.");
                }
            });
            buttonRow.add(btnPC);

        } else if (age >= 22 && age <= 29) {
            if (player.hasCareer() == false) { 
                JButton btnCareer = makeIconButton("Start Career", new Color(130, 180, 220));
                btnCareer.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        triggerAction("Start Career", 0, 2, "Welcome to adulthood. Salary unlocked!");
                    }
                });
                buttonRow.add(btnCareer);
            }
            JButton btnLoans   = makeIconButton("Pay Loans", new Color(220, 130, 130));
            JButton btnInvest  = makeIconButton("Invest 50%", new Color(130, 200, 150));
            JButton btnJewelry = makeIconButton("Buy Jewelry", new Color(200, 100, 100)); 

            btnLoans.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Pay Student Loans", 0, 3, "Paid a lump sum toward your education debt.");
                }
            });
            btnInvest.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Invest 50%", 0, 4, "Allocating 50% of your cash into volatile assets.");
                }
            });
            btnJewelry.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Buy Jewelry", 1, 1, "Drip acquired. +20 Happiness.");
                }
            });

            buttonRow.add(btnLoans);
            buttonRow.add(btnInvest);
            buttonRow.add(btnJewelry);

        } else if (age >= 30 && age <= 50) {
            JButton btnHouse  = makeIconButton("Buy House", new Color(200, 160, 100));
            JButton btnKids   = makeIconButton("Have Kids", new Color(220, 140, 180));
            JButton btnInvest = makeIconButton("Invest 50%", new Color(130, 200, 150));
            JButton btnCar    = makeIconButton("Buy Sports Car", new Color(200, 100, 100)); 

            btnHouse.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Buy House", 1, 2, "Down payment sent. You are officially a homeowner!");
                }
            });
            btnKids.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Have Kids", 1, 3, "Congratulations! Life just got much more expensive.");
                }
            });
            btnInvest.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Invest 50%", 0, 4, "Allocating 50% of your cash into volatile assets.");
                }
            });
            btnCar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Buy Sports Car", 1, 4, "Vroom. Mid-life crisis managed. +40 Happiness.");
                }
            });

            buttonRow.add(btnHouse);
            buttonRow.add(btnKids);
            buttonRow.add(btnInvest);
            buttonRow.add(btnCar);

        } else if (age >= 51 && age < 65) {
            JButton btnRetire = makeIconButton("Max Retirement", new Color(180, 140, 220));
            JButton btnBoat   = makeIconButton("Buy Boat", new Color(200, 100, 100)); 
            
            btnRetire.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Max Retirement", 1, 4, "Shoveling maximum capital into senior accounts.");
                }
            });
            btnBoat.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    triggerAction("Buy Boat", 2, 1, "You bought a boat! +30 Happiness.");
                }
            });
            
            buttonRow.add(btnRetire);
            buttonRow.add(btnBoat);
        }

        JLabel ageLabel = new JLabel(getLifeStageName(age), SwingConstants.CENTER);
        ageLabel.setForeground(new Color(180, 180, 220));
        ageLabel.setFont(new Font("Serif", Font.BOLD, 16));

        actionPanel.add(ageLabel, BorderLayout.NORTH);
        actionPanel.add(buttonRow, BorderLayout.CENTER);

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private void triggerAction(String actionName, int row, int col, String message) {
        GameEngine.takeInstantAction(player, actionName);
        ImageIcon popupIcon = sliceIconFromSheet(row, col);
        
        showCustomPopup(actionName, message, popupIcon, true);
        refreshAll();
    }

    private ImageIcon sliceIconFromSheet(int row, int col) {
        if (masterSheet == null) return null;
        try {
            int totalCols = 5;
            int totalRows = 3;
            int boxW = masterSheet.getWidth() / totalCols;
            int boxH = masterSheet.getHeight() / totalRows;

            int startX = col * boxW;
            int startY = row * boxH;

            BufferedImage cropped = masterSheet.getSubimage(startX, startY, boxW, boxH);
            return new ImageIcon(cropped.getScaledInstance(120, 120, Image.SCALE_SMOOTH));
        } catch (Exception ex) {
            return null;
        }
    }

    private void showCustomPopup(String title, String message, ImageIcon icon, boolean isGoodNews) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(30, 30, 45)); 

        JLabel imageLabel = new JLabel(icon);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        String colorHex = isGoodNews ? "#4CAF50" : "#E57373"; 
        String htmlText = "<html><div style='text-align: center; width: 250px; font-family: sans-serif;'>"
                        + "<h2 style='color: " + colorHex + "; margin-bottom: 5px;'>" + title + "</h2>"
                        + "<p style='color: white; font-size: 14px;'>" + message + "</p>"
                        + "</div></html>";
        
        JLabel textLabel = new JLabel(htmlText, SwingConstants.CENTER);
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JButton closeBtn = makeIconButton("Continue", new Color(100, 100, 150));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setPreferredSize(new Dimension(150, 40));
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        }); 

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 45));
        buttonPanel.add(closeBtn);

        dialog.add(imageLabel, BorderLayout.NORTH);
        dialog.add(textLabel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this); 
        dialog.setVisible(true); 
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
        labelAge.setText("Age: " + player.getAge());
        labelCashFlow.setText("Cash Flow: $" + (int)GameEngine.getAnnualCashFlow(player) + "/yr");
        
        nwProgressBar.setValue((int)player.getNetWorth());
        hapProgressBar.setValue(player.getHappiness());
        
        labelNetWorthText.setText(String.format("Net Worth: $%,d / $2,000,000", (int)player.getNetWorth()));
        labelHappinessText.setText("Happiness: " + player.getHappiness() + " / 100");
        
        if (player.getAge() >= 65) {
            showGameOverScreen();
        } else {
            updateActionPanel(player.getAge());
        }
    }

    // --- ADVANCED GRAPHICS: CUSTOM HOVER BUTTONS ---
    private JButton makeIconButton(String label, Color bgColor) {
        JButton button = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g); 
                g2.dispose();
            }
        }; 
        
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(Color.BLACK); 
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); 
        button.setPreferredSize(new Dimension(170, 60)); 
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    // --- ADVANCED GRAPHICS: CUSTOM PROGRESS BAR ---
    class ModernProgressBar extends JProgressBar {
        private Color barColor;

        public ModernProgressBar(int min, int max, Color color) {
            super(min, max);
            this.barColor = color;
            setOpaque(false);
            setBorderPainted(false);
            setStringPainted(false);
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
        String name = JOptionPane.showInputDialog(null, "Enter your name to begin your life:");
        if(name == null || name.trim().isEmpty()) name = "Player";
        
        String finalName = name;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameGUI(finalName);
            }
        });
    }
}