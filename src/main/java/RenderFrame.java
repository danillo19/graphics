import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RenderFrame extends JFrame {

    public RenderFrame(ArrayList<Point> points, int m, int m1) {
        super("Render");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ArrayList<Point> curvePoints = new ArrayList<>(points);
        RenderPanel panel = new RenderPanel(curvePoints,m,m1);

        add(panel);
        setVisible(true);
    }
}
