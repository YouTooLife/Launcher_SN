package net.youtoolife.launcher;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.youtoolife.handlers.Download;
import net.youtoolife.handlers.MD5Hasher;
import static net.youtoolife.launcher.Main.state;

public class Form extends JPanel implements ActionListener, Runnable {

	private static final long serialVersionUID = 1L;
	
	public final int INSTALL = 0;
	public final int MAIN_MENU = 1;
	public final int UPDATE = 2;
	public final int DOWNLOADING = 3;
	public final int HASHING = 4;
	
	//private Integer state = 4;
	private Download down = null;
	private MD5Hasher md5 = null;
	
	boolean flag = false;
	
	
	Timer mainTimer = new Timer(25, this);
	
	Thread thread;
	
	Image bg = new ImageIcon(getClass().getClassLoader().getResource("res/bg.png")).getImage();
	Image update = new ImageIcon(getClass().getClassLoader().getResource("res/update.png")).getImage();
	Image install = new ImageIcon(getClass().getClassLoader().getResource("res/install.png")).getImage();
	Image pb = new ImageIcon(getClass().getClassLoader().getResource("res/pb.png")).getImage();
	Image play = new ImageIcon(getClass().getClassLoader().getResource("res/play.png")).getImage();
	
	public Form() {
		
		mainTimer.start();
		
		addKeyListener(new myKeyAdapter()); 
		addMouseListener(new customListener());
		setFocusable(true);
		
		//this.state = Main.state;
		this.down = Main.down;
		this.md5 = Main.md5;
		
		Thread thread = new Thread(this);
		thread.start();
		
	}
	
	public class customListener implements MouseListener {
		 
        public void mouseClicked(MouseEvent e) {
            
        }

        public void mouseEntered(MouseEvent e) {
             
        }

        public void mouseExited(MouseEvent e) {
             
        }

        public void mousePressed(MouseEvent e) {
             
        }

        public void mouseReleased(MouseEvent e) {
        	
        	if (state == INSTALL || state == UPDATE) {
        		System.out.println(e.getX()+" "+ e.getY());
				if (e.getX()>=(Main.width/2-180/2) && e.getX()<=(Main.width/2+180/2)
						&& e.getY()>=(150) && e.getY()<=(150+53)) {
					upDate();
				}
			}
        	
        	if (state == MAIN_MENU) {
        		System.out.println(e.getX()+" "+ e.getY());
				if (e.getX()>=(Main.width/2-180/2) && e.getX()<=(Main.width/2+180/2)
						&& e.getY()>=(150) && e.getY()<=(150+53)) {
					play();
				}
			}
        	
        }
   }
	
	private class myKeyAdapter extends KeyAdapter {
	
		public void keyPressed(KeyEvent e) {
		
	
		}
		public void keyReleased(KeyEvent e) {
	
		}

	}
	
	public void paint(Graphics g) {
		
		g = (Graphics2D) g;
		
		g.fillRect(0, 0, Main.width, Main.height);
		
		g.setColor(Color.white);
		Font font = new Font("Arial", Font.BOLD, 20);
		g.setFont(font);
		
		/*for (int y = 0; y < Main.height/50; y++)
			for (int x = 0; x < Main.width/50; x++)
				g.drawImage(bg, x*50, y*50, null);*/
		g.drawImage(bg, 0, 0, 631, 354, null);
		
		if (state == INSTALL) {
			g.drawImage(install, Main.width/2-180/2, 150, null);
		}
		if (state == UPDATE) {
			g.drawImage(update, Main.width/2-180/2, 150, null);
		}
		
		if (state == DOWNLOADING || state == HASHING) {
			if (state == DOWNLOADING)
				g.drawString("DOWNLOADING...", Main.width/2-50-25, Main.height-70-30);
			else
			g.drawString("Verifying signature...", Main.width/2-50-25, Main.height-70-30);
			
			if (down != null) {
				if (down.status == down.COMPLETE)
					state = MAIN_MENU;
				g.drawImage(pb, 10, Main.height-70, (int)(((float)(Main.width-20)/down.size)*down.downloaded), 10, null);
				//System.out.println("PB:"+(Main.width-20)+" "+down.size+" "+down.downloaded);
				//System.out.println((((float)(Main.width-20)/down.size)*down.downloaded));
				
				g.drawImage(pb, 10, Main.height-70-5+30, (int)(((float)(Main.width-20)/down.files.size())*down.index), 10, null);
			}
			if (md5 != null)
				g.drawImage(pb, 10, Main.height-70, (Main.width-20)/(md5.filesCount>0?md5.filesCount:1)*md5.index, 10, null);
			
			Font font2 = new Font("Arial", Font.BOLD, 10);
			g.setFont(font2);
			
			if (down != null) {
			g.drawString(down.state, 10, Main.height-70-5);
			g.drawString("Files: "+down.index +"/"+down.files.size(), 10, Main.height-70-5+30-5);
			}
			if (md5 != null)
				g.drawString(down.state, 10, Main.height-50);
		}
		
		if (state == MAIN_MENU) {
			g.drawImage(play, Main.width/2-180/2, 150, null);
		}
		
		
	}
	
	public void play() {
		try {
			Runtime.getRuntime().exec("java -jar "+Main.dir+"Supernova.jar");
		} catch (IOException e) {
			e.printStackTrace();
			upDate();
		}
	}
	
	public void upDate()  {
		
		flag = (state == INSTALL);
		state = DOWNLOADING;
		
		down = new Download(Main.server, Main.dir);
		down.download(Main.files);
		Main.runThread(down, false);
		
	}

	public void actionPerformed(ActionEvent e) {
	    repaint();
	}
	
	public void runThread(Runnable object) {
		try {
			thread = new Thread(object);
			thread.run();
		}
		catch (Exception ex) {
			System.out.println("___Error__"+ex.getMessage());
		}
	}

	private void error(String text) {
			JOptionPane.showMessageDialog(null,text);
			System.exit(0);
	}

	public void run() {
		
	}

}