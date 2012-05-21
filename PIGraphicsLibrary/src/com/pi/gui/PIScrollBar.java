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

public class PIScrollBar extends PIContainer {
	private float step = 0.1f;
	private float scrollAmount = 0f;
	private PIButton scrollDown, scrollUp;
	private PIComponent scrollContainer, scrollCurrent;
	private boolean horizontal;

	private List<ScrollBarListener> scrollListeners = new ArrayList<ScrollBarListener>();
	private String overlayText = null;

	public PIScrollBar(final boolean horizontal) {
		this.horizontal = horizontal;

		MouseListener mListen = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getSource() == scrollContainer) {
					if (horizontal) {
						setScrollAmount(((float) e.getX())
								/ ((float) scrollContainer.getWidth()));
					} else {
						setScrollAmount(((float) e.getY())
								/ ((float) scrollContainer.getHeight()));
					}
					triggerEvent();
				} else if (e.getSource() == scrollUp) {
					setScrollAmount(scrollAmount - step);
					triggerEvent();
				} else if (e.getSource() == scrollDown) {
					setScrollAmount(scrollAmount + step);
					triggerEvent();
				}
			}
		};

		scrollDown = new PIButton();
		scrollDown.setContent(horizontal ? ">" : "\\/");
		scrollUp = new PIButton();
		scrollUp.setContent(horizontal ? "<" : "/\\");
		scrollCurrent = new PIComponent();
		scrollContainer = new PIComponent();

		PIStyle style = GUIKit.defaultStyle.clone();
		style.background = new Color(0.2f, 0.2f, 0.2f, 0.2f);
		scrollContainer.setStyle(StyleType.Normal, style);
		style = style.clone();
		style.background = style.background.darker().darker();
		scrollCurrent.setStyle(StyleType.Normal, style);

		scrollUp.addMouseListener(mListen);
		scrollDown.addMouseListener(mListen);
		scrollContainer.addMouseListener(mListen);

		add(scrollDown);
		add(scrollUp);
		add(scrollCurrent);
		add(scrollContainer);

		setSize(width, height);
	}

	public void setOverlay(String s) {
		this.overlayText = s;
	}

	private void triggerEvent() {
		ScrollEvent evt = new ScrollEvent(this, scrollAmount);
		for (ScrollBarListener l : scrollListeners)
			l.onScroll(evt);
	}

	public void addScrollBarListener(ScrollBarListener l) {
		scrollListeners.add(l);
	}

	public void removeScrollBarListener(ScrollBarListener l) {
		scrollListeners.remove(l);
	}

	@Override
	public void render(IGraphics g) {
		super.render(g);
		if (overlayText != null) {
			PIStyle style = scrollContainer.getCurrentStyle();
			Rectangle bounds = scrollContainer.getAbsoluteBounds();
			if (style.foreground != null && style.font != null
					&& content != null) {
				g.drawWrappedText(bounds, style.font, overlayText,
						style.foreground, style.hAlign, style.vAlign);
			}
		}
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		int size = horizontal ? height : width;
		scrollDown.setSize(size, size);
		scrollUp.setSize(size, size);
		scrollUp.setLocation(0, 0);
		if (horizontal) {
			scrollDown.setLocation(width - size, 0);
			scrollContainer.setLocation(size, 0);
			scrollContainer.setSize(width - (size * 2), height);
			scrollCurrent.setSize((int) (scrollContainer.getWidth() * step),
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

	public void setStep(float step) {
		this.step = step;
		setSize(getWidth(), getHeight());
	}

	public float getScrollAmount() {
		return scrollAmount;
	}

	public void setScrollAmount(float f) {
		this.scrollAmount = Math.max(0, Math.min(1, f));
		if (horizontal) {
			scrollCurrent
					.setLocation(
							scrollContainer.getX()
									+ (int) ((scrollContainer.getWidth() - scrollCurrent
											.getWidth()) * scrollAmount), 0);
		} else {
			scrollCurrent
					.setLocation(
							0,
							scrollContainer.getY()
									+ (int) ((scrollContainer.getHeight() - scrollCurrent
											.getHeight()) * scrollAmount));
		}
		scrollCurrent.compile();
	}

	public static interface ScrollBarListener {
		public void onScroll(ScrollEvent e);
	}

	public static class ScrollEvent extends AWTEvent {
		private static final long serialVersionUID = 1L;
		public static final int SCROLL_EVENT_ID = 91734;
		private float amount;

		public ScrollEvent(Object source, float amount) {
			super(source, SCROLL_EVENT_ID);
			this.amount = amount;
		}

		public float getScrollPosition() {
			return amount;
		}
	}
}
