package ui
import scala.swing._
import javax.swing.ImageIcon
import scala.swing.event._
import kasinocode._
import java.awt.Color
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import java.util.Scanner
import java.io.File


/*
 * Most buttons will look alike, so this class takes care of the font, properties and color of the buttons
 */
sealed class ButtonType extends Button {
 
  contentAreaFilled = false
  this.foreground = Color.CYAN
  
  font = play.newFont
  borderPainted = false
  contentAreaFilled = false
  focusPainted = false
  listenTo(mouse.clicks)
  
  
}


/*
 * A cardButton is used to illustrate a card on the screen. A cardButton can be clicked depending on the variable clickable.
 * CardButtons are easy and fast to create as it only requires the command CardButton(card) A carbutton also has an own indicator which indicates
 * whether or not it is selected. AllowDualclick variable determines whether other cardButtons may be actively selected at the same time as this one.
 */
case class CardButton(card:Card) extends ButtonType {

	var cardName = card.name + card.suit
			icon = new ImageIcon("src/files/" + cardName + ".png")
	var reference = card
	var clickable = false
	var clicked = false
	var allowDualClick = false
	var isSelected = false
	var personalIndicator = selectIndicator()
	var coords = (0,0)

	reactions += {
	case e: MouseClicked => {
		if(clickable) { 
			if(!clicked){
				Screen.add(personalIndicator,coords._1 + 45 ,coords._2 + 104)
				selectionHelper.add(this)
				clicked = true
				if(!allowDualClick) {
					var toRemove = selectionHelper.selectedCardButtons.filter(!_.allowDualClick)
							toRemove -= this
							toRemove.foreach(_.removeSelected)
							selectionHelper.selectedCardButtons = selectionHelper.selectedCardButtons -- toRemove
				}

			} else {
				removeSelected
				selectionHelper.remove(this)
			}
		}
	}


	}
  
  def removeSelected = {
      Screen.remove(personalIndicator)
      
      clicked = false
    }
  
}
/*
 * A cardButton which is not revealed.
 */
class FaceDownButton() extends CardButton(new Card("fake","fake",99)) {
  icon =  new ImageIcon("src/files/takapuoli.png")
}

case object makeMoveButton extends ButtonType {
  text = "make move"
   icon = new ImageIcon("src/files/MakeMove.png")
  pressedIcon = new ImageIcon("src/files/MakeMoveClicked.png")
  disabledIcon = new ImageIcon("src/files/MakeMoveClicked.png")
  preferredSize = new Dimension(750,150)
  var isPressed = false
  reactions += {
    case e: MouseClicked => {
      if(this.enabled) {
      isPressed = true
      }
    }
  }
}

case object resultsButton extends ButtonType{
  text = "See results of this round"
  reactions += {
    case e: MouseClicked => {
      Screen.showResults()
    }
  }
}

case object saveGameButton extends ButtonType{
  text = "Save & Quit"
  reactions += {
    case e: MouseClicked => {
   
    kasino.io.KasinoWriter.saveGame(Screen.table)
    play.quit()
    
    }
  }
}
case object loadGameButton extends ButtonType {
  text = "Load a Game"
  reactions += {
    case e: MouseClicked => {
     
  
    val string = Source.fromFile("state.txt").getLines.mkString
    if(!string.isEmpty()) {
    Screen.table = kasino.io.KasinoReader.loadGame(string)
    Screen.remove(startGameButton)
    Screen.remove(this)
    Screen.remove(instructionsButton)
    
    Screen.remove(copyRightString)
    Screen.restart()
    }
      
    
    }
  }
  
}
case object continueButton extends ButtonType {
  text = "continue"
  reactions += {
    case e: MouseClicked => {
      Screen.peer.removeAll
      Screen.table.resetBetweenRound
      Screen.restart
      namesAndPoints.redraw
      commandString.update("You can start a new round or save the game and come back later!")
    }
  }
}

case object finishButton extends ButtonType {
  text = "finish"
  reactions += {
    case e: MouseClicked => {
      Screen.table = new kasinocode.Table(ArrayBuffer())
      kasino.io.KasinoWriter.clear()
      Screen.peer.removeAll()
      Screen.drawStartingScreen()
    }
  }
}
case object startGameButton extends ButtonType {
  text = "Play!"
  reactions += {
    case e: MouseClicked => { 
    
    Screen.openRoom()
   
   
   
    }
  }
}
case object resumeButton extends ButtonType {
  text = "resume game"
  reactions += {
    case e: MouseClicked => {
    Screen.gameOn = true
    Screen.peer.removeAll()
    Screen.refreshHands()
   
    Screen.makeMove()
    }
  }
}
case object showResultsButton extends ButtonType {
 icon = new ImageIcon("src/files/cardStacks.png")
  pressedIcon = new ImageIcon("src/files/cardStacksClicked.png")
  disabledIcon = new ImageIcon("src/files/cardStacksClicked.png")
   
  
 
  preferredSize = new Dimension(750,150)
  reactions += {
    case e: MouseClicked => {
      if(this.enabled) {
      Screen.gameOn = false
      Screen.peer.removeAll()
      Screen.drawResult()
      Screen.add(resumeButton,600,900)
    }
    }
  }
}

case object instructionsButton extends ButtonType {
  text = "Instructions"
  reactions += {
    case e: ButtonClicked => {
      Screen.peer.removeAll()
      Screen.add(instructionsString,50,20)
      Screen.add(mainMenuButton,30,900)
    }
  }
}


case object startButton extends ButtonType{
  
  text = "Start new round"
  reactions += {
    case e: MouseClicked =>{
     if(Screen.table.players.size > 1) {
      Screen.start
     }
    }
  }
}
/*
 * The object which is a button for adding robots. Inside is a collection of different kinds of robots and one of them is picked at random each time the button
 * is clicked.
 */
case object addRobotButton extends ButtonType {
  text = "add robot"
  var robots = ArrayBuffer[RobotOpponent]()
  add
   private def add = {
    robots = ArrayBuffer[RobotOpponent]()
    robots += new RobotOpponent("Jack",new Hand,spadeParade())
    robots += new RobotOpponent("Mary",new Hand,specialCrave())
    robots += new RobotOpponent("Lucian",new Hand,greedy())
    robots += new RobotOpponent("Jessica",new Hand,clearFear())
    robots += new RobotOpponent("Bill", new Hand,spadeParade())
    robots += new RobotOpponent("Liz",new Hand,specialCrave())
    robots += new RobotOpponent("Tom",new Hand,greedy())
  }
    
  
  
  def reset = {
    this.enabled = true
    add
    
  }
  reactions += {
   
    case e: MouseClicked => {
      
      var randomName = new scala.util.Random
       robots = randomName.shuffle(robots)
       var bot = robots.remove(0)
       
       
      Screen.table.addPlayer(bot)
      namesAndPoints.redraw
      if(Screen.wantsSound) Screen.playAddSound
       if(Screen.table.players.size == 4) {
				    Screen.remove(addPlayerButton)
				    Screen.remove(this)
				        
				  }
      
    }
  }
  
}
// Handles music + sounds on/off

case object musicButton extends ButtonType {
      icon = new ImageIcon("src/files/volume.png")
      reactions += {
        case e: MouseClicked =>
          if (play.clip2.isActive()) {
            Screen.wantsSound = false
            play.clip2.stop()
            this.icon=new ImageIcon("src/files/novolume.png")
          } else {
            Screen.wantsSound = true
            play.clip2.start()
            play.clip2.loop(javax.sound.midi.Sequencer.LOOP_CONTINUOUSLY)
            this.icon= new ImageIcon("src/files/volume.png")
          }
        

      }
      
      
}
 

case object addPlayerButton extends ButtonType {
  text = "add player"
  def reset = this.enabled = true
  reactions += {
    case e: MouseClicked => {
   
   //Dialog.showMessage( play.top.contents.head, "Thank you!", title="A robot was added.")
   Screen.addPlayer()
   namesAndPoints.redraw
    
   
  }
  
}
     
}



case object mainMenuButton extends ButtonType {
  text = "Main Menu"
  
  reactions += {
    case e: MouseClicked => {
      Screen.gameOn = false
      Screen.table = new kasinocode.Table(ArrayBuffer())
      Screen.peer.removeAll()
      
      Screen.drawStartingScreen()
    }
  }
}



//The object for handling which cardButtons are selected by the player
object selectionHelper {
  var selectedCardButtons:ArrayBuffer[CardButton] = ArrayBuffer()
  
  def add(card:CardButton) = selectedCardButtons += card
  def remove(card:CardButton) = selectedCardButtons -= card
  def clear = {
    selectedCardButtons.foreach(_.removeSelected)
    selectedCardButtons.clear()
    
  }
}

