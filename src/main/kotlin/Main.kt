import Level.*
import engine.EnigView
import engine.OpenGL.*
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL14.*
import java.lang.Integer.min
import kotlin.math.abs

fun main() {
	val window = EnigWindow("LD48")
	glDisable(GL_DEPTH_TEST)
	glDisable(GL_CULL_FACE)
	EnigWindow.checkGLError()

	val main = Main(window)
	Shaders.init()
	main.runLoop()
	window.terminate()
}

class Main(window: EnigWindow) : EnigView(window) {

	private val floorVAO : VAO = VAO(-10.0f, -2.5f, 20.0f, 5.0f)
	private val shader = ShaderProgram("textureShader")
	private val texture = Texture("floor0.png")

	private var levels = arrayOf(Level(Light(4, 5), 3), Level(Light(5, 7), 6), Level(Light(5, 9), 6))

	private val levelRenderer  = LevelRenderer(window.getSquarePerspectiveMatrix(100f), window)

	private val player = Vector2f(0f, 2f)
	private var playerDir = true

	private val playerVelocity = Vector2f(0f, 0f)

	override fun loop() : Boolean {
		if (deltaTime > 0.1) {
			deltaTime = 0.1f
		}
		updatePlayer()
		FBO.prepareDefaultRender()
		levelRenderer.renderLevel(levels, player)
		levelRenderer.renderCharacter(player, playerDir)
		if (window.keys[GLFW_KEY_ESCAPE] > 1) {
			return true
		}
        return false
	}

	fun updatePlayer() {
		var index = ((player.y - 1) / 2f).toInt()
		if (index < 0) {
			index = 0
		}
		if (index >= levels.size) {
			index = levels.size - 1;
		}
		val levelRelativePos = Vector2f(player)
		levelRelativePos.y -= index * 2f
		val level = levels[index]
		val nextLevel = levels[min(index + 1, levels.size - 1)]
		if (level.playerCanRight(levelRelativePos, nextLevel.entrance)) {
			if (window.keys[GLFW_KEY_D] > 0) {
				player.x += 2 * deltaTime
				playerDir = true
			}
		}
		if (level.playerCanLeft(levelRelativePos, nextLevel.entrance)) {
			if (window.keys[GLFW_KEY_A] > 0) {
				player.x -= 2 * deltaTime
				playerDir = false
			}
		}

		if (window.keys[GLFW_KEY_W] > 0 || window.keys[GLFW_KEY_SPACE] > 0) {
			playerVelocity.y -= 2 * deltaTime
		}

		if (window.keys[GLFW_KEY_S] > 0) {
			playerVelocity.y += 1 * deltaTime
			if (playerVelocity.y < 0.5) {
				playerVelocity.y = 0.5f

			}
		}
		if (player.x < 0f) {
			player.x = 0f
		}
		if (player.x > LEVEL_MAX_INDEX + 0.5f) {
			player.x = LEVEL_MAX_INDEX + 0.5f
		}

		if (!level.playerCanAscend(levelRelativePos)) {
			playerVelocity.y = maxOf(playerVelocity.y, 0f)
		}

		if (nextLevel.playerCanDescend(levelRelativePos)) {
			playerVelocity.add(0f, deltaTime)
		} else {
			playerVelocity.y = minOf(playerVelocity.y, 0f)
		}
		player.add(playerVelocity.mul(deltaTime, Vector2f()))
		if (playerVelocity.y > 2f) {
			playerVelocity.y = 2f
		}
		else if (playerVelocity.y < -1f) {
			playerVelocity.y = -1f;
		}

		checkFlipSwitch()
	}

	fun checkFlipSwitch() {
		var levelI = ((player.y - 0.1) / 2f).toInt()
		if (levelI < 0) {
			levelI = 0
		}
		if (levelI >= levels.size) {
			levelI = levels.size - 1;
		}
		val switchLoc = levels[levelI].getLightSwitchPosition()

		if (abs(switchLoc - player.x + 0.5) < 1) {
			if (window.keys[GLFW_KEY_E] == 1) {
				levels[levelI].light.state = !levels[levelI].light.state
			}
		}else {
			println(abs(switchLoc - player.x))
		}
	}
}