package com.lilleman.engine.lighting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Material {
	private Color ambient;
	private Color diffuse;
	private Color specular;
	
	private float shininess = 1.0f;
	
	public void setAmbient(Color ambient) {
		this.ambient = ambient;
	}

	public void setAmbient(float r, float g, float b) {
		this.ambient = new Color(r, g, b, 1.0f);
	}

	public void setAmbient(float r, float g, float b, float a) {
		this.ambient = new Color(r, g, b, a);
	}

	public void setDiffuse(Color diffuse) {
		this.diffuse = diffuse;
	}

	public void setDiffuse(float r, float g, float b) {
		this.diffuse = new Color(r, g, b, 1.0f);
	}

	public void setDiffuse(float r, float g, float b, float a) {
		this.diffuse = new Color(r, g, b, a);
	}

	public void setSpecular(Color specular) {
		this.specular = specular;
	}

	public void setSpecular(float r, float g, float b) {
		this.specular = new Color(r, g, b, 1.0f);
	}

	public void setSpecular(float r, float g, float b, float a) {
		this.specular = new Color(r, g, b, a);
	}
	
	public void setShininess(float shininess) {
		this.shininess = shininess;
	}
	
	public void setUniforms(ShaderProgram shader) {
		shader.setUniformf("MaterialAmbient", ambient);
		shader.setUniformf("MaterialDiffuse", diffuse);
		shader.setUniformf("MaterialSpecular", specular);
		shader.setUniformf("MaterialShininess", shininess);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Ambient: " + ambient + "\n");
		builder.append("Diffuse: " + diffuse + "\n");
		builder.append("Specular: " + specular + "\n");
		builder.append("Shininess: " + shininess + "\n");
		return builder.toString();
	}
}
