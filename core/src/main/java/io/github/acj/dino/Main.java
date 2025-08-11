package io.github.acj.dino;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    //Base Variables
    private static final int initialCactusXVelocity = 5;
    public static final int SIDE = 800, FLOOR_HEIGHT = 400;
    protected static final float gravity = 0.6f;

    //Game variables
    protected static float cactusXVelocity = initialCactusXVelocity;
    private boolean gameRunning = true;
    private float score = 0;

    //Extra stuff
    protected static Random r;

    //Game objects
    Dino dino;
    Array<Cactus> cacti = new Array<>();

    //Debugging
    private final boolean useCollision = true, renderDebugBoxes = false;

    //Font stuff
    private static BitmapFont fontLarge, fontSmall;
    private static GlyphLayout layout;

    //Spawning stuff
    private static Timer.Task spawnTask;
    private float spawnDelay = 2f, speedUpThreshold = 300f;

    //Sprites and related
    private Sprite dinoSprite;
    private SpriteBatch batch;
    private static ShapeRenderer renderer;
    protected static Sprite shortCactusSprite;
    protected static Sprite[] cactusSprites = new Sprite[4];



    @Override
    public void create() {
        renderer = new ShapeRenderer();
        dino = new Dino(150, 400, 75, 195, FLOOR_HEIGHT);
        r = new Random();

        dinoSprite = new Sprite(new Texture("sprites/dino.png"));
        shortCactusSprite = new Sprite(new Texture("sprites/cactusshort.png"));

        for(int i = 0; i < 4; i++)
            cactusSprites[i] = new Sprite(new Texture("sprites/cactus" + (i+1) + ".png"));

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/NerdMono.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

        param.size = 30;

        layout = new GlyphLayout();
        batch = new SpriteBatch();
        fontLarge = gen.generateFont(param);
        param.size = 15;
        fontSmall = gen.generateFont(param);

        gen.dispose();

        spawnTask = new Timer.Task(){
            @Override
            public void run(){
                cacti.add(new Cactus(SIDE, FLOOR_HEIGHT, r.nextInt(3)+1, r.nextInt(2)));
            }
        };
        Timer.schedule(spawnTask, 0f, 2f);

    }

    @Override
    public void render() {
        screenClear();
        if(gameRunning) {
            input();
            logic(Gdx.graphics.getDeltaTime());
            draw();
        }
        else{
            gameEnd();
            pollExit();
            pollRestart();
        }
    }

    private void input(){
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) dino.jump();
        pollExit();
    }

    private void pollExit(){
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }
    private void pollRestart(){
        if(Gdx.input.isKeyPressed(Input.Keys.R)) restart();
    }


    private void logic(float delta){
        dino.move();

        float distance = cactusXVelocity * delta;
        score += distance * 5;

        if(score >= 100 && score <= 500)
            cactusXVelocity = 6f;
        else if (score > 500 && score <= 750)
            cactusXVelocity = 7f;
        else if (score > 750 && score <= 1000)
            cactusXVelocity = 8f;

        if(score >= speedUpThreshold && spawnDelay >= 0.5f){
            speedUpThreshold += 200f;
            reduceSpawnDelay();
        }

        for(int i = cacti.size - 1; i >= 0; i--){
            Cactus c = cacti.get(i);
            c.move();
            if (c.getX() <= -c.getTotalWidth()) cacti.removeIndex(i);

            if(isColliding(c))
                gameRunning = false;
        }
    }

    private void draw(){
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(Color.BROWN);
        renderer.rect(0, 350, SIDE, 50);

        if(renderDebugBoxes){
            renderer.setColor(Color.WHITE);
            renderer.rect(dino.getX(), dino.getY(), dino.getCollisionSide(), dino.getCollisionSide());

            renderer.setColor(Color.GREEN);

            for (Cactus c : cacti) {
                renderer.rect(c.getX(), c.getY(), c.getTotalWidth(), c.getTotalHeight());
            }
        }
        renderer.end();

        batch.begin();

        batch.draw(dinoSprite, dino.getX(), dino.getY(), dino.getSide(), dino.getSide());

        for (Cactus c : cacti) {
            float xIncrement = 0f;
            for (Sprite s : c.sprites) {
                batch.draw(s, c.getX()+xIncrement, c.getY(), c.getUnitLength(), c.getTotalHeight());
                xIncrement += c.getUnitLength();
            }
        }

        fontSmall.draw(batch, "Score: " + (int)score, SIDE-150, SIDE-20);
        batch.end();
    }

    private boolean isColliding(Cactus c){
        return dino.getCollisionBox().overlaps(c.getCollisionBox()) && useCollision;
    }

    private void gameEnd(){
        screenClear();

        String text = "You Lost.\nYour score was:    " + (int)score;

        layout.setText(fontLarge, text);
        float textWidth = layout.width;
        float textHeight = layout.height;

        float centerX = (SIDE - textWidth)/2f;
        float centerY = (textHeight + SIDE)/2f;

        batch.begin();
        fontLarge.draw(batch, text, centerX, centerY);
        batch.end();

    }

    private void restart(){
        score = 0;
        cactusXVelocity = initialCactusXVelocity;
        dino.reset();
        cacti.clear();
        gameRunning = true;
    }

    private void screenClear(){
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void reduceSpawnDelay(){
        spawnDelay -= 0.25f;
        spawnTask.cancel();
        Timer.schedule(spawnTask, spawnDelay-(spawnDelay/10), spawnDelay);
    }


    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (batch != null) batch.dispose();
        if (fontLarge != null) fontLarge.dispose();
        if (fontSmall != null) fontSmall.dispose();
        if (dinoSprite != null) dinoSprite.getTexture().dispose();
        if (shortCactusSprite != null) shortCactusSprite.getTexture().dispose();

        if (cactusSprites != null)
            for (Sprite s : cactusSprites)
                if (s != null) s.getTexture().dispose();

        Timer.instance().clear();
    }
}
