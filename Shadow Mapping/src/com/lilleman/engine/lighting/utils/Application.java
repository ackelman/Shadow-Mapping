package com.lilleman.engine.lighting.utils;

import com.badlogic.gdx.ApplicationListener;

public class Application implements ApplicationListener {
	ApplicationListener listener;
	
	public Application(ApplicationListener listener) {
		this.listener = listener;
	}

	@Override
	public void create() {
		try {
			listener.create();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dispose() {
		try {
			listener.dispose();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void pause() {
		try {
			listener.pause();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void render() {
		try {
			listener.render();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void resize(int arg0, int arg1) {
		try {
			listener.resize(arg0, arg1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void resume() {
		try {
			listener.resume();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
