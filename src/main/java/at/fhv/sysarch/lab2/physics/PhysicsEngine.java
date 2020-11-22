package at.fhv.sysarch.lab2.physics;

import at.fhv.sysarch.lab2.game.Ball;
import at.fhv.sysarch.lab2.game.BallStrikeListener;
import at.fhv.sysarch.lab2.game.Cue;
import at.fhv.sysarch.lab2.game.Table;
import at.fhv.sysarch.lab2.rendering.FrameListener;
import org.dyn4j.dynamics.*;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class PhysicsEngine implements StepListener, ContactListener, FrameListener {
    private World world;
    private Cue cue;
    private BallPocketedListener ballPocketedListener;
    private BallsCollisionListener ballsCollisionListener;
    private BallStrikeListener ballStrikeListener;
    private ObjectsRestListener objectsRestListener;

    public PhysicsEngine() {
        world = new World();
        world.setGravity(World.ZERO_GRAVITY);
        world.addListener(this);

    }

    // Step Listener wird bei jedem Frame aufgerufen
    // Contact wird bei collission aufgerufen
    @Override
    public void begin(Step step, World world) {


    }

    @Override
    public void updatePerformed(Step step, World world) {

    }

    @Override
    public void postSolve(Step step, World world) {


    }

    @Override
    public void end(Step step, World world) {
        // alle bodies von der world holen und überprüfen ob sie sich noch bewegen
        if (checkVelocityOnAll()) {
            objectsRestListener.onEndAllObjectsRest();
        }
    }

    // Frame Listener
    @Override
    public void onFrame(double dt) {
        world.update(dt);
    }

    // Contact Listener
    @Override
    public void sensed(ContactPoint point) {
    }

    @Override
    public boolean begin(ContactPoint point) {
        if (point.getBody1().getUserData() instanceof Ball && point.getBody2().getUserData() instanceof Ball) {
            Ball ball1 = (Ball) point.getBody1().getUserData();
            Ball ball2 = (Ball) point.getBody2().getUserData();
            ballsCollisionListener.onBallsCollide(ball1, ball2);
        }

        return true;
    }

    @Override
    public void end(ContactPoint point) {


    }

    @Override
    public boolean persist(PersistedContactPoint point) {

        if ((point.getBody1().getUserData() instanceof Ball && point.getFixture2().getUserData() == Table.TablePart.POCKET ||
                point.getFixture1().getUserData() == Table.TablePart.POCKET && point.getBody2().getUserData() instanceof Ball)) {
            Ball ball1;
            ball1 = (Ball) point.getBody1().getUserData();
            System.out.println("collide with pocket");

            double deltaX = getDelta(ball1.getBody().getWorldCenter().x, point.getFixture2().getShape().getCenter().x);
            double deltaY = getDelta(ball1.getBody().getWorldCenter().y, point.getFixture2().getShape().getCenter().y);

            if (deltaX < point.getFixture2().getShape().getRadius() - 0.02 && deltaY < point.getFixture2().getShape().getRadius() - 0.02) {
                ballPocketedListener.onBallPocketed(ball1);
                return true;
            }

        }
        return true;
    }

    @Override
    public boolean preSolve(ContactPoint point) {
        return true;
    }

    @Override
    public void postSolve(SolvedContactPoint point) {

    }

    public void addBallPocketedListener(BallPocketedListener listener) {
        ballPocketedListener = listener;
    }

    public void addBallsCollisionListener(BallsCollisionListener listener) {
        ballsCollisionListener = listener;
    }

    public void addBallStrikeListener(BallStrikeListener listener) {
        ballStrikeListener = listener;
    }

    public void addObjectsRestListener(ObjectsRestListener listener) {
        objectsRestListener = listener;
    }

    public World getWorld() {
        return world;
    }

    public double getDelta(double num1, double num2) {
        if (num1 < num2) {
            return Math.abs(num2 - num1);
        } else {
            return Math.abs(num1 - num2);
        }
    }

    public boolean checkVelocityOnAll() {
        boolean rest = true;
        for (Body body : this.world.getBodies()) {
            if (body.getLinearVelocity().x != 0 && body.getLinearVelocity().y != 0) {
                rest = false;
            }
        }
        return rest;
    }

    public void addCue(Cue cue) {
        this.cue = cue;
    }

    public void rayCast() {
        List<RaycastResult> raycastResults = new LinkedList<>();
        Ray ray = new Ray(new Vector2(cue.getStartX(), cue.getStartY()),
                new Vector2(cue.getStartX() - cue.getEndX(), cue.getStartY() - cue.getEndY()));
        world.raycast(ray, 0.05, true, false, raycastResults);
        if(raycastResults.size() == 1){
            RaycastResult result = raycastResults.get(0);
            if(result.getBody().getUserData() instanceof Ball) {
                Ball ball = (Ball) result.getBody().getUserData();
                Vector2 strikePoint = result.getRaycast().getPoint();
                Vector2 force = new Vector2(cue.getStartX() - cue.getEndX(), cue.getStartY() - cue.getEndY());
                ball.getBody().applyForce(new Vector2(force.x*420, force.y*420), strikePoint);
                ballStrikeListener.onBallStrike(ball);
            }
        }

    }
}
