#ifdef GL_ES
    precision highp float;
#endif

uniform samplerCube DepthMap;

varying vec2 vUv;

float unpack (vec4 colour) {
    const vec4 bitShifts = vec4(1.0,
                    1.0 / 255.0,
                    1.0 / (255.0 * 255.0),
                    1.0 / (255.0 * 255.0 * 255.0));
    return dot(colour, bitShifts);
}

void main () {
	vec3 dir;
	if ( vUv.y < 0.5 ) {
		float y = (0.25 - vUv.y) / 0.25;
		
		// Left
		if ( vUv.x < 0.33 )
			dir = vec3(-1.0,
						y,
						(vUv.x - 0.165) / 0.165);
		// Front
		else if ( vUv.x < 0.66 )
			dir = vec3(((vUv.x - 0.33) - 0.165) / 0.165,
						y,
						-1.0);
		// Right
		else
			dir = vec3(1.0,
						y,
						((vUv.x - 0.66) - 0.165) / 0.165);
	} else {
		float y = (0.75 - vUv.y) / 0.25;
	
		// Back
		if ( vUv.x < 0.33 )
			dir = vec3((vUv.x - 0.165) / 0.165,
						y,
						1.0);
		// Top
		else if ( vUv.x < 0.66 )
			dir = vec3(((vUv.x - 0.33) - 0.165) / 0.165,
						1.0,
						-y);
		// Bottom
		else
			dir = vec3(((vUv.x - 0.66) - 0.165) / 0.165,
						-1.0,
						-y);
	}

    float depth = unpack(textureCube(DepthMap, dir));
        
    gl_FragColor = vec4(depth, depth, depth, 1.0);    
}
          