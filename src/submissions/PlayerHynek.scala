package submissions;

import src._;
import scala.collection.JavaConversions.asScalaSet

class PlayerHynek extends Player {
  private val name: String = "Hynek"
  private var hand: Set[CardInHand] = null

  def initialize(config: GameConfig) {
  }

  def deal(h: java.util.Set[Card]) {
    val allmycards = h.toSet[Card] map (new MyCard(_))
    hand = allmycards map (new CardInHand(_, allmycards))
  }

  def movePlayed(index: Int, card: Card) {
    if (card != null) {
      val theCard = new MyCard(card)
      for (c <- hand) c.anotherCardPlayed(theCard)
    }
  }

  def play: Card = {
    val canPlay = hand filter (_.canPlay)
    if (canPlay.isEmpty)
      null
    else
      canPlay maxBy (_.score) play
  }

  def getName: String = name

  def getScore(leftInTower: Int, blocking: Int, firstWaiting: Int, waiting: Int, isSeven: Boolean): Int = {
    (-1 * waiting + 10000 * leftInTower + 100 * firstWaiting)
  }

  private class MyCard(val card: Card) {
    val index = card.getCardIndex()
    val suit = card.getSuit()
    val afterMe = 6 - Math.abs(index - 7)
    def isInSameTower(c: MyCard) = suit == c.suit && (index - 7) * (c.index - 7) >= 0
    def isAfterMe(c: MyCard) = isInSameTower(c) && afterMe > c.afterMe
    def isJustBeforeMe(c: MyCard) = isInSameTower(c) && afterMe == c.afterMe - 1
  }

  private class CardInHand(val card: MyCard, allmycards: Iterable[MyCard]) {
    val isSeven = card.index == 7
    var canGo = isSeven
    var played = false

    val leftInTower = allmycards filter (card.isAfterMe(_))
    val blocking = if (leftInTower.isEmpty) 0 else (leftInTower map (_.afterMe)).min
    val firstWaiting = if (leftInTower.isEmpty) 0 else card.afterMe - (leftInTower map (_.afterMe)).max
    val waiting = card.afterMe - blocking - leftInTower.size

    def anotherCardPlayed(c: MyCard) {
      if (card.isJustBeforeMe(c)) canGo = true
    }

    def canPlay: Boolean = canGo

    def play: Card = {
      canGo = false
      played = true
      card.card
    }

    def score: Int = getScore(leftInTower.size, blocking, firstWaiting, waiting, isSeven)
  }
}
