package kasinocode
import scala.collection.mutable.Buffer
import scala.util.Random

//A deck is a deck of a table. It contains 52 Card.scala objects. A deck is shuffled with a given seed.


class Deck(seed:Int) {
  var cards = Buffer[Card]()
  val shuffler = new Random(seed)
  
  val suits = Buffer[String]("clubs","spades","diamonds","hearts")
  val names = Buffer[String]("2","3","4","5","6","7","8","9","10","11","12","13","14")
  
  for(x<-suits;y<-names) {                                    //Adds the cards in a simple loop
     var value = {
       if (x=="diamonds"&& y == "10") 16
       else if (x=="spades" && y == "2") 15
       else y.toInt
    }
    cards += new Card(y,x,value)
  }
  
  cards = shuffler.shuffle(cards)
  
  def oneMore:Option[Card] = {
    if(!isOut) Some(cards.remove(0)) else None
  }
  
  def isOut = this.cards.isEmpty
  
 

 /*cards = cards.drop(30).filter(!_.isSpecialCard)
 cards += new Card("10","diamonds",16)
 cards += new Card("2","spades",15)
 * 
 */
 
 
  
  
}