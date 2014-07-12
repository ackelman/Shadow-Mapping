package com.lilleman.engine.lighting.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Utils {
	/*
	 * (0, 0) = lower left corner of screen (1, 1) = upper right corner of
	 * screen The returned mesh should be rendered with GL_TRIANGLE_FAN
	 */
	public static Mesh generateQuad(float x, float y, float w, float h) {
		float[] verts = new float[20];
		int i = 0;

		x -= 1;
		y -= 1;
		w *= 2;
		h *= 2;

		verts[i++] = x; // x1
		verts[i++] = y; // y1
		verts[i++] = 0;
		verts[i++] = 0f; // u1
		verts[i++] = 0f; // v1

		verts[i++] = x + w; // x2
		verts[i++] = y; // y2
		verts[i++] = 0;
		verts[i++] = 1f; // u2
		verts[i++] = 0f; // v2

		verts[i++] = x + w; // x3
		verts[i++] = y + h; // y2
		verts[i++] = 0;
		verts[i++] = 1f; // u3
		verts[i++] = 1f; // v3

		verts[i++] = x; // x4
		verts[i++] = y + h; // y4
		verts[i++] = 0;
		verts[i++] = 0f; // u4
		verts[i++] = 1f; // v4

		Mesh mesh = new Mesh(true, 4, 0, new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(
				Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

		mesh.setVertices(verts);

		return mesh;
	}

	public static ShaderProgram compileShader(String vert, String frag) {
		if (!vert.endsWith(".vert")) {
			vert = vert + ".vert";
		}
		if (!frag.endsWith(".frag")) {
			frag = frag + ".frag";
		}
		ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vert), Gdx.files.internal(frag));
		if (!shader.isCompiled())
			throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
		return shader;
	}
}
