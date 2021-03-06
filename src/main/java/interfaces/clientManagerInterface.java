package interfaces;

import client.ClientManager;
import client.views.components.CardSetView;
import client.views.components.CardView;
import commands.server.PlayerMove;
import javafx.scene.input.MouseEvent;
import server.models.CardSet;
import server.models.cards.Card;
import utils.constants;

public interface clientManagerInterface {

    public static ClientManager getInstance() {
        return ClientManager.getInstance();
    }

    ;

    public void startServer(int port, int numberOfPlayers, String adminName, constants.GameMode serverMode) throws Exception;

    public void loginServer(int port, String name);

    public void loginServer(String ip, int port, String name);

    public void dealHand(int seatNumber, CardSet hand);

    public void introducePlayer(String playerName, int playerId, int seatNumber);

    public void moveSelectedCards(CardSetView targetSet);

    public void cardSelected(CardView selectedCard);

    public boolean endTurn(MouseEvent event);

    public void switchTurn(int seatNumber);

    public void drawCard(int seatNumber, Card card);

    public void playMove(PlayerMove move);
}
