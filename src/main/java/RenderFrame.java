import utils.ExtensionFileFilter;
import utils.FileHandler;
import utils.Frame;
import utils.Vector3;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RenderFrame extends Frame {

    private RenderHandler handler;
    private RenderPanel panel;

    public RenderHandler getHandler() {
        return handler;
    }

    public RenderPanel getPanel() {
        return panel;
    }

    public RenderFrame() throws IOException, NoSuchMethodException {
        super();
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addSubMenu("File", KeyEvent.VK_C);
        addMenuItem("File/Save as", "Save image", KeyEvent.VK_S, "save.png", "onSave");
        addMenuItem("File/Open", "Open image", KeyEvent.VK_C, "open-folder.png", "onOpen");
        addMenuItem("File/Exit", "Close app", KeyEvent.VK_C, "power.png", "onExit");

        addSubMenu("Spline",KeyEvent.VK_C);
        addMenuItem("Spline/Draw spline", "Get spline template",KeyEvent.VK_C,"line.png","onDrawSpline");
        addMenuItem("Spline/Reset scale", "Rest scale",KeyEvent.VK_C,"scale.png","onResetScale");

        addToolBarButton("File/Open");
        addToolBarButton("File/Save as");
        addToolBarButton("Spline/Draw spline");

        addSubMenu("Help", KeyEvent.VK_S);
        addMenuItem("Help/About", "About app", 0, "about.png", "onAbout");

        panel = new RenderPanel();
        panel.setM(5);
        panel.setM1(2);
        handler = panel.getRenderHandler();

        FileHandler fileHandler = new FileHandler(handler.getCurrentObjectsPoints(), handler.getAxis(), handler.getZn());
        fileHandler.readInfoFromFile(new File("src/main/resources/2.moi"));
        handler.setAxis(fileHandler.getAxis());
        handler.setObjectPointsBeforeRotation(fileHandler.getPoints());
        handler.setZn(fileHandler.getZn());
        add(panel);
        setVisible(true);
    }




    public void onSave() {
        String[] extensions = {"moi"};
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        for (String extension : extensions) {
            ExtensionFileFilter fileFilter = new ExtensionFileFilter(extension, "");
            fileChooser.addChoosableFileFilter(fileFilter);
        }
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            FileFilter fileFilter = fileChooser.getFileFilter();
            String format = "moi";
            if (!fileFilter.accept(file)) {
                ExtensionFileFilter extendedFileFilter = (ExtensionFileFilter) fileFilter;
                file = new File(file.getAbsolutePath() + "." + extendedFileFilter.getExtension());
                format = extendedFileFilter.getExtension();
            }
            try {
                FileHandler fileHandler = new FileHandler(handler.getCurrentObjectsPoints(), handler.getAxis(), handler.getZn());
                fileHandler.dumpInfoIntoFile(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


    }

    public void onOpen() throws IOException {
        String[] extensions = {"moi"};
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        for (String extension : extensions) {
            ExtensionFileFilter fileFilter = new ExtensionFileFilter(extension, "");
            fileChooser.addChoosableFileFilter(fileFilter);
        }
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            System.out.println(file.getAbsolutePath());
            FileHandler fileHandler = new FileHandler(handler.getCurrentObjectsPoints(), handler.getAxis(), handler.getZn());
            try {
                fileHandler.readInfoFromFile(file);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this,"File has not valid data. Rewrite data");
                return;
            }
            handler.setAxis(fileHandler.getAxis());
            handler.setObjectPointsBeforeRotation(fileHandler.getPoints());
            handler.setZn(fileHandler.getZn());

            panel.repaint();

        }

    }

    public void onDrawSpline() {
        MainFrame mainFrame = new MainFrame(panel);
    }

    public void onResetScale() {
        panel.getRenderHandler().setZn(10);
        panel.repaint();
    }


    public void onExit() {
        System.exit(0);
    }

    public void onAbout() {
        try {
            getAbout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getAbout() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\danil\\IdeaProjects\\graphics_labs\\lab4\\" +
                "src\\main\\resources\\about.txt"));
        JFrame frame = new JFrame("About");
        frame.setSize(new Dimension(400, 400));
        Font font = new Font("Arial",Font.ITALIC,12);
        JTextArea area = new JTextArea();
        area.setFont(font);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            area.append(line);
            area.append("\n");
        }

        area.setEditable(false);
        frame.add(area);
        frame.setVisible(true);
    }
}
