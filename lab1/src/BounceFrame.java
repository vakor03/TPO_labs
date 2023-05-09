import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BounceFrame extends JFrame {
    public static final int WIDTH = 450;
    public static final int HEIGHT = 350;
    private static final int HOLE_X = 10;
    private static final int HOLE_Y = 10;
    private static final int HOLE_RADIUS = 0;
    private BallCanvas canvas;
    private int ballsInHole = 0;
    private JLabel label = new JLabel("Balls in hole: " + ballsInHole);
    private Hole hole;
    public Hole getHole() {
        return hole;
    }

    public synchronized void incBallsInHole() {
        this.ballsInHole+=1;
        label.setText("Balls in hole: " + ballsInHole);
    }
    public BounceFrame() {
        var bounceFrame = this;
        this.hole = new Hole(HOLE_X, HOLE_Y, HOLE_RADIUS);

        this.setSize(WIDTH, HEIGHT);
        this.setTitle("Bounce programm");
        this.canvas = new BallCanvas();
        canvas.setHole(hole);
        System.out.println("In Frame Thread name = "
                + Thread.currentThread().getName());
        Container content = this.getContentPane();
        content.add(this.canvas, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.lightGray);
        JButton buttonStart = new JButton("Start");
        JButton buttonSpawnMultiple = new JButton("Spawn multiple");
        JButton buttonStop = new JButton("Stop");
        JButton buttonSpawnDependent = new JButton("Spawn dependent");

        int lowPriorityBallsCount = 10;
        int maxPriorityBallsCount = 1;
        int spawnX = 20;
        int spawnY = 20;

        buttonStart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                Ball b = new Ball(canvas);
                canvas.add(b);

                BallThread thread = new BallThread(b, canvas, bounceFrame);
                thread.start();
                System.out.println("Thread name = " +
                        thread.getName());
            }
        });
        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.exit(0);
            }
        });
        buttonSpawnMultiple.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < lowPriorityBallsCount; i++) {
                    Ball b = new Ball(canvas, Color.BLUE, spawnX, spawnY);
                    canvas.add(b);
                    BallThread thread = new BallThread(b, canvas, bounceFrame);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    thread.start();
                }
                for (int i = 0; i < maxPriorityBallsCount; i++) {
                    Ball b = new Ball(canvas, Color.RED, spawnX, spawnY);
                    canvas.add(b);
                    BallThread thread = new BallThread(b, canvas, bounceFrame);
                    thread.setPriority(Thread.MAX_PRIORITY);
                    thread.start();
                }
            }
        });
        buttonSpawnDependent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ball b = new Ball(canvas, Color.BLUE, spawnX, spawnY);
                canvas.add(b);
                BallThread thread = new BallThread(b, canvas, bounceFrame, 100);
                thread.start();

                Ball b2 = new Ball(canvas, Color.YELLOW, spawnX, spawnY);
                canvas.add(b2);
                DependentBallThread thread2 = new DependentBallThread(b2, canvas, bounceFrame, thread, 100);
                thread2.start();
            }
        });


        buttonPanel.add(buttonStart);
        buttonPanel.add(buttonSpawnMultiple);
        buttonPanel.add(buttonSpawnDependent);
        buttonPanel.add(buttonStop);
        buttonPanel.add(label);

        content.add(buttonPanel, BorderLayout.SOUTH);
    }
}
