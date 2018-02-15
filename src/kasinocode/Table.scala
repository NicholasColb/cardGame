package kasinocode

import scala.util.Random
import scala.collection.mutable.ArrayBuffer

/*
 * The table is the setting of the game. It consists of a deck and an n-number of players.
 * The table class can access its players, and keeps track of their turns and points.
 * In a way, the entire course of the game is orchestrated from here.
 */


class Table (var players: ArrayBuffer[Player]) {
  var ruleBook = Logic                                                    //The rules of the game. Seperate variable makes it clearer.
	var deck = new Deck((new Random).nextInt())                             //The deck of the table
	var cardsOnTable = new Hand                                           //The cards currently visible on the table
	var currentTurn = 0                                                     //The index of whose turn it is
	var roundPoints = (players zip players.map(_.points)).toMap.withDefaultValue(0)      //the points which still exist after each round
	var lastOneToPick:Option[Player] = None   //Stores the player who last picked cards up. Necessary for method 'giveLastCards'
	var dealer:Option[Player] = None          //Stores the dealer of the game.
  var firstPlayer:Option[Player] = None     //Stores the first player to enter the game. This is necessary for Screen.scala to keep track of who to display on the bottom of the screen.
  
	def addPlayer(player:Player) = players += player                        //adds a player to the table
	def removePlayer(player:Player) = players -= player                     //removes a player from the table
	def newDeck = deck = new Deck((new Random).nextInt())                   //gets a new deck
	def changeTurn = {                                                      //changes the current turn
		this.players = this.players.takeRight(1) ++ this.players.dropRight(1) 
	
	}
	def dealStart = {                                        //Deals the starting set; each player 4 cards and 4 on the table
		for(a<-ArrayBuffer(0,1,2,3)){
		  var tableCard = deck.oneMore.get
		  tableCard.changeSpecialCardToTable
			this.cardsOnTable.addCard(tableCard)
			for(p<-this.players) {
				p.takeCard(deck.oneMore)
			}
		}
	}
	
	
	def hasEnded:Boolean = {                              //After each turn, checks if game has ended, as in whether players still have cards or not
	this.players.foldRight(true)(_.hand.components.isEmpty && _)
	}
	def gameHasEnded = {
	  this.players.foldRight(false)(_.ultimatePoints >= 16 || _)
	  
	}
	
	def resetBetweenRound = {                              //Resets the table between rounds. Does not reset a player's ultimatePoints.
	  this.cardsOnTable = new Hand
	  this.players.foreach(_.resetWithPoints(false))
	  this.newDeck
	 
	}
	
	
	
	
	
	def giveLastCards:Unit = {                                 //Deals the last cards on the table to the right player.
	  if(lastOneToPick.isDefined){ 
	    var player = lastOneToPick.get
	    cardsOnTable.components.foreach(player.claim(_))
	    cardsOnTable.removeAll
	   
	    
	 
	  } 
	}
	
	
	 
  def twoSpadesHolder:Option[Player] = {                                                                  //Determines the player who has the two of spades. Returns None if no-one has it.
    var holder = players.filter(_.winStack.components.exists(a => a.name == "2" && a.suit == "spades"))
    if(holder.size>0) Some(holder(0)) else None
  }
  def tenDiamondsHolder:Option[Player] = {                                                                    //Determines the player who has the ten of diamonds. Returns None if no-one has it.
    var holder = players.filter(_.winStack.components.exists(a=>a.name == "10" && a.suit == "diamonds"))
    if(holder.size >0 ) Some(holder(0)) else None
    
  }
  
  /****************************************************************************************************
   ****************************************************************************************************
   ****************************************************************************************************
   ****************************************************************************************************
   */
}