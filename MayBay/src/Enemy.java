import java.awt.*;
import javax.swing.ImageIcon;
import java.awt.Image;

public class Enemy {
    int x, y;
    int size = 80;
    int hp = 40;
    Image enemyImg;
    
    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;      
        try {
            enemyImg = new ImageIcon(
                getClass().getResource("/images/enemy.png")
            ).getImage();
        } catch (Exception e) {
            System.out.println("Không load được ảnh quái");
        }
    }

    public void update() {
        y += 2;
    }

    public void draw(Graphics g) {
        g.drawImage(enemyImg, x, y, size, size, null);
        g.setColor(Color.darkGray);
        g.fillRect(x, y - 6, size, 4);
        g.setColor(Color.green);
        int hpWidth = (int)((double)hp / 40 * size);
        g.fillRect(x, y - 6, hpWidth, 4);
        g.setColor(Color.white);
        g.drawString("" + hp, x + 10, y - 8);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, size, size);
    }
}