import Level.Level
import Level.LevelRenderer
import Level.Light
import engine.EnigView
import engine.OpenGL.*
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE

fun main() {
	val window = EnigWindow("LD48")
    val main = Main(window)
	Shaders.init()
	main.runLoop()
	window.terminate()
}

class Main(window: EnigWindow) : EnigView(window) {

	private val floorVAO : VAO = VAO(-10.0f, -2.5f, 20.0f, 5.0f)
	private val shader = ShaderProgram("textureShader")
	private val texture = Texture("floor0.png")

	private var currentLevel = Level(Light(2, 5));

	private val levelRenderer  = LevelRenderer(window.getSquarePerspectiveMatrix(100f))

	override fun loop() : Boolean {
		FBO.prepareDefaultRender()
		levelRenderer.renderLevel(currentLevel, 0)

		if (window.keys[GLFW_KEY_ESCAPE] > 1) {
			return true
		}
        return false
	}
}