package com.lilleman.engine.lighting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.lilleman.engine.lighting.utils.Utils;

public class ShadowMap {
	PerspectiveCamera cam;

	SpotLight light;

	ShaderProgram depthRenderShader;
	ShaderProgram shadowGenShader;
	ShaderProgram shadowMapShader;
	FrameBuffer shadowMap;

	ShadowMapRenderer renderer;
	
	int shadowMapSize;

	Mesh quad;

	public ShadowMap(int shadowMapSize) {
		this.shadowMapSize = shadowMapSize;
		
		shadowMap = new FrameBuffer(Format.RGBA8888, shadowMapSize, shadowMapSize, false);

		shadowGenShader = Utils.compileShader("data/depth", "data/depth");
		shadowMapShader = Utils.compileShader("data/shadowmap", "data/shadowmap");
		depthRenderShader = Utils.compileShader("data/depthRender", "data/depthRender");

		quad = Utils.generateQuad(0, 0, 0.25f, 0.25f);
	}

	public void setLight(SpotLight light) {
		this.light = light;
	}

	public void setRenderer(ShadowMapRenderer renderer) {
		this.renderer = renderer;
	}

	public void setCamera(PerspectiveCamera cam) {
		this.cam = cam;
	}

	boolean renderDepthMap = true;

	public void renderShadowMap(PerspectiveCamera lightCam) {
		Gdx.gl20.glViewport(0, 0, shadowMapSize, shadowMapSize);

		shadowMap.begin();

		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_FRONT);

		shadowGenShader.begin();

		shadowGenShader.setUniformMatrix("MVMatrix", lightCam.view);
		shadowGenShader.setUniformMatrix("MVPMatrix", lightCam.combined);
		
		renderer.renderShadowMap(shadowGenShader);

		shadowGenShader.end();

		Gdx.gl.glDisable(GL20.GL_CULL_FACE);

		shadowMap.end();
	}

	public void setUniforms(Camera cam) {
		shadowMapShader.setUniformMatrix("MVPMatrix", cam.combined);

		shadowMapShader.setUniformMatrix("u_lightProjTrans", light.getCamera().combined);

		int num = shadowMap.getColorBufferTexture().getTextureObjectHandle();
		shadowMap.getColorBufferTexture().bind(num);
		shadowMapShader.setUniformi("DepthMap", num);

		light.setUniforms(shadowMapShader);
		
		shadowMapShader.setUniformf("CameraPosition", cam.position);
	}

	public void render() {
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// Shadowmap pass
		renderShadowMap(light.getCamera());

		// Render pass
		shadowMapShader.begin();
		
		setUniforms(cam);
		
		renderer.renderFull(shadowMapShader);

		shadowMapShader.end();

		if (renderDepthMap) {
			renderDepthMap();
		}
	}

	public void renderDepthMap() {
		depthRenderShader.begin();

		int num = shadowMap.getColorBufferTexture().getTextureObjectHandle();
		shadowMap.getColorBufferTexture().bind(num);
		depthRenderShader.setUniformi("DepthMap", num);

		quad.render(depthRenderShader, GL20.GL_TRIANGLE_FAN);

		depthRenderShader.end();
	}

	public void dispose() {
		shadowMap.dispose();
	}

	public interface ShadowMapRenderer {
		void renderShadowMap(ShaderProgram shader);

		void renderFull(ShaderProgram shader);
	}
}
