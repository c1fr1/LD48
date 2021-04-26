import engine.OpenGL.ShaderProgram

object Shaders {
	lateinit var texture : ShaderProgram
	lateinit var light : ShaderProgram
	lateinit var doubleTex : ShaderProgram
	fun init() {
		texture = ShaderProgram("textureShader")
		light = ShaderProgram("colorShader")
		doubleTex = ShaderProgram("doubleTextureShader")
	}
}