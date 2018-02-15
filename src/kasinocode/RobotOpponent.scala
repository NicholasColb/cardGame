package kasinocode
import scala.collection.mutable._
/*
 *A robotOpponent is basically a player with a unique move making method. (Moves are made without a physical player's input but through algortithms presented by
 * Logic.scala and the robots Personality (Personality.scala)
 */
class RobotOpponent(var robotname:String,var robothand:Hand,personality:Personality) extends Player(robotname,robothand) {
  
  var iPicked:Option[(ArrayBuffer[Card],Card)] = None              //A variable which communicates with Screen.scala
  
  
  /*
   * Finds the best move with the given table cards
   */
  private def findBestMove(tableCards:ArrayBuffer[Card]):Option[(ArrayBuffer[Card],Card)] = {
    
    var returnee:Option[(ArrayBuffer[Card],Card)] = None
    if(tableCards.isEmpty) return returnee                                                               //An exchange move cannot be made on an empty table
    for(currentCard <- this.hand.components){                                                            //Go through own cards
      var answers = ArrayBuffer[ArrayBuffer[Card]]()
     
      //Get the best possible combination from all Logic.combinations by folding the collection with method prioritze ( Personality gives the prioritize method)     
                                                                                                                                                                                    //REDUNDANT REDUNDANT - saved for later speculation : val bestCombo = Logic.combinations( tableCards, currentCard.value, ArrayBuffer(), answers).fold(ArrayBuffer())((a,b:ArrayBuffer[Card]) => personality.prioritize(tableCards,(a,b)))
      val bestCombo = Logic.combinations2(tableCards,currentCard.value).fold(ArrayBuffer())((a,b:ArrayBuffer[Card]) => personality.prioritize(tableCards,(a,b)))
      
      if(returnee.isDefined) {                                  //->If we already have a potential answer
        
      if( personality.isThisBetter(tableCards,bestCombo :+ currentCard,returnee.get._1 :+ returnee.get._2)) {            //Compare that answer to this one and make according changes
        returnee = Some(bestCombo,currentCard)
        
      }
      } else returnee=Some(bestCombo,currentCard)               //<- Else this one is going to be a potential answer
      
    }
     
     returnee                                                    //Return the answer
  }
  override def makeMove(table:Table,cards:ArrayBuffer[Card],card:Card) = {
      
    var found = findBestMove(table.cardsOnTable.components)        //Find the best move (Option[ArrayBuffer..])
    if (found.isDefined&& !found.get._1.isEmpty) {                 //If it is defined and the cards to get is not empty     
      
       this.exchange(found.get._1,found.get._2)                    //Make the move
	     found.get._1.foreach(table.cardsOnTable.removeCard(_))      
	     checkMokki(table)
	     table.lastOneToPick = Some(this)                            //Add the robot to lastonetopick
	     this.iPicked = found                                        //Add the move to iPicked, which communicates with Screen.scala
      true                                                          //return true
          
    } else {                                                        //Else no move was found ( findBestMove returned None)  
                                          
      var cardToGive = personality.oneOfFour(this.hand , table.cardsOnTable.components )     //Get the best card to discard
     
      this.drawCard(cardToGive)                                                              //.... And make the move
      this.placeToTable(cardToGive,table)
      this.iPicked = Some(ArrayBuffer(),cardToGive)                                            //Update iPicked, which communicates with Screen.scala
      false
      
    }
      
  }
  
}