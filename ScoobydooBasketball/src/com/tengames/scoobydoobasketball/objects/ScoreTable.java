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
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quart;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.tengames.scoobydoobasketball.main.Assets;

public class ScoreTable extends Group {
	private TweenManager tween;
	private Image imgTable, imgLeftLight, imgRightLight;
	private Label lbPoint, lbLancers;
	private int lancers, point;

	public ScoreTable(TweenManager tween, float duration, Vector2 position, int lancers) {
		// initialize
		this.tween = tween;
		this.lancers = lancers;
		this.point = 0;

		// create images
		imgTable = new Image(Assets.taObjects.findRegion("score-table"));
		imgTable.setPosition(0, 0);

		this.setBounds(position.x, position.y, imgTable.getWidth(), imgTable.getHeight());

		imgLeftLight = new Image(Assets.taObjects.findRegion("green-light"));
		imgLeftLight.setPosition(2 * imgLeftLight.getWidth() + 9, imgTable.getHeight() - imgLeftLight.getHeight() - 2);
		imgLeftLight.setVisible(false);

		imgRightLight = new Image(Assets.taObjects.findRegion("red-light"));
		imgRightLight.setPosition(imgTable.getWidth() - 4 * imgLeftLight.getWidth() - 2,
				imgTable.getHeight() - imgRightLight.getHeight());
		imgRightLight.setVisible(false);

		// create labels
		LabelStyle lbsScooby = new LabelStyle(Assets.fScooby, Color.WHITE);
		lbPoint = new Label("" + point, lbsScooby);
		lbPoint.setPosition(30, imgTable.getHeight() / 2);

		lbLancers = new Label("" + lancers, lbsScooby);
		lbLancers.setPosition(110, imgTable.getHeight() / 2);

		// add to stage
		this.addActor(imgTable);
		this.addActor(imgLeftLight);
		this.addActor(imgRightLight);
		this.addActor(lbPoint);
		this.addActor(lbLancers);

		// create tween
		Timeline.createSequence().push(Tween.set(this, ActorAccessor.OPACITY).target(0))
				.push(Tween.to(this, ActorAccessor.OPACITY, duration).target(1).ease(Quart.OUT)).start(tween);
	}

	public void update(float deltaTime) {
		// update tween
		tween.update(deltaTime);
	}

	public void decreaseLancers(TweenManager tween) {
		// set visible
		imgRightLight.setVisible(true);
		// create tween light
		createTweenLight(imgRightLight, tween);
		// decrease lancers
		if (lancers - 1 >= 0)
			lancers--;
		else
			lancers = 0;
		// show label
		lbLancers.setText("" + lancers);
		// create tween label
		createTweenLabel(lbLancers, tween);
	}

	public void increasePoint(TweenManager tween) {
		// set visible
		imgLeftLight.setVisible(true);
		// create tween light
		createTweenLight(imgLeftLight, tween);
		// increase points
		point++;
		// show label
		lbPoint.setText("" + point);
		// create tween label
		createTweenLabel(lbPoint, tween);
	}

	public int getLancers() {
		return lancers;
	}

	public int getPoint() {
		return point;
	}

	private void createTweenLight(final Image img, final TweenManager tween) {
		Tween.to(img, ActorAccessor.OPACITY, 2f).target(0).repeat(10, 0.1f).setCallback(new TweenCallback() {
			@Override
			public void onEvent(int arg0, BaseTween<?> arg1) {
				// set invisible
				img.setVisible(false);
				// set opacity
				Tween.set(img, ActorAccessor.OPACITY).target(1).start(tween);
			}
		}).start(tween);
	}

	private void createTweenLabel(final Label lb, TweenManager tween) {
		Timeline.createSequence().push(Tween.to(lb, ActorAccessor.TINT, 2f).target(1, 0, 0).ease(Elastic.OUT))
				.pushPause(2f).push(Tween.to(lb, ActorAccessor.TINT, 4f).target(1, 1, 1).ease(Linear.INOUT))
				.start(tween);
	}
}
