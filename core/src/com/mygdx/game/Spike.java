package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Spike {
	public boolean isAlive;
	public Vector2 speed;
	public Rectangle spikeBody;
	public boolean isHit;
	
	Spike(){
		isAlive = false;
		speed = new Vector2(0,200);
		spikeBody = new Rectangle();
		spikeBody.x = 0;
		spikeBody.y = 0;
		spikeBody.width = 14;
		spikeBody.height = 480;
		isHit = false;
	}
	public void hit(){
		isHit = true;
		isAlive = false;
	}
}
