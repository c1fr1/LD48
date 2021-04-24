import engine.EnigView
import engine.OpenGL.*
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE

fun main() {
	val window = EnigWindow("LD48")
    val main = Main(window)
	main.runLoop()
}

public class Main(window: EnigWindow) : EnigView(window) {

	private val floorVAO : VAO = VAO(-10.0f, -2.5f, 20.0f, 5.0f)
	private val cam : Matrix4f = window.getSquarePerspectiveMatrix(100f)
	private val shader = ShaderProgram("textureShader")
	private val texture = Texture("floor0.png")

	override fun loop() : Boolean {
		FBO.prepareDefaultRender()
		floorVAO.prepareRender()
		shader.enable()
		texture.bind()
		shader.setUniform(0, 0, cam)
		floorVAO.draw()
		floorVAO.unbind()

		if (window.keys[GLFW_KEY_ESCAPE] > 1) {
			return true
		}
        return false
	}
}