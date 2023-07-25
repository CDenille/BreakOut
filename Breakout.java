package breakout;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.event.*;
import javafx.util.Duration;
import java.util.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;

public class Breakout extends Application {
    Scoring score=new Scoring();
    
    @Override
    public void start(Stage window) {
        //creates main layout
        score.setLives();
        
        Pane layout= new Pane();
        layout.setStyle("-fx-background-color: black");
        layout.setPrefSize(618,400); 
        
        
        Scene view= new Scene(layout);
        
        //creates components and adds them to the main layout
        
        ArrayList<Rectangle> allBricks = new ArrayList<>();
        for(int x=0; x<10; x++) {
            for(int y=0; y<8; y++) {
                Rectangle brick=new Rectangle(60,15);
                if(y<=1){
                    brick.setFill(Color.RED);
                }
                if(y>1 && y<=3){
                    brick.setFill(Color.ORANGE);
                }
                if(y>3 && y<=5){
                    brick.setFill(Color.GREEN); 
                }
                if(y>5 && y<8){
                    brick.setFill(Color.YELLOW);
                }
                brick.setLayoutX(x*62);
                brick.setLayoutY((y*16)+35);
                layout.getChildren().add(brick);
                allBricks.add(brick);
            }
        }
        Circle ball= new Circle(7,Color.BLUE);
        ball.relocate(300, 200);
        
        Rectangle paddle= new Rectangle(90,10, Color.ORANGERED);
        paddle.relocate(275, 393);  
        
        Label scoreLabel =new Label("Score: "+ score.getScore());
        scoreLabel.setLayoutX(400);
        scoreLabel.setLayoutY(10);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(Font.font("Monospaced", 20));
        
        Label livesLabel =new Label("Lives: " + score.getLives());
        livesLabel.setLayoutX(60);
        livesLabel.setLayoutY(10);
        livesLabel.setTextFill(Color.WHITE);
        livesLabel.setFont(Font.font("Monospaced", 20));
        
        layout.getChildren().addAll(paddle, scoreLabel, livesLabel, ball);
        
        //controls paddle movement
        
       int movement = 18;
        
        view.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                if(paddle.getLayoutX() < 0) {
                     paddle.setLayoutX(paddle.getLayoutX()+movement);
                }
                paddle.setLayoutX(paddle.getLayoutX()-movement);
            }

            if (event.getCode() == KeyCode.RIGHT) {
                if(paddle.getLayoutX() > 510) {
                     paddle.setLayoutX(510);
                } 
                paddle.setLayoutX(paddle.getLayoutX()+movement);
            }
        });
        
        
        //creates an indefinite bouncing ball
        
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(new KeyFrame(Duration.millis(20), new EventHandler<ActionEvent>() {

            double dx = 4; 
            double dy = 2; 
        	
            @Override
            public void handle(ActionEvent b) {
            	//ball movement
            	ball.setLayoutX(ball.getLayoutX() + dx);
            	ball.setLayoutY(ball.getLayoutY() + dy);
                
                final Bounds bounds=layout.getBoundsInLocal();
                
                boolean leftWall = ball.getLayoutX() <= (bounds.getMinX() +ball.getRadius()); 
                boolean topWall = ball.getLayoutY() <= ((bounds.getMinY()+35) +ball.getRadius());
                boolean rightWall = ball.getLayoutX() >= (bounds.getMaxX() -ball.getRadius()); 
                boolean bottomWall = ball.getLayoutY() >= (bounds.getMaxY() -ball.getRadius());
                
                // If the top wall has been touched, the ball reverses direction.
                if (topWall) {
                   dy = dy * -1;
                }
                
                // If the left or right wall has been touched, the ball reverses direction.
                if (leftWall || rightWall) {
                    dx = dx * -1;
                }
                if(bottomWall) {
                    dy = dy * -1;
                    score.loseLives();
                    livesLabel.setText("Lives: " +score.getLives());
                }
                
                //if ball collides with paddle
                if (collide(paddle)) {
                    dy = dy * -1;
                }
                    
                //if ball and brick collides, remove brick
                for(Rectangle brick:allBricks) { 
                    if(collide(brick)) {
                        layout.getChildren().remove(brick);
                        allBricks.remove(brick);
                        dy = dy * -1;
                        score.addPoints(brick.getLayoutY());
                        scoreLabel.setText("Score: " +score.getScore());
                        break;
                    //as the player scores more points, increase the speed
                    }
                    if(score.getScore()>20 && score.getScore() <=40) {
                        dy=3;      
                    }
                    if(score.getScore()>40 && score.getScore() <=60) {
                        dy=4;       
                    }
                    if(score.getScore()>60 && score.getScore() <=80) {
                        dy=5;       
                    }
                }
                
                //if player has no lives left, restart the game and the layout
                if(score.getLives() <1 || score.getScore() ==200) {
                   timeline.stop();
                   Button restart =new Button("Click to restart");
                   
                   restart.setLayoutX(300);
                   restart.setLayoutY(200);
                   layout.getChildren().add(restart);
                   restart.setVisible(true);
                   restart.setOnAction(new EventHandler<ActionEvent> () {
                       public void handle(ActionEvent event) {
                           for(Rectangle brick:allBricks) {
                                layout.getChildren().removeAll(brick);
                            }    
                            allBricks.clear();
                            for(int x=0; x<10; x++) {
                                for(int y=0; y<8; y++) {
                                    Rectangle brick=new Rectangle(60,15);
                                    if(y<=1){
                                        brick.setFill(Color.RED);
                                    }
                                    if(y>1 && y<=3){
                                        brick.setFill(Color.ORANGE);
                                    }
                                    if(y>3 && y<=5){
                                        brick.setFill(Color.GREEN); 
                                    }
                                    if(y>5 && y<8){
                                        brick.setFill(Color.YELLOW);
                                    }
                                    brick.setLayoutX(x*62);
                                    brick.setLayoutY((y*16)+35);
                                    layout.getChildren().add(brick);
                                    allBricks.add(brick);
                                }
                            }
                            score.setLives();
                            score.setPoints();
                            restart.setVisible(false);
                            timeline.play();
                       }
                       
                   });
                }
                
            }
            //if shape interects ball, collision detected
            public boolean collide(Rectangle other) {
                Shape collisionArea = Shape.intersect(ball, other);
                return collisionArea.getBoundsInLocal().getWidth() != -1;
            }
            
        }));
       
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        window.setTitle("Breakout Game!");
        window.setScene(view);
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
