package graphics;

import main.Main;

import javax.swing.*;
import java.awt.*;

public class GameOverPopup extends JPopupMenu {

    private GameOverPopup(String winner) {

        JPanel t = new JPanel();
        JLabel title = new JLabel("GAME OVER, " + (winner != null ? winner + " WINS" : "DRAW"));
        title.setSize(100, 500);
        t.add(title);
        t.setVisible(true);
        t.setSize(100, 500);
        t.setAlignmentX(10);
        this.add(t,0);


        JButton menuItem = new JButton("Play again");
        menuItem.addActionListener(e -> {
            this.setVisible(false);
            Main.main(null);
            this.setVisible(false);
        });

        JPanel buttons = new JPanel();
        buttons.add(menuItem);

        menuItem = new JButton("Exit");
        menuItem.addActionListener(e -> System.exit(0));
        buttons.add(menuItem);

        this.add(buttons,1);
        this.setLocation(new Point(500, 500));
        this.setSize(500, 500);
        this.setVisible(true);
    }

    public static void create(String winner) {
        new GameOverPopup(winner);
    }

}
