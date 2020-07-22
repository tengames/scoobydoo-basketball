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

import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.sprite.ObjectSprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.scoobydoobasketball.interfaces.GlobalVariables;

public class Ball extends ObjectSprite {
	private ObjectModel model;

	public Ball(World world, TextureRegion textureRegion, float x, float y, float scale, float pow, float angle,
			boolean isInBox) {
		super(textureRegion, x, y, textureRegion.getRegionWidth() * scale, textureRegion.getRegionHeight() * scale,
				isInBox);
		// create model
		model = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.CIRCLE, this.getOriginSize(),
				this.getOriginSize().x / 2, new Vector2(x, y), 0, 1, 0.2f, 0.5f, GlobalVariables.CATEGORY_BALL,
				GlobalVariables.MASK_BALL, "ball");
		// set attributes
		model.getBody().setBullet(true);
		model.getBody().setLinearVelocity(new Vector2(pow * MathUtils.cos(angle), pow * MathUtils.sin(angle)));
	}

	public void update(float deltaTime) {
		this.updateFollowModel(model, 0, 0, true);
	}

	public void render(SpriteBatch batch) {
		this.draw(batch);
	}

	public void dispose(World world) {
		model.dispose(world);
	}
}
