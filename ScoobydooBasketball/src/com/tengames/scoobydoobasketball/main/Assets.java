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

import woodyx.basicapi.screen.Asset;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets extends Asset {
	public static Texture txBgMenu, txBgGame;
	public static TextureAtlas taObjects;
	public static BitmapFont fScooby, fScore;
	public static Music muMenu, muGame;
	public static Sound soClick, soFail, soWin, soGoal, soKick, soReady;

	public static void preload() {
		loading("drawable/", "atlas", "loading");
		assetManager.finishLoading();
	}

	public static void load() {
		// loading backgrounds
		loading("drawable/backgrounds/", "jpg", "bg-menu", "bg-game");
		// loading objects
		loading("drawable/objects/", "atlas", "objects");
		// loading music
		loading("raw/", "mp3", "bmgame", "bmmenu");
		// loading sound
		loading("raw/", "ogg", "soclick", "sofail", "sogoal", "sokick", "soready", "sowin");
	}

	public static void loadDone() {
		// loaded backgrounds
		txBgGame = assetManager.get("drawable/backgrounds/bg-game.jpg");
		txBgGame.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		txBgMenu = assetManager.get("drawable/backgrounds/bg-menu.jpg");
		txBgMenu.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// loaded objects
		taObjects = assetManager.get("drawable/objects/objects.atlas");

		// loaded font
		fScooby = getFontGenerate("fonts/scoobydoo.ttf", 20, false);
		fScore = getFontGenerate("fonts/scoobydoo.ttf", 30, false);

		// loaded sound, music
		muGame = assetManager.get("raw/bmgame.mp3");
		muMenu = assetManager.get("raw/bmmenu.mp3");
		soClick = assetManager.get("raw/soclick.ogg");
		soFail = assetManager.get("raw/sofail.ogg");
		soGoal = assetManager.get("raw/sogoal.ogg");
		soKick = assetManager.get("raw/sokick.ogg");
		soReady = assetManager.get("raw/soready.ogg");
		soWin = assetManager.get("raw/sowin.ogg");
	}
}
