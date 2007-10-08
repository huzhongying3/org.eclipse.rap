/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.forms.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.internal.forms.widgets.FormUtil;

/**
 * This class is used to provide common scrolling services to a number of
 * controls in the toolkit. Classes that extend it are not required to implement
 * any method.
 * 
 * @since 1.0
 */
public abstract class SharedScrolledComposite extends ScrolledComposite {
//	private static final int H_SCROLL_INCREMENT = 5;

//	private static final int V_SCROLL_INCREMENT = 64;
	
	private boolean ignoreLayouts = true;

	private boolean ignoreResizes = false;

	private boolean expandHorizontal = false;

	private boolean expandVertical = false;

	private SizeCache contentCache = new SizeCache();

	private boolean reflowPending = false;

	private boolean delayedReflow = true;
	
	/**
	 * Creates the new instance.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the style to use
	 */
	public SharedScrolledComposite(Composite parent, int style) {
		super(parent, style);
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				if (!ignoreResizes) {
					scheduleReflow(false);
				}
			}
		});
		initializeScrollBars();
	}

	/**
	 * Sets the foreground of the control and its content.
	 * 
	 * @param fg
	 *            the new foreground color
	 */
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (getContent() != null)
			getContent().setForeground(fg);
	}

	/**
	 * Sets the background of the control and its content.
	 * 
	 * @param bg
	 *            the new background color
	 */
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (getContent() != null)
			getContent().setBackground(bg);
	}

	/**
	 * Sets the font of the form. This font will be used to render the title
	 * text. It will not affect the body.
	 */
	public void setFont(Font font) {
		super.setFont(font);
		if (getContent() != null)
			getContent().setFont(font);
	}

	/**
	 * Overrides 'super' to pass the proper colors and font
	 */
	public void setContent(Control content) {
		super.setContent(content);
		if (content != null) {
			content.setForeground(getForeground());
			content.setBackground(getBackground());
			content.setFont(getFont());
		}
	}

	/**
	 * If content is set, transfers focus to the content.
	 */
	public boolean setFocus() {
		boolean result;
		FormUtil.setFocusScrollingEnabled(this, false);
		if (getContent() != null)
			result = getContent().setFocus();
		else
			result = super.setFocus();
		FormUtil.setFocusScrollingEnabled(this, true);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Composite#layout(boolean)
	 */
	public void layout(boolean changed) {
		if (ignoreLayouts) {
			return;
		}

		ignoreLayouts = true;
		ignoreResizes = true;
		super.layout(changed);
		ignoreResizes = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.custom.ScrolledComposite#setExpandHorizontal(boolean)
	 */
	public void setExpandHorizontal(boolean expand) {
		expandHorizontal = expand;
		super.setExpandHorizontal(expand);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.custom.ScrolledComposite#setExpandVertical(boolean)
	 */
	public void setExpandVertical(boolean expand) {
		expandVertical = expand;
		super.setExpandVertical(expand);
	}

	/**
	 * Recomputes the body layout and the scroll bars. The method should be used
	 * when changes somewhere in the form body invalidate the current layout
	 * and/or scroll bars.
	 * 
	 * @param flushCache
	 *            if <code>true</code>, drop the cached data
	 */
	public void reflow(boolean flushCache) {
		Composite c = (Composite) getContent();
		Rectangle clientArea = getClientArea();
		if (c == null)
			return;

		contentCache.setControl(c);
		if (flushCache) {
			contentCache.flush();
		}
		setRedraw(false);
		Point newSize = contentCache.computeSize(FormUtil.getWidthHint(
				clientArea.width, c), FormUtil.getHeightHint(clientArea.height,
				c));

		// Point currentSize = c.getSize();
		if (!(expandHorizontal && expandVertical)) {
			c.setSize(newSize);
		}

		setMinSize(newSize);
		FormUtil.updatePageIncrement(this);

		ignoreLayouts = false;
		layout(flushCache);
		ignoreLayouts = true;

		contentCache.layoutIfNecessary();
		setRedraw(true);
	}

	private void updateSizeWhilePending() {
		Control c = getContent();
		Rectangle area = getClientArea();
		setMinSize(area.width, c.getSize().y);
	}

	private void scheduleReflow(final boolean flushCache) {
		if (delayedReflow) {
			if (reflowPending) {
				updateSizeWhilePending();
				return;
			}
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!isDisposed())
						reflow(flushCache);
					reflowPending = false;
				}
			});
			reflowPending = true;
		} else
			reflow(flushCache);
	}

	private void initializeScrollBars() {
		ScrollBar hbar = getHorizontalBar();
		if (hbar != null) {
//			hbar.setIncrement(H_SCROLL_INCREMENT);
		}
		ScrollBar vbar = getVerticalBar();
		if (vbar != null) {
//			vbar.setIncrement(V_SCROLL_INCREMENT);
		}
		FormUtil.updatePageIncrement(this);
	}

	/**
	 * Tests if the control uses delayed reflow.
	 * @return <code>true</code> if reflow requests will
	 * be delayed, <code>false</code> otherwise. 
	 */
	public boolean isDelayedReflow() {
		return delayedReflow;
	}

	/**
	 * Sets the delayed reflow feature. When used,
	 * it will schedule a reflow on resize requests
	 * and reject subsequent reflows until the
	 * scheduled one is performed. This improves
	 * performance by 
	 * @param delayedReflow
	 *            The delayedReflow to set.
	 */
	public void setDelayedReflow(boolean delayedReflow) {
		this.delayedReflow = delayedReflow;
	}
}
