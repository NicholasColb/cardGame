package kasinocode
import scala.collection.mutable.ArrayBuffer
/*NOTE: THIS VERSION OF THE GAME WAS DESIGNED FOR TEST-USE ONLY IN EARLY PHASES OF THE PROJECT. THIS OBJECT IS COMPLETELY REDUNDANT.
 *IT WAS DESIGNED TO WORK WITH METHODS BUILT TO BE GUI-COMPATIBLE,
 *THEREFORE THIS GAME SHOULD NOT BE EXPECTED TO WORK, BE FLAWLESS OR BE USER-FRIENDLY.  
 */

object consoleVersion extends App{
  var player = new Player("Nic", new Hand)
  /*
  var Jaakko = new RobotOpponent("Jaakko", new Hand())
  var Merja = new RobotOpponent("Merja", new Hand())
  var Jorma = new RobotOpponent("Jorma", new Hand())
  var table = new Table(ArrayBuffer(player,Jaakko,Merja))
  var gameOn = true
  
  println("Welcome to casino!")
  table.addPlayer(Jorma)
  println("Jorma joined the game.")
  
  table.dealStart
  println("The game has started") 
  println("You are playing against: " + Jaakko.name + ", " + Merja.name + ", " + Jorma.name) 
  
  
  
  
  
 def handString(hand:Hand):String =  {
   var b = ""
    for (a<-hand) {
      b += a.name + " of " + a.suit + ", "
    }
   b= b.dropRight(2)
   b+= "."
   b
   
  }
  
  def valueString(hand:Hand):String = {
    var b = ""
    for(a<-hand) {
      b += a.value + ", "
    }
    b = b.dropRight(2)
    b += "."
    b
  }
   
   
   def newGame():Unit = {
     var answer = readLine("Would you like to play another round?")
     answer match{
       case "yes" => {
         table.players.foreach(_.resetWithPoints(false))
         table.newDeck
         table.dealStart
         gameOn = true
         println("The game has started") 
         println("You are playing against: " + Jaakko.name + ", " + Merja.name + ", " + Jorma.name) 
       }
       case "no" => System.exit(1)
       case _ => newGame();return
     }
   }
   
   
  while(gameOn) {
   if(table.hasEnded) {
     println("The game ended")
     table.giveLastCards
     Logic.countPoints(table)
     table.players.foreach(a=>println(a.name + " had " + handString(a.winStack) + "\nPoints:" + a.points.toString))
     
     println("The winner is/are....... " )
     Logic.determineWinnerOfOneRound(table).foreach(a=>println("************" +a.name + "************" + {
     "\nWith " +a.finale._1 + " cards, " + a.finale._2 + " spades, " + a.finale._3 + " aces and " + a.finale._4 + " mokkis!" }))
     println(table.tenDiamondsHolder.name + " had the 10 of diamonds. (2 points)")
     println(table.twoSpadesHolder.name + " had the 2 of spades. (1 point)")
     gameOn = false
     newGame()
   } else if(!table.players(0).isInstanceOf[RobotOpponent]) {
     println("It's your turn!")
     println("You have: " + handString(player.hand))
     println("******************** on the table you see ********************: " + handString(this.table.cardsOnTable))
     var ask1 = readLine("Would you like to change cards on the table?")
     ask
       def ask:Unit = {
           ask1 match {
           case "yes" => player.makeMove(this.table)
           case "no" => {
                 var whichCard = readLine("Which card would you like to draw to the table?")
                 player.placeToTable(player.drawCard(player.hand(whichCard.toInt)),this.table)
                 player.takeCard(table.deck.oneMore)
                 }
           case _ => {ask1 =  readLine("Invalid. Would you like to change cards on the table?")
           ask
           }           
           }
     }
     
     println("You made your move. ON THE TABLE YOU SEE: " + handString(this.table.cardsOnTable))
     
     println("You have: " + handString(player.hand))
     
     println("Your winstack consists of: " + handString(player.winStack))
     table.changeTurn
   } else {
     var robot = table.players(table.currentTurn)
     println("It's " + robot.name + "'s turn!")
     table.players(table.currentTurn).makeMove(table)
     
     println("Move made. ON THE TABLE YOU SEE: " + handString(this.table.cardsOnTable))
     
     
     println(robot.name + "'s winstack consists of: " + handString(robot.winStack))
     table.changeTurn
     
     
   }
   
   
   
   
   
  }
  * 
  */
}
