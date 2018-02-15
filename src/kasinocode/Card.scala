  package kasinocode

/* A card has a name, suit and value. Some cards hasve different values on the table and in-hand. The isSpecalCard-method
 * changes the value accordingly.
 * 
 */

class Card(val name:String, val suit: String, var value:Int){
  def isSpecialCard:Boolean = {
    (name,suit) match {
      case ("2","spades") => return true
      case ("10","diamonds") => return true
      case ("14",any) => return true
      case _ => return false
    }
  }
  
  def isBiggerThan(another:Card)= {
    this.value>another.value
  }
  
  
  //The method for changing a special card's value to the according table value. (Eg. ace 14 to 1)
  def changeSpecialCardToTable = {                              
    if(this.isSpecialCard) {
      this.name match {
        case "2" => this.value = 2
        case "10" => this.value = 10
        case "14" => this.value = 1
        case _ => throw new Exception("changeSpecialCardValue did not work in class Card, something is wrong with its construction.")
      }
    }
  }
  
  
}