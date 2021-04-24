package Level

import engine.OpenGL.Texture
import engine.OpenGL.VAO
import org.joml.Matrix4f
import org.joml.Vector4f
import kotlin.properties.Delegates

const val LEVEL_WIDTH = 10
const val LEVEL_MAX_INDEX = LEVEL_WIDTH - 1

class LevelRenderer(private val baseMat : Matrix4f) {
	private val vao = VAO(0f, 0f, 1f, 1f)
	private val lightOnTex = Texture("lightOn.png")
	private val lightOffTex = Texture("lightOff.png")
	private val keyTex = Texture("key.png")
	private val floorNeutralTex = Texture("floor0.png")
	private val floorFromLeftTex = Texture("floor1.png")
	private val floorFromRightTex = Texture("floor2.png")
	private val floorPassTex = Texture("floor3.png")
	private val switchTex = Texture("switch.png");

	fun renderLevel(level : Level, depth : Int) {
		vao.prepareRender()
		renderLight(level, depth)
		vao.unbind()
		Shaders.texture.enable()
		vao.prepareRender()
		renderCeiling(level, depth)
		vao.unbind()
	}

	private fun renderCeiling(level : Level, depth : Int) {
		//floor textures
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
				.translate(8 * (x - LEVEL_WIDTH.toFloat() / 2f), 0f, 0f, Matrix4f())
				.scale(8f, 2f, 1f)
			Shaders.texture.setUniform(0, 0, mat)
			vao.draw()
		}

		//lights
		if (level.lightIsOn()) {
			lightOnTex.bind()
		} else {
			lightOffTex.bind()
		}
		var mat = baseMat
			.translate(8 * (level.getLightPosition() - LEVEL_WIDTH / 2f) + 2, -4f, 0f, Matrix4f())
			.scale(4f)
		Shaders.texture.setUniform(0, 0, mat)
		vao.draw()

		//switch
		switchTex.bind()
		mat = baseMat
			.translate(8 * (level.getLightSwitchPosition() - LEVEL_WIDTH / 2f) + 2, -8f, 0f, Matrix4f())
			.scale(4f, 8f, 1f)
		Shaders.texture.setUniform(0, 0, mat)
		vao.draw()
	}

	private fun renderLight(level : Level, depth : Int) {
		Shaders.light.enable()
		val mat = baseMat
			.translate(8 * (level.getLightPosition() - LEVEL_WIDTH / 2f) + 4, -2.5f, 0f, Matrix4f())
			.scale(16f * level.getLightBrightness())
			.translate(-0.5f, -0.5f, 0f)
		Shaders.light.setUniform(0, 0, mat)
		Shaders.light.setUniform(2, 0, 1f, 1f, 1f)

		val lines = getLines(level)
		Shaders.light.setUniform(2, 1, lines.size)
		Shaders.light.setUniform(2, 2, lines)
		vao.draw()
	}

	private fun getLeftCeilingLine(level : Level) : Vector4f {
		val lx = -1f
		val yl = -5f / (16f * level.getLightBrightness())
		val rx = (level.entrance - level.getLightPosition() - 0.5f) / level.getLightBrightness()
		return Vector4f(lx, yl, rx, yl)
	}

	private fun getRightCeilingLine(level : Level) : Vector4f {
		val lx = (level.entrance - level.getLightPosition() + 0.5f) / level.getLightBrightness()
		val yl = -5f / (16f * level.getLightBrightness())
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

	private fun getLines(level : Level) : Array<Vector4f> {
		return arrayOf(getLeftCeilingLine(level), getRightCeilingLine(level), getHorizontalLine(level))
	}
}