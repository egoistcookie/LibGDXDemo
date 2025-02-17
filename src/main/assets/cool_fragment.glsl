#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_resolution;

void main() {
    vec2 uv = gl_FragCoord.xy / u_resolution.xy;
    vec2 center = vec2(0.5);
    float dist = distance(uv, center);

    // 计算光芒扩散效果
    float flash = sin(u_time * 5.0 - dist * 10.0) * 0.5 + 0.5;

    vec4 texColor = texture2D(u_texture, v_texCoords);
    gl_FragColor = v_color * texColor * vec4(1.0, 1.0, 1.0, flash);
}