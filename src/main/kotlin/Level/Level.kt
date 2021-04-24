package Level

class Level(private val light : Light) {
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
}