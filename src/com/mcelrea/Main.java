package com.mcelrea;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Main extends Application {

    Image blackImage, caveImage, glitterImage, goldImage, groundImage, guyImage,
          spiderImage, stinkImage, webImage, windImage, wumpusImage;
    WumpusTile world[][] = new WumpusTile[10][10];
    boolean visible[][] = new boolean[10][10];
    boolean showMap = false;
    int currentTile = -1;
    int mouse_x=0, mouse_y=0;
    Player player;
    ArrayList<String> input = new ArrayList<String>();
    long actDelay = 1000; //1000 ms = 1 second
    long lastAct;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Wumpus World");
        Group root = new Group();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        Canvas canvas = new Canvas(800,600);
        root.getChildren().add(canvas);

        player = new Player(9,0,"C:\\Users\\mcelrea\\Dropbox\\WumpusWorld\\src\\Images\\guy.png");
        visible[9][0] = true;

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
        lastAct = System.currentTimeMillis();

        /*
        world[4][4] = new WumpusTile(WumpusTile.CAVE);
        placeHints(new Location(4,4), WumpusTile.CAVE);
        world[1][8] = new WumpusTile(WumpusTile.GOLD);
        placeHints(new Location(1,8), WumpusTile.GOLD);
        world[1][1] = new WumpusTile(WumpusTile.WUMPUS);
        placeHints(new Location(1,1), WumpusTile.WUMPUS);
        world[7][7] = new WumpusTile(WumpusTile.SPIDER);
        placeHints(new Location(7,7), WumpusTile.SPIDER);
        world[8][8] = new WumpusTile(WumpusTile.SPIDER);
        placeHints(new Location(8,8), WumpusTile.SPIDER);
        */


        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouse_x = (int) event.getX();
                mouse_y = (int) event.getY();
            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                input.add(code);
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
                //clicked on gold toolbar tile
                else if(x >= 650 && x <= 700 && y >= 250 && y <= 300) {
                    currentTile = WumpusTile.GOLD;
                }
                //clicked on wumpus toolbar tile
                else if(x >= 650 && x <= 700 && y >= 200 && y <= 250) {
                    currentTile = WumpusTile.WUMPUS;
                }
                //clicked on cave toolbar tile
                else if(x >= 650 && x <= 700 && y >= 100 && y <= 150) {
                    currentTile = WumpusTile.CAVE;
                }
                //clicked on ground toolbar tile
                else if(x >= 650 && x <= 700 && y >= 50 && y <= 100) {
                    currentTile = WumpusTile.GROUND;
                }

                if(currentTile != -1) {
                Location loc = convertMouseCoordsIntoLoc(x,y);
                if(isValid(loc)) {
                    world[loc.getRow()][loc.getCol()] = new WumpusTile(currentTile);
                    placeHints(loc,currentTile);
                    currentTile = -1;
                }
            }
        }
        });

        new AnimationTimer(){
            @Override
            public void handle(long now) {
                //clear screen
                gc.setFill(Color.CADETBLUE);
                gc.fillRect(0,0,800,600);

                processInput();
                ai();

                drawWorld(gc);
                drawToolbar(gc);

                player.draw(gc);
            }
        }.start();

        //this line should be last
        primaryStage.show();
    }

    private void ai() {
        if(lastAct+actDelay < System.currentTimeMillis()) {
            ArrayList<Location> possibleLocs = new ArrayList<Location>();
            if (isValid(new Location(player.getRow() + 1, player.getCol()))) {
                possibleLocs.add(new Location(player.getRow() + 1, player.getCol()));
            }
            if (isValid(new Location(player.getRow() - 1, player.getCol()))) {
                possibleLocs.add(new Location(player.getRow() - 1, player.getCol()));
            }
            if (isValid(new Location(player.getRow(), player.getCol() + 1))) {
                possibleLocs.add(new Location(player.getRow(), player.getCol() + 1));
            }
            if (isValid(new Location(player.getRow(), player.getCol() - 1))) {
                possibleLocs.add(new Location(player.getRow(), player.getCol() - 1));
            }
            int choice = (int) (Math.random() * possibleLocs.size());
            player.setRow(possibleLocs.get(choice).getRow());
            player.setCol(possibleLocs.get(choice).getCol());
            visible[player.getRow()][player.getCol()] = true;
            lastAct = System.currentTimeMillis();
        }
    }

    public void ai2() {
        if(lastAct+actDelay < System.currentTimeMillis()) {

            //last line
            lastAct = System.currentTimeMillis();
        }
    }

    private void processInput() {
        for(int i=0; i < input.size(); i++) {
            if(input.get(i).equals("W")) {
                if(isValid(new Location(player.getRow()-1,player.getCol()))) {
                    player.moveUp();
                    visible[player.getRow()][player.getCol()] = true;
                    input.remove(i);
                    i--;
                }
            }
            else if(input.get(i).equals("A")) {
                if(isValid(new Location(player.getRow(),player.getCol()-1))) {
                    player.moveLeft();
                    visible[player.getRow()][player.getCol()] = true;
                    input.remove(i);
                    i--;
                }
            }
            else if(input.get(i).equals("S")) {
                if(isValid(new Location(player.getRow()+1,player.getCol()))) {
                    player.moveDown();
                    visible[player.getRow()][player.getCol()] = true;
                    input.remove(i);
                    i--;
                }
            }
            else if(input.get(i).equals("D")) {
                if(isValid(new Location(player.getRow(),player.getCol()+1))) {
                    player.moveRight();
                    visible[player.getRow()][player.getCol()] = true;
                    input.remove(i);
                    i--;
                }
            }
            else if(input.get(i).equals("V")) {
                showMap = !showMap;
                input.remove(i);
                i--;
            }
        }
    }

    private void placeHints(Location loc, int currentTile) {
        ArrayList<Location> locs = new ArrayList<Location>();
        if(isValid(new Location(loc.getRow()-1,loc.getCol()))) {
            locs.add(new Location(loc.getRow()-1,loc.getCol()));
        }
        if(isValid(new Location(loc.getRow()+1,loc.getCol()))) {
            locs.add(new Location(loc.getRow()+1,loc.getCol()));
        }
        if(isValid(new Location(loc.getRow(),loc.getCol()-1))) {
            locs.add(new Location(loc.getRow(),loc.getCol()-1));
        }
        if(isValid(new Location(loc.getRow(),loc.getCol()+1))) {
            locs.add(new Location(loc.getRow(),loc.getCol()+1));
        }
        for(int i=0; i < locs.size(); i++) {
            if(currentTile == WumpusTile.CAVE) {
                world[locs.get(i).getRow()][locs.get(i).getCol()] = new WumpusTile(WumpusTile.WIND);
            }
            else if(currentTile == WumpusTile.WUMPUS) {
                world[locs.get(i).getRow()][locs.get(i).getCol()] = new WumpusTile(WumpusTile.STINK);
            }
            else if(currentTile == WumpusTile.GOLD) {
                world[locs.get(i).getRow()][locs.get(i).getCol()] = new WumpusTile(WumpusTile.GLITTER);
            }
            else if(currentTile == WumpusTile.SPIDER) {
                world[locs.get(i).getRow()][locs.get(i).getCol()] = new WumpusTile(WumpusTile.WEB);
            }
            else if(currentTile == WumpusTile.GROUND) {
                world[locs.get(i).getRow()][locs.get(i).getCol()] = new WumpusTile(WumpusTile.GROUND);
            }
        }
    }

    private boolean isValid(Location loc) {

        if(loc.getRow() >=0 && loc.getRow() < world.length &&
                loc.getCol() >= 0 && loc.getCol() < world[loc.getRow()].length) {
            return true;
        }

        return false;
    }

    public Location convertMouseCoordsIntoLoc(int x, int y) {

        int col = (x-5)/50;
        int row = (y-10)/50;

        Location loc = new Location(row,col);
        System.out.println(loc);
        return loc;
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
            else if(currentTile == WumpusTile.GOLD) {
                gc.drawImage(goldImage,mouse_x-25, mouse_y-25);
            }
            else if(currentTile == WumpusTile.WUMPUS) {
                gc.drawImage(wumpusImage,mouse_x-25, mouse_y-25);
            }
            else if(currentTile == WumpusTile.CAVE) {
                gc.drawImage(caveImage,mouse_x-25, mouse_y-25);
            }
            else if(currentTile == WumpusTile.GROUND) {
                gc.drawImage(groundImage,mouse_x-25, mouse_y-25);
            }
        }
    }

    public void initWorld() {
        for(int row=0; row<world.length; row++) {
            for (int col = 0; col < world[row].length; col++) {
                world[row][col] = new WumpusTile();
            }
        }
    }

    private void drawWorld(GraphicsContext gc) {
        for(int row=0; row<world.length; row++) {
            for(int col=0; col<world[row].length; col++) {
                if(!visible[row][col] && !showMap) {
                    gc.drawImage(blackImage,5+col*50,10+row*50);
                }
                else {
                    int type = world[row][col].getType();
                    if (type == WumpusTile.GROUND)
                        gc.drawImage(groundImage, 5 + col * 50, 10 + row * 50);
                    else if (type == WumpusTile.BLACK)
                        gc.drawImage(blackImage, 5 + col * 50, 10 + row * 50);
                    else if (type == WumpusTile.CAVE)
                        gc.drawImage(caveImage, 5 + col * 50, 10 + row * 50);
                    else if (type == WumpusTile.SPIDER)
                        gc.drawImage(spiderImage, 5 + col * 50, 10 + row * 50);
                    else if (type == WumpusTile.GLITTER)
                        gc.drawImage(glitterImage, 5 + col * 50, 10 + row * 50);
                    else if (type == WumpusTile.GOLD)
                        gc.drawImage(goldImage, 5 + col * 50, 10 + row * 50);
                    else if (type == WumpusTile.STINK)
                        gc.drawImage(stinkImage, 5 + col * 50, 10 + row * 50);
                    else if (type == WumpusTile.WEB)
                        gc.drawImage(webImage, 5 + col * 50, 10 + row * 50);
                    else if (type == WumpusTile.WIND)
                        gc.drawImage(windImage, 5 + col * 50, 10 + row * 50);
                    else if (type == WumpusTile.WUMPUS)
                        gc.drawImage(wumpusImage, 5 + col * 50, 10 + row * 50);
                }
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
