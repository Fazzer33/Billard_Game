package at.fhv.sysarch.lab2.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import at.fhv.sysarch.lab2.physics.BallPocketedListener;
import at.fhv.sysarch.lab2.physics.BallsCollisionListener;
import at.fhv.sysarch.lab2.physics.ObjectsRestListener;
import at.fhv.sysarch.lab2.physics.PhysicsEngine;
import at.fhv.sysarch.lab2.rendering.Renderer;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

public class Game implements BallPocketedListener, BallsCollisionListener, BallStrikeListener, ObjectsRestListener {
    private final Renderer renderer;
    private final PhysicsEngine physicsEngine;
    private boolean foul = false;
    private boolean roundOver = true;
    private boolean collisionDetected = false;
    // starting state - 0, player1 - 1, player 2 - 2
    private int player = 0;
    private int scoreCounter = 0;
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;
    private Vector2 whiteBallPos;

    private List<Ball> allBalls = new ArrayList<>();

    private Cue cue;

    public Game(Renderer renderer, PhysicsEngine physicsEngine) {
        this.renderer = renderer;
        this.physicsEngine = physicsEngine;
        this.initWorld();
    }

    public void onMousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = this.renderer.screenToPhysicsX(x);
        double pY = this.renderer.screenToPhysicsY(y);

//        System.out.println(pX);
//        System.out.println(pY);

        cue.setStartX(pX);
        cue.setStartY(pY);
        cue.setEndY(pY);
        cue.setEndX(pX);
        cue.setIsDragged();
    }

    public void onMouseReleased(MouseEvent e) {
        cue.setIsDragged();
        physicsEngine.rayCast();
    }


    public void setOnMouseDragged(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();


        double pX = renderer.screenToPhysicsX(x);
        double pY = renderer.screenToPhysicsY(y);

//        System.out.println(pX);
//        System.out.println(pY);

        cue.setEndX(pX);
        cue.setEndY(pY);
        renderer.setActionMessage("");
        renderer.setFoulMessage("");
    }

    private void placeBalls(List<Ball> balls) {
        Collections.shuffle(balls);

        // positioning the billard balls IN WORLD COORDINATES: meters
        int row = 0;
        int col = 0;
        int colSize = 5;

        double y0 = -2 * Ball.Constants.RADIUS * 2;
        double x0 = -Table.Constants.WIDTH * 0.25 - Ball.Constants.RADIUS;

        for (Ball b : balls) {
            if (player != 0 && balls.size()<15) {
                if (row == 0 && col == 0) {
                    row = 1;
                }
            }
            double y = y0 + (2 * Ball.Constants.RADIUS * row) + (col * Ball.Constants.RADIUS);
            double x = x0 + (2 * Ball.Constants.RADIUS * col);

            b.setPosition(x, y);
            b.getBody().setLinearVelocity(0, 0);

            physicsEngine.addBall(b);
            renderer.addBall(b);

            row++;

            if (row == colSize) {
                row = 0;
                col++;
                colSize--;
            }

        }
    }

    private void initWorld() {
        allBalls = new ArrayList<>();

        for (Ball b : Ball.values()) {
            if (b == Ball.WHITE)
                continue;

            allBalls.add(b);
        }

        this.placeBalls(allBalls);

        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);

        renderer.addBall(Ball.WHITE);

        Table table = new Table();
        cue = new Cue();
        renderer.setTable(table);
        renderer.setCue(cue);

        renderer.setStrikeMessage("Player 1s turn");
    }

    // BallPocketedListener
    @Override
    public boolean onBallPocketed(Ball ball) {
        renderer.removeBall(ball);
        if (ball.getColor() == Color.WHITE) {
            foul = true;
            renderer.setFoulMessage("White ball got pocketed");
            renderer.setActionMessage("Player " + player + " commited a foul, switching players.");

            Ball.WHITE.setPosition(whiteBallPos.x, whiteBallPos.y);
            Ball.WHITE.getBody().setLinearVelocity(0, 0);
            renderer.addBall(Ball.WHITE);

        } else {
            physicsEngine.removeBall(ball);
            scoreCounter++;
        }
        return true;
    }

    // BallsCollisionListener
    @Override
    public void onBallsCollide(Ball b1, Ball b2) {
        if (player != 0) {
            collisionDetected = true;
            if (b1 == Ball.WHITE || b2 == Ball.WHITE) {
                foul = false;
            }
        }
    }

    // BallStrikeListener
    @Override
    public void onBallStrike(Ball ball) {
        whiteBallPos = Ball.WHITE.getBody().getWorldCenter();

        if (player == 0) {
            player = 1;
        }
        roundOver = false;
        if (ball.getColor() != Color.WHITE) {
            foul = true;
            renderer.setActionMessage("Player " + player + " commited a foul, switching players.");
            renderer.setFoulMessage("FOUL: Wrong ball hit!");

        }
    }

    // ObjectRestListener
    @Override
    public void onEndAllObjectsRest() {
        if (!roundOver) {
            if (!collisionDetected) {
                foul = true;
                renderer.setFoulMessage("White ball did not touch any object ball!");
                renderer.setActionMessage("Player " + player + " commited a foul, switching players.");
            }
            if (foul) {
                roundOver = true;
                if (player == 1) {
                    scorePlayer1--;
                    player = 2;
                } else if (player == 2) {
                    scorePlayer2--;
                    player = 1;
                }
                foul = false;
            } else {
                roundOver = true;
                setScore();
            }
            collisionDetected = false;
            scoreCounter = 0;
            renderer.setStrikeMessage("Player " + player + "s turn");
            renderer.setPlayer1Score(scorePlayer1);
            renderer.setPlayer2Score(scorePlayer2);
            reloadBalls();
        }
    }

    private void reloadBalls() {
        List<Body> bodies = physicsEngine.getWorld().getBodies();
        List<Ball> balls = new LinkedList<>();
        for (Body body :
                bodies) {
            if (body.getUserData() instanceof Ball) {
                balls.add((Ball) body.getUserData());
            }
        }

        if (balls.size() <= 2) {
            for (Ball ball :
                    balls) {
                removeBall(ball);
            }
            placeBalls(allBalls);
            for (Ball ball :
                    balls) {
                addBall(ball);
            }
        }

    }

    @Override
    public void onStartAllObjectsRest() {

    }

    public void addBall(Ball b) {
        allBalls.add(b);
    }

    public void removeBall(Ball b) {
        allBalls.remove(b);
    }

}