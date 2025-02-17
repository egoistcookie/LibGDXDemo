#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_time;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    float flash = sin(u_time * 5.0) * 0.5 + 0.5;
    gl_FragColor = v_color * texColor * vec4(1.0, 1.0, 1.0, flash);
}