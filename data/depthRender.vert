attribute vec3 a_position;
attribute vec2 a_texCoord0;

varying vec2 vUv;

void main () {
    gl_Position = vec4(a_position, 1.0);
    vUv = a_texCoord0;
}