package io.github.acj.dino;

import com.badlogic.gdx.math.Rectangle;


public class Dino {
    private final int x, floorHeight;
    private int y;
    private final int side, collisionSide;
    private final float jumpVelocity;
    private float yVelocity;
    private final Rectangle collisionBox;

    Dino(int x, int y, int side, int jumpHeight, int floorHeight){
        this.x = x;
        this.y = y;
        this.side = side;
        this.collisionSide = side - side/10;
        this.floorHeight = floorHeight;

        this.jumpVelocity = (float) Math.sqrt(2 * Main.gravity * jumpHeight);
        this.collisionBox = new Rectangle(x, y, collisionSide, collisionSide);
    }

    public void jump(){
        if(isOnFloor()) yVelocity = jumpVelocity;

    }

    public void move(){
        if(!isOnFloor()) applyGravity();

        y += (int) yVelocity;

        if(isOnFloor()) {
            yVelocity = 0;
            y = floorHeight;
        }
        collisionBox.setPosition(x, y);
    }

    private boolean isOnFloor(){
        return (y <= floorHeight);
    }

    private void applyGravity(){
        yVelocity -= Main.gravity;
    }

    public int getSide() {
        return side;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }
    public void reset(){
        this.y = Main.FLOOR_HEIGHT;
        this.move();
    }

    public int getCollisionSide() {
        return collisionSide;
    }
}
