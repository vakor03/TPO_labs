public class BallThread extends Thread {
    private Ball ball;
    private BallCanvas canvas;
    private BounceFrame bounceFrame;
    private int framesCount = 10_000;

    public BallThread(Ball ball, BallCanvas canvas, BounceFrame bounceFrame) {
        this.ball = ball;
        this.canvas = canvas;
        this.bounceFrame = bounceFrame;
    }

    public BallThread(Ball ball, BallCanvas canvas, BounceFrame bounceFrame, int framesCount) {
        this.ball = ball;
        this.canvas = canvas;
        this.bounceFrame = bounceFrame;
        this.framesCount = framesCount;
    }


    @Override
    public void run() {
        try {
            for (int i = 1; i < framesCount; i++) {
                ball.move();
                System.out.println("Thread name = "
                        + Thread.currentThread().getName());
                Thread.sleep(5);

                if (bounceFrame.getHole().BallInHole(ball)) {
                    canvas.remove(ball);
                    canvas.repaint();
                    bounceFrame.incBallsInHole();
                    return;
                }
            }
        } catch (InterruptedException ex) {
        }
    }
}

