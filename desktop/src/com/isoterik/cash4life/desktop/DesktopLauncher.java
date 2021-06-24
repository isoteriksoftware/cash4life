package com.isoterik.cash4life.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.isoterik.cash4life.Cash4Life;
import com.isoterik.cash4life.GlobalConstants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(GlobalConstants.GUI_WIDTH, GlobalConstants.GUI_HEIGHT - 300);
		new Lwjgl3Application(new Cash4Life(), config);
	}
}
