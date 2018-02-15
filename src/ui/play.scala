package ui
import scala.swing._
import scala.collection.mutable.ArrayBuffer
import javax.swing.ImageIcon
import javax.sound.sampled._
import java.net.URL
import java.io.File
import javax.swing._
import java.awt.Font



object play extends SimpleSwingApplication{


	//Load audio resources used throughout game
	val audioUrl = resourceFromUserDirectory("src/files/card_Slide.wav")
			val audioUrl2 = resourceFromUserDirectory("src/files/jazz_clip.wav")
			val audioUrl3 = resourceFromUserDirectory("src/files/cheering.wav")
			val audioUrl4 = resourceFromUserDirectory("src/files/add_robot.wav")
			val audioUrl5 = resourceFromUserDirectory("src/files/coin.wav")
			// Soundtrack to start right away and to loop continuously
			val audioIn2 = AudioSystem.getAudioInputStream(audioUrl2)
			var clip2 = AudioSystem.getClip
			clip2.open(audioIn2)
			clip2.start
			clip2.loop(javax.sound.midi.Sequencer.LOOP_CONTINUOUSLY)  


			//Derive a font to be used by various UI elements
			val font = resourceFromUserDirectory("src/files/bebasneue.ttf")
			var newFont = Font.createFont(Font.TRUETYPE_FONT, font)
			var newFontSmaller= Font.createFont(Font.TRUETYPE_FONT, font)
			newFont = newFont.deriveFont(35f)
			newFontSmaller = newFontSmaller.deriveFont(23f)




			Screen.drawStartingScreen()              //Draw the starting screen
			var gameOn = false                       //The game has not yet started

			def top = new MainFrame {
		    title = "Kasino"
				preferredSize = new Dimension(1470, 1000)
		    contents = Screen
		    resizable= false 


	    }
  //Repaint the screen 
	
	    TimerHelp(50) {

		    Screen.repaint()
	    }
	    
	   






}
