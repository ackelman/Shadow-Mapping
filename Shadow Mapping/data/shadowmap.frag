#ifdef GL_ES
	precision highp float; 
#endif

const float Near = 1.0;
const float Far = 30.0;
const float LinearDepthConstant = 1.0 / (Far - Near);

uniform vec3 Position;
uniform vec3 Direction;
uniform float OuterCutoff;
uniform float InnerCutoff;
uniform vec3 Attenuation;
uniform vec4 Color;
uniform float Exponent;

uniform vec4 MaterialAmbient;
uniform vec4 MaterialDiffuse;
uniform vec4 MaterialSpecular;
uniform float MaterialShininess;

uniform sampler2D DepthMap;

varying vec3 vNormal;
varying vec3 vWorldVertex;
varying vec3 vEye;

varying vec4 vPosition;

float unpack (vec4 colour) {
	const vec4 bitShifts = vec4(1.0,
								1.0 / 255.0,
								1.0 / (255.0 * 255.0),
								1.0 / (255.0 * 255.0 * 255.0));
	return dot(colour, bitShifts);
}

vec3 calculateLighting() {
	vec3 normal = normalize(vNormal);
	vec3 color = vec3(0.0);
	
	vec3 lightVec = normalize(Position - vWorldVertex);
	float l = dot(normal, lightVec);
	
	if (l > 0.0) {
		float spotlight = max(-dot(lightVec, Direction), 0.0);
		float spotlightFade = clamp((OuterCutoff - spotlight) / (OuterCutoff - InnerCutoff), 0.0, 1.0);
		spotlight = pow(spotlight * spotlightFade, Exponent);
		
    	vec3 fromEye = normalize(vEye);        
    	vec3 halfAngle = normalize(lightVec + fromEye);
    	float s = pow(max(dot(halfAngle, vNormal), 0.0), MaterialShininess);
		
		float d = length(vWorldVertex - Position);
		float a = 1.0 / (Attenuation.x +
					(Attenuation.y * d) +
					(Attenuation.z * d * d));
	
		color += ((MaterialDiffuse.rgb * l) + (MaterialSpecular.rgb * s)) * Color.rgb * a * spotlight;
	}
	
	return color;
}

float calculateShadow() {
	vec3 depth = vPosition.xyz / vPosition.w;
	depth.z = length(vWorldVertex - Position) * LinearDepthConstant;
	float shadow = 1.0;
	
	depth.z *= 0.99;
	
	// PCF	
	float texelSize = 1.0 / 1024.0;
	for (int y = -1; y <= 1; ++y) {
		for (int x = -1; x <= 1; ++x) {
			vec2 offset = depth.xy + vec2(float(x) * texelSize, float(y) * texelSize);
			if ( (offset.x >= 0.0) && (offset.x <= 1.0) && (offset.y >= 0.0) && (offset.y <= 1.0) ) {
				float shadowDepth = unpack(texture2D(DepthMap, offset));
				if ( depth.z > shadowDepth )
					shadow *= 0.9;
			}
		}
	}
	
	return shadow;
}

void main() {
	vec3 color = MaterialAmbient.rgb;
	
	// Add diffuse + specular
	color += calculateLighting();
	
	// Calculate and apply shadow
	color *= calculateShadow();
	
	gl_FragColor = clamp(vec4(color, MaterialDiffuse.w), 0.0, 1.0);
}