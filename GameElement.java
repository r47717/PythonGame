package myfavoritegames;

import javax.swing.*;
import java.awt.event.*;

public abstract class GameElement extends JPanel implements Runnable, ActionListener {


	public GameElement() {
		super();
	}

	//@Override
	//public abstract void run() {
		// TODO Auto-generated method stub

	//}

	public abstract void Start();
	public abstract void Stop();
}
