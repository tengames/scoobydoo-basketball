/*
The MIT License

Copyright (c) 2014 kong <tengames.inc@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.tengames.scoobydoobasketball.objects;

import woodyx.basicapi.physics.BoxUtility;
import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.sound.SoundManager;
import woodyx.basicapi.sprite.ObjectSprite;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.scoobydoobasketball.interfaces.GlobalVariables;
import com.tengames.scoobydoobasketball.main.Assets;
import com.tengames.scoobydoobasketball.screens.ScreenGame;

public class ScoobyDoo extends ObjectSprite {
	public static final byte RUN = 1;
	public static final byte WAIT = 2;
	public static final byte JUMP = 3;
	public static final byte HOPE = 4;
	public static final byte WIN = 5;
	public static final byte FAIL = 6;
	public static final byte FALL = 7;

	private static final byte VELOCITY = 35;
	public float stateTime;
	private ObjectModel model;
	private Animation aniRun, aniJump, aniHope, aniWin, aniFail, aniFall;
	private float target, oldPosition, timeChangeJump, timeChangeFall;
	private byte action, state;
	private boolean isMove, isReady, isFinish;

	public ScoobyDoo(World world, TextureRegion textureRegion, float x, float y, float scale, byte stateGame,
			boolean isInBox) {
		super(textureRegion, x, y, textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale,
				isInBox);
		// initialize
		this.state = stateGame;
		stateTime = 0;
		timeChangeJump = 0;
		timeChangeFall = 0;
		action = RUN;
		isMove = true;
		isFinish = false;
		setReady(false);

		this.setOriginCenter(this);

		// create model
		model = new ObjectModel(world, ObjectModel.KINEMATIC, ObjectModel.POLYGON,
				new Vector2(this.getOriginSize().x * 0.5f, this.getOriginSize().y * 0.8f), 0, new Vector2(x, y), 0, 5,
				0.2f, 0.1f, GlobalVariables.CATEGORY_SCENE, GlobalVariables.MASK_SCENE, "scoobydoo");

		// create animations
		TextureRegion[] trRun = Assets.getKeyFrames(Assets.taObjects, "scoo-run-", 1, 7);
		aniRun = Assets.loadAnimation(0.1f, trRun);

		TextureRegion[] trJump = Assets.getKeyFrames(Assets.taObjects, "scoo-jump-", 1, 5);
		aniJump = Assets.loadAnimation(0.15f, trJump);

		TextureRegion[] trHope1 = Assets.getKeyFrames(Assets.taObjects, "scoo-hope-", 1, 4);
		TextureRegion[] trHope2 = Assets.getKeyFrames(Assets.taObjects, "scoo-hope-", 3, 4);
		aniHope = Assets.loadAnimation(0.2f, trHope1, trHope2, trHope2);

		TextureRegion[] trFall1 = Assets.getKeyFrames(Assets.taObjects, "scoo-fall-", 1, 6);
		TextureRegion[] trFall2 = Assets.getKeyFrames(Assets.taObjects, "scoo-fall-", 4, 6);
		aniFall = Assets.loadAnimation(0.1f, trFall1, trFall2, trFall2);

		TextureRegion[] trWin = Assets.getKeyFrames(Assets.taObjects, "scoo-win-", 1, 9);
		aniWin = Assets.loadAnimation(0.1f, trWin);

		TextureRegion[] trFail = Assets.getKeyFrames(Assets.taObjects, "scoo-fail-", 1, 7);
		aniFail = Assets.loadAnimation(0.1f, trFail);
	}

	public void update(float deltaTime) {
		// update stateTime
		stateTime += deltaTime;
		// update follow
		this.updateFollowModel(model, -this.getOriginSize().x * 0.25f, 0, false);
		// update move
		if (isMove) {
			// move back
			if (oldPosition > target) {
				if (this.getX() <= target) {
					// move done
					switch (state) {
					case ScreenGame.STATE_NULL:
						// play sound
						SoundManager.playeSound(Assets.soReady);
						setAction(WAIT);
						// set ready
						setReady(true);
						break;
					case ScreenGame.STATE_WIN:
						isFinish = true;
						setAction(WIN);
						break;
					case ScreenGame.STATE_FAIL:
						isFinish = true;
						setAction(FAIL);
						break;
					default:
						break;
					}
					// turn off flag
					isMove = false;
				}
				// moving
				model.getBody().setLinearVelocity(-VELOCITY * deltaTime, 0);
			} else {
				// move forward
				if (this.getX() >= target) {
					// move done
					switch (state) {
					case ScreenGame.STATE_NULL:
						// play sound
						SoundManager.playeSound(Assets.soReady);
						setAction(WAIT);
						// set ready
						setReady(true);
						break;
					case ScreenGame.STATE_WIN:
						isFinish = true;
						setAction(WIN);
						break;
					case ScreenGame.STATE_FAIL:
						isFinish = true;
						setAction(FAIL);
						break;
					default:
						break;
					}
					// turn off flag
					isMove = false;
				}
				// moving
				model.getBody().setLinearVelocity(VELOCITY * deltaTime, 0);
			}
		} else {
			// stop move
			model.getBody().setLinearVelocity(0, 0);
		}
		// auto change action
		if (action == JUMP) {
			timeChangeJump += deltaTime;
			if (timeChangeJump >= 0.65f) {
				setAction(HOPE);
				timeChangeJump = 0;
			}
		}
		if (action == FALL) {
			timeChangeFall += deltaTime;
			if (timeChangeFall >= 1.1f) {
				setAction(HOPE);
				timeChangeFall = 0;
			}
		}
	}

	public void render(SpriteBatch batch) {
		TextureRegion keyFrame = null;
		switch (action) {
		case RUN:
			keyFrame = aniRun.getKeyFrame(stateTime, true);
			break;
		case WAIT:
			keyFrame = Assets.taObjects.findRegion("scoo-wait");
			break;
		case JUMP:
			keyFrame = aniJump.getKeyFrame(stateTime, true);
			break;
		case HOPE:
			keyFrame = aniHope.getKeyFrame(stateTime, true);
			break;
		case WIN:
			keyFrame = aniWin.getKeyFrame(stateTime, true);
			break;
		case FAIL:
			keyFrame = aniFail.getKeyFrame(stateTime, false);
			break;
		case FALL:
			keyFrame = aniFall.getKeyFrame(stateTime, true);
			break;
		default:
			break;
		}
		// render
		this.renderKeyFrame(keyFrame, batch);
	}

	public void moveTo(byte state, float target) {
		// get state
		this.state = state;

		// turn on move
		isMove = true;

		// get this position
		oldPosition = this.getX();

		// change action
		setAction(RUN);

		switch (state) {
		case ScreenGame.STATE_NULL:
			// add target
			this.target = BoxUtility.ConvertToBox(target);
			break;
		default:
			// add target
			this.target = BoxUtility.ConvertToBox(150);
			break;
		}
	}

	public void setAction(byte action) {
		if (this.action != action)
			this.action = action;
	}

	public boolean getReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public boolean getFinish() {
		return isFinish;
	}
}
