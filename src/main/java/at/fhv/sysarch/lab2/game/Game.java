package at.fhv.sysarch.lab2.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.fhv.sysarch.lab2.physics.BallPocketedListener;
import at.fhv.sysarch.lab2.physics.BallsCollisionListener;
import at.fhv.sysarch.lab2.physics.ObjectsRestListener;
import at.fhv.sysarch.lab2.rendering.Renderer;
import javafx.scene.input.MouseEvent;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Polygon;

public class Game implements BallPocketedListener, BallsCollisionListener, ObjectsRestListener {
    private final Renderer renderer;

    public Game(Renderer renderer) {      
        this.renderer = renderer;
        this.initWorld();
    }

    public void onMousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = this.renderer.screenToPhysicsX(x);
        double pY = this.renderer.screenToPhysicsY(y);
    }

    public void onMouseReleased(MouseEvent e) {
        System.out.println(this.renderer.screenToPhysicsX(e.getX()));
        System.out.println(this.renderer.screenToPhysicsX(e.getY()));
    }

    public void setOnMouseDragged(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = renderer.screenToPhysicsX(x);
        double pY = renderer.screenToPhysicsY(y);
    }

    private void placeBalls(List<Ball> balls) {
        Collections.shuffle(balls);

        // positioning the billard balls IN WORLD COORDINATES: meters
        int row = 0;
        int col = 0;
        int colSize = 5;

        double y0 = -2*Ball.Constants.RADIUS*2;
        double x0 = -Table.Constants.WIDTH * 0.25 - Ball.Constants.RADIUS;

        for (Ball b : balls) {
            double y = y0 + (2 * Ball.Constants.RADIUS * row) + (col * Ball.Constants.RADIUS);
            double x = x0 + (2 * Ball.Constants.RADIUS * col);

            b.setPosition(x, y);
            b.getBody().setLinearVelocity(0, 0);

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
        List<Ball> balls = new ArrayList<>();
        
        for (Ball b : Ball.values()) {
            if (b == Ball.WHITE)
                continue;

            balls.add(b);
        }
       
        this.placeBalls(balls);

        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);
        
        renderer.addBall(Ball.WHITE);
        
        Table table = new Table();
        renderer.setTable(table);
    }

    @Override
    public boolean onBallPocketed(Ball b) {

        renderer.removeBall(b);

        return true;
    }

    @Override
    public void onBallsCollide(Ball b1, Ball b2) {
        if (b1.getBody().getWorldCenter().x == b2.getBody().getWorldCenter().x &&
                b1.getBody().getWorldCenter().y == b2.getBody().getWorldCenter().y) {
            System.out.println("Collision");
        }
    }

    @Override
    public void onEndAllObjectsRest() {

    }

    @Override
    public void onStartAllObjectsRest() {

    }
}