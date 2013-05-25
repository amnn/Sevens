package src;
public class Card {
    private final Suit suit;
    private final int cardIndex;
    
    public Card(int index) {
        assert(1 <= index && index < 53);
        
        this.suit = Suit.values()[(index - 1) / 13];
        this.cardIndex = ((index - 1) % 13) + 1;
    }

    public Card(Suit suit, int cardIndex) {
        if (1 <= cardIndex && cardIndex < 14) {
            this.suit = suit;
            this.cardIndex = cardIndex;
        } else
            throw new IllegalArgumentException();
    }
    
    public Suit getSuit() {
        return suit;
    }
    
    public int getCardIndex() {
        return cardIndex;
    }

    @Override
    public boolean equals( Object other ) {
      if( other instanceof Card ) {
        Card c = (Card) other;
        return c.suit == suit && c.cardIndex == cardIndex;
      }

      return false;
    }

    @Override
    public int hashCode() { return 13 * suit.ordinal() + cardIndex; }

    @Override
    public String toString() {
       return cardToString(cardIndex)
               + " of "
               + suit.toString().substring(0, 1).toUpperCase()
               + suit.toString().substring(1).toLowerCase()
               + "s";
    }

    private String cardToString(int card) {
       switch (card) {
       case 1:  return "Ace";
       case 11: return "Jack";
       case 12: return "Queen";
       case 13: return "King";
       default: return Integer.toString(card);
       }
    }
}
