package graphics;

import chess.ChessDriver;

import javax.swing.*;
import java.awt.*;

public class PromotionPopup extends JPopupMenu {

    private final ChessDriver driver;
    private final JPanel chessBoard;
    private final int x, y;
    private final String turn;

    public PromotionPopup(int x, int y, ChessDriver driver, JPanel chessBoard) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.driver = driver;
        this.turn = driver.getOtherTurn();
        this.x = x;
        this.y = y;

        addPromoPopupOption("QUEEN");
        addPromoPopupOption("ROOK");
        addPromoPopupOption("BISHOP");
        addPromoPopupOption("KNIGHT");

        this.chessBoard = chessBoard;
        this.setLocation(new Point(x, y));
        this.setVisible(true);
    }

    public static void create(int x, int y, ChessDriver driver, JPanel chessBoard) {
        new PromotionPopup(x, y, driver, chessBoard);
    }

    private void addPromoPopupOption(String piece) {
        JMenuItem menuItem = new JMenuItem(
                piece,
                new ImageIcon("assets/pieces/" + turn.toLowerCase().charAt(0) + piece.toLowerCase() + ".png")
        );
        menuItem.getAccessibleContext().setAccessibleDescription(piece);
        menuItem.addActionListener(e -> {
            driver.promote(piece, turn);
            Component c = chessBoard.findComponentAt(x, y);
            c.setVisible(false);
            Container panel = c.getParent();
            panel.remove(0);
            panel.add(new JLabel(new ImageIcon(
                    "assets/pieces/" + turn.toLowerCase().charAt(0) + piece.toLowerCase() + ".png"
            )));
            this.setVisible(false);
        });
        this.add(menuItem);
    }
}
