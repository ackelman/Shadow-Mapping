package com.lilleman.engine.lighting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class SpotLight {
	private PerspectiveCamera cam;
	private Vector3 attenuation = new Vector3(1, 0, 0);
	private Color color = Color.WHITE;
	private float exponent = 1.0f;
	private float outerCutoff = 90.0f;
	private float innerCutoff = 0.0f;
	
	public SpotLight(float fov, int shadowmapSize) {
		cam = new PerspectiveCamera(fov, shadowmapSize, shadowmapSize);
		outerCutoff = cam.fieldOfView;
	}
	
	public void setPosition(Vector3 position) {
		this.cam.position.set(position);
		this.cam.update(true);
	}

	public void setPosition(float x, float y, float z) {
		this.cam.position.set(x, y, z);
		this.cam.update(true);
	}
		
	public void setDirection(Vector3 direction) {
		this.cam.direction.set(direction);
		this.cam.update(true);
	}

	public void setDirection(float x, float y, float z) {
		this.cam.direction.set(x, y, z);
		this.cam.update(true);
	}
	
	public void lookAt(float x, float y, float z) {
		cam.lookAt(x, y, z);
		cam.update(true);
	}
	
	public PerspectiveCamera getCamera() {
		return cam;
	}

	public void setUniforms(ShaderProgram shader) {
		shader.setUniformf("Position", cam.position);
		shader.setUniformf("Direction", cam.direction);
		shader.setUniformf("OuterCutoff", outerCutoff);
		shader.setUniformf("InnerCutoff", innerCutoff);
		shader.setUniformf("Attenuation", attenuation);
		shader.setUniformf("Color", color);
		shader.setUniformf("Exponent", exponent);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Position: " + cam.position.toString() + "\n");
		builder.append("Direction: " + cam.direction + "\n");
		builder.append("Cutoff: " + cam.fieldOfView + "\n");
		return builder.toString();
	}
}
