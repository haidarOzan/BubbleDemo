package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class BubbleDemo extends ApplicationAdapter {
	OrthographicCamera camera;
	SpriteBatch batch;
	Texture dropImage;
	Texture backGround;
	TextureRegion defaultImage;
	Texture bubbleImage;
	TextureRegion spikeImage;
	Sound popSound;
	Music backMusic;
	Rectangle avatar;
	Spike spike;
	Vector3 touchPos;
	Array<Bubble> bubbleArr;
	Pool<Bubble> bubblePool;
	boolean isAnyBubbleAlive;
	public int freeBubIndex = 0;
	int diffLevel = 0;
	float deltaTime;
	private static final int STAND_FRAME_COLS = 7;
	private static final int STAND_FRAME_ROWS = 2;
	
	TextureRegion[] spikeFrames;
	Animation spikeAnimation;
	Animation walkLeftAnimation;
	Animation walkRightAnimation;
	Texture walkSheet;
	TextureRegion[] walkFrames;

	Animation standAnimation;
	Texture standSheet;
	TextureRegion[] standFrames;
	TextureRegion currentFrame;

	float stateTime;

	@Override
	public void create() {
		batch = new SpriteBatch();
		backGround = new Texture(Gdx.files.internal("images/BG.JPG"));
		bubbleImage = new Texture(Gdx.files.internal("images/29.png"));
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		touchPos = new Vector3();
		bubblePool = new Pool<Bubble>() {

			@Override
			protected Bubble newObject() {
				return new Bubble();
			}

		};
		bubbleArr = new Array<Bubble>(40);
		int count = 0;
		while (count < 4) {
			bubbleArr.add(bubblePool.obtain());
			count++;
		}
		isAnyBubbleAlive = true;
		freeBubIndex = 1;
		spike = new Spike();
		spike.isAlive = false;
		createAnimations();

		avatar = new Rectangle();
		avatar.x = 800 / 2 - 64 / 2;
		avatar.y = 58;
		avatar.width = 36;
		avatar.height = 56;
		touchPos.x = avatar.x + 64 / 2;

		// backMusic = Gdx.audio.newMusic(Gdx.files.internal("862_gong.mp3"));
		// backMusic.play();
	}

	private void createAnimations() {
		TextureAtlas spikeatlas = new TextureAtlas(
				Gdx.files.internal("frames/rope2.pack"));
		TextureRegion[] spikeFrames = new TextureRegion[2];
		for (int i = 0; i < 2; i++) {
			spikeFrames[i] = spikeatlas.findRegion("rope" + (i));
		}
		spikeAnimation = new Animation(0.045f, spikeFrames);
		
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("frames/textures.pack"));
		TextureRegion[] walkLeftFrames = new TextureRegion[20];
		for (int i = 0; i < 20; i++) {
			walkLeftFrames[i] = atlas.findRegion("m" + (i));
		}
		walkLeftAnimation = new Animation(0.033f, walkLeftFrames);

		TextureRegion[] walkRightFrames = new TextureRegion[20];
		for (int i = 0; i < 20; i++) {
			walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
			walkRightFrames[i].flip(true, false);
		}
		walkRightAnimation = new Animation(0.033f, walkRightFrames);

		int index = 0;
		standSheet = new Texture(
				Gdx.files.internal("frames/standing_animation_sheet.png"));
		TextureRegion[][] tmp2 = TextureRegion.split(standSheet,
				standSheet.getWidth() / STAND_FRAME_COLS,
				standSheet.getHeight() / STAND_FRAME_ROWS);
		standFrames = new TextureRegion[STAND_FRAME_COLS * STAND_FRAME_ROWS];
		index = 0;
		for (int i = 0; i < STAND_FRAME_ROWS; i++) {
			for (int j = 0; j < STAND_FRAME_COLS; j++) {
				standFrames[index++] = tmp2[i][j];
			}
		}
		standAnimation = new Animation(0.05f, standFrames);
		defaultImage = standFrames[0];
		stateTime = 0f;
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		currentFrame = defaultImage;
		deltaTime = Gdx.graphics.getDeltaTime();
		stateTime += deltaTime;
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), 0, 0);
			camera.unproject(touchPos);
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT) || (avatar.x > (touchPos.x + 32))) {
			avatar.x -= 200 * deltaTime;
			currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT)
				|| (avatar.x < (touchPos.x - 32))) {
			avatar.x += 200 * deltaTime;
			currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
		} else {
			currentFrame = standAnimation.getKeyFrame(stateTime, true);
		}
		if (avatar.x < 0) {
			avatar.x = 0;
		}
		if (avatar.x > 800 - 36) {
			avatar.x = 800 - 36;
		}
		if (Gdx.input.isKeyPressed(Keys.SPACE) && !spike.isAlive) {
			shootSpike();
		}
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(backGround, 0, 0, 800, 480);
		batch.draw(currentFrame, avatar.x, avatar.y);
		for (Bubble bub : bubbleArr) {
			if (bub.isAlive) {
				batch.draw(bubbleImage, bub.body.x, bub.body.y);
			}
		}
		if(spike.isAlive) {
			batch.draw(spikeAnimation.getKeyFrame(stateTime, true), spike.spikeBody.x, spike.spikeBody.y);

		}
		camera.update();
		batch.end();
		touchPos.x = avatar.x + 32;
		stepWorld();
	}

	private void stepWorld() {
		isAnyBubbleAlive = false;
		for (Bubble tempBub : bubbleArr) {
			if (tempBub.isAlive) {
				isAnyBubbleAlive = true;
				checkWallCollusion(tempBub);
				checkAvatarHit(tempBub);
				checkSpikeHit(tempBub);
				tempBub.body.x += tempBub.speed.x * deltaTime;
				tempBub.body.y += tempBub.speed.y * deltaTime;
				tempBub.speed.y -= 3;
			}

		}
		if (!isAnyBubbleAlive) {
			createBubbles();
		}
		if (spike.isAlive) {
			spike.spikeBody.y += spike.speed.y * deltaTime;
			if(spike.spikeBody.y > 480){
				spike.isAlive = false;
			}
		}
	}

	private void createBubbles() {
		diffLevel++;
		freeBubIndex = diffLevel + 1;
		int temp = diffLevel;
		while (temp >= 0) {
			bubbleArr.get(temp--).reset();
		}
	}

	private void checkWallCollusion(Bubble bub) {
		if (bub.body.x < 0 + 8) {
			bub.body.x = 0 + 8;
			bub.speed.x = -bub.speed.x;
		}
		if (bub.body.x > 800 - 8) {
			bub.body.x = 800 - 8;
			bub.speed.x = -bub.speed.x;
		}
		if (bub.body.y < 0 + bub.size) {
			bub.body.y = 0 + bub.size;
			bub.speed.y = -bub.speed.y;
		}
		if (bub.body.y > 480 - bub.size) {
			bub.body.y = 480 - (int) bub.size;
			bub.speed.y = -bub.speed.y;
		}
	}

	public void checkAvatarHit(Bubble tempBub) {
		if (tempBub.body.overlaps(avatar)) {
			create();
		}
	}

	public void checkSpikeHit(Bubble tempBub) {
		if (spike.spikeBody.overlaps(tempBub.body)) {
			spike.isAlive = false;
			spike.spikeBody.y = -480;
			// spike.hit(bubbleArr, freeBubIndex);
			if (tempBub.size < 8) {
				tempBub.kill();
				bubblePool.free(tempBub);
			} else {
				bubbleArr.add(bubblePool.obtain());
				tempBub.pop(bubbleArr.peek());
			}
		}
	}

	public void shootSpike() {
		spike.spikeBody.y = -480;
		spike.isAlive = true;
		spike.spikeBody.x = avatar.x;
	}
}
