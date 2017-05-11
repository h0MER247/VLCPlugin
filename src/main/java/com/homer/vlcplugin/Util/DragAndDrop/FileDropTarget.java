package com.homer.vlcplugin.Util.DragAndDrop;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;



public final class FileDropTarget implements DropTargetListener {
    
    // The consumer that is interested in "drag and dropped" files
    private final Consumer<List<File>> m_callback;
    
    
    
    protected FileDropTarget(Consumer<List<File>> callback) {
        
        m_callback = callback;
    }
    
    
    
    @Override
    public void dragEnter(DropTargetDragEvent evt) {
        
        if(evt.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
            evt.acceptDrag(DnDConstants.ACTION_COPY);
        else
            evt.rejectDrag();
    }

    @Override
    public void dragOver(DropTargetDragEvent evt) {

        dragEnter(evt);
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent evt) {
    }

    @Override
    public void dragExit(DropTargetEvent evt) {
    }

    @Override
    public void drop(DropTargetDropEvent evt) {

        if(evt.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

            try {

                evt.acceptDrop(evt.getDropAction());
                m_callback.accept((List<File>)evt.getTransferable()
                                                 .getTransferData(DataFlavor.javaFileListFlavor));
                evt.dropComplete(true);
            }
            catch(UnsupportedFlavorException | IOException ex) {

                evt.dropComplete(false);
            }
        }
        else {

             evt.rejectDrop();
        }
    }
    
    
    
    // Creates a file drop target for a Component
    
    public static void setAsFileDropTarget(Component component,
                                           Consumer<List<File>> callback) {
        
        component.setDropTarget(new DropTarget(component,
                                               DnDConstants.ACTION_COPY_OR_MOVE,
                                               new FileDropTarget(callback)));
    }
}
