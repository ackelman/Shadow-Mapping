attribute vec3 a_position;
 
varying vec4 vPosition;

uniform mat4 MVMatrix;
uniform mat4 MVPMatrix;

void main() {
	vPosition = MVMatrix * vec4(a_position, 1.0);
	gl_Position = MVPMatrix * vec4(a_position, 1.0);
}