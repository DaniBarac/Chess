package graphics;

import chess.ChessDriver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends JFrame implements MouseListener, MouseMotionListener {
    private final int BOARD_WIDTH = 600;
    private final int SQUARE_WIDTH = BOARD_WIDTH / 8;

    private final JLayeredPane layeredPane;
    private final JPanel chessBoard;
    private final Sidebar sideBar;
    private JLabel chessPiece;
    private int xAdjustment;
    private int yAdjustment;

    private int[] pieceStart;

    private final ChessDriver driver;

    public Game(String aiColor) {
        Dimension boardSize = new Dimension(BOARD_WIDTH, BOARD_WIDTH);
        setLayout(new FlowLayout());
        setTitle("Chess");
        setResizable(false);

        layeredPane = new JLayeredPane();
        add(layeredPane);
        layeredPane.setPreferredSize(boardSize);
        layeredPane.addMouseListener(this);
        layeredPane.addMouseMotionListener(this);

        sideBar = new Sidebar(BOARD_WIDTH / 2, BOARD_WIDTH);
        add(sideBar);

        chessBoard = new JPanel();
        layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
        chessBoard.setLayout( new GridLayout(8, 8) );
        chessBoard.setPreferredSize(boardSize);
        chessBoard.setBounds(0, 0, boardSize.width, boardSize.height);

        driver = new ChessDriver(aiColor);

        for (int i = 0; i < 64; i++) {
            Square square = new Square( new BorderLayout() );
            square.setName("basic square");
            chessBoard.add( square );

            int row = (i / 8) % 2;
            if (row == 0)
                square.setBackground( i % 2 == 1 ? new Color(100, 165, 100) : Color.white );
            else
                square.setBackground( i % 2 == 1 ? Color.white : new Color(100, 165, 100) );
        }

        initBoard();
//        System.out.println(driver.getBoard().getLegalMovesReadable());
//        initPromBoard();
    }

    public void initBoard() {
        JLabel piece;
        JPanel panel;
        int i = 0;
        String s = "b";
        // Initializing all pieces except the pawns
        for(int h = 0; h < 2; h++) {
            String[] list = {"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"};
            for (int j = 0; j < 8; j++) {
                piece = new JLabel(new ImageIcon("assets/pieces/" + s + list[j]+ ".png"));
                panel = (JPanel) chessBoard.getComponent(i + j);
                panel.add(piece);
            }

            i = 56;
            s = "w";
        }

        // Initializing the pawns
        for(int j = 0; j < 8; j++) {
            piece = new JLabel(new ImageIcon("assets/pieces/bpawn.png"));
            panel = (JPanel) chessBoard.getComponent(8 + j);
            panel.add(piece);
            piece = new JLabel(new ImageIcon("assets/pieces/wpawn.png"));
            panel = (JPanel) chessBoard.getComponent(48 + j);
            panel.add(piece);
        }
    }

    public void initPromBoard() {
        JLabel piece = new JLabel(new ImageIcon("assets/pieces/wpawn.png"));
        JPanel panel = (JPanel) chessBoard.getComponent(48 + 3);
        panel.add(piece);

        piece = new JLabel(new ImageIcon("assets/pieces/wking.png"));
        panel = (JPanel) chessBoard.getComponent(48 + 4);
        panel.add(piece);

        piece = new JLabel(new ImageIcon("assets/pieces/bking.png"));
        panel = (JPanel) chessBoard.getComponent(48 + 6);
        panel.add(piece);
    }

    public void movePiece(int x, int y) {
        int[] position = coordScreenToBoard(x, y);
        int i = position[0];
        int j = position[1];
        int i0 = pieceStart[0];
        int j0 = pieceStart[1];

        if(driver.checkMove(i0, j0, i, j)) {
            driver.makeMove(i0, j0, i, j);
            chessPiece.setVisible(false);

            Component c = chessBoard.findComponentAt(x, y);
            String situation = driver.getSituation();
            Container parent;

            // If there is already a piece on the destination remove it
            if (c instanceof JLabel) {
                parent = c.getParent();
                parent.remove(0);
                c = parent;
            }
            // Remove pawn to be captured enpassant
            if(situation.equals("ENPASSANT")) {
                Component d = chessBoard.findComponentAt(x, y +
                        (driver.getTurn().equals("BLACK") ? SQUARE_WIDTH : -SQUARE_WIDTH));
                d.setVisible(false);
                parent = d.getParent();
                parent.remove(0);
            }
            if(situation.equals("CASTLE")) {
                int rookPos = (j0 - j) > 0 ? 1 : SQUARE_WIDTH * 7 + 1;
                Component rook = chessBoard.findComponentAt(rookPos, y);
                Container dest = (Container) chessBoard.findComponentAt(
                        x + (rookPos == 1 ? SQUARE_WIDTH : -SQUARE_WIDTH), y);
                rook.setVisible(false);
                parent = rook.getParent();
                parent.remove(0);
                dest.add(rook);
                rook.setVisible(true);
            }
            parent = (Container) c;
            parent.add(chessPiece);

            if(situation.equals("PROMOTION")) {
                PromotionPopup.create(x, y, driver, chessBoard);
            }

            if(driver.isGameOver()) {
                GameOverPopup.create(driver.getWinner());
            }
            chessPiece.setVisible(true);
        } else {
            chessPiece.setVisible(false);
            Component c = chessBoard.findComponentAt(j0 * SQUARE_WIDTH + 1, (7-i0) * SQUARE_WIDTH + 1);
            Container parent = (Container) c;
            parent.add(chessPiece);
            chessPiece.setVisible(true);
            chessPiece = null;
        }
    }

    private void aiMove() {
//        System.out.println(driver.getLegalMovesReadable());
        int[] move;
        try {
            move = driver.getChessAIMove();
            driver.makeAIMove();
        } catch(NullPointerException e) {
            System.out.print("[ChessAI] There is no move.\n");
            return;
        }
        int[] start = coordBoardToScreen(move[0], move[1]);
        int[] dest  = coordBoardToScreen(move[2], move[3]);

        try {
            Square square = (Square) chessBoard.getComponentAt(start[0], start[1]);
            JLabel piece = (JLabel) square.getComponent(0);
            piece.setVisible(false);

            square = (Square) chessBoard.getComponentAt(dest[0], dest[1]);
            square.removeAll();
            square.add(piece);

            piece.setVisible(true);

        } catch(ClassCastException e) {
            Graphics g = chessBoard.getGraphics();
            g.setColor(Color.RED);
            g.fillOval(start[0], start[1], 10, 10);
            System.out.printf("[ChessAI] Invalid move. No piece at x=%d y=%d\n", start[0], start[1]);
        }
    }

    private int[] coordBoardToScreen(int a, int b) {
        int[] position = new int[2];
        position[0] = (int) (b * ((float) BOARD_WIDTH / 8)) + SQUARE_WIDTH / 2;
        position[1] = (int) ((7 - a) * ((float) BOARD_WIDTH / 8)) + SQUARE_WIDTH / 2;
        return position;
    }

    private int[] coordScreenToBoard(int a, int b) {
        int[] position = new int[2];
        position[0] = 7 - (int)((float)b / BOARD_WIDTH * 8);
        position[1] = (int)((float)a / BOARD_WIDTH * 8);
        if(position[0] > BOARD_WIDTH) position[0] = BOARD_WIDTH;
        if(position[1] > BOARD_WIDTH) position[1] = BOARD_WIDTH;
        if(position[0] < 0) position[0] = 0;
        if(position[1] < 0) position[1] = 0;
        return position;
    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            chessPiece = null;
            try {
                Component c = chessBoard.findComponentAt(e.getX(), e.getY());

                Point parentLocation = c.getParent().getLocation();
                xAdjustment = parentLocation.x - e.getX();
                yAdjustment = parentLocation.y - e.getY();
                chessPiece = (JLabel) c;
                chessPiece.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
                chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
                layeredPane.add(chessPiece, JLayeredPane.DRAG_LAYER);

                // game logic
                pieceStart = coordScreenToBoard(e.getX(), e.getY());
            } catch (ClassCastException ignored) {
            }
        } else if(e.getButton() == MouseEvent.BUTTON3) {
            Component c = chessBoard.findComponentAt(e.getX(), e.getY());
            Square square;
            try {
                square = (Square) c;
            } catch (ClassCastException z) {
                square = (Square) c.getParent();
            }
            square.toggleHighlight();
        }
    }

    public void mouseDragged(MouseEvent me) {
        if (chessPiece == null) return;
        chessPiece.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment);
    }

    public void mouseReleased(MouseEvent e) {
        if(chessPiece != null)
            movePiece(e.getX(), e.getY());
        if(chessPiece != null && driver.isComputerGame())
            aiMove();
        chessPiece = null;
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}