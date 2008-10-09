package gullsview;

import java.util.*;
import java.util.concurrent.*;
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
	private BlockingQueue<String> queue;
	
	public class ColorOutput extends JPanel {
		private java.util.List<String> lines;
		private java.util.List<Color> colors;
		private int width, height;
		
		public ColorOutput(){
			this.lines = new ArrayList<String>();
			this.colors = new ArrayList<Color>();
			this.setFont((this.getFont()).deriveFont(Font.BOLD));
		}
		
		private Color getColor(String name){
			if("red".equalsIgnoreCase(name)){
				return Color.red.brighter();
			} else if("blue".equalsIgnoreCase(name)){
				return Color.blue.darker();
			} else if("gray".equalsIgnoreCase(name)){
				return Color.gray;
			} else if("orange".equalsIgnoreCase(name)){
				return Color.orange.darker();
			} else {
				return new Color(0xaa4400);
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
			Container parent = this.getParent();
			if(parent != null) parent.doLayout();
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
			for(int i = 0; i < size; i++){
				String line = this.lines.get(i);
				Color color = this.colors.get(i);
				g.setColor(color);
				g.setFont(font);
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
		this.queue = new LinkedBlockingQueue<String>();
		Dimension screenSize = (Toolkit.getDefaultToolkit()).getScreenSize();
		this.frame = new JFrame(this.r("title"));
		this.frame.setSize(800, 600);
		this.frame.setLocation((screenSize.width - this.frame.getWidth()) / 2, (screenSize.height - this.frame.getHeight()) / 2);
		this.frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				SwingConsole.this.dispose();
				System.exit(1);
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
		try {
			ret = this.queue.take();
		} catch (InterruptedException e){
			throw new RuntimeException(e);
		}
		this.print(this.r("question") + ": " + question, "gray");
		this.print(this.r("answer") + ": " + ret, "blue");
		return ret;
	}
	
	public void sendInput(){
		String line = this.text.getText();
		this.text.setText("");
		this.label.setText("");
		try {
			this.queue.put(line);
		} catch (InterruptedException e){
			throw new RuntimeException(e);
		}
	}
	
	public void print(String text, String color){
		if(this.frame == null) return;
		this.output.add(text, color);
		(this.scrollPane.getViewport()).setViewPosition(new Point(0, this.output.getHeight()));
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
		this.frame = null;
	}
	
	public void close(){
		this.inputString("accept-to-close", null, null);
		this.dispose();
	}
}


