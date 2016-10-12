package com.mcelrea;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;

public class Main extends Application {

    Image blackImage, caveImage, glitterImage, goldImage, groundImage, guyImage,
          spiderImage, stinkImage, webImage, windImage, wumpusImage;
    WumpusTile world[][] = new WumpusTile[10][10];
    int currentTile = -1;
    int mouse_x=0, mouse_y=0;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Wumpus World");
        Group root = new Group();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        Canvas canvas = new Canvas(800,600);
        root.getChildren().add(canvas);

        File file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\blackTile.png");
        blackImage = new Image(new FileInputStream(file));
        file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\caveTile.png");
        caveImage = new Image(new FileInputStream(file));
        file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\glitterTile.png");
        glitterImage = new Image(new FileInputStream(file));
        file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\goldTile.png");
        goldImage = new Image(new FileInputStream(file));
        file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\groundTile.png");
        groundImage = new Image(new FileInputStream(file));
        file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\guy.png");
        guyImage = new Image(new FileInputStream(file));
        file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\spiderTile.png");
        spiderImage = new Image(new FileInputStream(file));
        file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\stinkTile.png");
        stinkImage = new Image(new FileInputStream(file));
        file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\webTile.png");
        webImage = new Image(new FileInputStream(file));
        file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\windTile.png");
        windImage = new Image(new FileInputStream(file));
        file = new File("C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\wumpusTile.png");
        wumpusImage = new Image(new FileInputStream(file));

        GraphicsContext gc = canvas.getGraphicsContext2D();

        initWorld();

        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouse_x = (int) event.getX();
                mouse_y = (int) event.getY();
            }
        });

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                System.out.println(x + ", " + y);

                //clicked on spider toolbar tile
                if(x >= 650 && x <= 700 && y >= 150 && y <= 200) {
                    currentTile = WumpusTile.SPIDER;
                }
            }
        });

        new AnimationTimer(){
            @Override
            public void handle(long now) {
                //clear screen
                gc.setFill(Color.CADETBLUE);
                gc.fillRect(0,0,800,600);

                drawWorld(gc);
                drawToolbar(gc);
            }
        }.start();

        //this line should be last
        primaryStage.show();
    }

    public Location convertMouseCoordsIntoLoc(int x, int y) {
        return null;
    }

    private void drawToolbar(GraphicsContext gc) {
        int xoffset = 650;
        int yoffset = 50;
        gc.drawImage(groundImage, xoffset, yoffset);
        gc.drawImage(caveImage, xoffset, yoffset+50);
        gc.drawImage(spiderImage, xoffset, yoffset+100);
        gc.drawImage(wumpusImage, xoffset, yoffset+150);
        gc.drawImage(goldImage, xoffset, yoffset+200);

        if(currentTile != -1) {
            if(currentTile == WumpusTile.SPIDER) {
                gc.drawImage(spiderImage,mouse_x-25, mouse_y-25);
            }
        }
    }

    public void initWorld() {
        for(int row=0; row<world.length; row++) {
            for (int col = 0; col < world[row].length; col++) {
                world[row][col] = new WumpusTile();
            }
        }

        world[3][3].setType(WumpusTile.WUMPUS);
    }

    private void drawWorld(GraphicsContext gc) {
        for(int row=0; row<world.length; row++) {
            for(int col=0; col<world[row].length; col++) {
                int type = world[row][col].getType();
                if(type == WumpusTile.GROUND)
                    gc.drawImage(groundImage,5+col*50,10+row*50);
                else if(type == WumpusTile.BLACK)
                    gc.drawImage(blackImage,5+col*50,10+row*50);
                else if(type == WumpusTile.CAVE)
                    gc.drawImage(caveImage,5+col*50,10+row*50);
                else if(type == WumpusTile.SPIDER)
                    gc.drawImage(spiderImage,5+col*50,10+row*50);
                else if(type == WumpusTile.GLITTER)
                    gc.drawImage(glitterImage,5+col*50,10+row*50);
                else if(type == WumpusTile.GOLD)
                    gc.drawImage(goldImage,5+col*50,10+row*50);
                else if(type == WumpusTile.STINK)
                    gc.drawImage(stinkImage,5+col*50,10+row*50);
                else if(type == WumpusTile.WEB)
                    gc.drawImage(webImage,5+col*50,10+row*50);
                else if(type == WumpusTile.WIND)
                    gc.drawImage(windImage,5+col*50,10+row*50);
                else if(type == WumpusTile.WUMPUS)
                    gc.drawImage(wumpusImage,5+col*50,10+row*50);
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
