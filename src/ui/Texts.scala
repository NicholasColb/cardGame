package ui
import scala.swing._
import java.awt.Color
import java.awt.Font
import javax.swing.ImageIcon
import java.io.File
import java.net.URL
import javax.swing._

/*
 * A class which unique single-lined strings can extend. All strings to be displayed on the screen have similar properties such as color, font and border. These types of strings
 * also have an "update" method, which allows the string to be changed.
 */
sealed class Texts(var string:String, fground: Color) extends TextField{
      
      text = string
      font = play.newFont
      background = Screen.mainColor
      border = Swing.EmptyBorder(0, 0, 0, 0)
      
      foreground = fground
      editable = false
      def update(string2:String) = text = string2 
}    

//An object string which displays who's turn it is
case object turnString extends Texts("It's " + Screen.table.players(0).name + "'s turn!", new Color(0, 250,154) ) {
  columns = 50
  def updateContent = this.text = "It's " + Screen.table.players(0).name + "'s turn!"
}

//An object string which displays who is playing in the room
case object opponentString extends Texts("Players in room:           " + Screen.table.players.map(_.name).mkString(", "),new Color(0, 250,154) ) {
  columns =75
  font = play.newFontSmaller
  visible = true
  def updateContent = update("Players in current room: " + Screen.table.players.map(_.name).mkString(", "))
}

//An object string which displays each player's current points 
case object pointsString extends Texts("Total points at the moment: " + Screen.table.players.map(_.ultimatePoints).mkString("   /   "),new Color(0, 250,154)) {
 columns = 65
 font = play.newFontSmaller
  visible = true
  def updateContent = update("Total points at the moment: " + Screen.table.players.map(_.ultimatePoints).mkString("   /   "))
}

//An object string which draws the opponentString and pointsString close to eachother on the screen.
case object namesAndPoints {
  def redraw = {
    pointsString.updateContent
    opponentString.updateContent
    Screen.add(opponentString,500,740)
    Screen.add(pointsString,500,770)
  }
 
  
}

//A command string which tells the player what is happening during the course of the game
case object commandString extends Texts("Welcome to kassino!",new Color(0, 250,154)){
  columns = 100
}

//A string which displays who won the round. Is updated through Screen.scala
case object winnerString extends Texts("Winner", new Color(0, 250,154)) {
columns = 40
}

//A string which displays who had most spades at the end of a round. Is updated through Screen.scala
case object mostSpadesString extends Texts("Spades", new Color(0, 250,154)) {
  columns = 30
}

//A string which displays who had most cards at the end of a round. Is updated through Screen.scala
case object mostCardsString extends Texts("Cards", new Color(0, 250,154)) {
  columns = 30
}

//A string which displays who had the ten of diamonds at the end of a round. Is updated through Screen.scala
case object tenDiamondsString extends Texts("TenDiamonds", new Color(0, 250,154)) {
  columns = 30
}

//A string which displays who had the ten of diamonds at the end of a round. Is updated through Screen.scala
case object twoSpadesString extends Texts("twoSpades", new Color(0, 250,154)) {
  columns = 30
}

//A string which displays an ultimate winner of a game - consisting of many rounds. Is updated through Screen.scala
case object ultimateWinnerString extends Texts("Winner",new Color(0, 250,154)) {
  columns = 40
}

//A string which displays who the game is made by
case object copyRightString extends Texts("Made by Nicholas Colb",new Color(0, 250,154)) {
  columns = 20
}

//A string which displays the instructions of the game. Extends TextArea, which is used in multi-lined strings.
case object instructionsString extends TextArea {
  text = """
Kassino is an old Italian fishing card game.

The dealer deals four cards to each player and lays four more face up on the table. Cards on the table are captured using cards from a player’s hand.
This is done by matching pairs (an 8 in your hand can take any 8 on the table), or by taking a combination of cards on the table that add up to 
a card in your hand (an 8 in your hand can take both a 2 and a 6 on the table). One card can also take many combinations off the table (an 8 in your hand 
can take an 8, 2, and 6 on the table all at once). Each card on the table may only be used for one combination (an 8 in your hand
cannot take a 2, 6 and a 6). You can only make one capture per turn

If a player cannot make a capture, he/she must place a card face up on the table. 

The jack, queen and king are considered 11, 12 and 13's. Any ace in a player's hand is worth 14, whereas aces on the table account for 14. 
There are two special cards in the game: the two of spades is 15 in a player's hand, but only 2 on the table. Similarly, the ten of diamonds is a 16 when held
and 10 on the table.

The game continues until each player is out of cards, at which point the player who has last made a capture gets the cards remaining on the table.

After this each pile of captured cards is counted up and each player's score is calculated.

Points are awarded accordingly:

	Most number of cards = 1 point
	Most number of spades = 2 points
	2 of spades = 1 point
	10 of diamonds = 1 point
	Aces = 1 point each

Players also earn a point every time they clear the table during the course of the game.
 
Rounds are played until anyone wins the game by reaching 16 points.
"""
  this.foreground = new Color(0,250,154)
  font = play.newFontSmaller
  columns = 120
 
  lineWrap = true
  wordWrap = true
  background = Screen.mainColor
  border = Swing.EmptyBorder(0, 0, 0, 0)
  editable = false
  
 
}
  
//A string for each unique player which indicates his/her name. Is added through Screen.scala, when a hand or winStack is drawn.
case class playerString(str:String) extends Texts( str,new Color(0, 250,154)){
  columns = 30
}

  


//A case class which is basically a Label of an image which indicates which cards the player has selected. Is added through Buttons.scala and Screen.scala
case class selectIndicator() extends Label { icon = new ImageIcon("src/files/selectIndicator.png")}

  




