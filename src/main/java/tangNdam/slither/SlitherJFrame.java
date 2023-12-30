package tangNdam.slither;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayDeque;
import java.util.Timer;
import java.util.TimerTask;

//Représente la fenêtre principale du jeu, y compris la mise en page,
// les paramètres, le canevas et les fonctionnalités de connexion.
public class SlitherJFrame extends JFrame {
    public SlitherModel model;

    public SlitherCanvas canvas;

//    addWindowListener(new WindowAdapter() {
//        @Override
//        public void windowClosing(WindowEvent e) {
//            // Call the cleanup method on mainTest.SlitherCanvas
//            canvas.stopRepaintTimer();
//            // ... [other cleanup actions]
//        }
//    });

private static final String[] SNAKES = {
        "00 - purple",
        "01 - blue",
        "02 - cyan",
        "03 - green",
        "04 - yellow",
        "05 - orange",
        "06 - salmon",
        "07 - red",
        "08 - violet",
        "09 - flag: USA",
        "10 - flag: Russia",
        "11 - flag: Germany",
        "12 - flag: Italy",
        "13 - flag: France",
        "14 - white/red",
        "15 - rainbow",
        "16 - blue/yellow",
        "17 - white/blue",
        "18 - red/white",
        "19 - white",
        "20 - green/purple",
        "21 - flag: Brazil",
        "22 - flag: Ireland",
        "23 - flag: Romania",
        "24 - cyan/yellow +extra",
        "25 - purple/orange +extra",
        "26 - grey/brown",
        "27 - green with eye",
        "28 - yellow/green/red",
        "29 - black/yellow",
        "30 - stars/EU",
        "31 - stars",
        "32 - EU",
        "33 - yellow/black",
        "34 - colorful",
        "35 - red/white/pink",
        "36 - blue/white/light-blue",
        "37 - Kwebbelkop",
        "38 - yellow",
        "39 - PewDiePie",
        "40 - green happy",
        "41 - red with eyes",
        "42 - Google Play",
        "43 - UK",
        "44 - Ghost",
        "45 - Canada",
        "46 - Swiss",
        "47 - Moldova",
        "48 - Vietnam",
        "49 - Argentina",
        "50 - Colombia",
        "51 - Thailand",
        "52 - red/yellow",
        "53 - glowy-blue",
        "54 - glowy-red",
        "55 - glowy-yellow",
        "56 - glowy-orange",
        "57 - glowy-purple",
        "58 - glowy-green",
        "59 - yellow-M",
        "60 - detailed UK",
        "61 - glowy-colorful",
        "62 - purple spiral",
        "63 - red/black",
        "64 - blue/black"
};

    private final JTextField name;
    private final JComboBox<String> snake;
    private final JLabel rank, kills;
    private final JSplitPane rightSplitPane, fullSplitPane;
    private final JTable highscoreList;

    final Object modelLock = new Object();


    private Timer gameUpdateTimer;

//    mainTest.SlitherJFrame() {
//        super("MySlither");
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//
//        getContentPane().setLayout(new BorderLayout());
//
//        canvas = new mainTest.SlitherCanvas(this);
//
//        // === upper row ===
//        JPanel settings = new JPanel(new GridBagLayout());
//
//        name = new JTextField("MySlitherPlayer", 16);
//
//        snake = new JComboBox<>(SNAKES);
//        snake.setMaximumRowCount(snake.getItemCount());
//
//        rank = new JLabel();
//        kills = new JLabel();
//
//        // Add components to settings panel
//        settings.add(new JLabel("name:"),
//                new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
//        settings.add(name,
//                new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
//        settings.add(new JLabel("skin:"),
//                new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
//        settings.add(snake,
//                new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
//
//        JComponent upperRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        upperRow.add(settings);
//        getContentPane().add(upperRow, BorderLayout.NORTH);
//
//        // === center ===
//        highscoreList = new JTable(10, 2);
//        highscoreList.setEnabled(false);
//        highscoreList.getColumnModel().getColumn(0).setMinWidth(64);
//        highscoreList.getColumnModel().getColumn(1).setMinWidth(192);
//        highscoreList.getColumnModel().getColumn(0).setHeaderValue("length");
//        highscoreList.getColumnModel().getColumn(1).setHeaderValue("name");
//        highscoreList.getTableHeader().setReorderingAllowed(false);
//        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
//        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
//        highscoreList.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
//        highscoreList.setPreferredScrollableViewportSize(new Dimension(64 + 192, highscoreList.getPreferredSize().height));
//
//        // == split-panes ==
//        rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, canvas, new JScrollPane(highscoreList));
//        rightSplitPane.setDividerSize(rightSplitPane.getDividerSize() * 4 / 3);
//        rightSplitPane.setResizeWeight(0.99);
//
//        fullSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, new JPanel(), rightSplitPane);
//        fullSplitPane.setDividerSize(fullSplitPane.getDividerSize() * 4 / 3);
//        fullSplitPane.setResizeWeight(0.1);
//
//        getContentPane().add(fullSplitPane, BorderLayout.CENTER);
//
//        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
//        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
//        setSize(screenWidth * 3 / 4, screenHeight * 4 / 5);
//        setLocation((screenWidth - getWidth()) / 2, (screenHeight - getHeight()) / 2);
//        setExtendedState(MAXIMIZED_BOTH);
//
//        validate();
//        model = new mainTest.SlitherModel(1000, 50, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 100, this);
//    }

    SlitherJFrame() {
        super("MySlither");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());

        canvas = new SlitherCanvas(this);

        // Initialize settings panel
        JPanel settings = new JPanel(new GridBagLayout());

        name = new JTextField("MySlitherPlayer", 16);

        String[] SNAKES = {"Skin1", "Skin2", "Skin3"}; // Replace with your actual skins
        snake = new JComboBox<>(SNAKES);
        snake.setMaximumRowCount(snake.getItemCount());

        rank = new JLabel("Rank: N/A");
        kills = new JLabel("Kills: N/A");

        // Add components to settings panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        settings.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        settings.add(name, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        settings.add(new JLabel("Skin:"), gbc);

        gbc.gridx = 1;
        settings.add(snake, gbc);

        // Add upper row
        JPanel upperRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        upperRow.add(settings);
        getContentPane().add(upperRow, BorderLayout.NORTH);

        // Initialize highscore list
        highscoreList = new JTable(10, 2);
        highscoreList.setEnabled(false);
        highscoreList.getColumnModel().getColumn(0).setMinWidth(64);
        highscoreList.getColumnModel().getColumn(1).setMinWidth(192);
        highscoreList.getColumnModel().getColumn(0).setHeaderValue("Length");
        highscoreList.getColumnModel().getColumn(1).setHeaderValue("Name");
        highscoreList.getTableHeader().setReorderingAllowed(false);

        // Setup split panes
        rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, canvas, new JScrollPane(highscoreList));
        fullSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, new JPanel(), rightSplitPane);
        getContentPane().add(fullSplitPane, BorderLayout.CENTER);

        // Window size and location
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        setSize(screenWidth * 3 / 4, screenHeight * 4 / 5);
        setLocationRelativeTo(null);
        setExtendedState(Frame.MAXIMIZED_BOTH);

        // Initialize model and start game loop
        model = new SlitherModel(1000, 50, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 100, this);
        initializeGame();
        startGameLoop();

        // Handle window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopGameLoop();
                canvas.stopRepaintTimer();
            }
        });

        validate();
    }


    private void initializeGame() {
        // Example to add a snake
        ArrayDeque<SnakeBody> snakeBody = new ArrayDeque<>();
        snakeBody.add(new SnakeBody(100, 100));
        model.addSnake(1, "Snake1", 100, 100, 0, 0, 1, 0, snakeBody);
        // Add more initial game setup here
    }

    void startGameLoop() {
        gameUpdateTimer = new Timer();
        gameUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (modelLock) {
                    model.update();
                }
                canvas.repaint();
            }
        }, 0, 1000 / 60); // Update at approximately 60 FPS
    }

    private void stopGameLoop() {
        if (gameUpdateTimer != null) {
            gameUpdateTimer.cancel();
        }
    }

    void setMap(boolean[] map) {
        canvas.setMap(map);
    }

    void setRank(int newRank, int playerCount) {
        rank.setText(newRank + "/" + playerCount);
    }

    void setKills(int newKills) {
        kills.setText(String.valueOf(newKills));
    }

    void setHighscoreData(int row, String name, int length, boolean highlighted) {
        highscoreList.setValueAt(highlighted ? "<html><b>" + length + "</b></html>" : length, row, 0);
        highscoreList.setValueAt(highlighted ? "<html><b>" + name + "</b></html>" : name, row, 1);
    }


}
