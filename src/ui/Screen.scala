package ui
import scala.swing._
import scala.collection.mutable.ArrayBuffer
import kasinocode._
import java.awt.Color
import javax.swing.UIManager
import javax.swing.ImageIcon
import javax.sound.sampled._




/*
 * *****************************************************************************
 * *****************************************************************************
 * *****************************************************************************
 * ***************************************************************************** *****************************************************************************
 * *****************************************************************************
 * @copyright Nicholas Colb 2017
 * THE DEFINED NULLPANEL OBJECT 'SCREEN', WHICH ALLOWS ELEMENTS TO BE ADDED via (x,y) co-ordinates. 
 * The contents of the swing app is this object. 
 * This object is designeds to follow the course of the game by showing various visual elements, such as cards and points, to the user. 
 * The object works hand-in-hand with package kasinocode, as it basically gets orders from kasinocode for what is to be shown on the screen. 
 * The object always has its own variable Table.scala table. 
 * *****************************************************************************
 * *****************************************************************************
 * *****************************************************************************
 * *****************************************************************************
 *
 * 
 */


object Screen extends Panel {
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName) 

	var table = new kasinocode.Table(ArrayBuffer())              //The Table.scala variable
	var handToButtons = Map[Hand, ArrayBuffer[CardButton]]()     //A map to keep track of each Hand and its connection to visual card elements
	var gameOn = false                                           //A variable that changes in between rounds to false, when the game has physically stopped
	var wantsSound = true                                        //A variable to help keep track of whether the user wants the sound on or off

	val dealerImage = new ImageIcon("src/files/dealer.png")      //The image which indicates who's the dealer
	val startingImage = new ImageIcon("src/files/kassino.png")   //The image which is shown at the top of the screen

	val mainColor = Color.blue.darker().darker().darker()        //The "main" color of the game, which can be used by other classes      
	background = mainColor

	peer.setLayout(null)                                         //Initiate the layout of the Panel.peer to null




	/* *****************************************
	 * *****************************************
	 * *****************************************
	 * *****************************************
	 * Some basic methods for adding and removing components and players.
	 * Also has methods for handling audio streams.
	 * *****************************************
	 * *****************************************
	 * *****************************************
	 */

	def add(comp: Component, x: Int, y: Int): Unit = {
		val p = comp.peer
				p.setLocation(x, y)
				p.setSize(p.getPreferredSize)
				peer.add(p)
	}

	def addImage(image: ImageIcon ): Label = {
		new Label {
			icon = image
		}
	}


	def remove(comp: Component) = {
		val p = comp.peer
				peer.remove(p)
	}

	def addPlayer():Unit =  {
			val r = Dialog.showInput(contents.head, "Type your nickname with a maximum of 10 characters.", initial="")
					r match {
					case Some(s) => {
						if(s.length < 11) {
							table.addPlayer(new Player(s,new Hand)  )

							if(Screen.wantsSound) this.playAddSound

							if(table.players.size == 4) {
								remove(addPlayerButton)
								remove(addRobotButton)

							}
						} else addPlayer()
					}
					case None => 

			}
	}

	def playCardSound = {

			val audioIn = AudioSystem.getAudioInputStream(play.audioUrl)
					var clip = AudioSystem.getClip
					clip.open(audioIn)
					clip.start 

	}
	def playAddSound = {
			val audioIn = AudioSystem.getAudioInputStream(play.audioUrl4)
					var clip = AudioSystem.getClip
					clip.open(audioIn)
					clip.start 
	}
	
	def playKaChing = {
	  val audioIn = AudioSystem.getAudioInputStream(play.audioUrl5)
	  var clip = AudioSystem.getClip
	  clip.open(audioIn)
	  clip.start
	}



	/******************************************
	 * ***********************************************************
	 * *******************************************************************************
	 * *********************************************************************************************
	 * Methods for painting elements onto screen in relation to the state of the game. All "non-constant" elements, 
	 * eg. cards and scoreboards are managed here. Buttons and other "constant" elements are managed in another section
	 * of the code.
	 * *********************************************************************************************
	 * *******************************************************************************
	 * ***********************************************************
  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	 * 
	 * 
	 */

	private def moveOneCard(card:Card):Unit = {                          //Moves a card from the players stack to the table
			require(selectionHelper.selectedCardButtons.size == 1)         //Only one card should be selected at this time
			this.handToButtons(table.cardsOnTable).foreach(this.remove(_))    //At this point tablecards will already be updated, so we are removing too many cards. Does not, however throw errors.
			card.changeSpecialCardToTable                                      //Change aces to 1's etc.
			table.cardsOnTable.addCard(card)                                   //Add the card to the tablecards
			removeSelectionImgs                                                  //Clear the selectionhelper - no cards need to be selected anymore
			drawTableCards                                                       //redraw the tablecards


	}

	private def removeSelectionImgs:Unit = {                                 //A method for removing any selected cards, used to follow what the player has selected.                              
				//The object itself is located in Buttons.scala
				selectionHelper.selectedCardButtons.foreach(this.remove(_))          
				selectionHelper.clear
			}


//A method for fetching co-ordinates for drawing results @par index
	// @param index -> which index is the player at
			private def findCoordsForResults(index:Int):(Int,Int) = {            
				index match {
				case 0 => (50,100)
				case 1 => (50,250)
				case 2=> (50,400)
				case 3=> (50,550)
				case _ => throw new Exception("You can't have that many players")
				}
			}
			
          //Draws one hand onto the screen is called by drawHands and drawTableCards
			private def drawHand( x:Int,y:Int,hand: Hand,visible:Boolean,dualClick:Boolean,stack:Boolean):Unit = {


				var index = 0
				var increment = 0

				if(stack) increment = 30 else increment = 80
			  var images = ArrayBuffer[CardButton]()
				if(hand.components.isEmpty) {
				  var emptyImg = CardButton(new Card("ifNoCards","",0))
					 this.add(emptyImg,x,y)
						images += emptyImg
				}
				
				for(current <- hand.components) {
					var card = {
					  if(visible) CardButton(current)
						  else new FaceDownButton()
					}
					  images.foreach(this.remove(_))
					    card.coords = (x+ index , y)
					      this.add(card,x + index, y)
					        if(dualClick) card.allowDualClick = true
					           images.reverse.foreach( a => this.add(a,x+ index - increment - images.reverse.indexOf(a) * increment,y))
					              images += card
					                index += increment
				}
				handToButtons = handToButtons + (hand -> images)
			}

			
			

				def drawHands() = {             //Physically draws the cards onto the table
					this.handToButtons = Map()
							var newStrings = ArrayBuffer[(Player,playerString)]()

							var player1 = table.firstPlayer.get
							var player1Coords = (90,705)
							var dealerText = ""
							if(player1.isDealer(table)) add(addImage(dealerImage),35,725)
							add(playerString(dealerText + player1.name),player1Coords._1+ 20, player1Coords._2 - 40)
							drawHand(player1Coords._1,player1Coords._2,player1.hand,true,false,false)



							var player2 = table.players(0)
							if(player1 == player2) player2 = table.players.last
							var visible = true
							var player2Coords = (90,160) 
							if(player2.isInstanceOf[RobotOpponent]) visible = false
							if(player2.isDealer(table)) add(addImage(dealerImage),35,180)
							add(playerString(player2.name),player2Coords._1+ 20, player2Coords._2 - 40)
							drawHand(player2Coords._1,player2Coords._2,player2.hand,visible,false,false)


							drawTableCards






				}
				
				
				//Table cards require distinct method due to cards possibly stacking and extending over the screen. Not enough to just place one after the other.

			def drawTableCards:Unit = {

					var threeBuffers:ArrayBuffer[ArrayBuffer[Card]] = ArrayBuffer()
							var tableSize = table.cardsOnTable.components.size
							if(tableSize >17) {
								var splitted = table.cardsOnTable.components.splitAt(17)
										threeBuffers += splitted._1
										if(tableSize > 34) {
											var reSplit = splitted._2.splitAt(17)
													threeBuffers += (reSplit._1,reSplit._2)
										} else threeBuffers += splitted._2


							} else threeBuffers += table.cardsOnTable.components

							var images = ArrayBuffer[CardButton]()
							var index = 0
							var increment = 80
							var i = 0
							for(currentBuffer <- threeBuffers) {
								var y = if(i == 0) 420 else if(i == 1) 540 else 300
										index = 0
										for(currentCard <- currentBuffer) {

											var card = CardButton(currentCard)
													card.coords = (90 + index, y)
													add(card,90 + index, y) 
													card.allowDualClick = true
													images += card

													index += increment
										}
								i+=1
							}

				handToButtons = handToButtons + (table.cardsOnTable -> images)

				}



				



				//At the end of a round, shows the results
					def showResults() =  {

									var reached16 = false


											Screen.peer.removeAll
											drawResult()
											var result = Logic.countPoints(table)
											var winners = Logic.determineWinnerOfOneRound(table)
											if(table.gameHasEnded) reached16 = true
											if(winners.size == 1) winnerString.update("The winner of this round was: " + winners.head.name + " with " + winners.head.points + " points.")
											else winnerString.update(winners.map(_.name).mkString(" and ") + " tied this round!")

											if(result._1.size == 1) mostCardsString.update(result._1.head.name + " had most cards!")
											else mostCardsString.update(result._1.map(_.name).mkString(" and ") + " tied in having most cards!")

											if(result._2.size == 1) mostSpadesString.update(result._2.head.name + " had most Spades!")
											else mostSpadesString.update(result._2.map(_.name).mkString(" and ") + " tied in having most spades!")

											if(table.tenDiamondsHolder.isDefined && table.twoSpadesHolder.isDefined) {
												tenDiamondsString.update(table.tenDiamondsHolder.get.name + " had the ten of Diamonds")
												twoSpadesString.update(table.twoSpadesHolder.get.name + " had the two of Spades!")
											}

									if(reached16) {
										var ultimWinners = Logic.determineUltimateWinner(table)
												if(ultimWinners.size == 1){
													ultimateWinnerString.update( winners.head.name + " reached 16 points and won the game!")
													if(wantsSound) {
														val audioIn = AudioSystem.getAudioInputStream(play.audioUrl3)
																var clip = AudioSystem.getClip
																clip.open(audioIn)
																clip.start 
													}
												}
												else ultimateWinnerString.update(winners.map(_.name).mkString(" and ") + " reached 16 points and tied the entire game!")
												add(ultimateWinnerString,650,820)
												add(finishButton,1300,900)
									}
									add(mostCardsString,50,680)
									add(mostSpadesString,50,720)
									add(tenDiamondsString,50,760)
									add(twoSpadesString,50,800)
									add(winnerString,50,840)
									pointsString.updateContent
									opponentString.updateContent
									Screen.add(opponentString,50,880)
									Screen.add(pointsString,50,920)
									if(!reached16 ) add(continueButton,1300,900)
							}
				
				
				def drawResult() = {             //Physically draws the cards onto the table when showing winStacks.
					this.handToButtons = Map()
							var newStrings = ArrayBuffer[(Player,playerString)]()
							for(current <- table.players.indices) {
								var player = table.players(current)
										var hand = player.winStack
										var coords = findCoordsForResults(current)
										drawHand(coords._1,coords._2,hand,true,false,true)
										var dealerText = ""
										if(player.isDealer(table)) dealerText = "Dealer: "
										add(playerString(dealerText + player.name + ", " + player.mokkiCount + " clears"),coords._1+ 20, coords._2 - 40)

							}


				}




				/******************************************
				 * ***********************************************************
				 * *******************************************************************************
				 * *********************************************************************************************
				 * 
				 * Methods for handling the information of the state of the game and calling the methods above which
				 * manage what is shown on the user interface.
				 * 
				 * *********************************************************************************************
				 * *******************************************************************************
				 * **********************************************************
				 * 
				 */
				private def rotateTurns() = table.changeTurn          //Rotates turns 


				  /*
				   * The main method for making a move. Whoever is currently up for making a turn is either a player or a robot. 
				   * 
				   * If it is a plyer, 
				   * A loop will start for checking if the player has clicked the "make move" button. When clicked, the selected cards are checked and the move is either made or
				   * the loop will start checking again, and the player will have to make a legit move. The loop is implemented by the recursive method "readyCheck"
				   * 
				   * If it is a robot, 
				   * the robot will make his/her turn and wait two seconds before ending the turn. 
				   */

						def makeMove():Unit = {
						  try {
								var player = table.players(0)

								if(!player.isInstanceOf[RobotOpponent]) {
									this.handToButtons(player.hand).foreach(_.clickable = true)
									this.handToButtons(this.table.cardsOnTable).foreach(_.clickable = true)
									commandString.update("Select your card(s) and click 'make move'.")
									makeMoveButton.enabled = true
									showResultsButton.enabled = true

									def readyCheck: Unit = {
										
										if(makeMoveButton.isPressed) {
											makeMoveButton.isPressed = false
													var found = false
													for(current <- selectionHelper.selectedCardButtons) {

														if(player.hasCard(current.reference)){
															found = true
																	if(selectionHelper.selectedCardButtons.size >1) {
																		       //Determines which card is the one in the player's hand
																		var toGet = selectionHelper.selectedCardButtons.map(_.reference).filter(_ != current.reference)
																		
																		if(player.makeMove(table,toGet,current.reference)){
																			commandString.update("You made your move!")
																			removeSelectionImgs
																			this.endTurn(player)
																			return

																		} else {
																			commandString.update("That is an illegal move!")
																			selectionHelper.clear
																			readyCheck
																			return
																		}
																	} else {
																		player.drawCard(current.reference)
																		commandString.update("You made your move!")
																		moveOneCard(current.reference)
																		this.endTurn(player)
																		return
																	}
														}
													}
											commandString.update("Please select one of your own cards!")
											selectionHelper.clear


										}

										TimerHelp(100,false){ if(gameOn) readyCheck}

									}
									readyCheck
								} else {
									object fakeCard extends Card("test","test",99) 

									var robot = player.asInstanceOf[RobotOpponent]
											commandString.update(robot.name + " is thinking....")
											robot.makeMove(table, table.cardsOnTable.components,fakeCard)


											TimerHelp(2000,false){
										this.endTurn(robot) 
									}
								}
						  } catch {
						    case e: Exception => //Main menu pressing may cause an exception to be thrown
						  }
						}
				
				
				
						//A method for ending a turn
						private def endTurn(player:Player):Unit = {
						  if(gameOn) {              //Helps avoid exeptions when player presses main menu in the middle of a robot's turn
						  if(!player.isInstanceOf[RobotOpponent]){
									this.handToButtons(player.hand).foreach(_.clickable = false)
									this.handToButtons(this.table.cardsOnTable).foreach(_.clickable = false)
								}
								player.takeCard(this.table.deck.oneMore)
								this.rotateTurns()
								this.refreshHands()
								if(wantsSound) this.playCardSound
								if(table.cardsOnTable.components.isEmpty) playKaChing
								if(table.hasEnded) endRound()
								else makeMove()

						  }
						}

						
						//A method for ending a round
						private def endRound():Unit = {
								table.giveLastCards
								refreshHands
								commandString.update("The game ended.")
								add(resultsButton,1000,800)
								gameOn = false

						}




						//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
						//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
						//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
						//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
						//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

						//Basic methods for taking care of "constant" visual elements and 
						//updating the screen. 

						//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
						//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
						//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
						//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
						//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>




						def restart():Unit = {
								add(addImage(startingImage),600,10)
								add(musicButton,1300,20)
								add(mainMenuButton,10,50)
								add(startButton,640,600)
								add(makeMoveButton, 0,850)
								makeMoveButton.enabled = false
								add(showResultsButton,730,850)
								showResultsButton.enabled = false
								if(gameOn) add(commandString,750,220)
								turnString.updateContent
								if(gameOn) add(turnString,750,170)
								if(!gameOn &&table.players.size > 1) add(saveGameButton, 1300,150) 
								namesAndPoints.redraw
								selectionHelper.clear


						}
						def gameOnClean():Unit = {
								remove(startButton)
								remove(addPlayerButton)
								remove(addRobotButton)
						}


						def start():Unit = {
								gameOn = true

										if(table.dealer.isDefined) {
											table.players = table.players.drop(table.players.indexOf(table.dealer.get)) ++ table.players.take(table.players.indexOf(table.dealer.get) )
													table.changeTurn
													table.dealer = Some(table.players.head)
													table.changeTurn

													while(table.players(1) != table.dealer.get){
														table.changeTurn
													}


										} else {
											table.dealer = Some(table.players(1))
													

										}



								restart()
								gameOnClean()
								table.dealStart
								drawHands()
								if(wantsSound) playCardSound
								makeMove


						}
						def refreshHands():Unit = {
								Screen.peer.removeAll()
								this.handToButtons.values.flatten.foreach(this.remove(_))
								restart
								gameOnClean
								drawHands()
						}
						
						def refreshScreen():Unit = {
								peer.removeAll
								restart
								drawHands()

						}

						def drawStartingScreen():Unit  = {
								peer.removeAll
								add(addImage(startingImage),600,10)
								add(startGameButton,690,300)
								add(loadGameButton,660,400)
								add(instructionsButton,655,500)
								add(musicButton,1300,20)
								add(copyRightString, 50,900)

						}
						
						/* The method which is called when a player first tries to enter a room. A room is drawn only after a player has entered his/her name and the player
						 * object is created. Implemented by variable "ready" and recursive method "check" (called every 100msecond).
						 * 
						 */
						
						def openRoom(): Unit = {
								
								var ready = false
										def addFirstPlayer():Unit = {
												val r = Dialog.showInput(contents(1), "Type your nickname with a maximum of 10 characters.", initial="")
														r match {
														case Some(s) => {
															if(s.length < 11) {
																table.addPlayer(new Player(s,new Hand)  )
																ready= true
															} else  addFirstPlayer()
														}
														case None => 

												}
										}

										def check: Unit = {
												if(ready) {
													table.firstPlayer = Some(table.players.head)
															remove(startGameButton)
															remove(loadGameButton)
															remove(instructionsButton)
															remove(copyRightString)
															addPlayerButton.reset
															addRobotButton.reset
															add(addPlayerButton,525,500)
															add(addRobotButton,825,500)
															restart()
												}
												else {
													TimerHelp(100,false) {
														check
													}
												}
										}

										addFirstPlayer()
										check
						}







}