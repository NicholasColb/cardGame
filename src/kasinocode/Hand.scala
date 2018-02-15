package kasinocode
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer

/*
 * A hand is a collection of cards. Hands have to be unique regardless of its components, however, so each object has a Buffer 
 * variable "components". (Compared to class Hand extending collection.mutable.ArrayBuffer)
 */


class Hand{
  var components = ArrayBuffer[Card]()
  def addCard(card:Card) = components += card
  def removeCard(card:Card):Unit = {
    components -= card
    
  }
  
  def removeCards( cards: ArrayBuffer[Card]) = {
    
    for(card <- cards) {
      removeCard(card)
    }
  }
  
  
  def countValue = {
   components.foldRight(0)((a,b) => a.value + b)
  }
  def removeAll = {
    components.clear()
    
  }
  
  
}