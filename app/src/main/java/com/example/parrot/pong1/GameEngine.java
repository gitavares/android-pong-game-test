package com.example.parrot.pong1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameEngine extends SurfaceView implements Runnable {

    // -----------------------------------
    // ## ANDROID DEBUG VARIABLES
    // -----------------------------------

    // Android debug variables
    final static String TAG="XXXX";

    // -----------------------------------
    // ## SCREEN & DRAWING SETUP VARIABLES
    // -----------------------------------

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;


    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;



    // -----------------------------------
    // ## GAME SPECIFIC VARIABLES
    // -----------------------------------

    // ----------------------------
    // ## SPRITES
    // ----------------------------
    int ballXPosition;      // keep track of ball -x
    int ballYPosition;      // keep track of ball -y

    int ballWidth;
    int ballHeight;

    int racketXPosition;  // top left corner of the racket
    int racketYPosition;  // top left corner of the racket

    int racketWidth;
    int racketHeight;


    // ----------------------------
    // ## GAME STATS - number of lives, score, etc
    // ----------------------------
    int score;
    int lives;


    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;


        this.printScreenInfo();

        this.initialValues();

    }

    // ------------------------------
    // HELPER FUNCTIONS
    // ------------------------------

    public void initialValues(){
        this.ballXPosition = this.screenWidth / 2;
        this.ballYPosition = 1;

        this.ballWidth = 50;
        this.ballHeight = 50;

        this.racketXPosition = 525;
        this.racketYPosition = 1700;

        this.racketWidth = 400;
        this.racketHeight = 50;
    }


    // This funciton prints the screen height & width to the screen.
    private void printScreenInfo() {
        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }


    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        this.score = 0;
        this.lives = 3;
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------


    String directionYBallIsMoving = "down";
    String directionXBallIsMoving = "center";
    String personTapped = "right";

    // 1. Tell Android the (x,y) positions of your sprites
    // refactor
    // detected better the racket and ball position
    public void updatePositions() {
        // @TODO: Update the position of the sprites

        if (directionYBallIsMoving == "down") {
            this.ballYPosition = this.ballYPosition + 10;

            changeBallXDirection();

            if (ballYPosition == (racketYPosition + racketHeight) + 1) {
                Log.d(TAG, "BALL PASSED THE RACKET / OUT OF BOUNDS");
                Log.d(TAG, "Life: " + this.lives);
                Log.d(TAG, "ballYPosition: " + ballYPosition);
                Log.d(TAG, "racketYPosition + racketHeight: " + (racketYPosition + racketHeight + 1));
                if(this.lives > 1){
                    this.lives -= 1;
                    this.initialValues();
                } else {
                    this.gameOver();
                }
            }
        }
        if (directionYBallIsMoving == "up") {
            this.ballYPosition = this.ballYPosition - 10;

            changeBallXDirection();

            // if ball hits ceiling, then change directions
            if (this.ballYPosition <= 0 ) {
                // hit upper wall
                Log.d(TAG,"BALL HIT CEILING / OUT OF BOUNDS ");
                directionYBallIsMoving = "down";

                changeBallXDirection();
            }
        }

        // if the ball touche the walls, change the X direction
        if(ballXPosition + ballWidth >= screenWidth){
            directionXBallIsMoving = "left";
            ballXPosition -= 10;
        } else if(ballXPosition <= 0){
            directionXBallIsMoving = "right";
            ballXPosition += 10;
        }


        // calculate the racket's new position
        if (personTapped.contentEquals("right")){
            this.racketXPosition = this.racketXPosition + 10;

            if(racketXPosition + racketWidth >= screenWidth){
                personTapped = "left";
            }
        }
        else if (personTapped.contentEquals("left")){
            this.racketXPosition = this.racketXPosition - 10;

            if(racketXPosition <= 0){
                personTapped = "right";
            }
        }



        // @TODO: Collision detection code

        // detect when ball hits the racket
        // ---------------------------------
        if (ballYPosition >= racketYPosition - racketHeight
            && ballXPosition <= (racketXPosition + racketWidth)
            && ballXPosition >= (racketXPosition - racketWidth)) {
//            Log.d(TAG, "ballYPosition: " + ballYPosition + ", racketYPosition: " + racketYPosition);
//            Log.d(TAG, "racketYPosition - racketHeight: " + (racketYPosition - racketHeight));
            Log.d(TAG, "ballXPosition: " + ballXPosition + ", racketXPosition: " + racketXPosition);
            Log.d(TAG, "racketWidth: " + racketWidth + ", racketXPosition + racketWidth: " + (racketXPosition + racketWidth));
            Log.d(TAG, "racketWidth: " + racketWidth + ", racketXPosition - racketWidth: " + (racketXPosition - racketWidth));

            // ball is touching racket
            Log.d(TAG, "Ball IS TOUCHING RACKET!");

            if(ballXPosition <= racketXPosition){
                directionXBallIsMoving = "left";
                Log.d(TAG, "Ball is going to LEFT!");
            } else if(ballXPosition > racketXPosition) {
                directionXBallIsMoving = "right";
                Log.d(TAG, "Ball is going to RIGHT!");
            }
            directionYBallIsMoving = "up";

            // increase the game score!
            this.score = this.score + 50;
        }

    }

    // 2. Tell Android to DRAW the sprites at their positions
    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------
            // Put all your drawing code in this section

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,0,0,255));
            paintbrush.setColor(Color.WHITE);

            //@TODO: Draw the sprites (rectangle, circle, etc)

            // 1. Draw the ball
            this.canvas.drawRect(
                    ballXPosition,
                    ballYPosition,
                    ballXPosition + ballWidth,
                    ballYPosition + ballHeight,
                    paintbrush);

            // draw the racket
            this.canvas.drawRect(
                    racketXPosition,
                    racketYPosition,
                    racketXPosition + racketWidth,
                    racketYPosition + racketHeight,
                    paintbrush);


            //@TODO: Draw game statistics (lives, score, etc)
            paintbrush.setTextSize(60);
            canvas.drawText("Score: " + this.score, 20, 100, paintbrush);
            canvas.drawText("Life: " + this.lives, 20, 160, paintbrush);

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    // Sets the frame rate of the game
    public void setFPS() {
        try {
            gameThread.sleep(10);
        }
        catch (Exception e) {

        }
    }

    public void changeBallXDirection(){
        if(directionXBallIsMoving == "left"){
            ballXPosition -= 10;
        } else if(directionXBallIsMoving == "right") {
            ballXPosition += 10;
        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {
            if(gameIsRunning){
                float fingerXposition = event.getX();

                float middleScreen = this.screenWidth / 2;

                if(fingerXposition < middleScreen){
                    personTapped = "left";
                } else {
                    personTapped = "right";
                }
            } else {
                startGame();
            }
        }
        else if (userAction == MotionEvent.ACTION_UP) {
            // user lifted their finger
        }
        return true;
    }


    public void gameOver(){
        if (this.holder.getSurface().isValid()) {

            this.canvas = this.holder.lockCanvas();
            this.canvas.drawColor(Color.argb(255, 0, 0, 0));
            paintbrush.setColor(Color.WHITE);
            paintbrush.setTextSize(60);
            canvas.drawText("Score: " + this.score, screenWidth/2 - 120, screenHeight - 800, paintbrush);
            paintbrush.setTextSize(200);
            canvas.drawText("GAME OVER", screenWidth/2 - 550, screenHeight / 4, paintbrush);
            paintbrush.setColor(Color.YELLOW);
            paintbrush.setTextSize(100);
            canvas.drawText("TAP to PLAY AGAIN", screenWidth / 5, screenHeight - 500, paintbrush);
            this.holder.unlockCanvasAndPost(canvas);

            this.initialValues();
            this.pauseGame();

        }
    }
}



// intersect() ---> search for it. collision detect