package test
import kasinocode._
import scala.collection.mutable.ArrayBuffer
//import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.Assertions._


/*
 * Some simple tests testing various parts of the game
 */
class test extends FlatSpec with Matchers {
  
  
  case class two() extends Card("2","test",2) 
  case class three() extends Card("3","test",3) 
  case class four() extends Card("4","test",4) 
  case class five() extends Card("5","test",5) 
  case class six() extends Card("6","test",6)  
  case class seven() extends Card("7","test",7)  
  case class eight() extends Card("8","test",8) 
  case class nine() extends Card("9","test",9) 
  case class ten() extends Card("10","test",10) 
  case class jack() extends Card("11","test",11) 
  case class queen() extends Card("12","test",12) 
  case class king() extends Card("13","test",13) 
  case class tableAce() extends Card("14","test",1)
  case class tableTenD() extends Card("10","diamonds",10)
  case class tableTwoS() extends Card("2", "spades", 2)
  case class ace() extends Card("14","test",14)
  case object twoSpades extends Card("2","spades",15) 
  case object tenDiamonds extends Card("10","diamonds",16) 
  case class testSpade() extends Card("test","spades",0)
  
  case object deck extends Deck(5)
  case class player() extends Player("1",new Hand)
  
  
  
  "A card" should "correctly change special cards' value" in {
   var ace1 = ace()
   var two = twoSpades
   var ten = tenDiamonds
   ace1.changeSpecialCardToTable
   two.changeSpecialCardToTable
   ten.changeSpecialCardToTable
   assert(ace1.value==1 && two.value==2 && ten.value==10)
   tenDiamonds.value = 16
   twoSpades.value = 15
  
  }
   
 "A card" should "not be able to have multiple values if not special" in {
     var a = ten()
        
     
       a.changeSpecialCardToTable
    
     assert(a.value == 10)
   }
  
  "A deck" should "have correct number of cards" in {
   assert(deck.cards.size==52)
  }
  
  
  "A deck" should "have 'random' cards in a test with different seeds" in { //Values could be the same but in this test the seeds are chosen so that they are not. 
    var newDeck = new Deck(190)
    assert(deck.cards(17).value != (newDeck.cards(17).value))
  }
  
  "A player" should "be able to pick up a card" in {
    var player1 = player()
    player1.takeCard(Some(twoSpades))
    assert(player1.hasCard(twoSpades))
    
  }
  
  "A player" should "be able to draw a card" in {
    var player1 = player()
    player1.takeCard(Some(twoSpades))
    player1.drawCard(twoSpades)
    assert(!player1.hasCard(twoSpades))
  }
  
  "A player" should "not be able to draw a non-existent card" in {
   var player1 = player()
   var thrown = false
   try {
     player1.drawCard(twoSpades)
   } catch {
     case e: Exception => thrown = true
   }
   
   assert(thrown)
    
    
  }
  
  "The table" should "be able to start round" in {
    var table = new Table(ArrayBuffer(player(),player(),player()))
        table.dealStart
        assert(table.cardsOnTable.components.size == 4)
        assert(table.players.forall(_.hand.components.size == 4))
  }
  
  "The table" should "be able to add a new player" in {
    var table = new Table(ArrayBuffer(player(),player()))
    table.addPlayer(player())
    assert(table.players.size==3)
  }
  
  
  "Logic class" should "have proper move checking" in {
    
    var rulebook = Logic
    
    assert(rulebook.isLegit2(ArrayBuffer(six(),jack(),five()),jack()))
    assert(!rulebook.isLegit2(ArrayBuffer(six(),jack(),ten(),five(),three(),tableAce()),jack()))
    
  }
  
  
  "Logic class" should "count points correctly" in {
    var rulebook = Logic
   
    var p1 = player()
    var p2 = player()
    var p3 = player()
    var p4 = player()
    var p5 = player()
    var p6 = player()
    
    
    
    p1.claim(three(),four(),five(),six(),seven(),eight())
    p2.claim(three(),four(),five(),six(),seven(),testSpade(),testSpade(),testSpade())
    p3.claim(three(),four(),five(),six(),testSpade(),testSpade(),testSpade())
    p4.claim(ace(),two(),three(),four(),five(),six(),seven(),eight())
    p5.claim(ace(),tenDiamonds)
    p6.claim(twoSpades,ace(),ace())
    
   
    
    
    p1.addMokki
    p1.addMokki  
    p2.addMokki
    
    var table = new Table(ArrayBuffer(p1,p2,p3,p4,p5,p6))
    
    Logic.countPoints(table)
    
    assert(p1.points == 2)
    assert(p2.points == 4)
    assert(p3.points == 2)
    assert(p4.points == 2)
    assert(p5.points == 3)
    assert(p6.points == 3) 
    
  }
  
  "Logic class" should "function in the unlikely case where all players have zero points" in {
    var table = new Table(ArrayBuffer(player(),player(),player(),player()))
    
    // *************No one makes any exchanges*************
    // ****************************************************
    table.giveLastCards
    Logic.countPoints(table)
    var a = Logic.determineWinnerOfOneRound(table)
    assert(table.players.forall(_.points == 0))
    assert(a.isEmpty)
      
  }
  "Logic class" should "not allow double card sets" in {
    println("this is the test")
    var rulebook = Logic
    assert(!rulebook.isLegit2(ArrayBuffer(six(),jack(),five(),tableAce()),queen()))
    assert(!rulebook.isLegit2(ArrayBuffer(queen(),tableAce(),two(),three(),ten()),twoSpades))
    //assert(rulebook.isLegit2(ArrayBuffer(jack,tableAce),queen))
    //assert(rulebook.isLegit2(ArrayBuffer(six,five,tableAce),queen))
     //assert(rulebook.isLegit2(ArrayBuffer(queen,tableAce,two),twoSpades))
  }
  

  
  
  
  
 
  /*
   * *******************************************************************************************
   * ****************************Tests For Artificial Intelligence*****************************
   * *******************************************************************************************
   * *******************************************************************************************
   */
  
  
  
 
  
  "AI special crave" should "function as expected" in {
    var player1 = new RobotOpponent("jack",new Hand(), specialCrave())
   
    
    player1.hand.components += (ten(),eight())
    var table = new Table(ArrayBuffer(player1))
    table.cardsOnTable.components += (two().copy(),two().copy(),tableTenD(),two().copy(),two().copy())
    player1.makeMove(table,table.cardsOnTable.components,two())
    
    
    assert(player1.winStack.components.size == 2)
    
  }
  
 
     
     
     "AI spadeparade" should "function as expected" in {
       var player1 = new RobotOpponent("jack",new Hand,spadeParade())
       var a = testSpade()
       a.value = 2
       var b = testSpade()
       b.value = 6
       var c = testSpade()
       c.value = 1
       player1.hand.components += (queen(),nine())
       var table = new Table(ArrayBuffer(player1))
       table.cardsOnTable.components += (a,five().copy(),five().copy(),c,tableAce(),b)
       
       player1.makeMove(table,table.cardsOnTable.components,two())
       assert(player1.winStack.components.contains(a))
       assert(player1.winStack.components.contains(b))
       assert(player1.winStack.components.contains(c))
       assert(player1.winStack.components.size == 4)
     }
     
      "AI clearFear" should "function as expected" in {
    var player1 = new RobotOpponent("jack",new Hand(), specialCrave())
     var twoh = new two()
  
    
    player1.hand.components += (queen(),twoh)                                  //picking the queen off the table would render the table divisible by 15
    var table = new Table(ArrayBuffer(player1))
    table.cardsOnTable.components += (king(),queen(),two())
    player1.makeMove(table,table.cardsOnTable.components,two())
    
    
    assert(player1.winStack.components.contains(twoh))
    assert(player1.winStack.components.size == 2)
    
  }
      
      
     "subsets" should "work in algorithm" in {
       assert(Logic.isLegit2(ArrayBuffer(six()),new Card("6","spades",6)))
       assert(Logic.isLegit2(ArrayBuffer(six(),five(),jack(),three(),seven(),tableAce()),new Card("11","spades",11)))
       assert(!Logic.isLegit2(ArrayBuffer(king(), queen(),three()),new Card("14","spades",14)))
       assert(Logic.isLegit2(ArrayBuffer(king(), queen(),three(),four()),new Card("16","spades",16)))
       assert(!Logic.isLegit2(ArrayBuffer(tableAce(),ten(),seven(),five(),two()),king()))
      
     }
  
  
   "combinations 2" should "work" in {
        var ace1 = new Card("14","spades",1)
        var ace2 = new Card("14","hearts",1)
       
         var a = Logic.combinations2(ArrayBuffer(ace1,ace2,ten(),three(),king(),queen(),three()), 12)
         
         
        
        assert(a.size == 3)
      }
     
  
  
  
  
  
  
 
  

  
  
}

