package com.lilleman.engine.lighting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cubemap.CubemapSide;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.lilleman.engine.lighting.utils.Utils;

public class ShadowMapCube {
	PerspectiveCamera cam;

	PointLight light;

	ShaderProgram depthRenderShader;
	ShaderProgram shadowGenShader;
	ShaderProgram shadowMapShader;
	
	FrameBuffer fbo;
	Cubemap cubemap;

	ShadowMapRenderer renderer;
	
	int shadowMapSize;
	
	Mesh quad;
	
	boolean renderDepthMap = true;
	
	public ShadowMapCube(int shadowMapSize) {
		this.shadowMapSize = shadowMapSize;
		
		fbo = new FrameBuffer(Format.RGBA8888, shadowMapSize, shadowMapSize, false);
        cubemap = new Cubemap(shadowMapSize, shadowMapSize, shadowMapSize, Format.RGBA8888);

		shadowGenShader = Utils.compileShader("data/depth", "data/depth");
		shadowMapShader = Utils.compileShader("data/shadowmapCube", "data/shadowmapCube");
		depthRenderShader = Utils.compileShader("data/depthRender", "data/depthRenderCube");
		
		quad = Utils.generateQuad(0, 0, 3.0f / 6.0f, 2.0f / 6.0f);
	}

	public void setLight(PointLight light) {
		this.light = light;
	}

	public void setRenderer(ShadowMapRenderer renderer) {
		this.renderer = renderer;
	}

	public void setCamera(PerspectiveCamera cam) {
		this.cam = cam;
	}

	public void renderShadowMapSide(int i) {
		Gdx.gl20.glViewport(0, 0, shadowMapSize, shadowMapSize);
		
		fbo.begin();
    	Gdx.gl20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, CubemapSide.PositiveX.glEnum + i, cubemap.getTextureObjectHandle(), 0);
		
		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_FRONT);

		PerspectiveCamera lightCam = light.getCamera(i);
		
		shadowGenShader.begin();

		shadowGenShader.setUniformMatrix("MVMatrix", lightCam.view);
		shadowGenShader.setUniformMatrix("MVPMatrix", lightCam.combined);
		
		renderer.renderShadowMap(shadowGenShader);

		shadowGenShader.end();

		Gdx.gl.glDisable(GL20.GL_CULL_FACE);

		fbo.end();
	}

	public void setUniforms(Camera cam) {
		shadowMapShader.setUniformMatrix("MVPMatrix", cam.combined);

		int num = cubemap.getTextureObjectHandle();
    	cubemap.bind(num);
		shadowMapShader.setUniformi("DepthMap", num);

		light.setUniforms(shadowMapShader);
		
		shadowMapShader.setUniformf("CameraPosition", cam.position);
	}

	public void render() {
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// Shadowmap pass
		for (int i = 0; i < 6; i++) {
			renderShadowMapSide(i);
		}

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

		int num = cubemap.getTextureObjectHandle();
    	cubemap.bind(num);
		depthRenderShader.setUniformi("DepthMap", num);

		quad.render(depthRenderShader, GL20.GL_TRIANGLE_FAN);

		depthRenderShader.end();
	}

	public void dispose() {
		fbo.dispose();
    	cubemap.dispose();
		depthRenderShader.dispose();
		shadowGenShader.dispose();
		shadowMapShader.dispose();
		quad.dispose();
	}

	public interface ShadowMapRenderer {
		void renderShadowMap(ShaderProgram shader);

		void renderFull(ShaderProgram shader);
	}
}
