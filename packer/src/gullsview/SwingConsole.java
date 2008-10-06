package gullsview;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class SwingConsole extends Console {
	private JFrame frame;
	private JTextField text;
	private JLabel label;
	private JScrollPane scrollPane;
	private ColorOutput output;
	private GridBagLayout layout;
	private GridBagConstraints gbc;
	private Object lock = new Object();
	private String line;
	
	public class ColorOutput extends JPanel {
		private java.util.List<String> lines;
		private java.util.List<Color> colors;
		private int width, height;
		
		public ColorOutput(){
			this.lines = new ArrayList<String>();
			this.colors = new ArrayList<Color>();
		}
		
		private Color getColor(String name){
			if("red".equalsIgnoreCase(name)){
				return Color.red;
			} else if("blue".equalsIgnoreCase(name)){
				return Color.blue;
			} else if("gray".equalsIgnoreCase(name)){
				return Color.gray;
			} else if("orange".equalsIgnoreCase(name)){
				return Color.orange;
			} else {
				return Color.black;
			}
		}
		
		public void add(String line, String color){
			this.lines.add(line);
			this.colors.add(this.getColor(color));
			int gap = 3;
			FontMetrics fm = this.getFontMetrics(this.getFont());
			this.height += fm.getHeight() + gap;
			int w = fm.stringWidth(line);
			if(this.width < w) this.width = w;
			this.repaint();
		}
		
		public void paint(Graphics g){
			g.setColor(this.getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			int gap = 3;
			Font font = this.getFont();
			FontMetrics fm = this.getFontMetrics(font);
			int y = fm.getAscent();
			int size = this.lines.size();
			for(int i = size - 1; i >= 0; i--){
				String line = this.lines.get(i);
				Color color = this.colors.get(i);
				g.setColor(color);
				g.drawString(line, 0, y);
				y += fm.getHeight() + gap;
			}
		}
		
		public Dimension getPreferredSize(){
			return new Dimension(this.width, this.height);
		}
		
		public int getHeight(){
			return this.height;
		}
	}
	
	public SwingConsole(){
		Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
		this.frame = new JFrame(this.r("title"));
		this.frame.setSize(500, 300);
		this.frame.setLocation((screenSize.width - this.frame.getWidth()) / 2, (screenSize.height - this.frame.getHeight()) / 2);
		this.frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				SwingConsole.this.close();
			}
		});
		this.layout = new GridBagLayout();
		this.gbc = new GridBagConstraints();
		this.frame.setLayout(this.layout);
		this.output = new ColorOutput();
		this.scrollPane = new JScrollPane(this.output);
		this.add(this.scrollPane, 0, 2, 3, 1, 0, 5);
		this.label = new JLabel();
		this.label.setHorizontalAlignment(SwingConstants.CENTER);
		JScrollPane lsp = new JScrollPane(this.label);
		this.add(lsp, 0, 0, 3, 1, 0, 1);
		this.add(new JLabel(this.r("answer") + ": "), 0, 1, 1, 1, 0, 0);
		this.text = new JTextField();
		this.text.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SwingConsole.this.sendInput();
			}
		});
		this.add(this.text, 1, 1, 1, 1, 1, 0);
		JButton button = new JButton(this.r("accept"));
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				SwingConsole.this.sendInput();
			}
		});
		this.add(button, 2, 1, 1, 1, 0, 0);
		this.frame.setVisible(true);
	}
	
	public void add(Component c, int x, int y, int width, int height, double weightx, double weighty){
		this.gbc.gridx = x;
		this.gbc.gridy = y;
		this.gbc.weightx = weightx;
		this.gbc.weighty = weighty;
		this.gbc.gridwidth = width;
		this.gbc.gridheight = height;
		this.gbc.fill = GridBagConstraints.BOTH;
		this.layout.setConstraints(c, this.gbc);
		this.frame.add(c);
	}
	
	public String input(String id, String question, String def){
		if(this.frame == null) return null;
		this.label.setText(question);
		this.text.setText(def);
		this.text.selectAll();
		String ret;
		synchronized(this.lock){
			try {
				while(this.line == null) this.lock.wait();
			} catch (InterruptedException e){
				return null;
			}
			ret = this.line;
			this.line = null;
		}
		this.print(this.r("question") + ": " + question, "gray");
		this.print(this.r("answer") + ": " + ret, "blue");
		return ret;
	}
	
	public void sendInput(){
		String line = this.text.getText();
		this.text.setText("");
		this.label.setText("");
		this.sendInput(line);
	}
	
	public void sendInput(String line){
		synchronized(this.lock){
			while(this.line != null) this.lock.notify();
			this.line = line;
			this.lock.notify();
		}
	}
	
	public void print(String text, String color){
		if(this.frame == null) return;
		this.output.add(text, color);
		(this.scrollPane.getViewport()).setViewPosition(new Point(0, 0));
	}
	
	public void error(String message, Throwable t){
		if(this.frame == null) return;
		this.print(message, "red");
		while(t != null){
			this.print(t.toString(), "red");
			t = t.getCause();
		}
	}
	
	private void dispose(){
		this.frame.setVisible(false);
		this.frame.dispose();
	}
	
	public void close(){
		this.printRes("please-close-window");
		// this.dispose();
		this.frame = null;
	}
}


