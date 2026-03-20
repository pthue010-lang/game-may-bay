import javax.swing.*; // dùng swing để tạo giao diện(jpanel,timer,jframe..)
import java.awt.*;    // vẽ hình và màu sắc
import java.awt.event.*; // xử lý chuột bàn phím
import java.util.ArrayList; 
import java.util.Iterator; // duyệt và xóa phần tủ
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    Timer timer = new Timer(16, this);
    player player = new player(280, 420);
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();

    boolean left, right, up, down;
    Random rand = new Random(); // tạo quái ở vị trí ngẫu nhiên
    int highScore = 0;
    int score = 0;
    boolean gameOver = false;
    private Rectangle nutChoiLai = new Rectangle(220, 250, 150, 50);
    private Image backgroundImage;
    private int backgroundY;

    public GamePanel() {
        setPreferredSize(new Dimension(600, 500));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);
        SoundEngine.playBackgroundMusic();
        backgroundImage = new ImageIcon(getClass().getResource("/images/space.png")).getImage();
        
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (gameOver && nutChoiLai.contains(e.getPoint())) {
                    resetGame();
                    gameOver = false;
                }
            } 
        });
        timer.start();
        loadHighScore();
    }

    public void spawnEnemy() {
        int x = rand.nextInt(getWidth() - 80);
        enemies.add(new Enemy(x, 0));
    }

    public void shoot() {
        bullets.add(new Bullet(player.x + player.size/2 - 5, player.y));
        SoundEngine.shoot();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, backgroundY, getWidth(), getHeight(), null);
        g.drawImage(backgroundImage, 0, backgroundY - getHeight(), getWidth(), getHeight(), null);
        player.draw(g);

        for (Bullet b : bullets) b.draw(g);
        for (Enemy e : enemies) e.draw(g);

        g.setColor(Color.white);
        g.drawString("HP: " + player.hp, 10, 20);
        g.drawString("Score: " + score, 10, 40);
        g.drawString("High Score: " + highScore, 10, 60);

        if (gameOver) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // ====== VẼ GAME OVER Ở TRUNG TÂM ======
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.setColor(Color.RED);

            String gameOverText = "GAME OVER";
            FontMetrics fm1 = g.getFontMetrics();

            int x1 = (panelWidth - fm1.stringWidth(gameOverText)) / 2;
            int y1 = panelHeight / 2 - 40;

            g.drawString(gameOverText, x1, y1);

            // ====== VẼ NÚT CHƠI LẠI Ở TRUNG TÂM ======
            int buttonWidth = 180;
            int buttonHeight = 50; 

            int buttonX = (panelWidth - buttonWidth) / 2;
            int buttonY = panelHeight / 2 + 10;

            nutChoiLai.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);

            // vẽ chữ trong nút
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.setColor(Color.YELLOW);

            String buttonText = "CHOI LAI";
            FontMetrics fm2 = g.getFontMetrics();

            int textX = buttonX + (buttonWidth - fm2.stringWidth(buttonText)) / 2;
            int textY = buttonY + (buttonHeight + fm2.getAscent()) / 2 - 5;

            g.drawString(buttonText, textX, textY);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        if (left) player.x -= 5;
        if (right) player.x += 5;
        if (up) player.y -= 5;
        if (down) player.y += 5;
        
        // không cho máy bay ra ngoài màn hình
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        if (player.x < 0) player.x = 0;
        if (player.y < 0) player.y = 0;
        if (player.x > panelWidth - player.size)
            player.x = panelWidth - player.size;
        if (player.y > panelHeight - player.size)
            player.y = panelHeight - player.size;

        if (rand.nextInt(80) == 0) spawnEnemy();
        for (Bullet b : bullets) b.update();
        for (Enemy en : enemies) en.update();

        // đạn trúng quái
        Iterator<Bullet> itB = bullets.iterator();
        while (itB.hasNext()) {
            Bullet b = itB.next();
            Iterator<Enemy> itE = enemies.iterator();
            while (itE.hasNext()) {
                Enemy en = itE.next();
                if (b.getRect().intersects(en.getRect())) {                                                        
                    en.hp -= 20;
                    itB.remove();
                    if (en.hp <= 0) {
                        itE.remove();
                        score += 10;
                    }
                    break;
                }
            }
        }

        // quái chạm đáy
        Iterator<Enemy> itE = enemies.iterator();
        while (itE.hasNext()) {
            Enemy en = itE.next();
            if (en.y > getHeight()) {
                player.hp -= 10;
                itE.remove();
                if (player.hp <= 0) {
                    gameOver = true;
                    if (score > highScore) {
                        highScore = score;
                        saveHighScore();
                    }
                }
            }
        }
        backgroundY += 2; // tốc độ cuộn
        if (backgroundY >= getHeight()) {
            backgroundY = 0;
        }
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) left = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) right = true;
        if (e.getKeyCode() == KeyEvent.VK_UP) up = true;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) down = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) shoot();
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) left = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) right = false;
        if (e.getKeyCode() == KeyEvent.VK_UP) up = false;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) down = false;
    }

    public void keyTyped(KeyEvent e) {}
    
    void loadHighScore() {
        try {
            java.io.File file = new java.io.File("highscore.txt");
            if (!file.exists()) return;

            java.util.Scanner sc = new java.util.Scanner(file);
            if (sc.hasNextInt()){
                highScore = sc.nextInt();
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Không đọc được high score");
        }
    } 

    void saveHighScore() {
        try {
            java.io.PrintWriter pw = new java.io.PrintWriter("highscore.txt");
            pw.println(highScore);
            pw.close();
        } catch (Exception e) {
            System.out.println("Không lưu được high score");
        }
    }
    
    public void resetGame() {
        score = 0;
        player.hp = 100;
        enemies.clear();
        bullets.clear(); 
        // Reset vị trí player về giữa màn hình
        player.x = getWidth() / 2 - player.size / 2;
        player.y = getHeight() - 100;
    }
}
