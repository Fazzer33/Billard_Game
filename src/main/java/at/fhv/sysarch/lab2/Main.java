package at.fhv.sysarch.lab2;

import at.fhv.sysarch.lab2.game.Game;
import at.fhv.sysarch.lab2.physics.PhysicsEngine;
import at.fhv.sysarch.lab2.rendering.Renderer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    private final static int SCENE_WIDTH  = 1500;
    private final static int SCENE_HEIGHT = 1000;

    @Override
    public void start(Stage stage) {
        final Group root = new Group();
        final Scene s = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.BURLYWOOD);
        final Canvas c = new Canvas(SCENE_WIDTH, SCENE_WIDTH);

        PhysicsEngine physicsEngine = new PhysicsEngine();

        Renderer renderer = new Renderer(
                c.getGraphicsContext2D(),
                SCENE_WIDTH,
                SCENE_HEIGHT,
                physicsEngine);
        
        Game game = new Game(renderer, physicsEngine);

        physicsEngine.addBallPocketedListener(game);
        physicsEngine.addBallsCollisionListener(game);
        physicsEngine.addBallStrikeListener(game);
        physicsEngine.addObjectsRestListener(game);

        c.setOnMousePressed(game::onMousePressed);
        c.setOnMouseReleased(game::onMouseReleased);
        c.setOnMouseDragged(game::setOnMouseDragged);

        root.getChildren().add(c);
        stage.setScene(s);
        stage.setTitle("Billiard Game");
        stage.show();
        renderer.start();
    }

    public static void main(String[] args) {
        launch();
    }
}