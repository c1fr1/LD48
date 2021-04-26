package Level

class Light(val light : Int, val switch : Int, val brightness : Float) {
	constructor(light : Int, switch : Int) : this(light, switch, 5.0f)
	var state = false
}