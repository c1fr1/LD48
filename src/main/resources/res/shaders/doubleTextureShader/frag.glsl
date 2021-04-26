#version 420 core

in vec2 texCoords;

out vec4 color;

uniform float windowWidth;
uniform float windowHeight;

layout (binding = 0) uniform sampler2D tex;
layout (binding = 1) uniform sampler2D lighting;

void main() {
	vec2 windowPos = gl_FragCoord.xy;
	windowPos.x /= windowWidth;
	windowPos.y /= windowHeight;
	color = texture(tex, texCoords);
	color.xyz *= texture(lighting, windowPos).w;
}