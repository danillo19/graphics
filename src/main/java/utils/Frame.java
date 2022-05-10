package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.Objects;

public class Frame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Integer DEFAULT_ICON_WIDTH = 24, DEFAULT_ICON_HEIGHT = 24;
    protected JMenuBar menuBar;
    protected JToolBar toolBar;

    public Frame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("Main toolbar");
        toolBar.setRollover(true);
        add(toolBar, BorderLayout.PAGE_START);
    }

    public Frame(int x, int y, String title) {
        this();
        setPreferredSize(new Dimension(x, y));
        setMinimumSize(new Dimension(480, 300));
        setLocationByPlatform(true);
        setTitle(title);
    }

    private ImageIcon createProperIcon(String path) throws IOException {
        //ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        ImageIcon imageIcon = new ImageIcon("src/main/resources/" + path);

        Image originImage = imageIcon.getImage();
        Image resizedImage = originImage.getScaledInstance(DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT, Image.SCALE_FAST);
        return new ImageIcon(resizedImage);
    }

    public JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String icon, String actionMethod) throws SecurityException, NoSuchMethodException, IOException {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(title);
        item.setName(title);
        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);
        if (icon != null)
            item.setIcon(createProperIcon(icon));
        final Class<? extends Frame> c = getClass();
        final Method method = c.getMethod(actionMethod);
        item.addActionListener(evt -> {
            try {
                method.invoke(Frame.this);
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        return item;
    }


    public JMenu createSubMenu(String title, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        return menu;
    }


    public void addSubMenu(String title, int mnemonic) {
        MenuElement element = getParentMenuElement(title);
        if (element == null)
            throw new InvalidParameterException("Menu path not found: " + title);
        JMenu subMenu = createSubMenu(getMenuPathName(title), mnemonic);
        if (element instanceof JMenuBar)
            ((JMenuBar) element).add(subMenu);
        else if (element instanceof JMenu)
            ((JMenu) element).add(subMenu);
        else if (element instanceof JPopupMenu)
            ((JPopupMenu) element).add(subMenu);
        else
            throw new InvalidParameterException("Invalid menu path: " + title);
    }


    public void addMenuItem(String title, String tooltip, int mnemonic, String icon, String actionMethod) throws SecurityException, NoSuchMethodException, IOException {
        MenuElement element = getParentMenuElement(title);
        if (element == null)
            throw new InvalidParameterException("Menu path not found: " + title);
        JMenuItem item = createMenuItem(getMenuPathName(title), tooltip, mnemonic, icon, actionMethod);
        if (element instanceof JMenu)
            ((JMenu) element).add(item);
        else if (element instanceof JPopupMenu)
            ((JPopupMenu) element).add(item);
        else
            throw new InvalidParameterException("Invalid menu path: " + title);
    }


    public void addMenuItem(String title, String tooltip, int mnemonic, String actionMethod) throws SecurityException, NoSuchMethodException, IOException {
        addMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }


    public void addMenuSeparator(String title) {
        MenuElement element = getMenuElement(title);
        if (element == null)
            throw new InvalidParameterException("Menu path not found: " + title);
        if (element instanceof JMenu)
            ((JMenu) element).addSeparator();
        else if (element instanceof JPopupMenu)
            ((JPopupMenu) element).addSeparator();
        else
            throw new InvalidParameterException("Invalid menu path: " + title);
    }

    private String getMenuPathName(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if (pos > 0)
            return menuPath.substring(pos + 1);
        else
            return menuPath;
    }


    private MenuElement getParentMenuElement(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if (pos > 0)
            return getMenuElement(menuPath.substring(0, pos));
        else
            return menuBar;
    }


    public MenuElement getMenuElement(String menuPath) {
        MenuElement element = menuBar;
        for (String pathElement : menuPath.split("/")) {
            MenuElement newElement = null;
            for (MenuElement subElement : element.getSubElements()) {
                if ((subElement instanceof JMenu && ((JMenu) subElement).getText().equals(pathElement))
                        || (subElement instanceof JMenuItem && ((JMenuItem) subElement).getText().equals(pathElement))) {
                    if (subElement.getSubElements().length == 1 && subElement.getSubElements()[0] instanceof JPopupMenu)
                        newElement = subElement.getSubElements()[0];
                    else
                        newElement = subElement;
                    break;
                }
            }
            if (newElement == null) return null;
            element = newElement;
        }
        return element;
    }


    public JButton createToolBarButton(JMenuItem item) {
        JButton button = new JButton(item.getIcon());
        button.setName(item.getName());
        for (ActionListener listener : item.getActionListeners())
            button.addActionListener(listener);
        button.setToolTipText(item.getToolTipText());
        return button;
    }


    public JButton createToolBarButton(String menuPath) {
        JMenuItem item = (JMenuItem) getMenuElement(menuPath);
        if (item == null)
            throw new InvalidParameterException("Menu path not found: " + menuPath);
        return createToolBarButton(item);
    }


    public void addToolBarButton(String menuPath) {
        toolBar.add(createToolBarButton(menuPath));
    }

    public void addToolBarSeparator() {
        toolBar.addSeparator();
    }


}
