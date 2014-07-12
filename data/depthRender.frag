#ifdef GL_ES
    precision highp float;
#endif

uniform sampler2D DepthMap;

varying vec2 vUv;

float unpack (vec4 colour) {
    const vec4 bitShifts = vec4(1.0,
                    1.0 / 255.0,
                    1.0 / (255.0 * 255.0),
                    1.0 / (255.0 * 255.0 * 255.0));
    return dot(colour, bitShifts);
}

void main () {
    float depth = unpack(texture2D(DepthMap, vUv));
        
    gl_FragColor = vec4(depth, depth, depth, 1.0);    
}
          