package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Spike {
	public boolean isAlive;
	public Vector2 speed;
	public Rectangle spikeBody;
	public boolean isHit;
	
	Spike(){
		isAlive = false;
		speed = new Vector2(0,300);
		spikeBody = new Rectangle();
		spikeBody.x = 0;
		spikeBody.y = 0;
		spikeBody.width = 14;
		spikeBody.height = 480;
		isHit = false;
	}
}
