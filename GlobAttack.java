package globattack;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import processing.core.PFont;


import java.awt.*;

import static processing.core.PApplet.*;

public class GlobAttack extends PApplet {
    PImage cat;
    PImage reverseCat;
    PImage forwardCat;
    PImage blackcat;
    PImage bullet;
    PImage bg;
    PImage[] explosionAnimation = new PImage[6];

    float playerX = 960, playerY = 540;
    boolean up = false;
    boolean down = false;
    boolean right = false;
    boolean left = false;

    float enemySpeed = 1f;
    int animationFrame = 1;

    int score = 0;
    int highScore = 0;
    PFont scoreFont;

    PImage gameOverImg;
    PImage restartButton;
    PImage startButton;
    ArrayList<Enemy> enemies = new ArrayList<Enemy>();

    float spawnRate = 300;

    float bulletSpeed = 15;

    ArrayList<Bullet> bullets = new ArrayList<Bullet>();


    public static void main(String[] args) {
        PApplet.main("globattack.GlobAttack");

    }

    public void settings() {
        size(1920, 1080);
    }

    public void setup() {
        currentState = GameState.START;
        bg = loadImage("Images/GlobAttackAssets/spacebg.jpg");
        forwardCat = loadImage("Images/therealcat.png");
        reverseCat = loadImage("Images/reversecat.png");
        bullet = loadImage("Images/bullet.png");
        blackcat = loadImage("Images/blackcat.png");
        gameOverImg = loadImage("Images/GlobAttackAssets/GameOverImg.png");
        restartButton = loadImage("Images/GlobAttackAssets/restart.png");
        startButton = loadImage("Images/GlobAttackAssets/startbutton.png");

        forwardCat.resize(200, 200);
        reverseCat.resize(200, 200);
        bg.resize(1920,1080);
        cat = forwardCat;
        gameOverImg.resize(700, 0);
        restartButton.resize(400, 0);
        startButton.resize(400,0);
        enemies.add(new Enemy(random(0, width), random(0, width)));
        for (int i = 1; i <= 6; i++) {
            explosionAnimation[i - 1] = loadImage("Images/GlobAttackAssets/Explosion_FX" + i + ".png");
            explosionAnimation[i - 1].resize(200, 200);
        }

    }

    public void draw() {
        background(bg);
        switch (currentState) {
            case START:
                drawStartMenu();
                break;
            case OVER:
                drawGameOver();
                break;
            case RUNNING:
                noStroke();
                drawPlayer();
                increaseDifficulty();
                drawScore();
                if (frameCount % 5 == 0) {
                    animationFrame++;
                    animationFrame = animationFrame % 6;
                }

                for (int b = 0; b < bullets.size(); b++) {
                    Bullet bull = bullets.get(b);
                    bull.move();
                    bull.drawBullet();
                    if (bull.x < 0 || bull.x > width || bull.y < 0 || bull.y > height) {
                        bullets.remove(b);

                    }

                }

                for (int i = 0; i < enemies.size(); i++) {
                    Enemy en = enemies.get(i);
                    en.move(playerX, playerY);
                    en.drawEnemy();
                    for (int j = 0; j < bullets.size(); j++) {
                        Bullet b = bullets.get(j);
                        if (abs(b.x - en.x) < 150 && abs(b.y - en.y) < 150 && en.isDead == false) {
                            en.isDead = true;
                            bullets.remove(j);
                            score += 1;

                            break;
                        }
                    }
                    if (abs(playerX - en.x) < 100 && abs(playerY - en.y) < 100) {
                        if (score > highScore) {
                            highScore = score;
                        }
                        currentState = GameState.OVER;
                    }
                }
                for(int i = 0; i < enemies.size(); i++) {
                    Enemy en = enemies.get(i);
                    if(en.isDead == true) {
                        en.explosionFrame ++;
                        if (en.explosionFrame == 5) {
                            enemies.remove(i);
                        }
                    }
                }
                break;
        }

    }
    public void drawScore(){
        scoreFont = createFont("Leelawadee UI Bold", 40, true);
        textFont(scoreFont);
        fill(255, 255, 255);
        textAlign(CENTER);
        text("Score: " + score, width - 90, 40);
    }

    public void drawPlayer() {
        image(cat, playerX, playerY);

        playerX = constrain(playerX, 0, width-cat.width);
        playerY = constrain(playerY, 0, height- cat.height);

        if (up) {
            playerY -= 10;
        }
        if (left) {
            playerX -= 10;
        }
        if (down) {
            playerY += 10;
        }
        if (right) {
            playerX += 10;

        }
    }

    public void increaseDifficulty() {
        if (frameCount % spawnRate == 0) {
            generateEnemy();
            if (enemySpeed < 3) {
                enemySpeed += 0.1f;
            }
            if (spawnRate > 50) {
                spawnRate -= 10;
            }
        }
    }
    public void drawStartMenu() {
        imageMode(CENTER);
        image(gameOverImg, width / 2, height / 2);
        fill(122, 64, 51);
        textAlign(CENTER);
        textSize(32);
        text("Welcome to Banana Cat Shooter! ", width / 2, height / 2 - 100);
        text("Goal of the game: Shoot the Enemy Cats", width / 2, height / 2 - 40);
        image(startButton, width / 2, height / 2+200);
    }
    public void  drawGameOver(){
        imageMode(CENTER);
        image(gameOverImg, width / 2, height / 2);
        fill(122, 64, 51);
        textAlign(CENTER);
        text("Game Over ", width / 2, height / 2 - 100);
        text("Score: " + score, width / 2, height / 2 - 40);
        text("High Score: " + highScore, width / 2, height / 2 + 10);
        image(restartButton, width / 2, height / 2+200);
    }

    public void generateEnemy() {
        int side = (int) random(0, 2);
        int side2 = (int) random(0, 2);
        if (side % 2 == 0) {
            enemies.add(new Enemy(random(0, width), height * (side2 % 2)));
        } else {
            enemies.add(new Enemy(width * (side2 % 2), random(0, height)));
        }
    }

    public void mousePressed() {
        switch(currentState) {
            case START:
                if(mouseX > (width / 2 - 200) && mouseX < (width / 2 + 200) && mouseY > height / 2 + 100 - 100 && mouseY < (height / 2 + 100 + 100)) {
                    for (int i = 0; i < enemies.size(); i++) {
                        enemies.remove(i);
                        score = 0;
                        enemySpeed = 1f;
                        spawnRate = 300;
                        currentState = GameState.RUNNING;
                    }
                }
                break;
            case OVER:
                if (mouseX > (width / 2 - 200) && mouseX < (width / 2 + 200) && mouseY > height / 2 + 100 - 100 && mouseY < (height / 2 + 100 + 100)) {
                    for (int i = 0; i < enemies.size(); i++) {
                        enemies.remove(i);
                        score = 0;
                        enemySpeed = 1f;
                        spawnRate = 300;
                        currentState = GameState.RUNNING;
                    }
                }
                break;
            case RUNNING:
                float dx = mouseX - playerX;
                float dy = mouseY - playerY;
                float angle = atan2(dy, dx);
                float vx = bulletSpeed * cos(angle);
                float vy = bulletSpeed * sin(angle);
                bullets.add(new Bullet(playerX, playerY, vx, vy));
        }
    }

    public void keyPressed() {
        if (key == 'w') {
            up = true;
        }
        if (key == 'a') {
            left = true;
            cat = forwardCat;
        }
        if (key == 's') {
            down = true;
        }
        if (key == 'd') {
            right = true;
            cat = reverseCat;
        }
    }

    public void keyReleased() {
        if (key == 'w') {
            up = false;
        }
        if (key == 'a') {
            left = false;
        }
        if (key == 's') {
            down = false;
        }
        if (key == 'd') {
            right = false;
        }
    }

    class Enemy {
        float x, y, vx, vy;
        boolean isDead = false;
        int explosionFrame = 0;

        Enemy(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void drawEnemy() {
            if (isDead == false) {
                blackcat.resize(200, 200);
                image(blackcat, x, y);
            } else {
                image(explosionAnimation[animationFrame], x, y);
            }
        }

        public void move(float px, float py) {
            if (isDead == false) {
                float angle = atan2(py - y, px - x);
                vx = cos(angle);
                vy = sin(angle);
                x += vx * enemySpeed;
                y += vy * enemySpeed;
            }
            else {
                image(explosionAnimation[animationFrame], x, y);

            }
        }
    }

    class Bullet {
        float x, y, vx, vy;

        Bullet(float x, float y, float vx, float vy) {
            this.x = x+100;
            this.y = y+100;
            this.vx = vx;
            this.vy = vy;
        }

        public void drawBullet() {
            bullet.resize(25, 25);
            image(bullet, x, y);
        }
        public void move() {
            x += vx;
            y += vy;
        }
    }
    enum GameState {
        START, OVER, RUNNING
    }
    static GameState currentState;
}

