/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal;

import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.presentations.PresentationFactoryUtil;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.internal.registry.ViewRegistry;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

public class PageLayout implements IPageLayout {

  private final ViewFactory viewFactory;
  private final ViewSashContainer rootLayoutContainer;
  private final LayoutPart editorFolder;
  private PerspectiveDescriptor descriptor;
  private boolean editorVisible = true;
  private Map mapIDtoPart = new HashMap( 10 );
  private Map mapIDtoFolder = new HashMap( 10 );
  private Map mapIDtoViewLayoutRec = new HashMap( 10 );
  private ArrayList showViewShortcuts = new ArrayList(3);
  private boolean fixed = false;

  public PageLayout( final ViewSashContainer container, 
                     final ViewFactory viewFactory, 
                     final LayoutPart editorFolder, 
                     final PerspectiveDescriptor descriptor )
  {
    this.viewFactory = viewFactory;
    this.rootLayoutContainer = container;
    this.editorFolder = editorFolder;
    this.descriptor = descriptor;
    prefill();

  }
  
  public static int swtConstantToLayoutPosition( final int swtConstant ) {
    // TODO: [fappel] copied from original PageLayout implementation
    switch( swtConstant ) {
      case SWT.TOP:
        return IPageLayout.TOP;
      case SWT.BOTTOM:
        return IPageLayout.BOTTOM;
      case SWT.RIGHT:
        return IPageLayout.RIGHT;
      case SWT.LEFT:
        return IPageLayout.LEFT;
    }
    return -1;
  }
  
  ViewFactory getViewFactory() {
    return viewFactory;
  }

  void setFolderPart( final String viewId, final ViewStack folder ) {
    mapIDtoFolder.put( viewId, folder );
  }

  //////////////////////////
  // interface IPageLayout
  
  public void addActionSet( String actionSetId ) {
    throw new UnsupportedOperationException();
  }

  public void addFastView( String viewId ) {
    throw new UnsupportedOperationException();
  }

  public void addFastView( String viewId, float ratio ) {
    throw new UnsupportedOperationException();
  }

  public void addNewWizardShortcut( String id ) {
    throw new UnsupportedOperationException();
  }

  public void addPerspectiveShortcut( String id ) {
    throw new UnsupportedOperationException();
  }

  public void addPlaceholder( String viewId,
                              int relationship,
                              float ratio,
                              String refId )
  {
    if( !checkValidPlaceholderId( viewId ) ) {
      return;
    }
    // Create the placeholder.
    PartPlaceholder newPart = new PartPlaceholder( viewId );
    addPart( newPart, viewId, relationship, ratio, refId );
    // force creation of the view layout rec
    getViewLayoutRec( viewId, true );

  }

  public void addShowInPart( String id ) {
    throw new UnsupportedOperationException();
  }

  public void addShowViewShortcut( String id ) {
      if (!showViewShortcuts.contains(id)) {
          showViewShortcuts.add(id);
      }
  }
  
  /**
   * @return the show view shortcuts associated with the page. This is a <code>List</code> of 
   * <code>String</code>s.
   */
  public ArrayList getShowViewShortcuts() {
      return showViewShortcuts;
  }

  public void addStandaloneView( String viewId,
                                 boolean showTitle,
                                 int relationship,
                                 float ratio,
                                 String refId )
  {
    addView( viewId, relationship, ratio, refId, true, showTitle );
    ViewLayoutRec rec = getViewLayoutRec( viewId, true );
    rec.isStandalone = true;
    rec.showTitle = showTitle;
  }

  public void addStandaloneViewPlaceholder( String viewId,
                                            int relationship,
                                            float ratio,
                                            String refId,
                                            boolean showTitle )
  {
    throw new UnsupportedOperationException();
  }

  public void addView( String viewId,
                       int relationship,
                       float ratio,
                       String refId )
  {
    throw new UnsupportedOperationException();
  }

  public IFolderLayout createFolder( String folderId,
                                     int relationship,
                                     float ratio,
                                     String refId )
  {
//    if( checkPartInLayout( folderId ) ) {
//      return new FolderLayout( this,
//                               ( ViewStack )getRefPart( folderId ),
//                               viewFactory );
//    }
    // Create the folder.
    ViewStack folder = new ViewStack( rootLayoutContainer.page );
    folder.setID( folderId );
    addPart( folder, folderId, relationship, ratio, refId );
    // Create a wrapper.
    return new FolderLayout( this, folder, viewFactory );
  }

  public IPlaceholderFolderLayout createPlaceholderFolder( String folderId,
                                                           int relationship,
                                                           float ratio,
                                                           String refId )
  {
    throw new UnsupportedOperationException();
  }

  public IPerspectiveDescriptor getDescriptor() {
    return descriptor;
  }

  public String getEditorArea() {
    return ID_EDITOR_AREA;
  }

  public int getEditorReuseThreshold() {
    throw new UnsupportedOperationException();
  }

  public IViewLayout getViewLayout( String id ) {
    ViewLayoutRec rec = getViewLayoutRec(id, true);
    if (rec == null) {
        return null;
    }
    return new ViewLayout(this, rec);
  }

  public boolean isEditorAreaVisible() {
    return editorVisible;
  }

  public boolean isFixed() {
    return fixed;
  }

  public void setEditorAreaVisible( final boolean showEditorArea ) {
    editorVisible = showEditorArea;
  }

  public void setEditorReuseThreshold( int openEditors ) {
    throw new UnsupportedOperationException();
  }

  public void setFixed( boolean fixed ) {
    this.fixed = fixed;
  }

  ViewLayoutRec getViewLayoutRec( String id, boolean create ) {
    Assert.isTrue( getRefPart( id ) != null /*|| isFastViewId( id )*/ );
    ViewLayoutRec rec = ( ViewLayoutRec )mapIDtoViewLayoutRec.get( id );
    if( rec == null && create ) {
      rec = new ViewLayoutRec();
      // set up the view layout appropriately if the page layout is fixed
      if( isFixed() ) {
        rec.isCloseable = false;
        rec.isMoveable = false;
      }
      mapIDtoViewLayoutRec.put( id, rec );
    }
    return rec;
  }
  
  //////////////////
  // helping methods
  
  private void prefill() {
    addEditorArea();
    // Add default action sets.
//    ActionSetRegistry reg = WorkbenchPlugin.getDefault().getActionSetRegistry();
//    IActionSetDescriptor[] array = reg.getActionSets();
//    int count = array.length;
//    for( int nX = 0; nX < count; nX++ ) {
//      IActionSetDescriptor desc = array[ nX ];
//      if( desc.isInitiallyVisible() ) {
//        addActionSet( desc.getId() );
//      }
//    }
  }
  
  private void addEditorArea() {
    try {
      // Create the part.
      LayoutPart newPart = createView( ID_EDITOR_AREA );
      if( newPart == null ) {
        // this should never happen as long as newID is the editor ID.
        return;
      }
      setRefPart( ID_EDITOR_AREA, newPart );
      // Add it to the layout.
      rootLayoutContainer.add( newPart );
    } catch( PartInitException e ) {
      Activator.log( getClass(), "addEditorArea()", e ); //$NON-NLS-1$
    }
  }

  private LayoutPart createView( String partID ) throws PartInitException {
    if( partID.equals( ID_EDITOR_AREA ) ) {
      return editorFolder;
    }
//    IViewDescriptor viewDescriptor = viewFactory.getViewRegistry()
//      .find( ViewFactory.extractPrimaryId( partID ) );
//    if( WorkbenchActivityHelper.filterItem( viewDescriptor ) ) {
//      return null;
//    }
    return LayoutHelper.createView( getViewFactory(), partID );
  }
  
  private void addPart( LayoutPart newPart,
                        String partId,
                        int relationship,
                        float ratio,
                        String refId )
  {
    setRefPart( partId, newPart );
    // If the referenced part is inside a folder,
    // then use the folder as the reference part.
    LayoutPart refPart = getFolderPart( refId );
    if( refPart == null ) {
      refPart = getRefPart( refId );
    }
    // Add it to the layout.
    if( refPart != null ) {
      ratio = normalizeRatio( ratio );
      rootLayoutContainer.add( newPart,
                               getPartSashConst( relationship ),
                               ratio,
                               refPart );
    } else {
//      WorkbenchPlugin.log( NLS.bind( WorkbenchMessages.PageLayout_missingRefPart,
//                                     refId ) );
      rootLayoutContainer.add( newPart );
    }
  }

  void setRefPart( final String partID, final LayoutPart part ) {
    mapIDtoPart.put( partID, part );
  }

  LayoutPart getRefPart( final String partID ) {
    return ( LayoutPart )mapIDtoPart.get( partID );
  }

  private ViewStack getFolderPart( final String viewId ) {
    return ( ViewStack )mapIDtoFolder.get( viewId );
  }

  private int getPartSashConst( final int nRelationship ) {
    return nRelationship;
  }

  private float normalizeRatio( final float in ) {
    float result = in;
    if( in < RATIO_MIN ) {
      result = RATIO_MIN;
    }
    if( in > RATIO_MAX ) {
      result = RATIO_MAX;
    }
    return result;
  }

  private void addView( String viewId,
                        int relationship,
                        float ratio,
                        String refId,
                        boolean standalone,
                        boolean showTitle )
  {
    if( checkPartInLayout( viewId ) ) {
      return;
    }
    try {
      // Create the part.
      LayoutPart newPart = createView( viewId );
      if( newPart == null ) {
        addPlaceholder( viewId, relationship, ratio, refId );
        LayoutHelper.addViewActivator( this, viewId );
      } else {
        int appearance = PresentationFactoryUtil.ROLE_VIEW;
        if( standalone ) {
          if( showTitle ) {
            appearance = PresentationFactoryUtil.ROLE_STANDALONE;
          } else {
            appearance = PresentationFactoryUtil.ROLE_STANDALONE_NOTITLE;
          }
        }
        ViewStack newFolder = new ViewStack( rootLayoutContainer.page,
                                             true,
                                             appearance,
                                             null );
        newFolder.add( newPart );
        setFolderPart( viewId, newFolder );
        addPart( newFolder, viewId, relationship, ratio, refId );
        // force creation of the view layout rec
        getViewLayoutRec( viewId, true );
      }
    } catch( PartInitException e ) {
      Activator.log( getClass(), "addView", e ); //$NON-NLS-1$
    }
  }

  boolean checkPartInLayout( String partId ) {
    if( getRefPart( partId ) != null /** || isFastViewId( partId )*/ ) {
//      WorkbenchPlugin.log( NLS.bind( WorkbenchMessages.PageLayout_duplicateRefPart,
//                                     partId ) );
      return true;
    }
    return false;
  }
  
  /**
   * Checks whether the given id is a valid placeholder id.
   * A placeholder id may be simple or compound, and can optionally contain a wildcard.
   * 
   * @param id the placeholder id
   * @return <code>true</code> if the given id is a valid placeholder id, <code>false</code> otherwise
   */
  boolean checkValidPlaceholderId(String id) {
      // Check that view is not already in layout.
      // This check is done even if the id has a wildcard, since it's incorrect to create
      // multiple placeholders with the same id, wildcard or not.
      if (checkPartInLayout(id)) {
          return false;
      }

      // check that primary view id is valid, but only if it has no wildcard
      String primaryId = ViewFactory.extractPrimaryId(id);
      if (!ViewFactory.hasWildcard(primaryId)) {
            IViewRegistry reg = ViewRegistry.getInstance();
            IViewDescriptor desc = reg.find(primaryId);
            if (desc == null) {
                // cannot safely open the dialog so log the problem
                WorkbenchPlugin.log("Unable to find view with id: " + primaryId + ", when creating perspective " + getDescriptor().getId()); //$NON-NLS-1$ //$NON-NLS-2$
                return false;
            }
      }

      return true;
  }
}
