package kasino.io
import java.io._
import scala.io.Source
import java.util.Scanner
import java.io.File
import ui._
import kasinocode._
import scala.collection.mutable.ArrayBuffer

//The writer overwrites a single line of text in state.txt, which the reader interprets. 
object KasinoWriter {
  
  
def saveGame(table:Table):String =  {                        //The table which is being saved
  var string = ""

 for(player <- table.players) {                              //Go through players
    var personalString = {
      if(player.isInstanceOf[RobotOpponent]) "ROB"
      else "PLR"                                             //Add "PLR" or "ROB" to start depending on whether the player is a robot or not
    }
    
    personalString += {
      if(player.name.size > 9) player.name.size.toString                  //Add the size of the player's name
      else "0" + player.name.size.toString
    }
    personalString += player.name                                          //Add the name
    if(player.isDealer(table)) personalString += "1" else personalString += "0"              //Add a binary which tells if player is dealing
    if(table.firstPlayer.get == player) personalString += "1" else personalString += "0"     //Add binary to tell if player is the first player
    personalString += {                                                                      // Add 2 digits for points
      if(player.ultimatePoints >9) player.ultimatePoints.toString
      else "0" + player.ultimatePoints.toString
    }
    
      string += personalString
      
  }
  
  
  string += "END00"                                                                            //Add END00 to end



  new PrintWriter("state.txt") { write(string); close }                                            //Overwrite the file


  string



  }

  def clear():Unit = new PrintWriter("state.txt") {write("");close}                                    //Clear the file (to be called just before the entire project package is exported and shared.)
}