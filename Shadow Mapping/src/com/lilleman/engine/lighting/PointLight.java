package com.lilleman.engine.lighting;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class PointLight {
	private List<PerspectiveCamera> cams;
	private Vector3 attenuation = new Vector3(1, 0, 0);
	private Color color = Color.WHITE;
	
	public PointLight(int shadowmapSize) {
		cams = new ArrayList<PerspectiveCamera>();
		for (int i = 0; i < 6; i++) {
			cams.add(new PerspectiveCamera(90, shadowmapSize, shadowmapSize));
		}
		cams.get(0).rotate(-90, 0, 1, 0);
		cams.get(1).rotate(90, 0, 1, 0);
		cams.get(2).rotate(-90, 1, 0, 0);
		cams.get(3).rotate(90, 1, 0, 0);
		cams.get(4).rotate(0, 0, 0, 0);
		cams.get(5).rotate(180, 0, 1, 0);
		for (int i = 0; i < 6; i++) {
			cams.get(i).update(true);
		}
	}
	
	public void setPosition(Vector3 position) {
		for (int i = 0; i < 6; i++) {
			cams.get(i).position.set(position);
			cams.get(i).update(true);
		}
	}
	
	public void setPosition(float x, float y, float z) {
		for (int i = 0; i < 6; i++) {
			cams.get(i).position.set(x, y, z);
			cams.get(i).update(true);
		}
	}
	
	public PerspectiveCamera getCamera(int i) {
		return cams.get(i);
	}

	public void setUniforms(ShaderProgram shader) {
		shader.setUniformf("Position", cams.get(0).position);
		shader.setUniformf("Attenuation", attenuation);
		shader.setUniformf("Color", color);
	}
}
