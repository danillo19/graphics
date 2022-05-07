import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("B-spline");

        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(800, 700));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        DrawPanel panel = new DrawPanel();
        SplineSettingPanel settingPanel = new SplineSettingPanel(panel);
        panel.setSettingPanel(settingPanel);

        add(panel);
        add(settingPanel,BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
