package kasinocode
import scala.collection.mutable.ArrayBuffer
/*The Logic object is the brains of the game. It validates moves, counts points, determines winners and determines when
 * the game has ended. 
 * Being the brains of the game, the object also helps with the artificial intelligence of robot opponents by 
 * finding suitable combinations of cards. 
 * 
 */


object Logic {  
  
  
  
   
  
  def countPoints(table:Table):(ArrayBuffer[Player],ArrayBuffer[Player]) = {                                  //Counts the points accordingly
	  var mostCards:ArrayBuffer[Player] = ArrayBuffer()                      //Sets a container for player(s) with most cards
		var mostSpades:ArrayBuffer[Player]= ArrayBuffer() 
			  for(player<-table.players) {                                    //Goes through each player individually
				      var pointCounter = 0                                          //Sets the personal pointcounter at 0
						  pointCounter += player.mokkiCount                             //Adds one point per Mökki 
						  var specialCards = player.winStack.components.filter(_.isSpecialCard)          //Sets a buffer of only special cards
						  pointCounter += specialCards.filter(_.name=="14").size                   //Adds one point per Ace in the player's winstack
						  pointCounter += 2*specialCards.filter(_.name=="10").size                 //Adds two points if the player has the ten of diamonds
						  pointCounter += specialCards.filter(_.name == "2").size                  //Adds one point if the player has the two of spades
						  player.addPoints(pointCounter)
						  
						  
						   /*
						    * These if-else structures determine the player(s) with most cards and most spades
						    */
						  
						  if(!mostCards.isEmpty){                          
							  var toCompareMost = mostCards(0).winStack
							  var playerStack = player.winStack
									  if(toCompareMost.components.size<playerStack.components.size) mostCards = ArrayBuffer(player)
									  else if(toCompareMost.components.size == playerStack.components.size) mostCards += player

						  } else if(player.winStack.components.length > 0){
						    
							  mostCards += player
						  }
				  
				  
				      if(!mostSpades.isEmpty) {
				        var toCompareSpades = mostSpades(0).winStack.components.filter(_.suit=="spades")
				        var playerStack = player.winStack.components.filter(_.suit=="spades")
				        if(toCompareSpades.size<playerStack.size) mostSpades = ArrayBuffer(player) 
				        else if(toCompareSpades.size==playerStack.size) mostSpades += player
				

			    		} else if (player.winStack.components.exists(_.suit == "spades")) {
						    	mostSpades += player
					    }
				  



			  }
			  /*
			   * Now add the according points
			   */
	  mostCards.foreach(_.addPoints(1))                         
	  mostSpades.foreach(_.addPoints(2))                            
	  return(mostCards,mostSpades)

  }

  def checkEnd(table:Table)= deckIsOut(table)
  def checkMokki(table:Table) = {                          //should check clear or "Mokki" after each turn
    table.cardsOnTable.components.isEmpty
  }
  
  def mustPlaceCard(table:Table) = {                      //true only after a clear
        checkMokki(table)
  }
  
  def deckIsOut(table:Table) = {                          //tells when no more cards have to be picked up
    table.deck.isOut
  }
  
  def determineWinnerOfOneRound(table:Table):ArrayBuffer[Player] = {                    //Determines the player with most points after one round. (Not ultimate points!)
    var winner:ArrayBuffer[Player]= ArrayBuffer()
    for(player <- table.players) {
      if(!winner.isEmpty) {
        if(player.points>winner(0).points) winner = ArrayBuffer(player)
        else if(player.points == winner(0).points) winner += player
      } else if(player.points>0) winner = ArrayBuffer(player)
    }
    winner
    
  }
  
  def determineUltimateWinner(table:Table):ArrayBuffer[Player] = {                     //Determines the player who has won the entire game
    require(table.gameHasEnded)                                                        //Only to be called once the game has ended.
    var winner: ArrayBuffer[Player] = ArrayBuffer()
    for(player <- table.players) {
     if(!winner.isEmpty) {
       if(player.ultimatePoints>winner(0).points) winner = ArrayBuffer(player)
        else if(player.ultimatePoints == winner(0).points) winner += player
      } else if(player.ultimatePoints>0) winner = ArrayBuffer(player)
    }
    winner
    }
  
  
  
  /*
   * The main move checking algorithm. 
   * @param cards is a Buffer containing the cards the player has selected from the table (to capture)
   * @param card is the card the player has selected from his/her own cards
   */
  def isLegit2(cards:ArrayBuffer[Card],card:Card):Boolean = {                            
    
	 
    canFormSubSets(cards.toSet,card.value)
  }
  
 

  
  //A helper method for artificial intelligence.
  def canFormSubSets(cards:Set[Card],cardValue:Int):Boolean = {
    
    var found = false
   
    def findPairs(f:ArrayBuffer[Card],from:ArrayBuffer[Card]):Unit = {
      var sum = f.map(_.value).fold(0)(_+_)
      var sum2 = from.map(_.value).fold(0)(_+_)
      if(sum == cardValue) {
        
        if(from.isEmpty) {
          found = true
          return
        }
        findPairs(ArrayBuffer(),from)
        return
      }
      if(sum > cardValue) return
      if(sum + sum2 < cardValue) return
      
      for(card <- from) {
        if(found) return
        findPairs(f :+ card,from - card)
      }
    }
  
    findPairs(ArrayBuffer(),cards.toBuffer.asInstanceOf[ArrayBuffer[Card]])
    found
  }
  
  //A recursive method with algorithms to determine combinations of possible moves for robots. The possible moves are stored in param answers.

  def combinations2(tableCards:ArrayBuffer[Card], cardValue: Int):ArrayBuffer[ArrayBuffer[Card]]= {
  	var answers = ArrayBuffer[Set[Card]]()
	    def findMove(subArray:Set[Card],takeFrom:ArrayBuffer[Card]):Unit = {
		     
		      if(subArray.exists(_.value > cardValue)){
		        
		        return
		      }
		      if(!subArray.isEmpty && subArray.foldRight(0)(_.value + _) % cardValue ==0 && !answers.contains(subArray)){
		        if(canFormSubSets(subArray,cardValue)) {
		        answers += subArray 
		        }
		        
		      }
		      if(takeFrom.isEmpty) {
		        
		       
		        return
		      }
		      for(currentCard <- takeFrom) {
		        findMove(subArray ++ ArrayBuffer(currentCard), takeFrom - currentCard)
		      }
		
	    }
	  findMove(Set[Card](),tableCards.clone())
	  answers.map(_.toBuffer.asInstanceOf[ArrayBuffer[Card]])
}
 
  
  /* REDUNDANT - saved for later speculation
   *A recursive method I used before creating the actual one to determine combinations. REDUNDANT!
   */
 
  /*def combinations(tableCards:ArrayBuffer[Card], cardValue: Int, subArray: ArrayBuffer[Card], answers:ArrayBuffer[ArrayBuffer[Card]]):ArrayBuffer[ArrayBuffer[Card]]= {
  var s = 0
    if(!subArray.isEmpty)s = subArray.foldRight(0)((a,b) => a.value + b)
   
      if(s == cardValue) {
        
       answers += subArray
        
        
      }
      if (s>cardValue) return ArrayBuffer[ArrayBuffer[Card]]()
      
      for (i<-tableCards.indices) {
        var n = tableCards(i)
        var remaining = tableCards.drop(i+1)
        combinations(remaining,cardValue, subArray :+ n, answers)
        
      }
        answers
     
  }
  * 
  * In islegit :  /*var toCompare = cards                                                                  

			  if(cards.exists(_.value > card.value)) return false                              //If any card is larger than the card in the player's hand, return false
					  toCompare.foldRight(0)(_.value + _) % card.value == 0     
					  *                    //Is the total value of the selected table cards divisible by the card in the player's hand?
					  */
  */
  
  
}