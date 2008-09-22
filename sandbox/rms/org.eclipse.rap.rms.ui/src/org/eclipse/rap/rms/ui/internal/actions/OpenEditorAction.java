// Created on 13.09.2007
package org.eclipse.rap.rms.ui.internal.actions;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rap.rms.ui.Constants;
import org.eclipse.rap.rms.ui.internal.RMSMessages;
import org.eclipse.rap.rms.ui.internal.datamodel.EntityAdapter;
import org.eclipse.rap.rms.ui.internal.datamodel.ILock;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;

public class OpenEditorAction extends SelectionProviderAction {
  
  public OpenEditorAction( final ISelectionProvider provider, 
                           final String text )
  {
    super( provider, text );
  }

  public void run() {
    IStructuredSelection structuredSelection = getStructuredSelection();
    Object firstElement = structuredSelection.getFirstElement();
    openEditor( firstElement, true );
  }

  public static boolean openEditor( final Object entity,
                                    final boolean showMessage )
  {
    boolean result = false;
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    IWorkbenchPage activePage = window.getActivePage();
    if( canOpen( activePage ) ) {
      IEditorInput input = EntityAdapter.getEditorInput( entity );
      IEditorReference[] refs = activePage.getEditorReferences();
      boolean found = false;
      for( int i = 0; !found && i < refs.length; i++ ) {
        try {
          found = refs[ i ].getEditorInput() == input;
        } catch( PartInitException e ) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if( input != null && !found ) {
        if( canOpen( entity, activePage, showMessage ) ) {
          try {
            activePage.openEditor( input,
                                   Constants.ENTITY_EDITOR_ID,
                                   true );
            result = true;
          } catch( final PartInitException pie ) {
            Shell shell = window.getShell();
            String title = RMSMessages.get().OpenEditorAction_ProblemOccured;
            ErrorDialog.openError( shell, 
                                   title, 
                                   pie.getMessage(),
                                   pie.getStatus() );
          }
        }
      }
    }
    return result;
  }

  private static boolean canOpen( final Object entity, 
                                  final IWorkbenchPage activePage,
                                  final boolean showMessage )
  {
    IAdaptable adaptable = ( IAdaptable )entity;
    ILock lock = ( ILock )adaptable.getAdapter( ILock.class ); 
    boolean result = lock.lock();
    if( !result && showMessage ) {
      Shell shell = activePage.getWorkbenchWindow().getShell();
      String msg
        = RMSMessages.get().OpenEditorAction_UsedByAnother;
      MessageDialog.openInformation( shell,
                                     RMSMessages.get().OpenEditorAction_ElementLocked, 
                                     msg );
    }
    return result;
  }

  private static boolean canOpen( final IWorkbenchPage activePage ) {
    boolean result = true;
    IEditorReference[] openEditors = activePage.getEditorReferences();    
    if( openEditors.length >= 8 ) {
      IWorkbenchPart editor = openEditors[ 0 ].getPart( false );
      if( editor != null ) {
        result = activePage.closeEditor( ( IEditorPart )editor, true );
      }
    }
    if( !result ) {
      Shell shell = activePage.getWorkbenchWindow().getShell();
      String msg
        = RMSMessages.get().OpenEditorAction_CloseOneEditor;
      MessageDialog.openInformation( shell,
                                     RMSMessages.get().OpenEditorAction_TooManyEditors, 
                                     msg );
    }
    return result;
  }
}