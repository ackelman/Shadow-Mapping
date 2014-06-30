package com.lilleman.engine.lighting;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.lilleman.engine.lighting.ShadowMapCube.ShadowMapRenderer;
import com.lilleman.engine.lighting.utils.Application;

public class TestPointlight extends ApplicationAdapter implements ShadowMapRenderer {
	PerspectiveCamera cam;
	PointLight light;
	Mesh plane;
	Mesh cube;
	Material material;
	
	CameraInputController camController;
	
	public static final int SHADOW_MAP_SIZE = 512;
	ShadowMapCube shadowMap;

	@Override
	public void create() {
		setupScene();
		setup();
		setupShadowMap();

		camController = new CameraInputController(cam);

		Gdx.input.setInputProcessor(camController);
	}

	private void setupScene() {
		plane = new Mesh(true, 4, 4, new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));
		plane.setVertices(new float[] {
				-10, -1, 10,	0, 1, 0,
				10, -1, 10,		0, 1, 0,
				10, -1, -10,	0, 1, 0,
				-10, -1, -10,	0, 1, 0 });
		plane.setIndices(new short[] { 3, 2, 1, 0 });
		cube = new ObjLoader().loadModel(Gdx.files.internal("data/cube.obj")).meshes.get(0);
	}
	
	private void setupShadowMap() {
		shadowMap = new ShadowMapCube(SHADOW_MAP_SIZE);
		shadowMap.setRenderer(this);
		shadowMap.setLight(light);
		shadowMap.setCamera(cam);
	}

	private void setup() {
		light = new PointLight(SHADOW_MAP_SIZE);
		light.setPosition(-10, 5, 0);
		
		material = new Material();
		material.setSpecular(0.5f, 0.5f, 0.5f, 1);
		material.setAmbient(0.25f, 0, 0, 1);
		material.setDiffuse(0.25f, 0, 0, 1);
		
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 5, 10);
		cam.lookAt(0, 0, 0);
		cam.update();
	}
	
	@Override
	public void renderShadowMap(ShaderProgram shadowGenShader) {
		plane.render(shadowGenShader, GL20.GL_TRIANGLE_FAN);
		cube.render(shadowGenShader, GL20.GL_TRIANGLES);
	}
	
	@Override
	public void renderFull(ShaderProgram shaderMapShader) {
		material.setUniforms(shaderMapShader);
		plane.render(shaderMapShader, GL20.GL_TRIANGLE_FAN);
		cube.render(shaderMapShader, GL20.GL_TRIANGLES);
	}
	
	double radians = 0;
	
	@Override
	public void render() {		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		
		radians += Gdx.graphics.getDeltaTime() * Math.PI / 4.0f;
		if (radians > 2.0 * Math.PI) {
			radians -= 2.0 * Math.PI;
		}
		float x = (float) Math.sin(radians) * 2;
		float y = 3;
		float z = (float) Math.cos(radians) * 2;
		light.setPosition(x, y, z);
		
		shadowMap.render();
	}

	@Override
	public void dispose() {
		shadowMap.dispose();
		plane.dispose();
		cube.dispose();
	}

	public static void main(String[] args) {
		new LwjglApplication(new Application(new TestPointlight()), "Lighting test", 600, 600, true);
	}
}
