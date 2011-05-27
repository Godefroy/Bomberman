package bomberman.client.controller;

import bomberman.client.elements.Bomb;
import bomberman.client.elements.Element;
import bomberman.client.elements.Player;
import bomberman.client.gui.Board;
import bomberman.client.gui.Window;
import bomberman.client.model.Client;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game extends Thread implements KeyListener {

    private static Game instance;
    private Board board;
    private Map<Integer, Player> players = new HashMap();
    private int player_id;
    private boolean started = false;
    private int fps = 50;
    private boolean key_up = false;
    private boolean key_down = false;
    private boolean key_left = false;
    private boolean key_right = false;

    private Game() {
        this.start();
    }

    /**
     * Creates a unique instance of Game (Singleton)
     *
     * @return Instance of Game
     */
    public static synchronized Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void newGame() {
        this.setBoard(new Board());
        Window window = Window.getInstance();
        window.showBoard();
        window.addKeyListener(this);
    }

    @Override
    public void run() {
        int period = 1000 / this.fps;

        while (true) {
            try {
                if (!this.started) {
                    Thread.sleep(1000);
                    continue;
                }

                {
                    Player player = this.getCurrentPlayer();
                    if (player.getMovePogression() == 1) {
                        if (this.key_up && this.board.isSquareWalkable(player.getX(), player.getY() - 1)) {
                            player.startMoveRelative(0, -1);
                        } else if (this.key_down && this.board.isSquareWalkable(player.getX(), player.getY() + 1)) {
                            player.startMoveRelative(0, 1);
                        } else if (this.key_left && this.board.isSquareWalkable(player.getX() - 1, player.getY())) {
                            player.startMoveRelative(-1, 0);
                        } else if (this.key_right && this.board.isSquareWalkable(player.getX() + 1, player.getY())) {
                            player.startMoveRelative(1, 0);
                        }
                    }
                }

                for (Player player : this.players.values()) {
                    player.progressMove(period);
                }

                this.board.repaint();

                Thread.sleep(period);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public Board getBoard() {
        return this.board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getCurrentPlayerId() {
        return this.player_id;
    }

    public void setCurrentPlayerId(int player_id) {
        this.player_id = player_id;
    }

    public Player getCurrentPlayer() {
        return this.players.get(this.player_id);
    }

    public Map<Integer, Player> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, Map> data) throws Exception {
        this.players = new HashMap();
        for (Map.Entry<String, Map> player_data_entry : data.entrySet()) {
            int id = Integer.parseInt(player_data_entry.getKey());
            Map player_data = player_data_entry.getValue();
            Player player = new Player(Client.getInstance().convertToInt(player_data.get("x")), Client.getInstance().convertToInt(player_data.get("y")));
            this.players.put(id, player);
            if (player_data.containsKey("client") && (Boolean) player_data.get("client")) {
                this.player_id = id;
            }
        }
        this.started = true;
    }

    public Player getPlayer(int player_id) {
        return this.players.get(player_id);
    }

    public void addPlayer(int player_id, int x, int y) {
        this.players.put(player_id, new Player(x, y));
    }

    public void delPlayer(int player_id) {
        if (this.players.containsKey(player_id)) {
            this.players.remove(player_id);
        }
    }

    public void dropBomb(int x, int y) {
        int target_index = x + this.board.getCols() * y;
        Game.getInstance().getBoard().setElement(target_index, new Bomb());
    }

    public void burstBomb(int x, int y) {
        List<Integer> fire = new ArrayList();

        for (int i = 0; i < this.board.getCols(); i++) {
            int index = i + this.board.getCols() * y;
            Element element = this.board.getElements().get(index);
            if (element != null && element.isBreakable()) {
                this.board.setElement(index, null);
            }
            fire.add(index);
        }

        for (int i = 0; i < this.board.getRows(); i++) {
            int index = x + this.board.getCols() * i;
            Element element = this.board.getElements().get(index);
            if (element != null && element.isBreakable()) {
                this.board.setElement(index, null);
            }
            fire.add(index);
        }

        this.board.addFire(fire);
    }

    public void addElement(Element element, int x, int y) {
        int index = x + this.board.getCols() * y;
        Game.getInstance().getBoard().setElement(index, element);
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_SPACE) {
            Client.getInstance().dropBomb();
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            this.key_up = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            this.key_down = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            this.key_left = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.key_right = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            this.key_up = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            this.key_down = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            this.key_left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.key_right = false;
        }
    }
}
