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

import woodyx.basicapi.accessor.ActorAccessor;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Quart;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Presentation extends Group {
	private TweenManager tween;

	// for logo
	public Presentation(TweenManager tween, TextureRegion region, float duration, int x, int y) {
		this.tween = tween;
		// create image
		Image img = new Image(region);
		this.setBounds(x - img.getWidth() / 2, y, img.getWidth(), img.getHeight());
		this.addActor(img);
		// create tween
		Timeline.createSequence().push(Tween.set(this, ActorAccessor.SCALE_XY).target(0, 0))
				.push(Tween.to(this, ActorAccessor.SCALE_XY, duration).target(1, 1).ease(Elastic.OUT)).start(tween);
	}

	// for scooby doo
	public Presentation(TweenManager tween, TextureRegion region, float duration, Vector2 position, Vector2 target) {
		this.tween = tween;
		// create image
		Image img = new Image(region);
		this.setBounds(position.x, position.y, img.getWidth(), img.getHeight());
		this.addActor(img);
		// create tween
		Tween.to(this, ActorAccessor.POS_XY, duration).target(target.x, target.y).ease(Quart.OUT).start(tween);
	}

	public void update(float deltaTime) {
		// update tween
		tween.update(deltaTime);
	}
}
