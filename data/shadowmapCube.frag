#ifdef GL_ES
	precision highp float; 
#endif

const float Near = 1.0;
const float Far = 30.0;
const float LinearDepthConstant = 1.0 / (Far - Near);

uniform vec3 Position;
uniform vec3 Attenuation;
uniform vec4 Color;

uniform vec4 MaterialAmbient;
uniform vec4 MaterialDiffuse;
uniform vec4 MaterialSpecular;
uniform float MaterialShininess;

uniform samplerCube DepthMap;

varying vec3 vNormal;
varying vec3 vWorldVertex;
varying vec3 vEye;

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
	
	vec3 lightDirection = normalize(Position - vWorldVertex);
	float l = dot(normal, lightDirection);
	
	if (l > 0.0) {
    	vec3 fromEye = normalize(vEye);        
    	vec3 halfAngle = normalize(lightDirection + fromEye);
    	float s = pow(max(dot(halfAngle, vNormal), 0.0), MaterialShininess);
		
		float d = length(vWorldVertex - Position);
		float a = 1.0 / (Attenuation.x +
					(Attenuation.y * d) +
					(Attenuation.z * d * d));
	
		color += ((MaterialDiffuse.rgb * l) + (MaterialSpecular.rgb * s)) * Color.rgb * a;
	}
	
	return color;
}

float calculateShadow() {
	vec3 lightVec = normalize(vWorldVertex.xyz - Position);
	lightVec.y = -lightVec.y;
	lightVec.z = -lightVec.z;
	float depth = length(vWorldVertex.xyz - Position) * LinearDepthConstant;
	float shadow = 1.0;
	
	depth *= 0.99;
	
	float shadowDepth = unpack(textureCube(DepthMap, lightVec));
	if ( depth > shadowDepth )
		shadow = 0.5;
	
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