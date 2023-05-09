public class DependentBallThread extends Thread {
    private Ball ball;
    private BallCanvas canvas;
    private BounceFrame bounceFrame;
    private BallThread ballThread;
    private int framesCount = 10_000;

    public DependentBallThread(Ball ball, BallCanvas canvas, BounceFrame bounceFrame, BallThread ballThread) {
        this.ball = ball;
        this.canvas = canvas;
        this.bounceFrame = bounceFrame;
        this.ballThread = ballThread;
    }

    public DependentBallThread(Ball ball, BallCanvas canvas, BounceFrame bounceFrame, BallThread ballThread, int framesCount) {
        this.ball = ball;
        this.canvas = canvas;
        this.bounceFrame = bounceFrame;
        this.ballThread = ballThread;
        this.framesCount = framesCount;
    }



    @Override
    public void run() {
        try {
            ballThread.join();
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
