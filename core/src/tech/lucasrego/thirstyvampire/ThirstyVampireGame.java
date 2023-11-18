package tech.lucasrego.thirstyvampire;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class ThirstyVampireGame extends ApplicationAdapter {
    private final Integer viewportWidth = 800;
    private final Integer viewportHeight = 400;
    private final Integer bucketSize = 64;
    private final Integer dropletSize = 64;
    private final Integer windowPadding = 20;
    private final Integer bucketVelocity = 200;
    private final long dropTimeSpan = 1000000000;
    private final Integer dropletVelocity = 200;
    private SpriteBatch batch;
    private Texture dropImage;
    private Texture bucketImage;
    private Music rainMusic;
    private Sound dropSound;
    private OrthographicCamera camera;
    private Rectangle bucket;
    private Array<Rectangle> raindrops;
    private long lastDropTime;

    @Override
    public void create() {
        dropImage = new Texture("droplet.png");
        raindrops = new Array<Rectangle>();
        spawnDroplet();

        bucketImage = new Texture("bucket.png");
        bucket = new Rectangle();
        bucket.x = viewportWidth / 2 - bucketSize / 2;
        bucket.y = windowPadding;

        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);
        rainMusic.play();

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);
    }

    private void spawnDroplet() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, viewportWidth - dropletSize);
        raindrop.y = viewportHeight + dropletSize + windowPadding;
        raindrop.width = dropletSize;
        raindrop.height = dropletSize;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {
        ScreenUtils.clear(.2f, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop : raindrops) {
            batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        batch.end();

        // Touch + Mouse
        if (Gdx.input.isTouched()) {
            Vector3 touchPosition = new Vector3();
            touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPosition);
            bucket.x = clampWidthToWindow(touchPosition.x - bucketSize / 2, bucketSize);
        }

        // Kbd
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x = clampWidthToWindow(bucket.x - bucketVelocity * Gdx.graphics.getDeltaTime(), bucketSize);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x = clampWidthToWindow(bucket.x + bucketVelocity * Gdx.graphics.getDeltaTime(), bucketSize);
        }


        if (TimeUtils.nanoTime() - lastDropTime > dropTimeSpan) spawnDroplet();

        for (Iterator<Rectangle> iterator = raindrops.iterator(); iterator.hasNext(); ) {
            Rectangle raindrop = iterator.next();
            raindrop.y -= dropletVelocity * Gdx.graphics.getDeltaTime();

            if (raindrop.y + dropletSize < 0) {
                iterator.remove();
            }

            if (raindrop.overlaps(bucket)) {
                dropSound.play();
                iterator.remove();
            }
        }
    }

    private Float clampWidthToWindow(Float n, Integer objectWidth) {
        float maxX = viewportWidth - windowPadding - objectWidth;
        float minX = windowPadding;

        if (n >= minX && n <= maxX) {
            return n;
        } else if (n < minX) {
            return minX;
        } else if (n > maxX) {
            return maxX;
        } else {
            return viewportWidth / 2.0f;
        }
    }

    @Override
    public void dispose() {
        dropSound.dispose();
        rainMusic.dispose();
        dropImage.dispose();
        bucketImage.dispose();
        batch.dispose();
    }
}
