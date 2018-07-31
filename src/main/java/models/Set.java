package models;

import models.cards.Card;

import java.util.ArrayList;

public class Set {

    private ArrayList<Card> cards;


    public Set(ArrayList<Card> cards) {

        this.cards = cards;
    }


    public ArrayList<Card> getCards() {
        return cards;
    }

}
