import java.awt.*;

public class Hole {
    private int x;
    private int y;
    private int radius;

    public Hole(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public boolean BallInHole(Ball ball){
        int ballX = ball.getX();
        int ballY = ball.getY();
        int ballXSize = ball.getXsize();
        int ballYSize = ball.getYsize();
        int ballCenterX = ballX + ballXSize/2;
        int ballCenterY = ballY + ballYSize/2;
        int holeCenterX = x + radius;
        int holeCenterY = y + radius;
        int distance = (int) Math.sqrt(Math.pow(ballCenterX - holeCenterX, 2) + Math.pow(ballCenterY - holeCenterY, 2));

        return distance <= radius;
    }

    public void draw(Graphics2D g2){
        g2.setColor(Color.black);
        g2.fillOval(x, y, radius*2, radius*2);
    }
}
