/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.forms.widgets;

//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;

//import org.eclipse.core.runtime.FileLocator;
//import org.eclipse.core.runtime.Path;
//import org.eclipse.core.runtime.Platform;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.PaintEvent;
//import org.eclipse.swt.events.PaintListener;
//import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

//XXX RAP specific
//public class BusyIndicator extends Canvas {
public class BusyIndicator extends Label {
	private static final int MARGIN = 0;

//	private ImageData[] progressData;

//	protected ImageLoader loader;

	protected Image image;

	protected Image animationImage;

	protected Thread busyThread;

	protected boolean stop;

	/**
	 * BusyWidget constructor comment.
	 * 
	 * @param parent
	 *            org.eclipse.swt.widgets.Composite
	 * @param style
	 *            int
	 */
	public BusyIndicator(Composite parent, int style) {
		super(parent, style);

		loadProgressImage();

//		addPaintListener(new PaintListener() {
//			public void paintControl(PaintEvent event) {
//				onPaint(event);
//			}
//		});
	}

	private void loadProgressImage() {
//		InputStream is = null;
//		Bundle bundle = Platform.getBundle("org.eclipse.ui.forms"); //$NON-NLS-1$
//	    URL url = FileLocator.find(bundle, new Path("$nl$/icons/progress/ani/progress.gif"),null); //$NON-NLS-1$
//		if (url != null) {
//			try {
//				url = FileLocator.resolve(url);
//				is = url.openStream();
//			} catch (IOException e) {
//			    is = null;
//			}
//		}
//		if (is != null) {
//			loader = new ImageLoader();
//			try {
//				progressData = loader.load(is);
//			} catch (IllegalArgumentException e) {
//			}
//			try {
//				is.close();
//			} catch (IOException e) {
//			}
//		}
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point size = new Point(0, 0);
		if (image != null) {
			Rectangle ibounds = image.getBounds();
			size.x = ibounds.width;
			size.y = ibounds.height;
		}
//		if (loader != null && isBusy()) {
//			size.x = Math.max(size.x, loader.logicalScreenWidth);
//			size.y = Math.max(size.y, loader.logicalScreenHeight);
//		}
		size.x += MARGIN + MARGIN;
		size.y += MARGIN + MARGIN;
		return size;
	}

//	/**
//	 * Creates a thread to animate the image.
//	 */
//	protected synchronized void createBusyThread() {
//		if (busyThread != null)
//			return;
//
//		stop = false;
//		final Display display = getDisplay();
//		final Image offScreenImage = new Image(display,
//				loader.logicalScreenWidth,
//				loader.logicalScreenHeight);
//		final GC offScreenImageGC = new GC(offScreenImage);
//		busyThread = new Thread() {
//			private Image timage;
//
//			public void run() {
//				try {
//					/*
//					 * Create an off-screen image to draw on, and fill it with
//					 * the shell background.
//					 */
//					FormUtil.setAntialias(offScreenImageGC, SWT.ON);
//					display.syncExec(new Runnable() {
//						public void run() {
//							if (!isDisposed())
//								drawBackground(offScreenImageGC, 0, 0,
//										loader.logicalScreenWidth,
//										loader.logicalScreenHeight);
//						}
//					});
//					if (isDisposed())
//						return;
//
//					/*
//					 * Create the first image and draw it on the off-screen
//					 * image.
//					 */
//					int imageDataIndex = 0;
//					ImageData imageData = progressData[imageDataIndex];
//					if (timage != null && !timage.isDisposed())
//						timage.dispose();
//					timage = new Image(display, imageData);
//					offScreenImageGC.drawImage(timage, 0, 0,
//							imageData.width, imageData.height, imageData.x,
//							imageData.y, imageData.width, imageData.height);
//
//					/*
//					 * Now loop through the images, creating and drawing
//					 * each one on the off-screen image before drawing it on
//					 * the shell.
//					 */
//					int repeatCount = loader.repeatCount;
//					while (loader.repeatCount == 0 || repeatCount > 0) {
//						if (stop || isDisposed())
//							break;
//						switch (imageData.disposalMethod) {
//						case SWT.DM_FILL_BACKGROUND:
//							/*
//							 * Fill with the background color before
//							 * drawing.
//							 */
//							/*
//							 * offScreenImageGC.fillRectangle(imageData.x,
//							 * imageData.y, imageData.width,
//							 * imageData.height);
//							 */
//							final ImageData fimageData = imageData;
//							display.syncExec(new Runnable() {
//								public void run() {
//									if (!isDisposed()) {
//										drawBackground(offScreenImageGC, fimageData.x,
//												fimageData.y, fimageData.width,
//												fimageData.height);
//									}
//								}
//							});
//							break;
//						case SWT.DM_FILL_PREVIOUS:
//							/* Restore the previous image before drawing. */
//							offScreenImageGC.drawImage(timage, 0, 0,
//									imageData.width, imageData.height,
//									imageData.x, imageData.y,
//									imageData.width, imageData.height);
//							break;
//						}
//
//						imageDataIndex = (imageDataIndex + 1)
//								% progressData.length;
//						imageData = progressData[imageDataIndex];
//						timage.dispose();
//						timage = new Image(display, imageData);
//						offScreenImageGC.drawImage(timage, 0, 0,
//								imageData.width, imageData.height,
//								imageData.x, imageData.y, imageData.width,
//								imageData.height);
//
//						/* Draw the off-screen image to the shell. */
//						animationImage = offScreenImage;
//						display.syncExec(new Runnable() {
//							public void run() {
//								if (!isDisposed())
//									redraw();
//							}
//						});
//						/*
//						 * Sleep for the specified delay time (adding
//						 * commonly-used slow-down fudge factors).
//						 */
//						try {
//							int ms = imageData.delayTime * 10;
//							if (ms < 20)
//								ms += 50;
//							if (ms < 30)
//								ms += 20;
//							Thread.sleep(ms);
//						} catch (InterruptedException e) {
//						}
//
//						/*
//						 * If we have just drawn the last image, decrement
//						 * the repeat count and start again.
//						 */
//						if (imageDataIndex == progressData.length - 1)
//							repeatCount--;
//					}
//				} catch (Exception e) {
//					// Trace.trace(Trace.WARNING, "Busy error", e);
//					// //$NON-NLS-1$
//				} finally {
//					display.syncExec(new Runnable() {
//						public void run() {
//							if (offScreenImage != null
//									&& !offScreenImage.isDisposed())
//								offScreenImage.dispose();
//							if (offScreenImageGC != null
//									&& !offScreenImageGC.isDisposed())
//								offScreenImageGC.dispose();
//						}
//					});
//					if (timage != null && !timage.isDisposed())
//						timage.dispose();
//				}
//				if (busyThread == null)
//					display.syncExec(new Runnable() {
//						public void run() {
//							animationImage = null;
//							if (!isDisposed())
//								redraw();
//						}
//					});
//			}
//		};
//		busyThread.setPriority(Thread.NORM_PRIORITY + 2);
//		busyThread.setDaemon(true);
//		busyThread.start();
//	}

	public void dispose() {
		stop = true;
		busyThread = null;
		super.dispose();
	}

	/**
	 * Return the image or <code>null</code>.
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Returns true if it is currently busy.
	 * 
	 * @return boolean
	 */
	public boolean isBusy() {
		return (busyThread != null);
	}

	/*
	 * Process the paint event
	 */
//	protected void onPaint(PaintEvent event) {
//		if (animationImage != null && animationImage.isDisposed()) {
//			animationImage = null;
//		}
//		Rectangle rect = getClientArea();
//		if (rect.width == 0 || rect.height == 0)
//			return;
//
//		GC gc = event.gc;
//		Image activeImage = animationImage != null ? animationImage : image;
//		if (activeImage != null) {
//			Rectangle ibounds = activeImage.getBounds();
//			gc.drawImage(activeImage, rect.width / 2 - ibounds.width / 2,
//					rect.height / 2 - ibounds.height / 2);
//		}
//	}

	/**
	 * Sets the indicators busy count up (true) or down (false) one.
	 * 
	 * @param busy
	 *            boolean
	 */
	public synchronized void setBusy(boolean busy) {
		if (busy) {
			if (busyThread == null) {
//				createBusyThread();
			}
		} else {
			if (busyThread != null) {
				stop = true;
				busyThread = null;
			}
		}
	}

	/**
	 * Set the image. The value <code>null</code> clears it.
	 */
	public void setImage(Image image) {
		if (image != this.image && !isDisposed()) {
			this.image = image;
//			redraw();
		}
	}
}
