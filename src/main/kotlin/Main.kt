import engine.EnigView
import engine.OpenGL.EnigWindow

fun main() {
	val window = EnigWindow("LD48")
    val main = Main(window)
    main.runLoop()
	println("hello world")
}

public class Main(window: EnigWindow) : EnigView(window) {
	override fun loop(): Boolean {
        return false;
	}

}