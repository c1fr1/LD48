#version 330 core

in vec2 pos;

out vec4 color;

uniform vec3 ocolor;
uniform int numLines;
uniform vec4[10] lines;

float crossesT(vec2 a, vec2 b, float vx, float vy) {
    float bax = b.x - a.x;
    float bay = b.y - a.y;

    float above = -b.x * a.y + a.x * b.y;
    float below = vx * bay - vy * bax;
    return above / below;
}

float crossesT(vec2 a, vec2 b, vec2 v) {
    return crossesT(a, b, v.x, v.y);
}

float crossesT(vec2 a, vec2 b, vec2 v, vec2 o) {
    return crossesT(a - o, b - o, v - o);
}

float crossesTOrigin(vec2 v, vec2 from, vec2 to) {
    return crossesT(vec2(0.0, 0.0), v, to, from);
}

void main() {
    for (int i = 0; i < numLines; ++i) {
        float t = crossesT(lines[i].xy, lines[i].zw, pos.x, pos.y);
        if (t < 1 && t > 0) {
            t = crossesTOrigin(pos, lines[i].xy, lines[i].zw);
            if (t < 1 && t > 0) {
                discard;
            }
        }
    }
    float len = dot(pos, pos);
    color = vec4(ocolor * (1 - len), 1 - len);
}
