package kasino.io
import java.io._
import scala.io.Source
import java.util.Scanner
import java.io.File
import ui._
import kasinocode._
import scala.collection.mutable.ArrayBuffer
import java.io.IOException
import java.io.Reader


//The object which reads "state.txt" and interprets it.
object KasinoReader {

  
  
  def loadGame(string:String): Table = {  
    
    
    val input: Reader = new StringReader(string)                
   
  
    var players = ArrayBuffer[Player]()                //A buffer to store players
    var table = new Table(players)                     //The new table which is to be created
    
   
    
    def readAPlayer: Unit = {                            //The method to extract and create one player
      
      var chunkHeader = new Array[Char](5)
      var dealerOrNot = new Array[Char](1)
      var firstOrNot  = new Array[Char](1)
      var pointsArray = new Array[Char](2)
      
      Helpers.readFully(chunkHeader, input)                            
      var name = Helpers.extractChunkName(chunkHeader)                //Get the name of the chunk
     
      name match {
      case "PLR" => {                                                   //"PLR" refers to a player
        var size = Helpers.extractChunkSize(chunkHeader)  
        var playerBuffer = new Array[Char](size)                        //Get a buffer ready for the name
        
        Helpers.readFully(playerBuffer,input)                            //read the name
        Helpers.readFully(dealerOrNot,input)                             //Read binary for dealer
        Helpers.readFully(firstOrNot,input)                              //Read binary for first or not
        Helpers.readFully(pointsArray,input)                             //Read double-digit points
       
        /*
         * Add the points, name and qualities of the player by creating a player and adding it to the buffer.
         */
        
        var name = playerBuffer.mkString                                  
        var dealer = dealerOrNot.mkString.toInt
        var first = firstOrNot.mkString.toInt
        var points = pointsArray.mkString.toInt
       
        
        var player = new Player(name,new Hand)                          
        
        player.addPoints(points)
        
        player.points = 0
         
         
        table.addPlayer(player)
       
        if(dealer == 1) {
          table.dealer = Some(player)
          
        }
        if(first == 1) {
          table.firstPlayer = Some(player)
        }
        readAPlayer                                                                //Recursively calls the method again for the next chunk
        
      }
      
      case "ROB" => {                                                   //"ROB" refers to a player
        var size = Helpers.extractChunkSize(chunkHeader)
        var playerBuffer = new Array[Char](size)
        /*
         * Basically does the same as above
         */
        Helpers.readFully(playerBuffer,input)
        Helpers.readFully(dealerOrNot,input)
        Helpers.readFully(firstOrNot,input)
        Helpers.readFully(pointsArray,input)
        var name = playerBuffer.mkString
        var dealer = dealerOrNot.mkString.toInt
        var first = firstOrNot.mkString.toInt
        var points = pointsArray.mkString.toInt
        
        var player = new RobotOpponent(name,new Hand,Helpers.matchThisGuy(name))        //Has to add the distinctive personality to each robot
        player.addPoints(points)
        player.points = 0
        table.addPlayer(player)
        if(dealer == 1) {
          table.dealer = Some(player)
         
        }
         if(first == 1) {                                  //Should never be a robot, since a player always enters a room first, but prevents the program from crashing if file is broken. 
          table.firstPlayer = Some(player)
        }
        readAPlayer                                        //Recursively calls the method again
      }
      
      case "END" => {                                      //Return out of the recursion if reached end of file.
        return
        
      }
      case _ => throw new CorruptedKasinoFileException("Unexpect. name of chunk.")
      
      }
    }
    
    
    
    
    readAPlayer                                          //Start reading
      
   table                                                 //Return modified table.
    
  }
  
  object Helpers {
    
   /*
    * Maps each robot's name to its according Personality so that 
    * only the name of the robot has to be read by the reader.
    */
   
   
   val matchThisGuy = Map[String,Personality](("Jack" -> spadeParade())
        ,("Mary" -> specialCrave() )
        ,("Lucian" -> greedy() )
        ,("Jessica" -> clearFear() )
        ,("Bill" -> spadeParade() )
        ,("Liz" -> specialCrave() )
        ,("Tom" -> greedy() )
    
     )
     
    /** Helpers from o2 Studio Chess (game.io ChunkIo.scala)
     *  
     * Given a chunk header (an array of 5 chars) will return the size of this
     * chunks data.
     *
     * @param chunkHeader a chunk header to process
     * @return the size of this chunks data
     */
    def extractChunkSize(chunkHeader: Array[Char]): Int = {

      // Subtracting the ascii value of the character 0 from
      // a character containing a number will return the
      // number itself.
      10 * (chunkHeader(3) - '0') + (chunkHeader(4) - '0')
    }

    /**
     * Given a chunk header (an array of 5 chars) will return the name of this
     * chunk as a 3-letter String.
     *
     * @param chunkHeader a chunk header to process
     * @return the name of this chunk
     */
    def extractChunkName(chunkHeader: Array[Char]): String = {
      chunkHeader.take(3).mkString
    }

    /**
     * The read-method of the Reader class will occasionally read only part of
     * the characters that were requested. This method will repeatedly call read
     * to completely fill the given buffer. The size of the buffer tells the
     * algorithm how many bytes should be read.
     *
     * @param result the result of the reading will be stored in this array
     * @param input a character stream to read from
     * @throws IOException
     * @throws CorruptedChessFileException if end of file is reached before
     *    buffer is filled
     */
    def readFully(result: Array[Char], input: Reader) = {
      var cursor = 0
      while (cursor != result.length) {
        var numCharactersRead = input.read(result, cursor, result.length - cursor)

        // If the file end is reached before the buffer is filled
        // an exception is thrown.
        if (numCharactersRead == -1) {
          throw new CorruptedKasinoFileException("Unexpected end of file.")
        }
        cursor += numCharactersRead
      }
    }
  }
  
  
  
 
}

  
