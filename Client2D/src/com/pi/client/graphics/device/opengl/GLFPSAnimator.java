package com.pi.client.graphics.device.opengl;

import com.jogamp.opengl.util.AnimatorBase;
import com.pi.client.graphics.device.DisplayManager;

/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 * Copyright (c) 2010 JogAmp Community. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 * 
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */

public class GLFPSAnimator extends AnimatorBase {

	protected ThreadGroup threadGroup;
	private Runnable runnable;
	protected boolean isAnimating;
	protected boolean pauseIssued;
	protected volatile boolean stopIssued;
	protected final DisplayManager mgr;

	public GLFPSAnimator(DisplayManager mgr) {
		super();
		this.mgr = mgr;
		if (DEBUG) {
			System.err.println("Animator created");
		}
	}

	public GLFPSAnimator(DisplayManager mgr, ThreadGroup tg) {
		super();
		this.mgr = mgr;
		threadGroup = tg;

		if (DEBUG) {
			System.err.println("Animator created, ThreadGroup: " + threadGroup);
		}
	}

	protected String getBaseName(String prefix) {
		return prefix + "Animator";
	}

	private final void setIsAnimatingSynced(boolean v) {
		super.stateSync.lock();
		try {
			isAnimating = v;
		} finally {
			stateSync.unlock();
		}
	}

	class MainLoop implements Runnable {
		public String toString() {
			return "[started " + isStartedImpl() + ", animating "
					+ isAnimatingImpl() + ", paused " + isPausedImpl()
					+ ", drawable " + drawables.size() + "]";
		}

		public void run() {
			long lastFrame = -1;
			try {
				synchronized (GLFPSAnimator.this) {
					if (DEBUG) {
						System.err.println("Animator start:"
								+ Thread.currentThread() + ": " + toString());
					}
					resetCounter();
					animThread = Thread.currentThread();
					setIsAnimatingSynced(false); // barrier
					GLFPSAnimator.this.notifyAll();
				}

				while (!stopIssued) {
					synchronized (GLFPSAnimator.this) {
						// Don't consume CPU unless there is work to be done and
						// not paused
						while (!stopIssued && (pauseIssued || drawablesEmpty)) {
							boolean wasPaused = pauseIssued;
							if (DEBUG) {
								System.err.println("Animator pause:"
										+ Thread.currentThread() + ": "
										+ toString());
							}
							setIsAnimatingSynced(false); // barrier
							GLFPSAnimator.this.notifyAll();
							try {
								GLFPSAnimator.this.wait();
							} catch (InterruptedException e) {
							}

							if (wasPaused) {
								// resume from pause -> reset counter
								resetCounter();
								if (DEBUG) {
									System.err.println("Animator resume:"
											+ Thread.currentThread() + ": "
											+ toString());
								}
							}
						}
						if (!stopIssued && !isAnimating) {
							// resume from pause or drawablesEmpty,
							// implies !pauseIssued and !drawablesEmpty
							setIsAnimatingSynced(true);
							GLFPSAnimator.this.notifyAll();
						}
					} // sync Animator.this
					if (!stopIssued) {
						long time = mgr.minMSPerFrame
								- (System.currentTimeMillis() - lastFrame);
						if (lastFrame != -1 && time <= mgr.minMSPerFrame
								&& time > 0)
							try {
								Thread.sleep(time);
							} catch (InterruptedException e) {
							}
						lastFrame = System.currentTimeMillis();
						display();
					}
				}
			} finally {
				synchronized (GLFPSAnimator.this) {
					if (DEBUG) {
						System.err.println("Animator stop "
								+ Thread.currentThread() + ": " + toString());
					}
					stopIssued = false;
					pauseIssued = false;
					animThread = null;
					setIsAnimatingSynced(false); // barrier
					GLFPSAnimator.this.notifyAll();
				}
			}
		}
	}

	private final boolean isStartedImpl() {
		return animThread != null;
	}

	public final boolean isStarted() {
		stateSync.lock();
		try {
			return animThread != null;
		} finally {
			stateSync.unlock();
		}
	}

	private final boolean isAnimatingImpl() {
		return animThread != null && isAnimating;
	}

	public final boolean isAnimating() {
		stateSync.lock();
		try {
			return animThread != null && isAnimating;
		} finally {
			stateSync.unlock();
		}
	}

	private final boolean isPausedImpl() {
		return animThread != null && pauseIssued;
	}

	public final boolean isPaused() {
		stateSync.lock();
		try {
			return animThread != null && pauseIssued;
		} finally {
			stateSync.unlock();
		}
	}

	interface Condition {
		boolean result();
	}

	private synchronized void finishLifecycleAction(Condition condition) {
		boolean doWait = !impl.skipWaitForCompletion(animThread);
		if (doWait) {
			while (condition.result()) {
				try {
					wait();
				} catch (InterruptedException ie) {
				}
			}
		}
		if (DEBUG) {
			System.err.println("finishLifecycleAction("
					+ condition.getClass().getName() + "): finished - waited "
					+ doWait + ", started: " + isStartedImpl()
					+ ", animating: " + isAnimatingImpl() + ", paused: "
					+ isPausedImpl() + ", drawables " + drawables.size());
		}
	}

	public synchronized boolean start() {
		if (isStartedImpl()) {
			return false;
		}
		if (runnable == null) {
			runnable = new MainLoop();
		}
		resetCounter();
		String threadName = Thread.currentThread().getName() + "-" + baseName;
		Thread thread;
		if (null == threadGroup) {
			thread = new Thread(runnable, threadName);
		} else {
			thread = new Thread(threadGroup, runnable, threadName);
		}
		thread.start();
		finishLifecycleAction(waitForStartedCondition);
		return true;
	}

	private class WaitForStartedCondition implements Condition {
		public boolean result() {
			return !isStartedImpl() || (!drawablesEmpty && !isAnimating);
		}
	}

	Condition waitForStartedCondition = new WaitForStartedCondition();

	public synchronized boolean stop() {
		if (!isStartedImpl()) {
			return false;
		}
		stopIssued = true;
		notifyAll();
		finishLifecycleAction(waitForStoppedCondition);
		return true;
	}

	private class WaitForStoppedCondition implements Condition {
		public boolean result() {
			return isStartedImpl();
		}
	}

	Condition waitForStoppedCondition = new WaitForStoppedCondition();

	public synchronized boolean pause() {
		if (!isStartedImpl() || pauseIssued) {
			return false;
		}
		stateSync.lock();
		try {
			pauseIssued = true;
		} finally {
			stateSync.unlock();
		}
		notifyAll();
		finishLifecycleAction(waitForPausedCondition);
		return true;
	}

	private class WaitForPausedCondition implements Condition {
		public boolean result() {
			// end waiting if stopped as well
			return isAnimating && isStartedImpl();
		}
	}

	Condition waitForPausedCondition = new WaitForPausedCondition();

	public synchronized boolean resume() {
		if (!isStartedImpl() || !pauseIssued) {
			return false;
		}
		stateSync.lock();
		try {
			pauseIssued = false;
		} finally {
			stateSync.unlock();
		}
		notifyAll();
		finishLifecycleAction(waitForResumeCondition);
		return true;
	}

	private class WaitForResumeCondition implements Condition {
		public boolean result() {
			// end waiting if stopped as well
			return !drawablesEmpty && !isAnimating && isStartedImpl();
		}
	}

	Condition waitForResumeCondition = new WaitForResumeCondition();
}
