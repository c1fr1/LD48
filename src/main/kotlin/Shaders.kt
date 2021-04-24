import engine.OpenGL.ShaderProgram

object Shaders {
	lateinit var texture : ShaderProgram
	fun init() {
		texture = ShaderProgram("textureShader")
	}
}