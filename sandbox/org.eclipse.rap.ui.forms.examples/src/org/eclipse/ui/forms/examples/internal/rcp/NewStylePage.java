/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.forms.examples.internal.rcp;

import java.text.MessageFormat;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
//import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.HyperlinkSettings;
//import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.examples.internal.ExamplesPlugin;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author dejan
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class NewStylePage extends FormPage {
	private static final String SHORT_TITLE = "Short Title";
	private static final String LONG_TITLE = "This title is somewhat longer";
	private static final String SHORT_MESSAGE = "A short {0} message";
	private static final String LONG_MESSAGE = "This {0} message is longer and will also compete with other header regions";
	private static final String[] MESSAGE_NAMES = { "text", "info", "warning",
			"error" };

	/**
	 * @param id
	 * @param title
	 */
	public NewStylePage(FormEditor editor) {
		super(editor, "newStyle", "New Style");
	}

	protected void createFormContent(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		final FormToolkit toolkit = managedForm.getToolkit();
		toolkit.getHyperlinkGroup().setHyperlinkUnderlineMode(
				HyperlinkSettings.UNDERLINE_HOVER);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 10;
		layout.marginWidth = 6;
		layout.horizontalSpacing = 20;
		form.getBody().setLayout(layout);

		Section section = toolkit.createSection(form.getBody(),
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| ExpandableComposite.EXPANDED);
		Composite client = toolkit.createComposite(section);
		section.setClient(client);
		section.setText("Header features");
		section
				.setDescription("Use the switches below to control basic heading parameters.");
		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		client.setLayout(layout);
		final Button tbutton = toolkit.createButton(client, "Add title",
				SWT.CHECK);
		final Button sbutton = toolkit.createButton(client, "Short title",
				SWT.RADIO);
		sbutton.setSelection(true);
		sbutton.setEnabled(false);
		gd = new GridData();
		gd.horizontalIndent = 10;
		sbutton.setLayoutData(gd);
		final Button lbutton = toolkit.createButton(client, "Long title",
				SWT.RADIO);
		gd = new GridData();
		gd.horizontalIndent = 10;
		lbutton.setLayoutData(gd);
		lbutton.setEnabled(false);
		tbutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTitle(form, tbutton.getSelection(), sbutton
						.getSelection());
				sbutton.setEnabled(tbutton.getSelection());
				lbutton.setEnabled(tbutton.getSelection());
			}
		});
		sbutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTitle(form, tbutton.getSelection(), sbutton
						.getSelection());
			}
		});
		lbutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTitle(form, tbutton.getSelection(), sbutton
						.getSelection());
			}
		});
		final Button ibutton = toolkit.createButton(client, "Add image",
				SWT.CHECK);
		ibutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateImage(form, ibutton.getSelection());
			}
		});

		final Button tbbutton = toolkit.createButton(client, "Add tool bar",
				SWT.CHECK);
		
		final Button albutton = toolkit.createButton(client, "Set tool bar allignment to SWT.BOTTOM",
				SWT.CHECK);
		albutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				form.getForm().setToolBarVerticalAlignment(albutton.getSelection()?SWT.BOTTOM:SWT.TOP);
				form.reflow(true);
			}
		});
		gd = new GridData();
		gd.horizontalIndent = 10;
		albutton.setLayoutData(gd);
		albutton.setEnabled(false);
		tbbutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addToolBar(toolkit, form, tbbutton.getSelection());
				albutton.setEnabled(tbbutton.getSelection());
			}
		});

//		final Button gbutton = toolkit.createButton(client,
//				"Paint background gradient", SWT.CHECK);
//		gbutton.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				addHeadingGradient(toolkit, form, gbutton.getSelection());
//			}
//		});

		final Button clbutton = toolkit.createButton(client, "Add head client",
				SWT.CHECK);
		clbutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addHeadClient(toolkit, form, clbutton.getSelection());
			}
		});

		final Button mbutton = toolkit.createButton(client,
				"Add drop-down menu", SWT.CHECK);
		mbutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addMenu(toolkit, form, mbutton.getSelection());
			}
		});
		
		section = toolkit.createSection(form.getBody(),
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| ExpandableComposite.EXPANDED);
		client = toolkit.createComposite(section);
		section.setClient(client);
		section.setText("Messages and active state");
		section
				.setDescription("Use the buttons below to control messages and active state.");
		gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);

		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 4;
		client.setLayout(layout);

		final Button shortMessage = toolkit.createButton(client,
				"Short message", SWT.RADIO);
		shortMessage.setSelection(true);
		gd = new GridData();
		gd.horizontalSpan = 4;
		shortMessage.setLayoutData(gd);
		final Button longMessage = toolkit.createButton(client, "Long message",
				SWT.RADIO);
		gd = new GridData();
		gd.horizontalSpan = 4;
		longMessage.setLayoutData(gd);

		final IHyperlinkListener listener = new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				String title = e.getLabel();
				String details = (String)e.getHref();
				if (details==null) {
					details = title;
					title = null;
				}
				switch (form.getForm().getMessageType()) {
				case IMessageProvider.NONE:
				case IMessageProvider.INFORMATION:
					if (title==null)
						title = "Forms Information";
					MessageDialog.openInformation(form.getShell(), title, details);
					break;
				case IMessageProvider.WARNING:
					if (title==null)
						title = "Forms Warning";
					MessageDialog.openWarning(form.getShell(), title, details);
					break;
				case IMessageProvider.ERROR:
					if (title==null)
						title = "Forms Error";
					MessageDialog.openError(form.getShell(), title, details);
					break;
				}
			}
		};

		final Button hyperMessage = toolkit.createButton(client,
				"Message as hyperlink", SWT.CHECK);
		gd = new GridData();
		gd.horizontalSpan = 4;
		hyperMessage.setLayoutData(gd);
		hyperMessage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (hyperMessage.getSelection())
					form.getForm().addMessageHyperlinkListener(listener);
				else
					form.getForm().removeMessageHyperlinkListener(listener);
			}
		});

		shortMessage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				form.setMessage(getErrorMessage(form.getMessageType(),
						longMessage.getSelection()), form.getMessageType());
			}
		});
		longMessage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				form.setMessage(getErrorMessage(form.getMessageType(),
						longMessage.getSelection()), form.getMessageType());
			}
		});

		Button error = toolkit.createButton(client, "Error", SWT.PUSH);
		error.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				form.setMessage(getErrorMessage(IMessageProvider.ERROR,
						longMessage.getSelection()), IMessageProvider.ERROR);

			}
		});
		Button warning = toolkit.createButton(client, "Warning", SWT.PUSH);
		warning.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				form.setMessage(getErrorMessage(IMessageProvider.WARNING,
						longMessage.getSelection()), IMessageProvider.WARNING);
			}
		});
		Button info = toolkit.createButton(client, "Info", SWT.PUSH);
		info.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				form.setMessage(getErrorMessage(IMessageProvider.INFORMATION,
						longMessage.getSelection()),
						IMessageProvider.INFORMATION);
			}
		});
		Button cancel = toolkit.createButton(client, "Cancel", SWT.PUSH);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				form.setMessage(null, 0);
			}
		});

		final Button busy = toolkit.createButton(client, "Start Progress",
				SWT.PUSH);
		busy.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// IWorkbenchSiteProgressService service =
				// (IWorkbenchSiteProgressService)getSite().getAdapter(IWorkbenchSiteProgressService.class);

				if (form.getForm().isBusy()) {
					form.getForm().setBusy(false);
					busy.setText("Start Progress");
				} else {
					form.getForm().setBusy(true);
					busy.setText("Stop Progress");
				}
			}
		});
		gd = new GridData();
		gd.horizontalSpan = 2;
		busy.setLayoutData(gd);
	}

//	private void addHeadingGradient(FormToolkit toolkit, ScrolledForm form,
//			boolean add) {
//		FormColors colors = toolkit.getColors();
//		Color top = colors.getColor(IFormColors.H_GRADIENT_END);
//		Color bot = colors.getColor(IFormColors.H_GRADIENT_START);
//		if (add)
//			form.getForm().setTextBackground(new Color[] { top, bot },
//					new int[] { 100 }, true);
//		else {
//			form.getForm().setTextBackground(null, null, false);
//			form.getForm().setBackground(colors.getBackground());
//		}
//		form.getForm().setHeadColor(IFormColors.H_BOTTOM_KEYLINE1,
//				add ? colors.getColor(IFormColors.H_BOTTOM_KEYLINE1) : null);
//		form.getForm().setHeadColor(IFormColors.H_BOTTOM_KEYLINE2,
//				add ? colors.getColor(IFormColors.H_BOTTOM_KEYLINE2) : null);
//		form.getForm().setHeadColor(IFormColors.H_HOVER_LIGHT,
//				add ? colors.getColor(IFormColors.H_HOVER_LIGHT) : null);
//		form.getForm().setHeadColor(IFormColors.H_HOVER_FULL,
//				add ? colors.getColor(IFormColors.H_HOVER_FULL) : null);
//		form.getForm().setHeadColor(IFormColors.TB_TOGGLE,
//				add ? colors.getColor(IFormColors.TB_TOGGLE) : null);
//		form.getForm().setHeadColor(IFormColors.TB_TOGGLE_HOVER,
//				add ? colors.getColor(IFormColors.TB_TOGGLE_HOVER) : null);
//		form.getForm().setSeparatorVisible(add);
//		form.reflow(true);
//		form.redraw();
//	}

	private String getErrorMessage(int type, boolean longMessage) {
		String name = MESSAGE_NAMES[type];
		if (longMessage)
			return MessageFormat.format(LONG_MESSAGE, new Object[] { name });
		else
			return MessageFormat.format(SHORT_MESSAGE, new Object[] { name });
	}

	private void updateTitle(ScrolledForm form, boolean addTitle,
			boolean shortTitle) {
		if (addTitle) {
			String text = shortTitle ? SHORT_TITLE : LONG_TITLE;
			form.setText(text);
		} else {
			form.setText(null);
		}
	}

	private void updateImage(ScrolledForm form, boolean addImage) {
		if (addImage)
			form.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_DEF_VIEW));
		else
			form.setImage(null);
	}

	private void addToolBar(FormToolkit toolkit, ScrolledForm form, boolean add) {
		if (add) {
			Action haction = new Action("hor", Action.AS_RADIO_BUTTON) {
				public void run() {
				}
			};
			haction.setChecked(true);
			haction.setToolTipText("Horizontal orientation");
			haction.setImageDescriptor(ExamplesPlugin.getDefault()
					.getImageRegistry().getDescriptor(
							ExamplesPlugin.IMG_HORIZONTAL));
			Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) {
				public void run() {
				}
			};
			vaction.setChecked(false);
			vaction.setToolTipText("Vertical orientation");
			vaction.setImageDescriptor(ExamplesPlugin.getDefault()
					.getImageRegistry().getDescriptor(
							ExamplesPlugin.IMG_VERTICAL));
			ControlContribution save = new ControlContribution("save") {
				protected Control createControl(Composite parent) {
					Button saveButton = new Button(parent, SWT.PUSH);
					saveButton.setText("Save");
					return saveButton;
				}
			};
			form.getToolBarManager().add(haction);
			form.getToolBarManager().add(vaction);
			form.getToolBarManager().add(save);
			form.getToolBarManager().update(true);
		} else {
			form.getToolBarManager().removeAll();
		}
		form.reflow(true);
	}

	private void addMenu(FormToolkit toolkit, ScrolledForm form, boolean add) {
		if (add) {
			Action haction = new Action("hor", Action.AS_RADIO_BUTTON) {
				public void run() {
				}
			};
			haction.setChecked(true);
			haction.setText("Horizontal");
			haction.setToolTipText("Horizontal orientation");
			haction.setImageDescriptor(ExamplesPlugin.getDefault()
					.getImageRegistry().getDescriptor(
							ExamplesPlugin.IMG_HORIZONTAL));
			Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) {
				public void run() {
				}
			};
			vaction.setChecked(false);
			vaction.setText("Vertical");
			vaction.setToolTipText("Vertical orientation");
			vaction.setImageDescriptor(ExamplesPlugin.getDefault()
					.getImageRegistry().getDescriptor(
							ExamplesPlugin.IMG_VERTICAL));
			form.getForm().getMenuManager().add(haction);
			form.getForm().getMenuManager().add(vaction);
		} else {
			form.getForm().getMenuManager().removeAll();
		}
		form.reflow(true);
	}

	private void addHeadClient(FormToolkit toolkit, ScrolledForm form,
			boolean add) {
		if (add) {
			Composite headClient = new Composite(form.getForm().getHead(),
					SWT.NULL);
			GridLayout glayout = new GridLayout();
			glayout.marginWidth = glayout.marginHeight = 0;
			glayout.numColumns = 3;
			headClient.setLayout(glayout);
			Text t = new Text(headClient, toolkit.getBorderStyle());
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			new Combo(headClient, SWT.NULL);
			new Combo(headClient, SWT.NULL);
//			toolkit.paintBordersFor(headClient);
			form.setHeadClient(headClient);
		} else {
			Control client = form.getForm().getHeadClient();
			if (client != null) {
				client.dispose();
				form.setHeadClient(null);
			}
		}
	}
}
