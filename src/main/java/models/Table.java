package models;

import com.sun.javaws.exceptions.InvalidArgumentException;
import models.cards.Basic;
import models.cards.Card;
import models.cards.Joker;
import models.cards.Suit;

import java.util.ArrayList;
import java.util.Collections;

public class Table {

    /********************************
     ******** PRIVATES **************
     ********************************/
    private ArrayList<Card> deck;
    private ArrayList<Set> sets;

    /**
     * cards that have been taken from a set that have to be played
     */
    private ArrayList<Card> cardsInPlay;

    /**
     * CONSTRUCTOR
     */
    public Table() {
        deck = new ArrayList<>();
        sets = new ArrayList<>();
        cardsInPlay = new ArrayList<>();
        initMachiavelliDeck();
    }


    /**
     * gets deck
     *
     * @return
     */
    public ArrayList<Card> getDeck() {
        return deck;
    }

    /**
     * gets sets
     *
     * @return
     */
    public ArrayList<Set> getSets() {
        return sets;
    }

    /**
     * gets cardsInPlay
     *
     * @return
     */
    public ArrayList<Card> getCardsInPlay() {
        return cardsInPlay;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public void setSets(ArrayList<Set> sets) {
        this.sets = sets;
    }

    public void setCardsInPlay(ArrayList<Card> cardsInPlay) {
        this.cardsInPlay = cardsInPlay;
    }

    /**
     * creates a deck from two standard decks
     */
    private void initMachiavelliDeck() {
        //2 heart of 2s
        //2 spade of 2s
        //2 diamon of 2s
        //and so on
        deck = new ArrayList<>();
        deck.addAll(generateStandardDeck());
        deck.addAll(generateStandardDeck());
        shuffleDeck();
    }

    /**
     * generates a standard deck
     *
     * @return
     */
    private ArrayList<Card> generateStandardDeck() {
        ArrayList<Card> standardDeck = new ArrayList<>();
        int cardID = 0;
        for (int rank = 1; rank <= 14; rank++) {
            try {
                standardDeck.add(new Basic(Suit.CLUBS, rank, cardID));
                cardID++;
                standardDeck.add(new Basic(Suit.DIAMONDS, rank, cardID));
                cardID++;
                standardDeck.add(new Basic(Suit.HEARTS, rank, cardID));
                cardID++;
                standardDeck.add(new Basic(Suit.SPADES, rank, cardID));
                cardID++;
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }


        try {
            int rank = 15;
            standardDeck.add(new Joker(Suit.JOKER, rank, cardID));
            cardID++;
            standardDeck.add(new Joker(Suit.JOKER, rank, cardID));
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        return standardDeck;
    }

    /**
     * Shuffles the deck
     */
    public void shuffleDeck() {
        Collections.shuffle(deck);
    }


    public boolean decksDifferent(Table table1, Table tabl2) {
        for (int j = 0; j < 104; j++) {

            if (!table1.getCardByIndex(j).equals(tabl2.getCardByIndex(j))) return true;

        }

        return false;
    }

    public Card getCardByIndex(int j) {
        return deck.get(j);
    }


}
