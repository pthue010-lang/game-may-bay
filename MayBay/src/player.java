import java.awt.*;
import javax.swing.ImageIcon;
import java.awt.Image;

public class player {
    int x, y;
    int size = 100;
    int hp = 100;
    Image planeImg;

    public player(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            planeImg = new ImageIcon(
                getClass().getResource("/images/plane.png")
            ).getImage();
        } catch (Exception e) {
            System.out.println("Khong load đuoc anh may bay");
        }
    }

    public void draw(Graphics g) {
        g.drawImage(planeImg, x, y, size, size, null); 
    }
}