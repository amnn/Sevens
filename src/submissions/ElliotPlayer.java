package submissions;

import src.*;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

/**
 * A class to fuck up this pissant tournament with my massive axe of cleaving.
 * 
 * @author NubbyPoohBear
 * 
 */
public class ElliotPlayer implements Player {

	private int players;
	private int[] playerCards;
	private Set<Card> playedCards;
	private Set<Card> myCards;

	private Random random = new Random();

	private int heartLow, heartHigh, diamondLow, diamondHigh, spadeLow,
			spadeHigh, clubLow, clubHigh = 7;

	@Override
	public void initialize(GameConfig config) {
		this.players = config.getPlayerCount();
		this.playerCards = config.getPlayerCardCount();
		this.playedCards = new HashSet<Card>();
		this.heartLow = 7;
		this.heartHigh = 7;
		this.diamondLow = 7;
		this.diamondHigh = 7;
		this.spadeLow = 7;
		this.spadeHigh = 7;
		this.clubLow = 7;
		this.clubHigh = 7;
	}

	@Override
	public void deal(Set<Card> hand) {
		this.myCards = (HashSet<Card>) hand;
	}

	@Override
	public void movePlayed(int playerIndex, Card card) {
		if (card != null) {
			this.playedCards.add(card);
			switch (card.getSuit()) {
			case CLUB:
				this.clubHigh = Math.max(card.getCardIndex() + 1, this.clubHigh);
				this.clubLow = Math.min(card.getCardIndex() - 1, this.clubLow);
				break;
			case DIAMOND:
				this.diamondHigh = Math.max(card.getCardIndex() + 1,
						this.diamondHigh);
				this.diamondLow = Math
						.min(card.getCardIndex() - 1, this.diamondLow);
				break;
			case HEART:
				this.heartHigh = Math.max(card.getCardIndex() + 1, this.heartHigh);
				this.heartLow = Math.min(card.getCardIndex() - 1, this.heartLow);
				break;
			case SPADE:
				this.spadeHigh = Math.max(card.getCardIndex() + 1, this.spadeHigh);
				this.spadeLow = Math.min(card.getCardIndex() - 1, this.spadeLow);
				break;
			default:
				break;

			}
		}
	}

	@Override
	public Card play() {
		Card bestCard = null;
		int bestEnabledMoves = 999;
		if (this.random.nextFloat() < 0.000001) {
			System.out.println("I DO NOT TOLERATE COWARDICE");
		}
		for (Card c : this.myCards) {
			if (isCardValid(c)) {
				int enabledMoves = getEnabledMoves(c);
				if (enabledMoves < bestEnabledMoves) {
					bestEnabledMoves = enabledMoves;
					bestCard = c;
				}
			}
		}
		this.myCards.remove(bestCard);
		return bestCard;
	}

	@Override
	public String getName() {
		return "Elliot";
	}

	private boolean isCardValid(Card card) {
		switch (card.getSuit()) {
		case CLUB:
			if (card.getCardIndex() == this.clubLow
					|| card.getCardIndex() == this.clubHigh) {
				return true;
				
			}
			break;
		case DIAMOND:
			if (card.getCardIndex() == this.diamondLow
					|| card.getCardIndex() == this.diamondHigh) {
				return true;
			}
			break;
		case HEART:
			if (card.getCardIndex() == this.heartLow
					|| card.getCardIndex() == this.heartHigh) {
				return true;
			}
			break;
		case SPADE:
			if (card.getCardIndex() == this.spadeLow
					|| card.getCardIndex() == this.spadeHigh) {
				return true;
			}
			break;
		default:
			break;
		}

		return false;
	}

	private int getEnabledMoves(Card card) {
		int enabledMoves = 0;
		int side = 0;
		if (card.getCardIndex() <= 7) {
			side = -1;
			enabledMoves = card.getCardIndex() - 1;
		}
		if (card.getCardIndex() > 7) {
			side = 1;
			enabledMoves = 13 - card.getCardIndex();
		}
		if (card.getCardIndex() == 7) {
			side = 0;
		}
		switch (card.getSuit()) {
		case CLUB:
			for (Card c : this.myCards) {
				if (card != c) {
					if (c.getSuit() == Suit.CLUB) {
						switch (side) {
						case -1:
							if (c.getCardIndex() < card.getCardIndex()) {
								enabledMoves -= 1;
							}
							break;
						case 0:
							enabledMoves -= 1;
						case 1:
							if (c.getCardIndex() > card.getCardIndex()) {
								enabledMoves -= 1;
							}
						}
					}
				}
			}
		case DIAMOND:
			for (Card c : this.myCards) {
				if (card != c) {
					if (c.getSuit() == Suit.DIAMOND) {
						switch (side) {
						case -1:
							if (c.getCardIndex() < card.getCardIndex()) {
								enabledMoves -= 1;
							}
							break;
						case 0:
							enabledMoves -= 1;
						case 1:
							if (c.getCardIndex() > card.getCardIndex()) {
								enabledMoves -= 1;
							}
						}
					}
				}
			}
		case HEART:
			for (Card c : this.myCards) {
				if (card != c) {
					if (c.getSuit() == Suit.HEART) {
						switch (side) {
						case -1:
							if (c.getCardIndex() < card.getCardIndex()) {
								enabledMoves -= 1;
							}
							break;
						case 0:
							enabledMoves -= 1;
						case 1:
							if (c.getCardIndex() > card.getCardIndex()) {
								enabledMoves -= 1;
							}
						}
					}
				}
			}
		case SPADE:
			for (Card c : this.myCards) {
				if (card != c) {
					if (c.getSuit() == Suit.SPADE) {
						switch (side) {
						case -1:
							if (c.getCardIndex() < card.getCardIndex()) {
								enabledMoves -= 1;
							}
							break;
						case 0:
							enabledMoves -= 1;
						case 1:
							if (c.getCardIndex() > card.getCardIndex()) {
								enabledMoves -= 1;
							}
						}
					}
				}
			}
		default:
			break;

		}
		return enabledMoves;
	}

}


