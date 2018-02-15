package kasinocode

import scala.collection.mutable._



//A player is an object which plays the game and represents a physical player. A player has a name, a hand, points etc.

class Player(var name:String,var hand:Hand) {       
  
  
  var ruleBook = Logic
  var points:Int = 0
  var ultimatePoints:Int = 0
  var mokkiCount:Int = 0                                                  //Keeps track of the player's clears
  var winStack = new Hand                                                //The player's captured cards
 
  //A method which helps analyze a player's winstack
  def finale:(Int,Int,Int,Int) = (this.winStack.components.size,this.winStack.components.filter(_.suit=="spades").size,this.winStack.components.filter(_.name == "14").size,this.mokkiCount)
  def isDealer(table:Table) = {
    this == table.dealer.getOrElse(new Player("mrNull",new Hand))
    
  }
  
  
  def hasCard(card:Card) = hand.components.contains(card)
  
  def takeCard(card:Option[Card]):Boolean = {
    if(card.isDefined){ this.hand.addCard(card.get); true
    } else false
    
  }
  
  
  def drawCard(cardNumber:Int):Card =drawCard(this.hand.components.head)
   
  
  def drawCard(card:Card):Card = {
   
    if(hasCard(card)) {
    this.hand.removeCard(card)
    
    card
   } else throw new Exception("Player did not have a card which was drawn")
   
  }
  
  def claim(card:Card*) {
    card.foreach(winStack.addCard(_))
  }
  
  def placeToTable(card:Card, table:Table) {
   
    card.changeSpecialCardToTable
    table.cardsOnTable.addCard(card)
  }
  
  
  def exchange( cards:Buffer[Card],card:Card):(Card,Buffer[Card])= {                         //TODO: Check legitimacy in logic.scala                                 //Returns the card which was drawn to table
    
    if(hasCard(card)) {
    
    claim(drawCard(card))
    cards.foreach(claim(_))                                                                //Returns the card which was drawn to table paired up with the cards taken in an Array
    (card,cards)
    } else throw new Exception("Player did not have a card which was drawn")
  }
  
  
  
  def addPoints(number:Int)= {
    points += number
    ultimatePoints += number
  }
  
  
  
  def addMokki = mokkiCount += 1
  
 
  
  def resetPoints:Unit ={
    this.points = 0
  }
  def resetUltimatePoints:Unit = {
    this.ultimatePoints = 0
  }
  
  def resetWinStack:Unit = {
    this.winStack = new Hand
  }
  
  def resetHand:Unit = {
    this.hand = new Hand
  }
  def resetMokkis = this.mokkiCount = 0
  
  def resetWithPoints(a:Boolean):Unit = {
    if(a) resetUltimatePoints
    resetPoints
    resetWinStack
    resetHand
    resetMokkis
  }
  
  
  
  def checkMokki(table:Table) = {                    //Checks for mökki's and adds the according points to the player
    if(ruleBook.checkMokki(table)) {
      
      this.mokkiCount += 1
      
    } 
  }
  
  
  
  //The basic method which is called when a player makes his/her move
  /*
   * @param table - the table where the game takes place
   * @param cardsToClaim - the cards which the player has selected off the table
   * @param cardToDraw - the card which the player has selected from his/her hand
   * Returns a boolean value indicating if the move was made successfully
   */
    def makeMove(table:Table,cardsToClaim:ArrayBuffer[Card],cardToDraw: Card):Boolean = {     

    	if(ruleBook.isLegit2(cardsToClaim,cardToDraw)){                        //Check the move through Logic.scala
    		this.exchange(cardsToClaim,cardToDraw)                                
    		table.cardsOnTable.removeCards(cardsToClaim)
    		checkMokki(table)
    		table.lastOneToPick = Some(this)                                      //Add the player to the table's holder last one to pick
    		true
    	} else { 
    		false
    	}

    }
  

}


