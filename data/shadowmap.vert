#ifdef GL_ES
precision highp float; 
#endif

attribute vec4 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat3 NormalMatrix;
uniform mat4 MVPMatrix;
uniform mat4 u_lightProjTrans;

uniform vec3 CameraPosition;

const mat4 ScaleMatrix = mat4(0.5, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0.5, 1.0);

varying vec3 vNormal;
varying vec3 vWorldVertex;
varying vec3 vEye;

varying vec4 vPosition;

void main() {
	vPosition = ScaleMatrix * u_lightProjTrans * a_position;
	
	vNormal = normalize(a_normal);
	
	vWorldVertex = a_position.xyz;
	vEye = CameraPosition - vWorldVertex;
	
	gl_Position = MVPMatrix * a_position;
}