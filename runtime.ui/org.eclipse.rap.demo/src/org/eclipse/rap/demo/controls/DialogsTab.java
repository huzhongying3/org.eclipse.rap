/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.rap.jface.dialogs.*;
import org.eclipse.rap.jface.window.IWindowCallback;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.widgets.*;
import org.eclipse.rap.ui.Activator;

class DialogsTab extends ExampleTab {

  private Label inputDlgResLabel;
  private Label loginDlgResLabel;
  private Label messageDlgResLabel;
  private Label errorDlgResLabel;

  public DialogsTab( final TabFolder folder ) {
    super( folder, "Dialogs" );
  }

  protected void createStyleControls() {
  }

  protected void createExampleControls( final Composite parent ) {
    
    parent.setLayout( new GridLayout() );
    Group group1 = new Group( parent, RWT.NONE );
    group1.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    group1.setText( "JFace Dialogs" );
    Group group2 = new Group( parent, RWT.NONE );
    group2.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    group2.setText( "Custom Dialogs" );
    
    // JFace input dialog
    Button showInputDlgButton = new Button( group1, RWT.PUSH );
    showInputDlgButton.setText( "Input Dialog" );
    showInputDlgButton.setBounds( 20, 30, 90, 25 );
    showInputDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showInputDialog();
      }
    });
    inputDlgResLabel = new Label( group1, RWT.NONE );
    inputDlgResLabel.setText( "Result:" );
    inputDlgResLabel.setBounds( 50, 60, 100, 20 );
    
    Button showMessageInfoDlgButton = new Button( group1, RWT.PUSH );
    showMessageInfoDlgButton.setText( "Info Message" );
    showMessageInfoDlgButton.setBounds( 20, 90, 90, 25 );
    showMessageInfoDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogInfo();
      }
    } );
    Button showMessageWarningDlgButton = new Button( group1, RWT.PUSH );
    showMessageWarningDlgButton.setText( "Warning Dialog" );
    showMessageWarningDlgButton.setBounds( 120, 90, 90, 25 );
    showMessageWarningDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogWarning();
      }
    } );
    Button showMessageErrorDlgButton = new Button( group1, RWT.PUSH );
    showMessageErrorDlgButton.setText( "Error Message" );
    showMessageErrorDlgButton.setBounds( 220, 90, 90, 25 );
    showMessageErrorDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogError();
      }
    } );
    Button showMessageQuestionDlgButton = new Button( group1, RWT.PUSH );
    showMessageQuestionDlgButton.setText( "Question Dialog" );
    showMessageQuestionDlgButton.setBounds( 20, 120, 90, 25 );
    showMessageQuestionDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogQuestion();
      }
    } );
    Button showMessageConfirmDlgButton = new Button( group1, RWT.PUSH );
    showMessageConfirmDlgButton.setText( "Confirm Message" );
    showMessageConfirmDlgButton.setBounds( 120, 120, 90, 25 );
    showMessageConfirmDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogConfirm();
      }
    } );
    messageDlgResLabel = new Label( group1, RWT.NONE );
    messageDlgResLabel.setText( "Result:" );
    messageDlgResLabel.setBounds( 50, 150, 100, 20 );

    Button showErrorDlgButton = new Button( group1, RWT.PUSH );
    showErrorDlgButton.setText( "Error Dialog" );
    showErrorDlgButton.setBounds( 20, 180, 90, 25 );
    showErrorDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showErrorDialog();
      }
    } );
    errorDlgResLabel = new Label( group1, RWT.NONE );
    errorDlgResLabel.setText( "Result:" );
    errorDlgResLabel.setBounds( 50, 210, 100, 40 );
    
    Button showLoginDlgButton = new Button( group2, RWT.PUSH );
    showLoginDlgButton.setText( "Login Dialog" );
    showLoginDlgButton.setBounds( 20, 30, 90, 25 );
    showLoginDlgButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        showLoginDialog();
      }
    } );
    loginDlgResLabel = new Label( group2, RWT.NONE );
    loginDlgResLabel.setText( "Result:" );
    loginDlgResLabel.setBounds( 50, 60, 100, 40 );
  }

  private void showInputDialog() {
    final IInputValidator val = new IInputValidator() {
      public String isValid( String newText ) {
        String result = null;
        if ( newText.length() < 5 ) {
          result = "Input text too short!";
        }
        return result;
      }
    };
    String title = "Input Dialog";
    String mesg = "Enter at least five characters";
    String def = "default text";
    final InputDialog dlg = new InputDialog( getShell(), title, mesg, def, val );
    dlg.open( new IWindowCallback() {
      public void windowClosed( int returnCode ) {
        if( returnCode == InputDialog.OK ) {
          inputDlgResLabel.setText( "Input Result: " + dlg.getValue() );
        } else {
          inputDlgResLabel.setText( "No Result" );
        }
      }
    } );  
  }
  
  private void showMessageDialogInfo() {
    String title = "Information";
    String mesg = "Beer and pizza go well together.";
    MessageDialog.openInformation( getShell(), title, mesg, new IWindowCallback() {
      public void windowClosed( int returnCode ) {
        messageDlgResLabel.setText( "Info closed (" + returnCode + ")" );
      }
    } );
  }

  private void showMessageDialogError() {
    String title = "Error";
    String mesg = "An everyday error occured.\n "
                  + "Nothing to get worried.";
    MessageDialog.openError( getShell(), title, mesg, new IWindowCallback() {
      public void windowClosed( int returnCode ) {
        messageDlgResLabel.setText( "Error closed (" + returnCode + ")" );
      }
    } );
  }

  private void showMessageDialogQuestion() {
    String title = "Question";
    String mesg = "Do you think you're smart?\n\n"
                  + "Your answer will not be recorded or evaluated "
                  + "nor does this question have any purpose apart from "
                  + "filling the empty space in this dialog window.";
    MessageDialog.openQuestion( getShell(), title, mesg, new IWindowCallback() {
      public void windowClosed( int returnCode ) {
        messageDlgResLabel.setText( "Question closed (" + returnCode + ")" );
      }
    } );
  }
  
  private void showMessageDialogConfirm() {
    String title = "Confirmation";
    String mesg = "Nothing will be done. Ok?";
    MessageDialog.openConfirm( getShell(), title, mesg, new IWindowCallback() {
      public void windowClosed( int returnCode ) {
        messageDlgResLabel.setText( "Confirm closed (" + returnCode + ")" );
      }
    } );
  }

  private void showMessageDialogWarning() {
    String title = "Warning";
    String mesg = "You have been warned.";
    MessageDialog.openWarning( getShell(), title, mesg, new IWindowCallback() {
      public void windowClosed( int returnCode ) {
        messageDlgResLabel.setText( "Warning closed (" + returnCode + ")" );
      }
    } );
  }
  
  private void showErrorDialog() {
    String title = "Error";
    int code = 23;
    String mesg = "An absolutetly weird error occured";
    String reason = "Don't know, it just happened ...";
    Exception exception = new IndexOutOfBoundsException( "negative index: -1" );
    exception = new RuntimeException( exception );
    IStatus status = new Status( IStatus.ERROR,
                                 Activator.PLUGIN_ID,
                                 code,
                                 reason ,
                                 exception );
    IWindowCallback callback = new IWindowCallback() {
      public void windowClosed( int returnCode ) {
        errorDlgResLabel.setText( "Error Dialog closed (" + returnCode + ")" );
      }
    };
    ErrorDialog.openError( getShell(), title, mesg, status, callback );
  }
  
  private void showLoginDialog() {
    String title = "Login";
    String message = "Please sign in with your username and password:";
    final LoginDialog loginDialog = new LoginDialog( getShell(),
                                                       title,
                                                       message,
                                                       "john" );
    loginDialog.open( new IWindowCallback() {
      public void windowClosed( int returnCode ) {
        String username = loginDialog.getUsername();
        String password = loginDialog.getPassword();
        loginDlgResLabel.setText( "Login Dialog User: "
                                  + username
                                  + ", Password: "
                                  + ( password == null
                                                      ? "n/a"
                                                      : password.length()
                                                        + " chars." )
                                  + " ("
                                  + returnCode
                                  + ")" );
      }
    } );
  }
}
