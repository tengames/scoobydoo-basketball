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
package com.tengames.scoobydoobasketball.main;

import woodyx.basicapi.accessor.ActorAccessor;
import woodyx.basicapi.accessor.SpriteAccessor;
import woodyx.basicapi.camera.XCamera;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.tengames.scoobydoobasketball.interfaces.AndroidListener;
import com.tengames.scoobydoobasketball.screens.ScreenLoading;

public class ScoobydooBasketball extends Game {
	public AndroidListener androidListener;

	private Stage stage;

	private Array<SpriteBatch> batchs;
	private Array<XCamera> cameras;

	@Override
	public void create() {
		/**
		 * initialize param
		 */
		// create stage
		stage = new Stage(800, 480, true);
		((OrthographicCamera) stage.getCamera()).setToOrtho(false, 800, 480);

		// create array batchs
		batchs = new Array<SpriteBatch>(1);
		SpriteBatch batch = new SpriteBatch();
		batchs.add(batch);

		// create array cameras
		cameras = new Array<XCamera>(1);
		XCamera camera = new XCamera(800, 480, true);
		camera.setCameraToCenter();
		cameras.add(camera);

		// set tween manager
		Tween.setWaypointsLimit(5);
		Tween.setCombinedAttributesLimit(5);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		// set screen
		setScreen(new ScreenLoading(this, stage, batchs, cameras));
	}

	@Override
	public void dispose() {
		super.dispose();
		getScreen().dispose();
	}

	public void registerAndroidListener(AndroidListener androidListener) {
		this.androidListener = androidListener;
	}
}
