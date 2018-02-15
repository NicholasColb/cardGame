package test

import kasinocode._
import scala.collection.mutable.ArrayBuffer
//import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.Assertions._
import kasino.io._
import java.io._
import scala.io.Source
import java.util.Scanner
import java.io.File
import ui._


//Some simple tests to test the saving and loading of the game

class ioTest extends FlatSpec with Matchers {
  
  
 
  case class player(nameh:String) extends Player(nameh,new Hand)
  case class robot(nameh:String) extends RobotOpponent(nameh,new Hand,greedy())
  
  
  
  "Io" should "correctly load a game" in {
    new PrintWriter("state.txt") { write("PLR06jaakko0005PLR04asta1108ROB05jorma0009ROB14klausVonHerzen0015END00"); close }
    println(Source.fromFile("state.txt").getLines.mkString)
    var newTable = KasinoReader.loadGame(Source.fromFile("state.txt").getLines.mkString)
    
    
    assert(newTable.players.size == 4)
    assert(newTable.dealer.get.name == "asta")
    assert(newTable.firstPlayer.get.name == "asta")
    
  
  }
  
  "Io" should "correctly save a game" in {
   var jaakko = player("jaakko")
   var asta = player("asta")
   var jorma = robot("jorma")
   var klausVonHerzen = robot("klausVonHerzen")
   
   jaakko.addPoints(0)
   asta.addPoints(8)
   jorma.addPoints(9)
   klausVonHerzen.addPoints(15)
   var table = new Table(ArrayBuffer(jaakko,asta,jorma,klausVonHerzen))
   table.firstPlayer = Some(jaakko)
   table.dealer = Some(asta)
   
   
   var exampleString = "PLR06jaakko0100PLR04asta1008ROB05jorma0009ROB14klausVonHerzen0015END00"
   
   assert(KasinoWriter.saveGame(table) == exampleString)
  
  }
   
  
  
  
 
}
   
   
  
  
  
  
  
  
  
  
  
 

  
  
