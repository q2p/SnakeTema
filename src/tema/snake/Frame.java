package tema.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
final class Frame extends JFrame implements WindowListener, KeyListener {
	public Frame() {
		super("SnakeTema");
		setResizable(false);
		getContentPane().setPreferredSize(new Dimension(SnakeTema.minSizes, SnakeTema.minSizes));
		pack();

		addWindowListener(this);
		addKeyListener(this);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		setIconImage(null); // TODO
		
		// Отключение потери фокуса при нажатии F10
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F10"), "F10");
		getRootPane().getActionMap().put("F10", new AbstractAction() {public void actionPerformed(ActionEvent e) {}});
		
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	// Listeners
	public final void windowClosing(final WindowEvent e) {
		// TODO:
		System.exit(0);
	}

	public final void windowOpened(final WindowEvent e) {}
	public final void windowClosed(final WindowEvent e) {}
	public final void windowIconified(final WindowEvent e) {}
	public final void windowActivated(final WindowEvent e) {}
	public final void windowDeactivated(final WindowEvent e) {}
	public final void windowDeiconified(final WindowEvent e) {}

	public final void keyPressed(final KeyEvent ke) {
		SnakeTema.push(ke.getKeyCode());
	}

	public final void keyReleased(final KeyEvent ke) {
		SnakeTema.pull(ke.getKeyCode());
	}

	public final void keyTyped(final KeyEvent ke) {}
}