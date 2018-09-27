package client.views;

import client.CardEvent;
import client.GameSeats;
import client.ViewHelper;
import client.views.components.CardSetView;
import client.views.components.CardView;
import client.views.components.PlayArea;
import client.views.components.Player;
import commands.Command;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import server.models.CardSet;
import server.models.cards.Card;
import server.models.cards.HiddenCard;

import java.util.Map;

public class GameView extends View{
    /*************************************************************
     ************************FXML INJECTIONS**********************
     *************************************************************/
    @FXML // fx:id="board"
    private BorderPane board; // Value injected by FXMLLoader

    @FXML
    private ImageView deckImageView;

    @FXML
    private Text messageBox;

    @FXML
    private FlowPane setsArea;

    /*************************************************************
     ************************END OF FXML INJECTIONS***************
     *************************************************************/

    /*************************************************************
     *****************************PRIVATES************************
     *************************************************************/

    private PlayArea playArea;

    private GameSeats seats;

    private int playerCount = 0;

    private static GameView ourInstance = new GameView();

    public static GameView getInstance() {
        return ourInstance;
    }

    private GameView() {
        super();
        fxml = "/fxml/GameView.fxml";

        try {
            loadFxml();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Number of players the game set up for.
     * @return
     */
    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
        this.seats = new GameSeats(board, playerCount);
        this.playArea = new PlayArea(setsArea);

        deckImageView.setImage(ViewHelper.getImage(Card.BACK_OF_CARD_IMAGE));
        deckImageView.setVisible(false);
        setMessage("Click on the deck to get started!");
    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

    }

    /************************************************************
     ********** MODIFIERS TO BE USED BY CONTROLLERS **************
     *************************************************************/

    /**
     * set the text for messageBox
     *
     * @param message
     */
    public void setMessage(String message) {
        messageBox.setText(message);
    }


    /***********************************************
     *************************HANDS******************
     ************************************************/
    public void addCardToHand(int seatNumber, Card card, EventHandler<MouseEvent> mouseEvent) {
        Player player = seats.getPlayer(seatNumber);
        player.addCardToHand(card);
    }

    /**
     * Deal 1 card to each opponent
     */
    public void dealHands() {
        // TODO: maybe an animation?
        for (Player player : seats.getOpponents()) {
            player.addCardToHand(HiddenCard.getInstance());
        }
    }

    /***********************************************
     **************END OF HAND METHODS**************
     ************************************************/

    /***********************************************
     ******************PLAY AREA*************************
     ************************************************/
    /**
     * empties deckOfCards
     */
    public void emptyDeck() {
        deckImageView.setVisible(false);
    }

    public void fillDeck() {
        deckImageView.setVisible(true);
    }


    /********************************************************
     ************* HELPERS********************
     *********************************************************************************/

    /**
     * Set the owner player id and seat number for this client.
     * @param ownerPlayerId
     * @param seatNumber
     */
    public void setOwnerPlayer(int ownerPlayerId, int seatNumber) {
        seats.setOwnerSeat(seatNumber, ownerPlayerId);
    }

    /**
     * Add new player to specified seat.
     * @param playerName
     * @param playerId
     * @param seatNumber
     */
    public void fillSeat(String playerName, int playerId, int seatNumber) {
        seats.setPlayerInfo(seatNumber, playerName, playerId);
    }

    public void setPlayAreaActive(boolean active){
        playArea.setActive(active);
        seats.getOwnerPlayerHand().setReceiverMode(active);
    }

    public void setPlayAreaActive(Card card){
        playArea.setActive(card);
        seats.getOwnerPlayerHand().setReceiverMode(card);
    }


}
