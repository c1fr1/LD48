#version 330 core

in vec2 pos;

out vec4 color;

uniform vec3 ocolor;

void main() {
    float len = dot(pos, pos);
    color = vec4(ocolor * (1 - len), 1 - len);
}