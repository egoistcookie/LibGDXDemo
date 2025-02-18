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
    // 光芒起始位置：0.5为中心
    vec2 center = vec2(0.5);
    float dist = distance(uv, center);

    // 计算光芒扩散效果：
    // u_time：光芒扩散速度
    // dist：光芒闪烁频率
    float flash = sin(u_time * 5.0 - dist * 2.0) * 0.5 + 0.5;

    vec4 texColor = texture2D(u_texture, v_texCoords);
    // 0.0, 0.0, 0.0 代表光芒颜色为白色
    gl_FragColor = v_color * texColor * vec4(1.0, 1.0, 1.0, flash);
}