package com.pi.graphics.device;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import com.pi.common.game.ObjectHeap;
import com.pi.graphics.device.awt.AWTGraphics;

/**
 * A display manager that allows switching between {@link IGraphics}
 * implementations.
 * 
 * @author Westin
 * 
 */
public class DisplayManager {
	/**
	 * The font that is displayed by the frames per second counter.
	 */
	private static final Font FRAMES_PER_SECOND_FONT = Font
			.decode(Font.SERIF).deriveFont(10f);
	/**
	 * The maximum frames per second, or <code>-1</code> if not limited.
	 */
	public static final int MAXIMUM_FRAMES_PER_SECOND = 50;
	/**
	 * The minimum number of milliseconds for each frame.
	 */
	public static final long MINIMUM_MILLISECONDS_PER_FRAME =
			1000 / MAXIMUM_FRAMES_PER_SECOND;
	/**
	 * If this computer supports OpenGL.
	 */
	private boolean hasOpenGL = false;
	/**
	 * The currently open graphics instance.
	 */
	private IGraphics graphics;
	/**
	 * The render loop called each loop.
	 */
	private Renderable renderLoop;
	/**
	 * The device registration instance that this display manager is bound to.
	 */
	private DeviceRegistration dev;
	/**
	 * Should the frames per second be shown in the upper left corner.
	 */
	private boolean showFps = true;

	/**
	 * Gets a heap of all loaded graphics.
	 * 
	 * @return the graphics heap
	 */
	public final ObjectHeap<ObjectHeap<GraphicsStorage>> loadedGraphics() {
		if (graphics != null) {
			return graphics.loadedGraphics();
		} else {
			return null;
		}
	}

	/**
	 * Checks the cached value to see if this computer has Open GL.
	 * 
	 * @return <code>true</code> if this computer has OpenGL, <code>false</code>
	 *         if it does not.
	 */
	public final boolean hasOpenGL() {
		return hasOpenGL
				&& GraphicsMode.OpenGL.getGraphicsClass() != null;
	}

	/**
	 * Method that performs post initiation for this display manager. This
	 * method will check if the computer has Open GL, and sets the cached value.
	 */
	public void postInititation() {
		hasOpenGL = true;
		/*
		 * TODO try { client.getLog().info("Checking for opengl..."); new
		 * GLCapabilities(GLProfile.getDefault()); hasOpenGL = true; } catch
		 * (Exception e) { client.getLog().printStackTrace(e); }
		 * client.getLog().info( "We " + (hasOpenGL ? "" : "don't ") +
		 * "have OpenGL");
		 */
	}

	/**
	 * Creates a display manager registered on the specified device registration
	 * instance, and the specified default render loop.
	 * 
	 * @param reg the object to register this display on
	 * @param sRenderLoop the default render loop
	 */
	public DisplayManager(final DeviceRegistration reg,
			final Renderable sRenderLoop) {
		this.dev = reg;
		this.renderLoop = sRenderLoop;
		setMode(GraphicsMode.AWT);
	}

	/**
	 * Get the component that any listeners should be registered on.
	 * 
	 * @return the component to register listeners
	 */
	public final Component getListenerRegistration() {
		if (graphics != null) {
			return graphics.getCanvas();
		} else {
			return dev.getContainer();
		}
	}

	/**
	 * Get the source that this display manager is registered on.
	 * 
	 * @return the registration object
	 */
	public final DeviceRegistration getSource() {
		return dev;
	}

	/**
	 * Sets the graphics mode of this display manager.
	 * 
	 * @param m the new graphics mode
	 */
	public final void setMode(final GraphicsMode m) {
		try {
			// We also need to re-register all the listeners... *sigh*
			MouseListener[] mouse = null;
			MouseMotionListener[] mouseM = null;
			MouseWheelListener[] mouseW = null;
			KeyListener[] key = null;
			if (graphics != null) {

				key =
						getListenerRegistration()
								.getKeyListeners().clone();
				mouseW =
						getListenerRegistration()
								.getMouseWheelListeners()
								.clone();
				mouseM =
						getListenerRegistration()
								.getMouseMotionListeners()
								.clone();
				mouse =
						getListenerRegistration()
								.getMouseListeners().clone();
				for (MouseListener l : mouse) {
					getListenerRegistration()
							.removeMouseListener(l);
				}
				for (MouseWheelListener l : mouseW) {
					getListenerRegistration()
							.removeMouseWheelListener(l);
				}
				for (MouseMotionListener l : mouseM) {
					getListenerRegistration()
							.removeMouseMotionListener(l);
				}
				for (KeyListener l : key) {
					getListenerRegistration().removeKeyListener(
							l);
				}

				graphics.dispose();
			}
			graphics =
					m.getGraphicsClass()
							.getConstructor(DisplayManager.class)
							.newInstance(this);

			if (mouse != null) {
				for (MouseListener l : mouse) {
					getListenerRegistration()
							.addMouseListener(l);
				}
			}
			if (mouseW != null) {
				for (MouseWheelListener l : mouseW) {
					getListenerRegistration()
							.addMouseWheelListener(l);
				}
			}
			if (mouseM != null) {
				for (MouseMotionListener l : mouseM) {
					getListenerRegistration()
							.addMouseMotionListener(l);
				}
			}
			if (key != null) {
				for (KeyListener l : key) {
					getListenerRegistration().addKeyListener(l);
				}
			}

			getListenerRegistration()
					.setFocusTraversalKeysEnabled(false);
		} catch (Exception e) {
			dev.fatalError("Failed to switch to " + m.name());
			dev.getLog().printStackTrace(e);
		}
	}

	/**
	 * Gets the current graphics mode of this display manager.
	 * 
	 * @return the graphics mode
	 */
	public final GraphicsMode getMode() {
		for (GraphicsMode m : GraphicsMode.values()) {
			if (m.getGraphicsClass() != null
					&& graphics
							.getClass()
							.getCanonicalName()
							.equals(m.getGraphicsClass()
									.getCanonicalName())) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Render the contents of this display manager.
	 */
	public final void doRender() {
		renderLoop.render(graphics);
		if (showFps) {
			graphics.drawText("Fps: " + graphics.getFPS(),
					FRAMES_PER_SECOND_FONT.getSize(),
					FRAMES_PER_SECOND_FONT.getSize(),
					FRAMES_PER_SECOND_FONT, Color.GREEN);
		}
	}

	/**
	 * Disposes this display manager, and the related graphics instance.
	 */
	public final void dispose() {
		if (graphics != null) {
			graphics.dispose();
		}
	}

	/**
	 * Gets the render loop that this display manager renders to.
	 * 
	 * @return the render loop
	 */
	public final Renderable getRenderLoop() {
		return renderLoop;
	}

	/**
	 * An enum representing the available graphics modes for the display
	 * manager.
	 * 
	 * @author Westin
	 * 
	 */
	public static enum GraphicsMode {
		/**
		 * A graphics mode that uses the Open Graphics Library to render to the
		 * screen.
		 */
		OpenGL("com.pi.graphics.device.opengl.GLGraphics"),
		/**
		 * A graphics mode that uses the Java 2D library, or Abstract Windowing
		 * Toolkit (AWT).
		 */
		AWT(AWTGraphics.class);
		/**
		 * The class that is represented by this graphics mode.
		 */
		private Class<? extends IGraphics> clazz;

		/**
		 * Create a graphics mode that represents the provided class.
		 * 
		 * @param sClazz the class
		 */
		private GraphicsMode(
				final Class<? extends IGraphics> sClazz) {
			this.clazz = sClazz;
		}

		@SuppressWarnings("unchecked")
		private GraphicsMode(final String clazz) {
			try {
				Class<?> tmpClazz =
						GraphicsMode.class.getClassLoader()
								.loadClass(clazz);
				if (IGraphics.class.isAssignableFrom(tmpClazz)) {
					this.clazz =
							(Class<? extends IGraphics>) tmpClazz;
				} else {
					throw new ClassCastException(clazz
							+ " is not a subclass of "
							+ IGraphics.class.getName());
				}
			} catch (ClassNotFoundException e) {
				this.clazz = null;
			} catch (ClassCastException e) {
				this.clazz = null;
			}
		}

		/**
		 * Get the class represented by this graphics mode.
		 * 
		 * @return the class
		 */
		public Class<? extends IGraphics> getGraphicsClass() {
			return clazz;
		}
	}
}
