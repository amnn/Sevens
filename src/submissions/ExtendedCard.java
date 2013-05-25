package submissions;

import src.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author James
 */
public class ExtendedCard {
    public Card _card;
    public int _playerIndex = -1;
    public int _playPosition = -1;
    
    public ExtendedCard(Card card) {
        _card = card;
    }
    
    public ExtendedCard(Card card, int playPosition, int playerIndex) {
        _card = card;
        _playPosition = playPosition;
        _playerIndex = playerIndex;
    }
    
    public double PlayDesire;
    public int _distanceFromBeingPlayed;
    public boolean _isFullySafe;    //we have the next card in the sequence or this is an end card
    public boolean _isPartiallySafe; //we have another blocker on this line.
    public boolean _isBlocker; //we get no benefit by playing this card and should hold onto it
    public int _blockerStrength;
    public int _cardsOpenedFromPlaying;
    public int _totalHelpfulDistanceGainedByPlaying;
    public int _nextBlockerCount;
    
    public boolean isPlayedBefore(ExtendedCard card) {
       if(card._card.getSuit() == _card.getSuit())
       {
           if(_card.getCardIndex() <= 7 && _card.getCardIndex() > card._card.getCardIndex())
               return true;
           if(_card.getCardIndex() >= 7 && _card.getCardIndex() < card._card.getCardIndex())
               return true;
       }
       return false;
    }
    
    public boolean isPlayedAfter(ExtendedCard card) {
       if(card._card.getSuit() == _card.getSuit())
       {
           if( _card.getCardIndex() >= 7 && card._card.getCardIndex() >= 7 && card._card.getCardIndex() < _card.getCardIndex()  )
               return true;
           if(_card.getCardIndex()  <= 7 && card._card.getCardIndex() <= 7 && card._card.getCardIndex() > _card.getCardIndex() )
               return true;
       }
       return false;
    }
}
