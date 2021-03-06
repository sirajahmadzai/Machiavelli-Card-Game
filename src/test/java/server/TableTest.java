package server;

import org.junit.Test;
import server.models.CardSet;
import server.models.Table;
import server.models.cards.Basic;
import server.models.cards.Card;
import server.models.cards.Suit;

import java.util.ArrayList;
import java.util.Stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TableTest {

    @Test
    public void testConstructor() {
        final Stack<Card> INITIAL_DECK = new Stack<>();
        final ArrayList<CardSet> INITIAL_Card_SETS = new ArrayList<>();

        final Table table = new Table();
        table.setDeck(INITIAL_DECK);
        table.setCardSets(INITIAL_Card_SETS);

        assertEquals(INITIAL_DECK, table.getDeck());
        assertEquals(INITIAL_Card_SETS, table.getCardSets());
    }

    @Test
    public void setDeck() {
        final Stack<Card> DECK = new Stack<>();

        final Suit SUIT = Suit.SPADES;
        final int RANK = 4;
        final int CARD_ID = 1;

        try {
            final Card CARD = new Basic(SUIT, RANK, CARD_ID);

            DECK.add(CARD);

            final Table TABLE = new Table();

            TABLE.setDeck(DECK);

            assertEquals("getDeck() != DECK", DECK, TABLE.getDeck());
        } catch (IllegalArgumentException e) {
            fail("Unexpected Exception!");
        }
    }

    @Test
    public void setCardSets() {
        final Suit SUIT = Suit.SPADES;
        final int RANK = 4;
        final int CARD_ID = 1;

        try {
            final Card CARD = new Basic(SUIT, RANK, CARD_ID);
            final ArrayList<Card> LIST_OF_CARDS = new ArrayList<>();

            LIST_OF_CARDS.add(CARD);

            final CardSet CARD_SET = new CardSet(LIST_OF_CARDS);

            final ArrayList<CardSet> LIST_OF_CARD_SETS = new ArrayList<>();

            LIST_OF_CARD_SETS.add(CARD_SET);

            final Table TABLE = new Table();

            TABLE.setCardSets(LIST_OF_CARD_SETS);

            assertEquals("getCardSets() != LIST_OF_CARD_SETS", LIST_OF_CARD_SETS, TABLE.getCardSets());
        } catch (IllegalArgumentException e) {
            fail("Unexpected Exception");
        }
    }
}