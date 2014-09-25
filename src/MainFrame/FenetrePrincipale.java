package MainFrame;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import java.awt.GridLayout;

import javax.swing.JTabbedPane;
import java.awt.Window.Type;

public class FenetrePrincipale extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FenetrePrincipale frame = new FenetrePrincipale();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FenetrePrincipale() {
		setTitle("BlackBox Project");
		try
		{
		  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e){}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Param\u00E8tres");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmPseudo = new JMenuItem("Pseudo");
		mnNewMenu.add(mntmPseudo);
		
		JMenuItem mntmQuitter = new JMenuItem("Quitter");
		mnNewMenu.add(mntmQuitter);
		
		JMenu mnRseau = new JMenu("R\u00E9seau");
		menuBar.add(mnRseau);
		
		JMenuItem mntmRejoindreUnRseau = new JMenuItem("Rejoindre un r\u00E9seau");
		mnRseau.add(mntmRejoindreUnRseau);
		
		JMenuItem mntmQuitterLeRseau = new JMenuItem("Quitter le r\u00E9seau");
		mnRseau.add(mntmQuitterLeRseau);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1, 0, 0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Client", null, panel, null);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Serveur", null, panel_1, null);
	}

}
