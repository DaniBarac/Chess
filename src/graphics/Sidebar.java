package graphics;

import javax.swing.*;
import java.awt.*;

public class Sidebar extends JPanel {

    public Sidebar(int width, int height) {
        Dimension dimension = new Dimension(width, height);
        setPreferredSize(dimension);
        setBackground(new Color(51, 51, 51));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


//        label = new JLabel("sal");
//        label.setHorizontalTextPosition(JLabel.CENTER);
//        label.setVerticalTextPosition(JLabel.BOTTOM);
//        label.setPreferredSize(dimension);
//        label.setBackground(new Color(51, 51, 51));
//        label.setForeground(new Color(0xAAAAAA));
//        label.setVisible(true);
//        label.setOpaque(true);
//        add(label);
    }

    @Override
    public void removeAll() {
        super.removeAll();
    }

    public void addLabel(String text) {
        Label label = new Label(text);
        label.setForeground(new Color(0xA7A4A4));
        add(label);
    }

//    public void setLabelText(String text) {
//        label.setText(text);
//        label.setVisible(true);
//    }
}
