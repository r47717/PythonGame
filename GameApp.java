package myfavoritegames;

import java.awt.*;
import javax.swing.*;

public class GameApp implements Runnable 
{

	final int WIDTH = 800;
	final int HEIGHT = 800;
	JPanel phytonPanel, tetrisPanel, arcPanel, chessPanel, moviePanel, musicPanel;
	
    public void run() 
    {
        JFrame f = new JFrame("My Favorite Games");
        f.setSize(WIDTH, HEIGHT);
        f.setLocation(300, 50);
        f.setResizable(false);
        
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //
        // Creating Menus
        //
     
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("File");
        JMenu m2 = new JMenu("Edit");
        JMenu m3 = new JMenu ("View");
        
        m1.add(new JMenuItem("Item 1"));
        m1.add(new JMenuItem("Item 2"));
        m1.add(new JMenuItem("Item 3"));
        m2.add(new JMenuItem("Item 1"));
        m2.add(new JMenuItem("Item 2"));
        m2.add(new JMenuItem("Item 3"));
        m3.add(new JMenuItem("Item 1"));
        m3.add(new JMenuItem("Item 2"));
        
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);
        
        f.setJMenuBar(mb);
        
        //
        // Creating tabs with panels
        //
        JTabbedPane tp = new JTabbedPane(3);
        
        tp.addTab("Python", phytonPanel = new PythonGame());
        tp.addTab("Tetris", tetrisPanel = new JPanel());
        tp.addTab("Archanoid", arcPanel = new JPanel());
        tp.addTab("Chess", chessPanel = new JPanel());
        tp.addTab("Movies", moviePanel = new JPanel());
        tp.addTab("Music", musicPanel = new JPanel());
        tp.setTabPlacement(JTabbedPane.TOP);
        tp.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "none");
        tp.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "none");
        tp.getInputMap().put(KeyStroke.getKeyStroke("UP"), "none");
        tp.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "none");

        f.add(tp);
        
        f.setForeground(Color.green);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
	
	public static void main(String[] args)
	{
		GameApp app = new GameApp();
        SwingUtilities.invokeLater(app);
	}

}
