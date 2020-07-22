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
package com.tengames.scoobydoobasketball.screens;

import woodyx.basicapi.camera.XCamera;
import woodyx.basicapi.screen.XScreen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.tengames.scoobydoobasketball.main.Assets;
import com.tengames.scoobydoobasketball.main.ScoobydooBasketball;

public class ScreenLoading extends XScreen implements Screen {
	private ScoobydooBasketball coreGame;

	private Image logo;
	private Image loadingFrame;
	private Image loadingBarHidden;
	private Image screenBg;
	private Image loadingBg;
	private Actor loadingBar;
	private float startX, endX;
	private float percent;

	public ScreenLoading(ScoobydooBasketball coreGame, Stage stage, Array<SpriteBatch> batchs, Array<XCamera> cameras) {
		super(stage, batchs, cameras);
		this.coreGame = coreGame;
		// load data
		Assets.preload();
		Assets.load();
		// initialize
		initialize(stage);
	}

	public void initialize(Stage stage) {
		// get texture atlas
		TextureAtlas taLoading = Assets.assetManager.get("drawable/loading.atlas");

		// initialize
		logo = new Image(taLoading.findRegion("libgdx-logo"));
		loadingFrame = new Image(taLoading.findRegion("loading-frame"));
		loadingBarHidden = new Image(taLoading.findRegion("loading-bar-hidden"));
		screenBg = new Image(taLoading.findRegion("screen-bg"));
		loadingBg = new Image(taLoading.findRegion("loading-frame-bg"));
		loadingBar = new Image(taLoading.findRegion("loading-bar1"));

		// Add all the actors to the stage
		stage.addActor(screenBg);
		stage.addActor(loadingBar);
		stage.addActor(loadingBg);
		stage.addActor(loadingBarHidden);
		stage.addActor(loadingFrame);
		stage.addActor(logo);

		// Make the background fill the screen
		screenBg.setSize(800, 480);

		// Place the logo in the middle of the screen and 100 px up
		logo.setX((800 - logo.getWidth()) / 2);
		logo.setY((480 - logo.getHeight()) / 2 + 100);

		// Place the loading frame in the middle of the screen
		loadingFrame.setX((800 - loadingFrame.getWidth()) / 2);
		loadingFrame.setY(150);

		// Place the loading bar at the same spot as the frame, adjusted a few px
		loadingBar.setX(loadingFrame.getX() + 15);
		loadingBar.setY(loadingFrame.getY() + 5);

		// Place the image that will hide the bar on top of the bar, adjusted a few px
		loadingBarHidden.setX(loadingBar.getX() + 35);
		loadingBarHidden.setY(loadingBar.getY() - 3);
		// The start position and how far to move the hidden loading bar
		startX = loadingBarHidden.getX();
		endX = 440;

		// The rest of the hidden bar
		loadingBg.setSize(450, 50);
		loadingBg.setX(loadingBarHidden.getX() + 30);
		loadingBg.setY(loadingBarHidden.getY() + 3);
	}

	@Override
	public void update(float deltaTime) {
		// loaded
		if (Assets.assetManager.update() && percent > 0.99f) {
			// load done
			Assets.loadDone();
			// TODO: set menu screen
			coreGame.setScreen(new ScreenMenu(coreGame, stage, batchs, cameras));
		}

		percent = Interpolation.linear.apply(percent, Assets.assetManager.getProgress(), 0.02f);

		// Update positions (and size) to match the percentage
		loadingBarHidden.setX(startX + endX * percent);
		loadingBg.setX(loadingBarHidden.getX() + 30);
		loadingBg.setWidth(450 - 450 * percent);
		loadingBg.invalidate();

		// update stage
		updateStage(deltaTime);
	}

	@Override
	public void draw() {
		renderStage();
	}

	@Override
	public void render(float deltaTime) {
		update(deltaTime);
		clearScreen();
		draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}
