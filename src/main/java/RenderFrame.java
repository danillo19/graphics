import utils.ExtensionFileFilter;
import utils.FileHandler;
import utils.Frame;
import utils.Vector3;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RenderFrame extends Frame {

    private RenderHandler handler;
    private RenderPanel panel;

    public RenderFrame(ArrayList<Point> points, int m, int m1) throws IOException, NoSuchMethodException {
        super();
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addSubMenu("File", KeyEvent.VK_C);
        addMenuItem("File/Save as", "Save image", KeyEvent.VK_S, "save.png", "onSave");
        addMenuItem("File/Open", "Open image", KeyEvent.VK_C, "open-folder.png", "onOpen");
        addMenuItem("File/Exit", "Close app", KeyEvent.VK_C, "power.png", "onExit");

        addToolBarButton("File/Open");
        addToolBarButton("File/Save as");

        ArrayList<Point> curvePoints = new ArrayList<>();
        for (Point point : points) {
            curvePoints.add((Point) point.clone());
        }
        panel = new RenderPanel(curvePoints, m, m1);
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

    public void onExit() {
        System.exit(0);
    }
}
