package graphics;

import javax.swing.*;
import java.awt.*;

public class Square extends JPanel {
    Color highlight;
    boolean isHighlighted;

    public Square(LayoutManager layout) {
        super(layout);

        highlight = new Color(0xA1FF3F3F, true);
        isHighlighted = false;
    }

    public void toggleHighlight() {
        setVisible(false);
        if(!isHighlighted) {
            double a0 = highlight.getAlpha() / 255.0;
            int r0 = highlight.getRed();
            int g0 = highlight.getGreen();
            int b0 = highlight.getBlue();

            int r1 = getBackground().getRed();
            int g1 = getBackground().getGreen();
            int b1 = getBackground().getBlue();

            int red = (int) (r1 * (1 - a0) + r0 * a0);
            int green = (int) (g1 * (1 - a0) + g0 * a0);
            int blue = (int) (b1 * (1 - a0) + b0 * a0);

            setBackground(new Color(red, green, blue));
            isHighlighted = true;
        } else {
            double a0 = highlight.getAlpha() / 255.0;
            int r0 = highlight.getRed();
            int g0 = highlight.getGreen();
            int b0 = highlight.getBlue();

            int r1 = getBackground().getRed();
            int g1 = getBackground().getGreen();
            int b1 = getBackground().getBlue();

            int red = (int) ((r1 - r0 * a0) / (1 - a0));
            int green = (int) ((g1 - g0 * a0) / (1 - a0));
            int blue = (int) ((b1 - b0 * a0) / (1 - a0));

            setBackground(new Color(red, green, blue));
            isHighlighted = false;
        }
        setVisible(true);
    }
}
