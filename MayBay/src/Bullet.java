import java.awt.*;

public class Bullet {
    int x, y;
    int size = 10;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= 8;
    }

    public void draw(Graphics g) {
        g.setColor(Color.yellow);
        g.fillRect(x, y, size, size); 
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, size, size);
    }
}