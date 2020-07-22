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
import woodyx.basicapi.physics.ObjectsJoint;
import woodyx.basicapi.sprite.ObjectSprite;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.scoobydoobasketball.interfaces.GlobalVariables;
import com.tengames.scoobydoobasketball.main.Assets;

public class Basket extends ObjectSprite {
	public static final byte VELOCITY = 20;

	private ObjectModel mdBound, mdLeftSide, mdSensor, mdHangUp, mdHangBottom;
	private ObjectSprite spBasketHang;
	private Animation animation;
	private Vector2 target;
	private float stateTime, timeAnimation;
	private byte skillAffect;
	private boolean isAnimation, isMove;

	public Basket(World world, TextureRegion textureRegion, float x, float y, boolean isInBox) {
		super(textureRegion, x, y, isInBox);
		// initialize
		skillAffect = Zombie.SKILL_NULL;
		stateTime = 0;
		timeAnimation = 0;
		isAnimation = false;
		isMove = false;

		// create basket hang
		spBasketHang = new ObjectSprite(Assets.taObjects.findRegion("basket-hell"), x + 20, y - 342 / 2, true);

		// create models
		mdBound = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON, this.getOriginSize(), 0,
				new Vector2(x, y), 0, 5, 0.2f, 0.1f, GlobalVariables.CATEGORY_SCENE, GlobalVariables.MASK_SCENE,
				"basket");
		mdBound.getBody().getFixtureList().get(0).setSensor(true);

		mdLeftSide = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON,
				new Vector2(10, this.getOriginSize().y), 0, new Vector2(x, y), 0, 5, 0.5f, 0.1f,
				GlobalVariables.CATEGORY_SCENE, GlobalVariables.MASK_SCENE, "basket");

		mdSensor = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON, new Vector2(10, 5), 0,
				new Vector2(x, y), 0, 5, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENE, GlobalVariables.MASK_SCENE,
				"sensor");
		mdSensor.getBody().getFixtureList().get(0).setSensor(true);

		ObjectsJoint jSide = new ObjectsJoint(world, mdLeftSide, mdBound, ObjectsJoint.WELD, new Vector2(-5, 0),
				new Vector2(-getOriginSize().x / 2, 0), false);
		jSide.createJoint(world);

		ObjectsJoint jSensor = new ObjectsJoint(world, mdBound, mdSensor, ObjectsJoint.WELD, new Vector2(0, 0),
				new Vector2(0, 0), false);
		jSensor.createJoint(world);

		// create hang
		mdHangUp = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON, new Vector2(87 / 2, 130), 0,
				new Vector2(x, y), 0, 5, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENE, GlobalVariables.MASK_SCENE,
				"hang");

		mdHangBottom = new ObjectModel(world, ObjectModel.KINEMATIC, ObjectModel.POLYGON, new Vector2(12, 190), 0,
				new Vector2(x + 100, y - 342 / 2 - 30), 0, 5, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENE,
				GlobalVariables.MASK_SCENE, "hang");

		ObjectsJoint jHang = new ObjectsJoint(world, mdHangUp, mdHangBottom, ObjectsJoint.WELD,
				new Vector2(87 / 4, -130 / 2), new Vector2(0, 190 / 2), false);
		jHang.createJoint(world);

		// attach models
		ObjectsJoint jBound = new ObjectsJoint(world, mdBound, mdHangUp, ObjectsJoint.WELD,
				new Vector2(getOriginSize().x / 2, 0), new Vector2(-87 / 4, -getOriginSize().y), false);
		jBound.createJoint(world);

		// create animation
		TextureRegion[] trBasket = Assets.getKeyFrames(Assets.taObjects, "basket-", 1, 4);
		animation = Assets.loadAnimation(0.1f, trBasket);
	}

	public void update(float deltaTime) {
		// update stateTime
		stateTime += deltaTime;

		// update follow
		spBasketHang.updateFollowModel(mdHangBottom, -80, 0, true);
		this.updateFollowModel(mdBound, 0, 0, true);

		// auto change state
		if (isAnimation) {
			timeAnimation += deltaTime;
			if (timeAnimation >= 2) {
				isAnimation = false;
				timeAnimation = 0;
			}
		}

		// move effect by zombie
		if (isMove) {
			switch (skillAffect) {
			case Zombie.SKILL_DOWN:
				if (this.getY() > target.y) {
					mdHangBottom.getBody().setLinearVelocity(0, -VELOCITY * deltaTime);
				} else {
					mdHangBottom.getBody().setLinearVelocity(0, 0);
					isMove = false;
				}
				break;
			case Zombie.SKILL_LEFT:
				if (this.getX() > target.x) {
					mdHangBottom.getBody().setLinearVelocity(-VELOCITY * deltaTime, 0);
				} else {
					mdHangBottom.getBody().setLinearVelocity(0, 0);
					isMove = false;
				}
				break;
			case Zombie.SKILL_RIGHT:
				if (this.getX() < target.x) {
					mdHangBottom.getBody().setLinearVelocity(VELOCITY * deltaTime, 0);
				} else {
					mdHangBottom.getBody().setLinearVelocity(0, 0);
					isMove = false;
				}
				break;
			case Zombie.SKILL_UP:
				if (this.getY() < target.y) {
					mdHangBottom.getBody().setLinearVelocity(0, VELOCITY * deltaTime);
				} else {
					mdHangBottom.getBody().setLinearVelocity(0, 0);
					isMove = false;
				}
				break;
			default:
				break;
			}
		}
	}

	public void renderHanger(SpriteBatch batch) {
		// render basket hang
		spBasketHang.draw(batch);
	}

	public void renderBasket(SpriteBatch batch) {
		// render basket
		TextureRegion keyFrame = null;
		if (isAnimation) {
			keyFrame = animation.getKeyFrame(stateTime, true);
		} else {
			keyFrame = animation.getKeyFrame(0);
		}
		renderKeyFrame(keyFrame, batch);
	}

	public void setAnimation() {
		if (!isAnimation)
			isAnimation = true;
	}

	public void affectSkills(byte skillAffect) {
		this.skillAffect = skillAffect;
		// turn on flag
		isMove = true;
		// get current position
		target = new Vector2(this.getX(), this.getY());
		switch (skillAffect) {
		case Zombie.SKILL_DOWN:
			target.y -= BoxUtility.ConvertToBox(MathUtils.random(50));
			break;
		case Zombie.SKILL_LEFT:
			target.x -= BoxUtility.ConvertToBox(MathUtils.random(50));
			break;
		case Zombie.SKILL_RIGHT:
			target.x += BoxUtility.ConvertToBox(MathUtils.random(20));
			break;
		case Zombie.SKILL_UP:
			target.y += BoxUtility.ConvertToBox(MathUtils.random(50));
			break;
		default:
			break;
		}
	}
}
