package tangNdam.slither;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Timer;

import static tangNdam.slither.SlitherModel.*;

//Représente la fenêtre principale du jeu, y compris la mise en page,
// les paramètres, le canevas et les fonctionnalités de connexion.
public class SlitherJFrame extends JFrame {
    private final SlitherCanvas canvas;
    private final JTextField serverField;
    private final JTextField nameField;
    private final JComboBox<String> skinSelector;
    private final JButton connectButton;
    private final JLabel rankLabel, killsLabel;
    private final JTextArea logArea;
    public SlitherModel model;
    private JScrollBar logScrollBar;
    private final JTable highscoreTable;
    private JSplitPane splitPane;
    private final Timer updateTimer;
    final Object modelLock = new Object();



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



    public SlitherJFrame() {
        super("My Slither Game");
        this.model = new SlitherModel(this);

        // Pass the model to the canvas
        this.canvas = new SlitherCanvas(this);
        this.canvas.setModel(this.model);
        this.setPreferredSize(new Dimension(1064, 768));

        // Initialize components
        serverField = new JTextField(15);
        nameField = new JTextField("PlayerName", 15);
        skinSelector = new JComboBox<>(SNAKES);
        connectButton = new JButton("Connect");
        rankLabel = new JLabel("Rank: N/A");
        killsLabel = new JLabel("Kills: N/A");
        logArea = new JTextArea(5, 30);
        highscoreTable = new JTable(new Object[10][2], new String[]{"Length", "Name"});

        // Setup layout and add components
        setupLayout();

        // Setup window listener for closing operations
        setupWindowListener();

        // Initialize the timer for game updates
        updateTimer = new Timer();

        // Start the game
        initializeGame();
        startGameLoop();

        // Finalize frame setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
//        setUndecorated(true); //On enleve la barre de titre quitter etc, a faire pour mode full screen
//        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);


        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopGameLoop(); // Stop the game loop before closing
                canvas.stopRepaintTimer(); // Stop the repaint timer of the canvas
                // Any other cleanup if necessary
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { // Exit on escape key
                    SlitherJFrame.this.dispose();
                    GameMenu menu = new GameMenu();
                    menu.setVisible(true);
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();
    }

//    private static final Map<String, Image> SKIN_MAP = new HashMap<>();
//    static {
//        // Populate the SKIN_MAP with name-to-image mappings
//        SKIN_MAP.put("00 - purple", loadImage("path/to/purple_skin.png"));
//        SKIN_MAP.put("01 - blue", loadImage("path/to/blue_skin.png"));
//        SKIN_MAP.put("02 - cyan", loadImage("path/to/cyan_skin.png"));
//        SKIN_MAP.put("03 - green", loadImage("path/to/green_skin.png"));
//        SKIN_MAP.put("04 - yellow", loadImage("path/to/yellow_skin.png"));
//        SKIN_MAP.put("05 - orange", loadImage("path/to/orange_skin.png"));
//        SKIN_MAP.put("06 - salmon", loadImage("path/to/salmon_skin.png"));
//        SKIN_MAP.put("07 - red", loadImage("path/to/red_skin.png"));
//    }

    private static BufferedImage loadImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return bufferedImage;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(createSettingsPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(canvas, BorderLayout.CENTER);
    }
    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.add(new JLabel("Server:"));
        settingsPanel.add(serverField);
        settingsPanel.add(new JLabel("Name:"));
        settingsPanel.add(nameField);
        settingsPanel.add(new JLabel("Skin:"));
        settingsPanel.add(skinSelector);
        settingsPanel.add(connectButton);
        return settingsPanel;
    }

    private JSplitPane createMainPanel() {
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollBar = logScrollPane.getVerticalScrollBar();

        highscoreTable.setEnabled(false);
        JScrollPane highscoreScrollPane = new JScrollPane(highscoreTable);
        highscoreTable.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        highscoreTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, logScrollPane, highscoreScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);

        // Add the canvas to the layout
        add(canvas, BorderLayout.CENTER);

        return splitPane;
    }
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                updateTimer.cancel();
                // Perform any other cleanup you need here
            }
        });
    }

    private void initializeGame() {
        // Example to add a snake
        this.model.initializeGameState();
        // Add more initial game setup here
    }

    private void startGameLoop() {
        // Update the model and refresh the canvas in a loop
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (modelLock) {
                    model.update(); // Update game logic
                    canvas.repaint(); // Refresh the canvas
                }
            }
        }, 0, 1000 / 60); // 60 FPS
    }

    private void stopGameLoop() {
        updateTimer.cancel();
    }



    void setMap(boolean[] map) {
        canvas.setMap(map);
    }

//    void setRank(int newRank, int playerCount) {
//        rank.setText(newRank + "/" + playerCount);
//    }
//
//    void setKills(int newKills) {
//        kills.setText(String.valueOf(newKills));
//    }
//
//    void setHighscoreData(int row, String name, int length, boolean highlighted) {
//        highscoreList.setValueAt(highlighted ? "<html><b>" + length + "</b></html>" : length, row, 0);
//        highscoreList.setValueAt(highlighted ? "<html><b>" + name + "</b></html>" : name, row, 1);
//    }


}
