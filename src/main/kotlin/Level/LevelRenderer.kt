package Level

import engine.OpenGL.EnigWindow
import engine.OpenGL.FBO
import engine.OpenGL.Texture
import engine.OpenGL.VAO
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL14.glBlendEquation
import kotlin.math.sin

const val LEVEL_WIDTH = 10
const val LEVEL_MAX_INDEX = LEVEL_WIDTH - 1

class LevelRenderer(private val baseMat : Matrix4f, window: EnigWindow) {
	private val vao = VAO(0f, 0f, 1f, 1f)
	private val lightOnTex = Texture("lightOn.png")
	private val lightOffTex = Texture("lightOff.png")
	private val keyTex = Texture("key.png")
	private val floorNeutralTex = Texture("floor0.png")
	private val floorFromLeftTex = Texture("floor1.png")
	private val floorFromRightTex = Texture("floor2.png")
	private val floorPassTex = Texture("floor3.png")
	private val switchTex = Texture("switch.png")
	private val floorTex = Texture("wall.png")
	private val charTex = arrayOf(Texture("player0.png"), Texture("player1.png"), Texture("player2.png"), Texture("player3.png"))

	private val lightFBO : FBO = FBO(window.width, window.height)

	fun renderLevel(levels : Array<Level>, player: Vector2f) {
		//baseMat.translate(0f, 0.1f, 0f)
		lightFBO.prepareForTexture()
		renderLight(levels, player)
		FBO.prepareDefaultRender()
		Shaders.doubleTex.enable()
		lightFBO.boundTexture.bindPosition(1);
		vao.prepareRender()
		renderWalls(levels)
		renderCeiling(levels)
	}

	private var timer = 0.0

	private fun currentCharTex() : Texture {
		return charTex[((timer * 4) % 4).toInt()]
	}

	fun renderCharacter(position : Vector2f, direction : Boolean) {
		val mat = baseMat
			.translate(8f * (position.x - LEVEL_WIDTH / 2f), -8f * position.y + sin(timer / 3f).toFloat() / 2f + 2f, 0f, Matrix4f())
			.scale(4f, 8f, 1f)
		if (!direction) {
			mat.scale(-1f, 1f, 1f).translate(-1f, 0f, 0f)
		}
		currentCharTex().bind()
		Shaders.doubleTex.setUniform(0, 0, mat)
		vao.draw()
		timer += 0.1f;
	}

	private fun renderWalls(levels : Array<Level>) {
		floorTex.bindPosition(0)
		Shaders.doubleTex.setUniform(2, 0, lightFBO.boundTexture.width.toFloat())
		Shaders.doubleTex.setUniform(2, 1, lightFBO.boundTexture.height.toFloat())
		for (x in 0..LEVEL_MAX_INDEX) {
			for (h in 1..(levels.size * 2)) {
				val mat = baseMat
					.translate(8 * (x - LEVEL_WIDTH.toFloat() / 2f), -8f * h, 0f, Matrix4f())
					.scale(8f)
				Shaders.doubleTex.setUniform(0, 0, mat)
				vao.draw()
			}
		}
	}

	private fun renderCeiling(levels : Array<Level>) {
		//floor textures
		for ((level, i) in levels.zip(levels.indices)) {
			for (x in 0..LEVEL_MAX_INDEX) {
				if (level.entrance == x) {
					continue;
				}
				if (level.getLightPosition() > level.getLightSwitchPosition()) {
					if (x == level.getLightPosition()) {
						floorFromLeftTex.bind()
					} else if (x == level.getLightSwitchPosition()) {
						floorFromRightTex.bind()
					} else if (x > level.getLightSwitchPosition() && x < level.getLightPosition()) {
						floorPassTex.bind()
					} else {
						floorNeutralTex.bind()
					}
				} else {
					if (x == level.getLightPosition()) {
						floorFromRightTex.bind()
					} else if (x == level.getLightSwitchPosition()) {
						floorFromLeftTex.bind()
					} else if (x < level.getLightSwitchPosition() && x > level.getLightPosition()) {
						floorPassTex.bind()
					} else {
						floorNeutralTex.bind()
					}
				}
				val mat = baseMat
					.translate(8 * (x - LEVEL_WIDTH.toFloat() / 2f), -16f * i, 0f, Matrix4f())
					.scale(8f, 2f, 1f)
				Shaders.doubleTex.setUniform(0, 0, mat)
				vao.draw()
			}
		}

		//lights
		for ((level, i) in levels.zip(levels.indices)) {
			if (level.lightIsOn()) {
				lightOnTex.bind()
			} else {
				lightOffTex.bind()
			}
			val mat = baseMat
				.translate(8 * (level.getLightPosition() - LEVEL_WIDTH / 2f) + 2, -4f - 16f * i, 0f, Matrix4f())
				.scale(4f)
			Shaders.doubleTex.setUniform(0, 0, mat)
			vao.draw()
		}

		//switch
		for ((level, i) in levels.zip(levels.indices)) {
			switchTex.bind()
			val mat = baseMat
				.translate(8 * (level.getLightSwitchPosition() - LEVEL_WIDTH / 2f) + 2, -8f - 16f * i, 0f, Matrix4f())
				.scale(4f, 8f, 1f)
			Shaders.doubleTex.setUniform(0, 0, mat)
			vao.draw()
		}
	}

	private fun renderLight(levels : Array<Level>, player : Vector2f) {
		glBlendEquation(GL14.GL_MAX)
		Shaders.light.enable()
		Shaders.light.setUniform(2, 0, 1f, 1f, 0.5f)
		for ((level, i) in levels.zip(levels.indices)) {
			if (level.lightIsOn()) {
				val mat = baseMat
					.translate(8 * (level.getLightPosition() - LEVEL_WIDTH / 2f) + 4, -2.5f - i * 16f, 0f, Matrix4f())
					.scale(16f * level.getLightBrightness())
					.translate(-0.5f, -0.5f, 0f)
				Shaders.light.setUniform(0, 0, mat)

				val lines = getLines(levels, i)
				Shaders.light.setUniform(2, 1, lines.size)
				Shaders.light.setUniform(2, 2, lines)
				vao.draw()
			}
		}
		glBlendEquation(GL14.GL_FUNC_ADD)
	}

	private fun getLeftCeilingLine(level : Level) : Vector4f {
		val lx = -1f
		val yl = -5f / (16f * level.getLightBrightness())
		val rx = (level.entrance - level.getLightPosition() - 0.5f) / level.getLightBrightness()
		return Vector4f(lx, yl, rx, yl)
	}

	private fun getRightCeilingLine(level : Level) : Vector4f {
		val lx = (level.entrance - level.getLightPosition() + 0.5f) / level.getLightBrightness()
		val yl = -2.5f / (8f * level.getLightBrightness())
		val rx = 1f
		return Vector4f(lx, yl, rx, yl)
	}

	private fun getHorizontalLine(level : Level) : Vector4f {
		var xv : Float
		val ly = -5f / (16f * level.getLightBrightness())
		val hy = -9f / (16f * level.getLightBrightness())
		if (level.entrance > level.getLightPosition()) {
			xv = (level.entrance - level.getLightPosition() + 0.5f) / level.getLightBrightness()
		} else {
			xv = (level.entrance - level.getLightPosition() - 0.5f) / level.getLightBrightness()
		}
		return Vector4f(xv, ly, xv, hy)
	}

	private fun getLeftFloorLine(level : Level, light: Light) : Vector4f {
		val lx = -1f
		val yl = (-4.5f + 16f) / (8f * light.brightness)
		val rx = (level.entrance - light.light - 0.5f) / light.brightness
		return Vector4f(lx, yl, rx, yl)
	}

	private fun getRightFloorLine(level : Level, light: Light) : Vector4f {
		val rx = 1f
		val yl = (-4.5f + 16f) / (8f * light.brightness)
		val lx = (level.entrance - light.light + 0.5f) / light.brightness
		return Vector4f(lx, yl, rx, yl)
	}

	private fun getLeftHorizontalLine(level : Level, light : Light) : Vector4f {
		val xv = (level.entrance - light.light - 0.5f) / light.brightness
		val ly = (-2.5f + 16f) / (8f * light.brightness)
		val hy = (-4.5f + 16f) / (8f * light.brightness)
		return Vector4f(xv, ly, xv, hy)
	}

	private fun getRightHorizontalLine(level : Level, light : Light) : Vector4f {
		val xv = (level.entrance - light.light + 0.5f) / light.brightness
		val ly = (-2.5f + 16f) / (8f * light.brightness)
		val hy = (-4.5f + 16f) / (8f * light.brightness)
		return Vector4f(xv, ly, xv, hy)
	}

	private fun getLines(levels : Array<Level>, i : Int) : Array<Vector4f> {
		val level = levels[i]
		val ret = arrayListOf(getLeftCeilingLine(level), getRightCeilingLine(level), getHorizontalLine(level))
		if (levels.size > i + 1) {
			ret.add(getLeftFloorLine(levels[i + 1], level.light))
			ret.add(getRightFloorLine(levels[i + 1], level.light))
			if (levels[i + 1].entrance == level.getLightPosition()) {
				ret.add(getLeftHorizontalLine(levels[i + 1], level.light))
				ret.add(getRightHorizontalLine(levels[i + 1], level.light))
			} else if (levels[i + 1].entrance > level.getLightPosition()) {
				ret.add(getRightHorizontalLine(levels[i + 1], level.light))
			} else {
				ret.add(getLeftHorizontalLine(levels[i + 1], level.light))
			}
		} else {
			val yl = (-4.5f + 16f) / (8f * level.getLightBrightness())
			ret.add(Vector4f(-1f, yl, 1f, yl))
		}

		return ret.toTypedArray()
	}
}