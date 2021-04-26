package Level

import org.joml.Vector2f

class Level(val light : Light, public val entrance : Int) {
	fun getLightSwitchPosition() : Int {
		return light.switch
	}
	fun getLightPosition() : Int {
		return light.light
	}
	fun getLightBrightness() : Float {
		return light.brightness
	}
	fun lightIsOn() : Boolean {
		return light.state
	}

	fun playerCanDescend(player : Vector2f) : Boolean {
		if (player.x > entrance && player.x - 0.5 < entrance) {
			return true
		}
		return player.y < 2
	}

	fun playerCanAscend(player : Vector2f) : Boolean {
		if (player.x > entrance && player.x - 0.5 < entrance) {
			return true
		}
		return player.y > 1.2
	}

	fun playerCanLeft(player : Vector2f, exit : Int) : Boolean {
		if (player.y > 1.15 && player.y < 2.05f) {
			return player.x > 0
		}
		return if (player.y > 2.05f) {
			player.x - 0.05f > exit
		} else {
			player.x - 0.05f > entrance
		}
	}

	fun playerCanRight(player : Vector2f, exit : Int) : Boolean {
		if (player.y > 1.15 && player.y < 2.05f) {
			return player.x < LEVEL_WIDTH
		}
		return if (player.y > 2.05f) {
			player.x - 0.45f < exit
		} else {
			player.x - 0.45f < entrance
		}
	}
}