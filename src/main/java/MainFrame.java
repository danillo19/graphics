import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainFrame extends JFrame {

    public MainFrame(RenderPanel renderPanel) {
        super("B-spline");

        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(800, 700));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        DrawPanel panel = new DrawPanel();
        SplineSettingPanel settingPanel = new SplineSettingPanel(panel, renderPanel);
        panel.setSettingPanel(settingPanel);

        add(panel);
        add(settingPanel,BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) throws IOException, NoSuchMethodException {
       new RenderFrame();
    }
}
