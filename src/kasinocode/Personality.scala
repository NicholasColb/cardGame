package kasinocode
import scala.collection.mutable.ArrayBuffer

/*
 * Each robot has a personality which basically just determines what is valued by the robot. The AI requires three methods:
 * Method 1: prioritize -> How do we choose between two collections of cards? When a collection of possible moves is folded with this method, the best move will remain.
 * Method 2: oneOfFOur -> When the robot cannot make any moves, which of the four cards do we discard? 
 * Method 3: isThisBetter -> Basically the same as prioritize, except returns a boolean value which indicates whether one move is better than the other. Used for 
 * when a best-so-far move is stored and a new best move candidate has to be compared to it.
 */

//The abstract class Personality which requires the three methods to be implemented
abstract class Personality {
	def prioritize (tableCards:ArrayBuffer[Card],pair:Pair[ArrayBuffer[Card],ArrayBuffer[Card]]):ArrayBuffer[Card]
	def oneOfFour(hand:Hand,tableCards:ArrayBuffer[Card]):Card
	def isThisBetter(tableCards:ArrayBuffer[Card],toCompare:ArrayBuffer[Card],bestSoFar:ArrayBuffer[Card]):Boolean = prioritize(tableCards,(toCompare,bestSoFar)) == toCompare
	
}

case class greedy() extends Personality {     //A personality which prioritizes card amount and card values
	def prioritize(tableCards:ArrayBuffer[Card],pair:Pair[ArrayBuffer[Card],ArrayBuffer[Card]]):ArrayBuffer[Card] = if(pair._1.size<pair._2.size) pair._2 else pair._1
			def oneOfFour(hand:Hand,tableCards:ArrayBuffer[Card]):Card = hand.components.minBy(_.value)

}


case class specialCrave() extends Personality {                                    //A personality which prioritizes special cards over amount
	def prioritize(tableCards:ArrayBuffer[Card],pair:Pair[ArrayBuffer[Card],ArrayBuffer[Card]]):ArrayBuffer[Card] = {
			val a = pair._1
					val b = pair._2
					if(a.exists(_.isSpecialCard) || b.exists(_.isSpecialCard)) {            //---->if either one contains specials
						var f1 = a.filter(_.isSpecialCard)
								var f2 = b.filter(_.isSpecialCard)
								if(f1.exists(a => a.name == "10" && a.suit == "diamonds") && f2.size < 3) return a      //Prioritizes the ten of diamonds over other special cards
										if(f2.exists(a => a.name == "10" && a.suit == "diamonds") && f1.size <3) return b        
												if(f1.size != f2.size)  {
													if( Array(f1,f2).maxBy(_.size)  == f1) return a
															else return b
												} else Array(a,b).maxBy(_.size)                             //else total card amount decides
					} else Array(a,b).maxBy(_.size)                                                           //<----if neither contains specials, total card amount decides
	}

	def oneOfFour(hand:Hand,tableCards:ArrayBuffer[Card]):Card = {                                    
			var returnee = hand.components.find(!_.isSpecialCard)                        //Attempts to find non-special card
					returnee.getOrElse(hand.components.head)                                       // Otherwise draws the first one to the table
	}

}

case class spadeParade() extends Personality {                                         //A personality which prioritizes spades over other cards
	def prioritize(tableCards:ArrayBuffer[Card],pair:Pair[ArrayBuffer[Card],ArrayBuffer[Card]]):ArrayBuffer[Card] = {
			val a = pair._1
					val b = pair._2
					if(a.exists(_.suit == "spades") || b.exists(_.suit == "spades")) {            //---> If either contains spades
						var f1 = a.filter(_.suit == "spades")
								var f2 = b.filter(_.suit == "spades")
								if(f1.size == f2.size) return Array(a,b).maxBy(_.size)                  //Compare amount of spades and return the one with more
										else {
											if(Array(f1,f2).maxBy(_.size) == f1) return a
													else return b
										}

					} else return Array(a,b).maxBy(_.size)                                          //<--- Otherwise total card amount decides
	}

	def oneOfFour(hand:Hand,tableCards:ArrayBuffer[Card]):Card =  {        
			var returnee = hand.components.find(_.suit != "spades")                              //Try to find a non-spade
					returnee.getOrElse(hand.components.head)
	}

}

  
case class clearFear() extends Personality {                                      //A personality which watches out for leaving clear opportunities for opponents
  
	private def modChecker(i:Int):Boolean = {                                       //A simple helper method, which checks if an integer is "dangerous" - or divisible by numbers 2 to 16.                 

			for(x <- 2 to 16) {
				if(i%x == 0) return true

			}
			false

	}
	def prioritize(tableCards:ArrayBuffer[Card],pair:Pair[ArrayBuffer[Card],ArrayBuffer[Card]]):ArrayBuffer[Card] = {
			var case1 = tableCards -- pair._1                                                                                  //Speculates the two different cases
					var case2 = tableCards -- pair._2
					var case1Danger = modChecker(case1.foldRight(0)(_.value + _))                                                  //Boolean values indicating danger for the two cases
					var case2Danger = modChecker(case2.foldRight(0)(_.value + _ ))



					//Redundant "return" commands helps recognize what the method does

					if(case1.isEmpty) return pair._1                                                                                //If we ourselves get a clear, do that move
							else if(case2.isEmpty)  return pair._2
									else if(case1Danger&&  !case2Danger)  return pair._2                                                      //Try to find the non-dangerous one
											else if(!case1Danger && case2Danger) return pair._1
													else Array(pair._1,pair._2).maxBy(_.size)                                                        //If both are dangerous, return the one with which we get more cards


	}


	def oneOfFour(hand:Hand,tableCards:ArrayBuffer[Card]):Card =  {
			var checker = hand.components.sortBy(_.value)                                                   //start checking from smallest
			for (currentCard <- checker) {
			  
			  if(!modChecker((tableCards :+ currentCard).foldRight(0)(_.value + _))) return currentCard      //If card found which does not yield an easy clear opportunity, let's return it
			}
			
			return hand.components.head                                                                      //If none found, let's return our first card
			
	}

}



