package de.fu_berlin.inf.dpp.intellij.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.VirtualFile;

import de.fu_berlin.inf.dpp.filesystem.IContainer;
import de.fu_berlin.inf.dpp.filesystem.IFolder;
import de.fu_berlin.inf.dpp.filesystem.IPath;
import de.fu_berlin.inf.dpp.filesystem.IProject;
import de.fu_berlin.inf.dpp.filesystem.IResource;
import de.fu_berlin.inf.dpp.intellij.project.filesystem.IntelliJPathImpl;

public final class IntelliJFolderImplV2 extends IntelliJResourceImplV2
    implements IFolder {

    /** Relative path from the given project */
    private final IPath path;

    private final IntelliJProjectImplV2 project;

    public IntelliJFolderImplV2(@NotNull final IntelliJProjectImplV2 project,
        @NotNull final IPath path) {
        this.project = project;
        this.path = path;
    }

    @Override
    public boolean exists(@NotNull IPath path) {
        final VirtualFile file = project.findVirtualFile(path);

        return file != null && file.exists() && file.isDirectory();
    }

    @NotNull
    @Override
    public IResource[] members() throws IOException {
        // TODO run as read action
        // TODO filter files that belong to other modules

        final VirtualFile folder = project.findVirtualFile(path);

        if (folder == null || !folder.exists())
            throw new FileNotFoundException(this + " does not exist");

        if (!folder.isDirectory())
            throw new IOException(this + " is a file");

        final List<IResource> result = new ArrayList<>();

        final VirtualFile[] children = folder.getChildren();

        for (final VirtualFile child : children) {

            final IPath childPath = path.append(IntelliJPathImpl
                .fromString(child.getName()));

            result.add(child.isDirectory() ? new IntelliJFolderImplV2(project,
                childPath) : new IntelliJFileImplV2(project, childPath));

        }

        return result.toArray(new IResource[result.size()]);
    }

    @NotNull
    @Override
    public IResource[] members(final int memberFlags) throws IOException {
        return members();
    }

    @Nullable
    @Override
    public String getDefaultCharset() throws IOException {
        // TODO retrieve encoding for the module or use the project settings
        return getParent().getDefaultCharset();
    }

    @Override
    public boolean exists() {
        final VirtualFile file = project.findVirtualFile(path);

        return file != null && file.exists() && file.isDirectory();
    }

    @NotNull
    @Override
    public IPath getFullPath() {
        return project.getFullPath().append(path);
    }

    @NotNull
    @Override
    public String getName() {
        return path.lastSegment();
    }

    @Nullable
    @Override
    public IContainer getParent() {
        if (path.segmentCount() == 1)
            return project;

        return new IntelliJFolderImplV2(project, path.removeLastSegments(1));
    }

    @NotNull
    @Override
    public IProject getProject() {
        return project;
    }

    @NotNull
    @Override
    public IPath getProjectRelativePath() {
        return path;
    }

    @Override
    public int getType() {
        return IResource.FOLDER;
    }

    @Override
    public boolean isDerived(final boolean checkAncestors) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDerived() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void delete(final int updateFlags) throws IOException {

        Filesystem.runWriteAction(new ThrowableComputable<Void, IOException>() {

            @Override
            public Void compute() throws IOException {

                final VirtualFile file = project.findVirtualFile(path);

                if (file == null)
                    throw new FileNotFoundException(IntelliJFolderImplV2.this
                        + " does not exist");

                if (!file.isDirectory())
                    throw new IOException(this + " is not a folder");

                file.delete(IntelliJFolderImplV2.this);

                return null;
            }

        }, ModalityState.any());
    }

    @Override
    public void move(final IPath destination, final boolean force)
        throws IOException {
        throw new IOException("NYI");
    }

    @NotNull
    @Override
    public IPath getLocation() {
        // TODO might return a wrong location
        return project.getLocation().append(path);
    }

    @Override
    public void create(final int updateFlags, final boolean local)
        throws IOException {
        this.create((updateFlags & IResource.FORCE) != 0, local);
    }

    @Override
    public void create(final boolean force, final boolean local)
        throws IOException {

        Filesystem.runWriteAction(new ThrowableComputable<Void, IOException>() {

            @Override
            public Void compute() throws IOException {

                final IResource parent = getParent();

                final VirtualFile parentFile = project.findVirtualFile(parent
                    .getProjectRelativePath());

                if (parentFile == null)
                    throw new IOException(parent
                        + " does not exist, cannot create folder "
                        + IntelliJFolderImplV2.this);

                final VirtualFile file = project.findVirtualFile(path);

                /*
                 * TODO the interface needs a better spec, we do not know how to
                 * implement force as we use the VFS and have no control where
                 * it points to
                 */
                if (file != null)
                    throw new IOException(
                        IntelliJFolderImplV2.this
                            + " already exists, force option not supported - force="
                            + force);

                parentFile.createChildDirectory(IntelliJFolderImplV2.this,
                    getName());

                return null;

            }

        }, ModalityState.any());
    }

    @Override
    public int hashCode() {
        return project.hashCode() + 31 * path.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        IntelliJFolderImplV2 other = (IntelliJFolderImplV2) obj;

        return project.equals(other.project) && path.equals(other.path);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + path + " - " + project;
    }
}
