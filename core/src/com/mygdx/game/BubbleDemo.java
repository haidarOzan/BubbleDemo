package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
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
	Texture lifeImage;
	TextureRegion spikeImage;
	Sound popSound;
	Music backMusic;
	Music startMusic;
	Music playerHitSound;
	Music popMusic;
	Music upLevelMusic;
	Rectangle avatar;
	Spike spike;
	Vector3 touchPos;
	Array<Bubble> bubbleArr;
	Pool<Bubble> bubblePool;
	boolean isAnyBubbleAlive;
	boolean secondFingerTouching;
	int diffLevel = 1;
	float deltaTime;
	int life = 3;
	int score;
	String scoreStr;
	BitmapFont scoreFont;
	boolean firstLoad = false;

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
		if (life < 1) {
			backMusic.play();
			life = 3;
			score = 0;
		}
		if (!firstLoad) {
			playerHitSound = Gdx.audio.newMusic(Gdx.files
					.internal("sounds/player_hit.mp3"));
			startMusic = Gdx.audio.newMusic(Gdx.files
					.internal("sounds/startSound.mp3"));
			backMusic = Gdx.audio.newMusic(Gdx.files
					.internal("sounds/backMusic.mp3"));
			upLevelMusic = Gdx.audio.newMusic(Gdx.files
					.internal("sounds/gong.mp3"));

			popMusic = Gdx.audio
					.newMusic(Gdx.files.internal("sounds/pop2.mp3"));
			batch = new SpriteBatch();
			backGround = new Texture(Gdx.files.internal("images/BG.JPG"));
			lifeImage = new Texture(Gdx.files.internal("images/29.png"));
			bubbleImage = new Texture(Gdx.files.internal("images/green1.png"));
			camera = new OrthographicCamera();
			spike = new Spike();
			touchPos = new Vector3();
			avatar = new Rectangle();
			scoreFont = new BitmapFont();
			scoreStr = new String();
			createAnimations();
			bubbleArr = new Array<Bubble>(40);
			bubblePool = new Pool<Bubble>() {
				@Override
				protected Bubble newObject() {
					return new Bubble();
				}
			};
			startMusic.play();
			firstLoad = true;
		}
		bubbleArr.clear();
		bubblePool.clear();
		bubbleArr.add(bubblePool.obtain());
		diffLevel = 1;
		isAnyBubbleAlive = true;
		spike.isAlive = false;
		camera.setToOrtho(false, 800, 480);
		avatar.x = 800 / 2 - avatar.width / 2;
		avatar.y = 58;
		avatar.width = 36;
		avatar.height = 56;
		touchPos.x = avatar.x + avatar.width;

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

		TextureAtlas standAtlas = new TextureAtlas(
				Gdx.files.internal("frames/stand.pack"));
		TextureRegion[] standFrames = new TextureRegion[26];
		for (int i = 0; i < 26; i++) {
			standFrames[i] = standAtlas.findRegion("" + (i));
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
		scoreStr = "" + score;
		secondFingerTouching = Gdx.input.isTouched(1);
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), 0, 0);
			camera.unproject(touchPos);
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT)
				|| (avatar.x > (touchPos.x + avatar.width))) {
			avatar.x -= 200 * deltaTime;
			currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT)
				|| (avatar.x < (touchPos.x - avatar.width))) {
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
		if ((Gdx.input.isKeyPressed(Keys.SPACE) || secondFingerTouching)
				&& !spike.isAlive) {
			shootSpike();
		}
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(backGround, 0, 0, 800, 480);
		batch.draw(currentFrame, avatar.x, avatar.y);
		for (Bubble bub : bubbleArr) {
			if (bub.isAlive) {
				batch.draw(bubbleImage, bub.body.x, bub.body.y, bub.size,
						bub.size);
			}
		}
		for (int i = 0; i < life; i++) {
			batch.draw(lifeImage, (10 + 20 * i), 10);
		}
		if (spike.isAlive) {
			batch.draw(spikeAnimation.getKeyFrame(stateTime, true),
					spike.spikeBody.x, spike.spikeBody.y);

		}
		scoreFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		scoreFont.draw(batch, scoreStr, 700, 30);
		camera.update();
		batch.end();
		touchPos.x = avatar.x + avatar.width;
		stepWorld();
	}

	private void stepWorld() {
		isAnyBubbleAlive = false;
		for (Bubble tempBub : bubbleArr) {
			if (tempBub.isAlive) {
				isAnyBubbleAlive = true;
				if (checkAvatarHit(tempBub)) {
					break;
				}
				checkWallCollusion(tempBub);
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
			if (spike.spikeBody.y > 480) {
				spike.isAlive = false;
			}
		}
	}

	private void createBubbles() {
		diffLevel++;
		if (diffLevel % 4 == 0)
			life++;
		int temp = 0;
		while (temp < diffLevel) {
			bubbleArr.add(bubblePool.obtain());
			temp++;
		}
		upLevelMusic.play();
	}

	private void checkWallCollusion(Bubble bub) {
		if (bub.body.x < 12) {
			bub.body.x = 12;
			bub.speed.x = -bub.speed.x;
		}
		if (bub.body.x > 788 - bub.size) {
			bub.body.x = 788 - (bub.size);
			bub.speed.x = -bub.speed.x;
		}
		if (bub.body.y < 58) {
			bub.body.y = 58;
			bub.speed.y = -bub.speed.y;
		}
		if (bub.body.y > 480 - bub.size) {
			bub.body.y = 480 - bub.size;
			bub.speed.y = -bub.speed.y;
		}
	}

	public boolean checkAvatarHit(Bubble tempBub) {
		if (tempBub.body.overlaps(avatar)) {
			playerHitSound.play();
			life--;
			create();
			return true;
		}
		return false;
	}

	public void checkSpikeHit(Bubble tempBub) {
		if (spike.spikeBody.overlaps(tempBub.body)) {
			score += (100 + (25 * (tempBub.hitCount - 1)));
			spike.isAlive = false;
			spike.spikeBody.y = -480;
			if (tempBub.hitCount >= 3) {
				tempBub.kill();
				bubbleArr.removeValue(tempBub, false);
				bubblePool.free(tempBub);
			} else {
				bubbleArr.add(bubblePool.obtain());
				tempBub.pop(bubbleArr.peek());
			}
			popMusic.play();
		}
	}

	public void shootSpike() {
		spike.spikeBody.y = -480 + avatar.y;
		spike.isAlive = true;
		spike.spikeBody.x = avatar.x + 11;
	}

	@Override
	public void dispose() {
		playerHitSound.dispose();
		startMusic.dispose();
		backMusic.dispose();
		upLevelMusic.dispose();
		popMusic.dispose();
		batch.dispose();
		backGround.dispose();
		lifeImage.dispose();
		bubbleImage.dispose();
		scoreFont.dispose();
	}

}
