package io.github.acj.dino;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;


public class Cactus {

    private int unitLength = 35;
    private final int totalLength;
    private float x;
    private final boolean tall;
    private final int y;
    protected Sprite[] sprites;
    private final Rectangle collisionBox;

    /**
     *
     * @param units how many cacti next to each-other
     * @param tall 0 or 1, chance of being tall
     */
    Cactus(int x, int y, int units, int tall){
        this.tall = tall == 0;

        sprites = new Sprite[units];

        if(this.tall)
            for(int i = 0; i < units; i++)
                sprites[i] = Main.cactusSprites[Main.r.nextInt(Main.cactusSprites.length)];
        else {
            for (int i = 0; i < units; i++)
                sprites[i] = Main.shortCactusSprite;
            this.unitLength = 33;
        }

        totalLength = unitLength * units;

        this.x = x;
        this.y = y;
        this.collisionBox = new Rectangle(x, y, totalLength, getTotalHeight());
    }

    public void move(){
        this.x -= Main.cactusXVelocity;
        collisionBox.setPosition(x, y);
    }

    public int getTotalWidth() {
        return totalLength;
    }

    public int getUnitLength() {
        return unitLength;
    }

    public int getTotalHeight(){
        return tall? 80:65;
    }
    public float getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

}
