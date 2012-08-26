package com.pi.graphics.device.opengl;

/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved. Copyright (c)
 * 2010 JogAmp Community. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistribution of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS
 * LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT
 * OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed or intended for use in the
 * design, construction, operation or maintenance of any nuclear facility.
 * 
 * Sun gratefully acknowledges that this software was originally authored and
 * developed by Kenneth Bradley Russell and Christopher John Kline.
 */

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.media.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.AnimatorBase;

/**
 * An Animator subclass which attempts to achieve a target frames-per-second
 * rate to avoid using all CPU time. The target FPS is only an estimate and is
 * not guaranteed.
 */
public class FPSAnimator extends AnimatorBase {
	private static final long TERMINATION_TIMEOUT = 5000;
	private ScheduledExecutorService timer = null;
	private Runnable task = null;
	private int fps;
	private boolean scheduleAtFixedRate;
	private volatile boolean shouldRun;

	@Override
	protected String getBaseName(String prefix) {
		return "FPS" + prefix + "Animator";
	}

	/**
	 * Creates an FPSAnimator with a given target frames-per-second value.
	 * Equivalent to <code>FPSAnimator(null, fps)</code>.
	 */
	public FPSAnimator(int fps) {
		this(null, fps);
	}

	/**
	 * Creates an FPSAnimator with a given target frames-per-second value and a
	 * flag indicating whether to use fixed-rate scheduling. Equivalent to
	 * <code>FPSAnimator(null, fps,
    scheduleAtFixedRate)</code>.
	 */
	public FPSAnimator(int fps, boolean scheduleAtFixedRate) {
		this(null, fps, scheduleAtFixedRate);
	}

	/**
	 * Creates an FPSAnimator with a given target frames-per-second value and an
	 * initial drawable to animate. Equivalent to
	 * <code>FPSAnimator(null, fps, false)</code>.
	 */
	public FPSAnimator(GLAutoDrawable drawable, int fps) {
		this(drawable, fps, false);
	}

	/**
	 * Creates an FPSAnimator with a given target frames-per-second value, an
	 * initial drawable to animate, and a flag indicating whether to use
	 * fixed-rate scheduling.
	 */
	public FPSAnimator(GLAutoDrawable drawable, int fps,
			boolean scheduleAtFixedRate) {
		this.fps = fps;
		if (drawable != null) {
			add(drawable);
		}
		this.scheduleAtFixedRate = scheduleAtFixedRate;
	}

	@Override
	public final boolean isStarted() {
		stateSync.lock();
		try {
			return (timer != null);
		} finally {
			stateSync.unlock();
		}
	}

	@Override
	public final boolean isAnimating() {
		stateSync.lock();
		try {
			return (timer != null) && (task != null) && shouldRun;
		} finally {
			stateSync.unlock();
		}
	}

	@Override
	public final boolean isPaused() {
		stateSync.lock();
		try {
			return (timer != null) && (task == null);
		} finally {
			stateSync.unlock();
		}
	}

	private void startTask() {
		if (null != task) {
			return;
		}
		long delay = (long) (1000.0f / (float) fps);
		task = new Runnable() {
			@Override
			public void run() {
				if (FPSAnimator.this.shouldRun) {
					FPSAnimator.this.animThread =
							Thread.currentThread();
					// display impl. uses synchronized block on the animator
					// instance
					display();
				}
			}
		};

		fpsCounter.resetFPSCounter();
		shouldRun = true;

		if (scheduleAtFixedRate) {
			timer.scheduleAtFixedRate(task, 0, delay,
					TimeUnit.MILLISECONDS);
		} else {
			timer.scheduleWithFixedDelay(task, 0, delay,
					TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public synchronized boolean start() {
		if (timer != null) {
			return false;
		}
		stateSync.lock();
		try {
			timer = Executors.newScheduledThreadPool(1);
			startTask();
		} finally {
			stateSync.unlock();
		}
		return true;
	}

	/**
	 * Stops this FPSAnimator. Due to the implementation of the FPSAnimator it
	 * is not guaranteed that the FPSAnimator will be completely stopped by the
	 * time this method returns.
	 */
	@Override
	public synchronized boolean stop() {
		if (timer == null) {
			return false;
		}
		boolean terminated = false;
		stateSync.lock();
		try {
			shouldRun = false;
			if (null != timer) {
				timer.shutdown();
				try {
					terminated =
							timer.awaitTermination(
									TERMINATION_TIMEOUT,
									TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
				}
				timer = null;
			}
			task = null;
			animThread = null;
			try {
				Thread.sleep(20); // ~ 1/60 hz wait, since we can't ctrl stopped
									// threads
			} catch (InterruptedException e) {
			}
		} finally {
			stateSync.unlock();
		}
		return terminated;
	}

	@Override
	public synchronized boolean pause() {
		if (timer == null) {
			return false;
		}
		stateSync.lock();
		try {
			shouldRun = false;
			try {
				Thread.sleep(20); // ~ 1/60 hz wait, since we can't ctrl stopped
									// threads
			} catch (InterruptedException e) {
			}
		} finally {
			stateSync.unlock();
		}
		return true;
	}

	@Override
	public synchronized boolean resume() {
		if (timer == null) {
			return false;
		}
		return true;
	}
}
