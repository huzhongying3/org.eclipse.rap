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

package org.eclipse.rap.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rap.jface.resource.ImageDescriptor;
import org.eclipse.rap.jface.util.Assert;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.ui.*;
import org.eclipse.rap.ui.internal.util.Util;


public abstract class WorkbenchPartReference
  implements IWorkbenchPartReference
{
  public static int STATE_LAZY = 0;
  public static int STATE_CREATION_IN_PROGRESS = 1;
  public static int STATE_CREATED = 2;
  public static int STATE_DISPOSED = 3;

  private int state = STATE_LAZY;
  private String id;
  private String partName;
  private String title;
  private String tooltip;
  protected PartPane pane;
  protected IWorkbenchPart part;
  private String contentDescription;
  private Image image;
  private ImageDescriptor imageDescriptor;
  private ImageDescriptor defaultImageDescriptor;

  public final PartPane getPane() {
    if( pane == null ) {
      pane = createPane();
    }
    return pane;
  }

  public void init( String id,
                    String title,
                    String tooltip,
                    ImageDescriptor desc,
                    String paneName,
                    String contentDescription )
  {
    Assert.isNotNull( id );
    Assert.isNotNull( title );
    Assert.isNotNull( tooltip );
    Assert.isNotNull( desc );
    Assert.isNotNull( paneName );
    Assert.isNotNull( contentDescription );
    this.id = id;
    this.title = title;
    this.tooltip = tooltip;
    this.partName = paneName;
    this.contentDescription = contentDescription;
    this.defaultImageDescriptor = desc;
    this.imageDescriptor = computeImageDescriptor();
  }

  protected ImageDescriptor computeImageDescriptor() {
//    if( part != null ) {
//      return ImageDescriptor.createFromImage( part.getTitleImage(),
//                                              Display.getCurrent() );
//    }
    return defaultImageDescriptor;
  }

  public boolean getVisible() {
// if( isDisposed() ) {
// return false;
//    }
    return getPane().getVisible();
  }

  public void setVisible( boolean isVisible ) {
//    if( isDisposed() ) {
//      return;
//    }
    getPane().setVisible( isVisible );
  }

  protected void setTitle( String newTitle ) {
    if( Util.equals( title, newTitle ) ) {
      return;
    }
    title = newTitle;
//    firePropertyChange( IWorkbenchPartConstants.PROP_TITLE );
  }

  protected void setToolTip( String newToolTip ) {
    if( Util.equals( tooltip, newToolTip ) ) {
      return;
    }
    tooltip = newToolTip;
//    firePropertyChange( IWorkbenchPartConstants.PROP_TITLE );
  }

  protected void setContentDescription( String newContentDescription ) {
    if( Util.equals( contentDescription, newContentDescription ) ) {
      return;
    }
    contentDescription = newContentDescription;
//    firePropertyChange( IWorkbenchPartConstants.PROP_CONTENT_DESCRIPTION );
  }

  protected void setImageDescriptor( final ImageDescriptor descriptor ) {
    if( Util.equals( imageDescriptor, descriptor ) ) {
      return;
    }
//    Image oldImage = image;
//    ImageDescriptor oldDescriptor = imageDescriptor;
    image = null;
    imageDescriptor = descriptor;
    // Don't queue events triggered by image changes. We'll dispose the image
    // immediately after firing the event, so we need to fire it right away.
//    immediateFirePropertyChange( IWorkbenchPartConstants.PROP_TITLE );
//    if( queueEvents ) {
//      // If there's a PROP_TITLE event queued, remove it from the queue because
//      // we've just fired it.
//      queuedEvents.clear( IWorkbenchPartConstants.PROP_TITLE );
//    }
    // If we had allocated the old image, deallocate it now (AFTER we fire the
    // property change
    // -- listeners may need to clean up references to the old image)
//    if( oldImage != null ) {
//      JFaceResources.getResources().destroy( oldDescriptor );
//    }
  }


  
  protected abstract PartPane createPane();
  protected abstract IWorkbenchPart createPart();
  
  ////////////////////////////////////
  // Interface IWorkbenchPartReference
  
  public String getId() {
    return id;
  }

  public IWorkbenchPart getPart( boolean restore ) {
//    if( isDisposed() ) {
//      return null;
//    }
    if( part == null && restore ) {
      if( state == STATE_CREATION_IN_PROGRESS ) {
        IStatus result = Activator.getStatus( new PartInitException( NLS.bind( "Warning: Detected recursive attempt by part {0} to create itself (this is probably, but not necessarily, a bug)", //$NON-NLS-1$
                                                                                     getId() ) ) );
//        Activator.log( result );
        return null;
      }
      try {
        state = STATE_CREATION_IN_PROGRESS;
        IWorkbenchPart newPart = createPart();
        if( newPart != null ) {
          part = newPart;
          // Add a dispose listener to the part. This dispose listener does
          // nothing but log an exception
          // if the part's widgets get disposed unexpectedly. The workbench part
          // reference is the only
          // object that should dispose this control, and it will remove the
          // listener before it does so.
//          getPane().getControl().addDisposeListener( prematureDisposeListener );
//          part.addPropertyListener( propertyChangeListener );
//          refreshFromPart();
//          releaseReferences();
//          fireInternalPropertyChange( INTERNAL_PROPERTY_OPENED );
        }
      } finally {
        state = STATE_CREATED;
      }
    }
    return part;
  }

  public String getPartName() {
    return partName;
  }

  public String getTitle() {
    return Util.safeString( title );
  }

  public String getTitleToolTip() {
    return Util.safeString( tooltip );
  }

  public String getContentDescription() {
    return Util.safeString( contentDescription );
  }

  public final Image getTitleImage() {
//    if( isDisposed() ) {
//      return PlatformUI.getWorkbench()
//        .getSharedImages()
//        .getImage( ISharedImages.IMG_DEF_VIEW );
//    }
//    if( image == null ) {
//      image = JFaceResources.getResources()
//        .createImageWithDefault( imageDescriptor );
//    }
    if( image == null ) {
      image = imageDescriptor.createImage();
    }
    return image;
  }

  public boolean isDirty() {
//    if( !( part instanceof ISaveablePart ) ) {
//      return false;
//    }
//    return ( ( ISaveablePart )part ).isDirty();
    return false;
  }
}
