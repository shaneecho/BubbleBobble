package model;

import controller.LevelController;
import javafx.animation.AnimationTimer;
import utility.Logger;
import utility.Settings;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is the player class. It has a sprite to display.
 */
public class Player extends GravityObject {

    private static final int TIME_IMMORTAL = 3;
    private final int playerNumber;

    private boolean isJumping;
    private Input input;
    private double speed;
    private boolean isFacingRight;
    private int counter;
    private boolean isDead;
    private boolean isGameOver;
    private boolean isImmortal;
    private boolean isDelayed;
    private Timer immortalTimer;
    private Timer delayTimer;
    private LevelController levelController;
    private boolean isAbleToJump;
    private boolean isAbleToDoubleJump;

    private double playerMinX;
    private double playerMaxX;
    private double playerMinY;
    private double playerMaxY;

    private boolean doubleSpeed;
    private int doubleSpeedCounter;
    private int durationDoubleSpeed = 200;

    private boolean bubblePowerup;
    private int bubblePowerupCounter;
    private int durationBubblePowerup = 400;

    private double xStartLocation;
    private double yStartLocation;

    private int score;
    private int lives;

    private SpriteBase spriteBase;

    private AnimationTimer timer;

    /**
     * The constructor of the Player class.
     *
     * @param levelController The levelController.
     * @param x               The X coordinate.
     * @param y               The Y coordinate.
     * @param r               The rotation factor.
     * @param dx              The dx.
     * @param dy              The dy.
     * @param dr              The dr.
     * @param speed           The speed.
     * @param lives           The amount of lives.
     * @param input           The input.
     * @param playerNumber    The number of the player.
     */
    public Player(LevelController levelController,
                  double x,
                  double y,
                  double r,
                  double dx,
                  double dy,
                  double dr,
                  double speed,
                  int lives,
                  Input input,
                  int playerNumber) {

        this.speed = speed;
        this.input = input;
        this.counter = 31;
        this.isAbleToJump = false;
        this.isAbleToDoubleJump = false;
        this.isJumping = false;
        this.isDead = false;
        this.isGameOver = false;
        this.isFacingRight = true;
        this.levelController = levelController;
        this.lives = lives;
        this.score = 0;
        this.playerNumber = playerNumber;

        playerMinX = Level.SPRITE_SIZE;
        playerMaxX = Settings.SCENE_WIDTH - Level.SPRITE_SIZE;
        playerMinY = Level.SPRITE_SIZE;
        playerMaxY = Settings.SCENE_HEIGHT - Level.SPRITE_SIZE;
        xStartLocation = x;
        yStartLocation = y;

        this.spriteBase = new SpriteBase("/Bub" + playerNumber + "Left.png", x, y, r, dx, dy, dr);
        this.addObserver(levelController);
        this.addObserver(levelController.getScreenController());
        this.timer = createTimer();
    }

    private AnimationTimer createTimer() {
        return new AnimationTimer() {
            @SuppressWarnings("unchecked")
            @Override
            public void handle(long now) {

                    if (!levelController.getGamePaused()) {
                        processInput();
                        move();
                        levelController.getCurrLvl().getMonsters().forEach(
                                Player.this::checkCollideMonster
                        );
                    }

                    setChanged();
                    notifyObservers();
                }

        };
    }

    /**
     * The function that processes the input.
     */
    public void processInput() {

        if (!isDead && !isDelayed) {
            if (isJumping && spriteBase.getDy() <= 0) {
                spriteBase.setDy(spriteBase.getDy() + 0.6);
            } else if (isJumping && spriteBase.getDy() > 0) {
                spriteBase.setDy(spriteBase.getDy() + 0.6);
                setJumping(false);
            } else {
                spriteBase.setDy(0);
            }

            moveVertical();
            moveHorizontal();
            checkFirePrimary();
        } else {
            if (!isDelayed) {
                checkIfGameOver();
            }
        }

        spriteBase.checkBounds(playerMinX, playerMaxX, playerMinY, playerMaxY, levelController);
        checkPowerups();
    }

    /**
     * Check for collision combined with jumping.
     *
     * @param jumping    The variable whether a GravityObject is jumping.
     * @param ableToJump The variable whether a GravityObject is able to jump.
     * @return The ableToJump variable.
     */
    public boolean moveCollisionChecker(boolean jumping, boolean ableToJump) {
        if (!spriteBase.causesCollisionWall(spriteBase.getX(),
                spriteBase.getX() + spriteBase.getWidth(),
                spriteBase.getY() - calculateGravity(),
                spriteBase.getY() + spriteBase.getHeight() - calculateGravity(), levelController)) {
            if (!jumping) {
                spriteBase.setDy(spriteBase.getDy() - calculateGravity());
            }
            setAbleToJump(false);
        } else {
            if (!jumping) {
                setAbleToJump(true);
            }
        }
        return ableToJump;
    }

    private void checkPowerups() {
        if (doubleSpeed) {
            doubleSpeedCounter++;
            if (doubleSpeedCounter >= durationDoubleSpeed) {
                setDoubleSpeed(false);
                setSpeed(Settings.PLAYER_SPEED);
                setDoubleSpeedCounter(0);
            }
        } else {
            setDoubleSpeedCounter(0);
        }

        if (bubblePowerup) {
            bubblePowerupCounter++;
            if (bubblePowerupCounter >= durationBubblePowerup) {
                setBubblePowerup(false);
                setBubblePowerupCounter(0);
            }
        } else {
            setBubblePowerupCounter(0);
        }
    }

    /**
     * The move function that applies the movement to the player.
     */
    public void move() {
        applyGravity();
        spriteBase.move();

        Double newX = spriteBase.getX() + spriteBase.getDx();
        Double newY = spriteBase.getY() + spriteBase.getDy();

        if (!newX.equals(spriteBase.getX()) || !newY.equals(spriteBase.getY())) {
            Logger.log(String.format("Player moved from (%f, %f) to (%f, %f)",
                    spriteBase.getX(), spriteBase.getY(), newX, newY));
        }

    }

    /**
     * This function applies gravity.
     */
    private void applyGravity() {
        double x = spriteBase.getX();
        double y = spriteBase.getY();
        if (!spriteBase.causesCollisionWall(x, x + spriteBase.getWidth(),
                y - calculateGravity(), y + spriteBase.getHeight() - calculateGravity(),
                levelController)
                || spriteBase.causesCollisionWall(x, x + spriteBase.getWidth(),
                y, y + spriteBase.getHeight(), levelController)) {
            if (!isJumping) {
                if (isAbleToDoubleJump
                        && causesBubbleCollision(x, x + spriteBase.getWidth(),
                        y - calculateGravity(),
                        y + spriteBase.getHeight() - calculateGravity())) {
                    setAbleToJump(true);
                    setAbleToDoubleJump(false);
                } else if (isAbleToDoubleJump) {
                    setAbleToJump(false);
                }
                spriteBase.setY(y - calculateGravity());
            } else {
                setAbleToJump(false);
            }
        } else {
            if (!isJumping) {
                setAbleToJump(true);
                setAbleToDoubleJump(true);
            }
        }
    }

    /**
     * This function checks if the player collides with a bubble.
     *
     * @param x  Minimal x.
     * @param x1 Maximal x.
     * @param y  Minimal y.
     * @param y2 Maximal y.
     * @return True if collision.
     */
    @SuppressWarnings("unchecked")
    private boolean causesBubbleCollision(double x, double x1, double y, double y2) {
        ArrayList<Bubble> bubbles = new ArrayList<>();
        levelController.getPlayers().forEach(player -> bubbles.addAll(levelController.getBubbles()));


        if (bubbles.size() == 0) {
            return false;
        } else {
            boolean res = false;
            for (Bubble bubble : bubbles) {
                if (bubble.getSpriteBase().causesCollision(x, x1, y, y2)
                        && !bubble.isAbleToCatch()) {
                    res = true;
                }
            }
            return res;
        }
    }

    /**
     * This method checks if the monster has collides with the character.
     *
     * @param monster is the monster that is being checked for collisions.
     */
    public void checkCollideMonster(final Monster monster) {

        double x = spriteBase.getX();
        double y = spriteBase.getY();

        if (monster.getSpriteBase().causesCollision(x, x + spriteBase.getWidth(),
                y, y + spriteBase.getHeight())
                && !isDelayed) {
            if (!monster.isCaughtByBubble()) {
                if (!isImmortal) {
                    this.die();
                }
            } else {
                monster.die(this);
                monster.getPrisonBubble().setIsPopped(true);
            }
        }
    }

    /**
     * This method is used when the character is killed.
     */
    public void die() {
        if (this.getLives() <= 1 && !this.isDead) {
            this.isDead = true;
            spriteBase.setDx(0);
            spriteBase.setDy(0);
            counter = 0;
            spriteBase.setImage("/Bub" + playerNumber + "Death.png");
        } else {
            isDelayed = true;
            spriteBase.setImage("/Bub" + playerNumber + "Death.png");
            this.loseLife();
            this.scorePoints(Settings.POINTS_PLAYER_DIE);
            delayRespawn();
        }
    }

    private void delayRespawn() {
        delayTimer = new Timer();
        delayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                isDelayed = false;
                isImmortal = true;

                spriteBase.setDx(0);
                spriteBase.setDy(0);
                spriteBase.setX(xStartLocation);
                spriteBase.setY(yStartLocation);
                immortalTimer = new Timer();
                immortalTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isImmortal = false;
                        immortalTimer.cancel();
                    }
                }, 1000 * TIME_IMMORTAL);
            }
        }, 1000);
    }

    /**
     * This function checks how to move vertically.
     */
    private void moveVertical() {
        if (input.isMoveUp() && isAbleToJump) {
            jump();
        }
        if (isFacingRight) {
            if (isImmortal) {
                spriteBase.setImage("/Bub" + playerNumber + "RightRed.png");
            } else {
                spriteBase.setImage("/Bub" + playerNumber + "Right.png");
            }
        } else {
            if (isImmortal) {
                spriteBase.setImage("/Bub" + playerNumber + "LeftRed.png");
            } else {
                spriteBase.setImage("/Bub" + playerNumber + "Left.png");
            }
        }
    }

    private void jump() {
        setAbleToJump(false);
        setJumping(true);
        spriteBase.setDy(-Settings.JUMP_SPEED);
    }


    /**
     * This function checks how to move horizontally.
     */
    private void moveHorizontal() {
        if (input.isMoveLeft()) {
            moveLeft();
        } else if (input.isMoveRight()) {
            moveRight();
        } else {
            spriteBase.setDx(0d);
        }
    }

    /**
     * This function handles moving to the right.
     */
    private void moveRight() {
        double x = spriteBase.getX();
        double y = spriteBase.getY();

        if (!spriteBase.causesCollisionWall(x + speed,
                x + spriteBase.getWidth() + speed,
                y, y + spriteBase.getHeight(), levelController)) {
            spriteBase.setDx(speed);
        } else if (spriteBase.causesCollisionWall(x, x + spriteBase.getWidth(),
                y, y + spriteBase.getHeight(), levelController)) {
            spriteBase.setDx(speed);
        } else {
            if (!isJumping) {
                spriteBase.setDx(0);
            }
        }

        if (isImmortal) {
            spriteBase.setImage("/Bub" + playerNumber + "RightRed.png");
        } else {
            spriteBase.setImage("/Bub" + playerNumber + "Right.png");
        }
        isFacingRight = true;
    }

    /**
     * This function handles moving to the left.
     */
    private void moveLeft() {
        double x = spriteBase.getX();
        double y = spriteBase.getY();

        if (!spriteBase.causesCollisionWall(x - speed,
                x + spriteBase.getWidth() - speed,
                y,
                y + spriteBase.getHeight(), levelController)) {
            spriteBase.setDx(-speed);
        } else if (spriteBase.causesCollisionWall(x, x + spriteBase.getWidth(),
                y, y + spriteBase.getHeight(), levelController)) {
            spriteBase.setDx(-speed);
        } else {
            if (!isJumping) {
                spriteBase.setDx(0);
            }
        }

        if (isImmortal) {
            spriteBase.setImage("/Bub" + playerNumber + "LeftRed.png");
        } else {
            spriteBase.setImage("/Bub" + playerNumber + "Left.png");
        }
        setFacingRight(false);
    }

    /**
     * This function checks if the game is over. And if so, loads the game over screen.
     */
    private void checkIfGameOver() {
        if (counter > 50) {
            setGameOver(true);
        } else {
            counter++;
        }
    }

    /**
     * This function checks if it should fire a bubble.
     */
    private void checkFirePrimary() {
        if (input.isFirePrimaryWeapon() && counter > 30) {
            Bubble bubble = new Bubble(spriteBase.getX(), spriteBase.getY(), 0, 0, 0, 0,
                    isFacingRight, bubblePowerup, levelController);
            levelController.getBubbles().add(bubble);

            counter = 0;
        } else {
            counter++;
        }
    }

    /**
     * Add/subtract points to/from the player's score.
     *
     * @param points the amount of scored points.
     * @return the Player instance for chaining.
     */
    public Player scorePoints(int points) {
        this.setScore(this.getScore() + points);

        return this;
    }

    /**
     * Reduce the player's lives by 1.
     */
    private void loseLife() {
        this.setLives(getLives() - 1);
    }

    /**
     * Add 1 to the player's lives.
     */
    public void addLife() {
        this.setLives(getLives() + 1);
    }

    /**
     * Get the number of lives.
     *
     * @return the number of lives.
     */
    public int getLives() {
        return lives;
    }

    /**
     * Set the number of lives.
     *
     * @param lives the number of lives.
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * This is called when the speed powerup is picked up.
     * It doubles the speed for a while.
     */
    public void activateSpeedPowerup() {
        doubleSpeed = true;
        speed = 2 * Settings.PLAYER_SPEED;
    }

    /**
     * This activates the bubble powerup.
     * Bubbles fly horizontally longer.
     */
    public void activateBubblePowerup() {
        setBubblePowerup(true);
    }

    /**
     * This function returns whether the player is jumping.
     *
     * @return True if jumping.
     */
    public boolean isJumping() {
        return isJumping;
    }

    /**
     * This function sets whether the player is jumping.
     *
     * @param jumping True if jumping.
     */
    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    /**
     * This function returns the input.
     *
     * @return The input.
     */
    public Input getInput() {
        return input;
    }

    /**
     * This function sets the input.
     *
     * @param input The input.
     */
    public void setInput(Input input) {
        this.input = input;
    }

    /**
     * This function returns the speed.
     *
     * @return The speed.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * This function sets the speed.
     *
     * @param speed The speed.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * This function sets whether the player is facing right.
     *
     * @param facingRight True if facing right.
     */
    public void setFacingRight(boolean facingRight) {
        isFacingRight = facingRight;
    }

    /**
     * This function returns whether the player is dead.
     *
     * @return True if dead.
     */
    public boolean isDead() {

        if (isDead) {
            this.deleteObservers();
        }

        return isDead;
    }

    /**
     * This function returns whether the player is game over.
     * AKA no lives left.
     *
     * @return True if game over.
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * This function sets whether the player is game over.
     *
     * @param gameOver True if gameover.
     */
    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    /**
     * This function returns the levelcontroller.
     *
     * @return The levelcontroller.
     */
    public LevelController getLevelController() {
        return levelController;
    }

    /**
     * This function sets the levelcontroller.
     *
     * @param levelController The levelcontroller.
     */
    public void setLevelController(LevelController levelController) {
        this.levelController = levelController;
    }

    /**
     * This function sets if the player is able to jump.
     *
     * @param ableToJump True if able to jump.
     */
    public void setAbleToJump(boolean ableToJump) {
        isAbleToJump = ableToJump;
    }

    /**
     * This function sets if the player is able to double jump.
     *
     * @param ableToDoubleJump True if able to double jump.
     */
    public void setAbleToDoubleJump(boolean ableToDoubleJump) {
        isAbleToDoubleJump = ableToDoubleJump;
    }

    /**
     * This function sets the double speed.
     *
     * @param doubleSpeed True if double speed.
     */
    public void setDoubleSpeed(boolean doubleSpeed) {
        this.doubleSpeed = doubleSpeed;
    }

    /**
     * This function sets the double speed counter.
     *
     * @param doubleSpeedCounter The double speed counter.
     */
    public void setDoubleSpeedCounter(int doubleSpeedCounter) {
        this.doubleSpeedCounter = doubleSpeedCounter;
    }

    /**
     * This function sets the bubble powerup.
     *
     * @param bubblePowerup True if bubble powerup.
     */
    public void setBubblePowerup(boolean bubblePowerup) {
        this.bubblePowerup = bubblePowerup;
    }

    /**
     * This function sets the bubble powerup counter.
     *
     * @param bubblePowerupCounter The powerup counter.
     */
    public void setBubblePowerupCounter(int bubblePowerupCounter) {
        this.bubblePowerupCounter = bubblePowerupCounter;
    }

    /**
     * This function returns the score.
     *
     * @return The score.
     */
    public int getScore() {
        return score;
    }

    /**
     * This function sets the score.
     *
     * @param score The score.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * This function returns the sprite.
     *
     * @return The sprite.
     */
    public SpriteBase getSpriteBase() {
        return spriteBase;
    }

    /**
     * This function returns the player number.
     * @return The player number.
     */
    public int getPlayerNumber() {
        return playerNumber;
    }
}