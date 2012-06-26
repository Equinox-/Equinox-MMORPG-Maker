package com.pi.gui;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import com.pi.graphics.device.IGraphics;
import com.pi.gui.PIStyle.StyleType;

/**
 * An extension of the PIContainer class that provides a scroll bar.
 * 
 * @author Westin
 * 
 */
public class PIScrollBar extends PIContainer {
	/**
	 * The default step value the scroll amount is incremented by when clicking
	 * on a direction button.
	 */
	private static final float DEFAULT_SCROLL_STEP = 0.1f;
	/**
	 * The normalized step value the scroll amount is incremented by when
	 * clicking on a direction button.
	 */
	private float step = DEFAULT_SCROLL_STEP;
	/**
	 * The currently scrolled amount.
	 */
	private float scrollAmount = 0f;
	/**
	 * The buttons for scrolling up and down by the amount defined in the
	 * {@link PIScrollBar#step} field.
	 */
	private PIButton scrollDown, scrollUp;

	/**
	 * The scroll bar background component.
	 */
	private PIComponent scrollContainer;
	/**
	 * The scroll bar current position.
	 */
	private PIComponent scrollCurrent;
	/**
	 * If this is a horizontal scroll bar.
	 */
	private final boolean horizontal;

	/**
	 * The list of scroll bar listeners.
	 */
	private List<ScrollBarListener> scrollListeners =
			new ArrayList<ScrollBarListener>();
	/**
	 * The text overlaid on this scroll bar.
	 */
	private String overlayText = null;

	/**
	 * Creates a scroll bar instance, vertically by default, horizontally if
	 * specified.
	 * 
	 * @param sHorizontal if this should be a horizontal scroll bar
	 */
	public PIScrollBar(final boolean sHorizontal) {
		this.horizontal = sHorizontal;

		MouseListener mListen = new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getSource() == scrollContainer) {
					if (horizontal) {
						setScrollAmount(((float) e.getX())
								/ ((float) scrollContainer
										.getWidth()));
					} else {
						setScrollAmount(((float) e.getY())
								/ ((float) scrollContainer
										.getHeight()));
					}
					triggerScrollEvent();
				} else if (e.getSource() == scrollUp) {
					setScrollAmount(scrollAmount - step);
					triggerScrollEvent();
				} else if (e.getSource() == scrollDown) {
					setScrollAmount(scrollAmount + step);
					triggerScrollEvent();
				}
			}
		};

		scrollDown = new PIButton();
		scrollUp = new PIButton();
		if (horizontal) {
			scrollDown.setContent(">");
			scrollUp.setContent("<");
		} else {
			scrollDown.setContent("\\/");
			scrollUp.setContent("/\\");
		}
		scrollCurrent = new PIComponent();
		scrollContainer = new PIComponent();

		// TODO: Make this use styles set in the GUIKit class
		PIStyle style = GUIKit.DEFAULT_STYLE.clone();
		style.background = new Color(0.2f, 0.2f, 0.2f, 0.2f);
		scrollContainer.setStyle(StyleType.NORMAL, style);
		style = style.clone();
		style.background = style.background.darker().darker();
		scrollCurrent.setStyle(StyleType.NORMAL, style);

		scrollUp.addMouseListener(mListen);
		scrollDown.addMouseListener(mListen);
		scrollContainer.addMouseListener(mListen);

		add(scrollDown);
		add(scrollUp);
		add(scrollCurrent);
		add(scrollContainer);

		setSize(width, height);
	}

	/**
	 * Sets the text overlay of this scroll bar.
	 * 
	 * @param s the new overlay
	 */
	public final void setOverlay(final String s) {
		this.overlayText = s;
	}

	/**
	 * Triggers the scroll event on all the scroll listeners.
	 */
	private void triggerScrollEvent() {
		ScrollEvent evt = new ScrollEvent(this, scrollAmount);
		for (ScrollBarListener l : scrollListeners) {
			l.onScroll(evt);
		}
	}

	/**
	 * Adds the specified scroll bar listener to the scroll bar listener list.
	 * 
	 * @param l the listener to add
	 */
	public final void addScrollBarListener(
			final ScrollBarListener l) {
		scrollListeners.add(l);
	}

	@Override
	public final void render(final IGraphics g) {
		super.render(g);
		if (overlayText != null) {
			PIStyle style = scrollContainer.getCurrentStyle();
			Rectangle bounds =
					scrollContainer.getAbsoluteBounds();
			if (style.foreground != null && style.font != null
					&& content != null) {
				g.drawWrappedText(bounds, style.font,
						overlayText, style.foreground,
						style.hAlign, style.vAlign);
			}
		}
	}

	@Override
	public final void setSize(final int width, final int height) {
		super.setSize(width, height);

		int size;
		if (horizontal) {
			size = height;
		} else {
			size = width;
		}
		scrollDown.setSize(size, size);
		scrollUp.setSize(size, size);
		scrollUp.setLocation(0, 0);
		if (horizontal) {
			scrollDown.setLocation(width - size, 0);
			scrollContainer.setLocation(size, 0);
			scrollContainer.setSize(width - (size * 2), height);
			scrollCurrent.setSize(
					(int) (scrollContainer.getWidth() * step),
					height);
		} else {
			scrollDown.setLocation(0, height - size);
			scrollContainer.setLocation(0, size);
			scrollContainer.setSize(width, height - (size * 2));
			scrollCurrent.setSize(width,
					(int) (scrollContainer.getHeight() * step));
		}
		setScrollAmount(scrollAmount);
		compile();
	}

	/**
	 * Sets the amount that the scroll amount is incremented by when clicking on
	 * a direction button.
	 * 
	 * @param sStep the step amount
	 */
	public final void setStep(final float sStep) {
		this.step = sStep;
		setSize(getWidth(), getHeight());
	}

	/**
	 * Gets the amount that the scroll amount is incremented by when clicking on
	 * a direction button.
	 * 
	 * @return the step amount
	 */
	public final float getScrollAmount() {
		return scrollAmount;
	}

	/**
	 * Sets the currently scrolled amount, limited between <code>0</code> and
	 * <code>1</code>.
	 * 
	 * @param f the new scroll amount
	 */
	public final void setScrollAmount(final float f) {
		this.scrollAmount = Math.max(0, Math.min(1, f));
		if (horizontal) {
			scrollCurrent
					.setLocation(
							scrollContainer.getX()
									+ (int) ((scrollContainer
											.getWidth() - scrollCurrent
											.getWidth()) * scrollAmount),
							0);
		} else {
			scrollCurrent
					.setLocation(
							0,
							scrollContainer.getY()
									+ (int) ((scrollContainer
											.getHeight() - scrollCurrent
											.getHeight()) * scrollAmount));
		}
		scrollCurrent.compile();
	}

	/**
	 * Interface describing a listener that listens to the current scroll value.
	 * 
	 * @author Westin
	 * 
	 */
	public interface ScrollBarListener {
		/**
		 * Called when the current scroll bar value changes.
		 * 
		 * @param e the scroll event
		 */
		void onScroll(ScrollEvent e);
	}

	/**
	 * A class that describes a scroll event.
	 * 
	 * @author Westin
	 * 
	 */
	public static class ScrollEvent extends AWTEvent {
		/**
		 * The event identification for the scroll event.
		 */
		public static final int SCROLL_EVENT_ID = 91734;
		/**
		 * The new scroll amount.
		 */
		private float amount;

		/**
		 * Creates a scroll event with the given source object, and the
		 * specified scroll position.
		 * 
		 * @param source the source event
		 * @param sAmount the scroll position
		 */
		public ScrollEvent(final Object source,
				final float sAmount) {
			super(source, SCROLL_EVENT_ID);
			this.amount = sAmount;
		}

		/**
		 * Gets the scroll position that this scroll event was assigned.
		 * 
		 * @return the scroll position
		 */
		public final float getScrollPosition() {
			return amount;
		}
	}
}
