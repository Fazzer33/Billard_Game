package at.fhv.sysarch.lab2.physics;

import at.fhv.sysarch.lab2.game.Ball;
import at.fhv.sysarch.lab2.game.Table;
import at.fhv.sysarch.lab2.rendering.FrameListener;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;

public class PhysicsEngine implements StepListener, ContactListener, FrameListener {
    private World world;
    private BallPocketedListener ballPocketedListener;
    private BallsCollisionListener ballsCollisionListener;
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

        if ((point.getBody1().getUserData() instanceof Ball && point.getFixture2().getUserData() == Table.TablePart.POCKET)) {
            Ball ball1;
            ball1 = (Ball) point.getBody1().getUserData();

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
}
