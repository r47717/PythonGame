package myfavoritegames;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;


public class PythonGame extends GameElement {
	
	protected final String MANGOOSE = "M";
	protected final String PYTHON_HEAD = "@";
	protected final String PYTHON_BODY = "#";
	protected final int INITIAL_LEN = 5;
	protected final int INITIAL_POS_X = 20;
	protected final int INITIAL_POS_Y = 20;
	protected final Direction INITIAL_DIRECTION = Direction.UP;
	protected final int INITIAL_SPEED = 500;
	protected final int FIELD_WIDTH = 50;
	protected final int FIELD_HEIGHT = 50;
	

	protected enum Direction {UP, DOWN, LEFT, RIGHT};
	protected PythonGame game = this;

	
	// public flags representing game status
	//
	protected class GameStatus {
		public Boolean gameOver = false;
		public Boolean isPaused = false;

		public GameStatus() {
			gameOver = isPaused = false;
		}
		public void reset() {
			gameOver = isPaused = false;
		}
	}

	// Python data structure and its change
	//
	protected class Python implements Runnable {
		protected Thread t;
		protected Boolean resetting = false;
		protected Direction direction; 
		protected int speed;
		protected int grow;
		protected int length;
		protected int headX, headY, tailX, tailY;
		
		protected class Body extends Object {
			public String s;
			public int x;
			public int y;
			public Body next;
			public Body prev;
			public Body(int x, int y, String s, Body next, Body prev)
			{ this.x = x; this.y = y; this.s = s; this.next = next; this.prev = prev; }
		}
		protected Body head, tail;
		
		public Python() {
			this.reset();
		}
		
		public void reset() {
			resetting = true;
			
			direction = INITIAL_DIRECTION; // make it random
			speed = INITIAL_SPEED;
			length = INITIAL_LEN;
			headX = INITIAL_POS_X;
			headY = INITIAL_POS_Y;
			grow = 0;

			// put python into rendering matrix and create body structure
			//
			pythonField.put(headX, headY, PYTHON_HEAD);
			head = new Body(headX, headY, PYTHON_HEAD, null, null);
			Body curr = head;
			int i;
			
			switch( direction ) {
			case UP:
				tailX = headX + length - 1;
				tailY = headY;
				for(i = 1; i < length; i++) {
					pythonField.put(headX + i, headY, PYTHON_BODY);
					curr.prev = new Body(head.x + i, head.y, PYTHON_BODY, curr, null);
					curr = curr.prev;
				}
				break;
			case DOWN:
				tailX = headX - length + 1;
				tailY = headY;
				for(i = 1; i < length; i++) {
					pythonField.put(headX - i, headY, PYTHON_BODY);
					curr.prev = new Body(head.x - i, head.y, PYTHON_BODY, curr, null);
					curr = curr.prev;
				}
				break;
			case LEFT:
				tailX = headX;
				tailY = headY + length - 1;
				for(i = 1; i < length; i++) {
					pythonField.put(headX, headY + i, PYTHON_BODY);
					curr.prev = new Body(head.x, head.y + i, PYTHON_BODY, curr, null);
					curr = curr.prev;
				}
				break;
			case RIGHT:
				tailX = headX;
				tailY = headY - length + 1;
				for(i = 1; i < length; i++) {
					pythonField.put(headX, headY - i, PYTHON_BODY);
					curr.prev = new Body(head.x, head.y - i, PYTHON_BODY, curr, null);
					curr = curr.prev;
				}
				break;
			}
			
			tail = curr;
			
			//while(t.isAlive()) {}
			t = new Thread(this);
			resetting = false;
			t.start();
		}
		
		public void run() {
			try {
				while(!gameStatus.gameOver && !resetting) {
					if(!gameStatus.isPaused) {
						this.move(direction);
						options.setStatus(this.whoAmI());
						Thread.sleep(speed);
					}
				}
			}
			catch(InterruptedException e) {}
		}
		
		// is called from keyboard listener to handle key presses
		//
		public void newDirection(Direction newDir) {
			switch( newDir ) {
			case DOWN:
				if(direction == Direction.RIGHT || direction == Direction.LEFT)
					direction = Direction.DOWN;
				else if(direction == Direction.DOWN)
					this.faster();
				else
					this.slower();
				break;
			case UP:
				if(direction == Direction.RIGHT || direction == Direction.LEFT)
					direction = Direction.UP;
				else if(direction == Direction.UP)
					this.faster();
				else
					this.slower();
				break;
			case LEFT:
				if(direction == Direction.UP || direction == Direction.DOWN)
					direction = Direction.LEFT;
				else if(direction == Direction.LEFT)
					this.faster();
				else
					this.slower();
				break;
			case RIGHT:
				if(direction == Direction.UP || direction == Direction.DOWN)
					direction = Direction.RIGHT;
				else if(direction == Direction.RIGHT)
					this.faster();
				else
					this.slower();
				break;
			}
		}
		
		protected void moveHead(Direction dir) {
			switch(dir) {
			case LEFT:
				head.next = new Body(head.x, head.y - 1, PYTHON_HEAD, null, head);
				break;
			case RIGHT:
				head.next = new Body(head.x, head.y + 1, PYTHON_HEAD, null, head);
				break;
			case UP:
				head.next = new Body(head.x - 1, head.y, PYTHON_HEAD, null, head);
				break;
			case DOWN:
				head.next = new Body(head.x + 1, head.y, PYTHON_HEAD, null, head);
				break;
			}
			head.s = PYTHON_BODY;
			head = head.next;
		}
		
		protected void cutTail() {
			if(tail != head)
			{
				tail = tail.next;
				tail.prev = null;
			}
		}
		
		public Boolean move(Direction dir) {
			int newX = headX;
			int newY = headY;
			
			switch(dir) {
				case UP: newX--; break;
				case DOWN: newX++; break;
				case LEFT: newY--; break;
				case RIGHT: newY++; break;
			}
			
			if(newX < 0 || newX > pythonField.maxX() || newY < 0 || newY > pythonField.maxY()) {
				game.gameOver();
				return false;
			}
			
			moveHead(dir);
			
			if(pythonField.get(newX, newY) == MANGOOSE) {
				game.gameOver();
				return false;
			}
			
			if(pythonField.get(newX, newY) == PYTHON_HEAD || 
			   pythonField.get(newX, newY) == PYTHON_BODY) {
				game.gameOver();
				return false;
			}
			
			if(!pythonField.isEmpty(newX, newY)) {
				int n = Integer.parseInt(pythonField.get(newX, newY));
				grow += n;
			}
			
			pythonField.put(headX, headY, PYTHON_BODY);
			pythonField.put(headX = newX, headY = newY, PYTHON_HEAD);
			
			if(grow == 0) {
				cutTail();
				pythonField.clean(tailX, tailY);
				tailX = tail.x;
				tailY = tail.y;

			} else {
				grow--;
				length++;
			}
			
			pythonField.Update();

			return true;
		}
		
		public void faster() {
			if(speed - 50 > 0)
				speed -= 50;
		}
		
		public void slower() {
			speed += 50;
		}

		public String whoAmI() {
			String str;

			if(length <= 5) str = "Вы - очень маленький уж!";
			else if (length < 50) str = "Вы - маленький уж!"; 
			else if (length < 100) str = "Вы - большой уж!"; 
			else if (length < 150) str = "Вы - маленький желтопузик!";
			else if (length < 200) str = "Вы - желтопузик!";
			else if (length < 250) str = "Вы - большой желтопузик!";
			else if (length < 350) str = "Вы - анаконда!";
			else if (length < 500) str = "Вы - маленький питон!";
			else if (length < 650) str = "Вы - питон!";
			else str = "Вы - огромный питон!";
			
			return str + String.format(" (%d)", length);
		}
	}

	// Food and Mangoose random generation thread
	//
	protected class Items implements Runnable {
		protected Thread t;
		protected Boolean resetting = false;
		protected int maxX;
		protected int maxY;
		
		public Items() {			
			this.reset();
		}
		
		public void reset() {
			this.maxX = FIELD_HEIGHT - 1;
			this.maxY = FIELD_WIDTH - 1;
			resetting = true;
			while(t != null && t.isAlive()) {}
			t = new Thread(this);
			resetting = false;
			t.start();
		}
		
		public void run() {
			while(!gameStatus.gameOver && !resetting) {
				if(!gameStatus.isPaused) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				
					int x = (int)(Math.random() * maxX);
					int y = (int)(Math.random() * maxY);
					int n = (int)(Math.random() * 13);
					String s;
					if(n == 0 || n >= 10)
						s = MANGOOSE;
					else
						s = String.format("%d", n);
				
					if(pythonField.isEmpty(x, y))
						pythonField.put(x, y, s);
					
					pythonField.Update();
				}
			}
		}
	}
	
	protected class PythonField extends AbstractTableModel implements KeyListener {
		protected final int maxX = FIELD_HEIGHT - 1;
		protected final int maxY = FIELD_WIDTH - 1;
		protected Object [][] data = new Object[FIELD_HEIGHT][FIELD_WIDTH];
		protected JPanel parent;
		protected JTable table = null;

		public PythonField(JPanel parent) {
			this.reset(parent);
		}
		
		public void reset(JPanel parent) {
			for(int i = 0; i < FIELD_HEIGHT; i++)
				for(int j = 0; j < FIELD_WIDTH; j++)
					data[i][j] = " ";
			
			this.parent = parent;
			
			if(table == null) {
				table = new JTable(this);
				table.setTableHeader(null);
				table.setShowGrid(true);
				table.setGridColor(Color.lightGray);
				table.setForeground(Color.black);
				table.setBackground(Color.white);

				table.setRowHeight(12);
				table.setCellSelectionEnabled(false);
				table.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				table.addKeyListener(this);
				
				parent.add(new JScrollPane(table), BorderLayout.CENTER);
			}
		}
		
		public int maxX() {
			return maxX;
		}
		
		public int maxY() {
			return maxY;
		}
		
		public String get(int x, int y) {
			return data[x][y].toString();
		}
		
		public Boolean isEmpty(int x, int y) {
			return data[x][y].toString().equals(" ");
		}
		
		public void put(int x, int y, String s) {
			data[x][y] = s;
		}
		
		public void clean(int x, int y) {
			data[x][y] = " ";
		}
		
		public void Update() {
			fireTableDataChanged();
		}
		
		public void setBlackTheme() {
			table.setGridColor(Color.black);
			table.setForeground(Color.white);
			table.setBackground(Color.black);
		}
		
		public void setWhiteTheme() {
			table.setGridColor(Color.lightGray);
			table.setForeground(Color.black);
			table.setBackground(Color.white);
		}
		
		@Override
		public int getColumnCount() {
			return FIELD_WIDTH;
		}

		@Override
		public int getRowCount() {
			return FIELD_HEIGHT;
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			return data[arg0][arg1];
		}

		@Override
		public void keyPressed(KeyEvent ke) {
			switch(ke.getKeyCode()) {
			case KeyEvent.VK_DOWN: python.newDirection(Direction.DOWN ); break;
			case KeyEvent.VK_UP:   python.newDirection(Direction.UP   ); break;
			case KeyEvent.VK_RIGHT: python.newDirection(Direction.RIGHT); break;
			case KeyEvent.VK_LEFT: python.newDirection(Direction.LEFT ); break;
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}

	}
	
	protected class Options extends JPanel {
		protected JPanel parent;
		
		protected JPanel controls;
			protected JPanel buttons;
				protected JButton pause;
				protected JButton restart;
				protected JPanel speedButtons;
					protected JButton speedUp;
					protected JButton speedDown;
			protected JPanel themes;
				protected ButtonGroup themeGroup;
				protected JRadioButton theme1;
				protected JRadioButton theme2;
				protected JRadioButton theme3;
			protected JPanel symbols;
				protected JTextField head;
				protected JTextField body;
				protected JTextField mangoose;
				
				
		protected JLabel status;

		Options(JPanel parent) {
			this.parent = parent;
			
			this.setLayout(new BorderLayout());
			controls = new JPanel();
			this.add(controls, BorderLayout.CENTER);
			controls.setLayout(new GridLayout(1, 3));
				buttons = new JPanel();
				buttons.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder() ,"Controls"));
				buttons.setLayout(new GridLayout(3, 1));
					pause = new JButton("Pause");
				buttons.add(pause);
					restart = new JButton("Restart");
				buttons.add(restart);
					speedButtons = new JPanel();
					speedButtons.setLayout(new GridLayout(1, 5));
						speedDown = new JButton("<<");
					speedButtons.add(new JPanel());
					speedButtons.add(speedDown);
						speedUp = new JButton(">>");
						speedButtons.add(new JPanel());
					speedButtons.add(speedUp);
					speedButtons.add(new JPanel());
				buttons.add(speedButtons);
			controls.add(buttons);
				themes = new JPanel();
				themes.setLayout(new GridLayout(3, 1));
				themes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder() ,"Themes"));
			controls.add(themes);
				symbols = new JPanel();
				symbols.setLayout(new GridLayout(3, 1));
				symbols.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder() ,"Symbols"));
			controls.add(symbols);
			
			themeGroup = new ButtonGroup();
			theme1 = new JRadioButton("Light View", true);
			theme2 = new JRadioButton("Black View", false);
			theme3 = new JRadioButton("Surprize View", false);
			themeGroup.add(theme1);
			themeGroup.add(theme2);
			themeGroup.add(theme3);
			themes.add(theme1);
			themes.add(theme2);
			themes.add(theme3);
			
			head = new JTextField(); symbols.add(head);
			body = new JTextField(); symbols.add(body);
			mangoose = new JTextField(); symbols.add(mangoose);
			
			
			status = new JLabel("");
			this.add(status, BorderLayout.SOUTH);
			status.setHorizontalAlignment(SwingConstants.CENTER);
			status.setBorder(BorderFactory.createEtchedBorder());
			
			pause.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if(!gameStatus.isPaused){
						pause.setText("Resume");
						game.pause();
					} else {
						pause.setText("Pause");
						game.resume();
					}
					
					
				}
			});
			
			restart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					pause.setText("Pause");
					resetGame();
				}
			});
			
			parent.add(this, BorderLayout.SOUTH);
		}
		
		public void setStatus(String str) {
			status.setText(str);
		}
		
		public void gameOver() {
			
		}
		
		public void reset() {
			status.setText("");
			pause.setText("Pause");
		}
	}
	
	//
	// Instances
	//
	protected GameStatus gameStatus;
	protected PythonField pythonField;
	protected Items items;
	protected Python python;
	protected Options options;
	
	public PythonGame() {
		super();
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setForeground(Color.yellow);
		this.setBackground(Color.blue);
		this.setLayout(new BorderLayout());
		
		pythonField = new PythonField(this);
		options = new Options(this);
		gameStatus = new GameStatus();
		items = new Items();
		python = new Python();
		
	}
	
	protected void pause() {
		gameStatus.isPaused = true;
	}
	
	protected void resume() {
		gameStatus.isPaused = false;
	}
	
	protected void resetGame() {
		options.reset();
		pythonField.reset(this);;
		gameStatus.reset();
		items.reset();
		python.reset();
	}
	
	protected void gameOver() {
		gameStatus.gameOver = true;
		options.setStatus("GAVE OVER!");
	}
	
	@Override
	public void Start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
