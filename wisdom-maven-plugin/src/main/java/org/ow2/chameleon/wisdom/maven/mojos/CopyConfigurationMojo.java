package org.ow2.chameleon.wisdom.maven.mojos;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.ow2.chameleon.wisdom.maven.Constants;
import org.ow2.chameleon.wisdom.maven.WatchingException;
import org.ow2.chameleon.wisdom.maven.utils.ResourceCopy;

import java.io.File;
import java.io.IOException;

/**
 * Copy configuration files.
 */
@Mojo(name = "copy-configuration", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class CopyConfigurationMojo extends AbstractWisdomWatcherMojo implements Constants {

    private File source;
    private File destination;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        source = new File(basedir, CONFIGURATION_SRC_DIR);
        destination = new File(getWisdomRootDirectory(), CONFIGURATION_DIR);

        try {
            ResourceCopy.copyConfiguration(this);
        } catch (IOException e) {
            throw new MojoExecutionException("Error during configuration copy", e);
        }
    }

    @Override
    public boolean accept(File file) {
        return file.getAbsolutePath().contains(CONFIGURATION_SRC_DIR);
    }

    @Override
    public boolean fileCreated(File file) throws WatchingException {
        try {
            ResourceCopy.copyFileToDir(file, source, destination);
        } catch (IOException e) {
            throw new WatchingException(e.getMessage(), file, e);
        }
        getLog().info(file.getName() + " copied to the configuration directory");
        return false;
    }

    @Override
    public boolean fileUpdated(File file) throws WatchingException {
        try {
            ResourceCopy.copyFileToDir(file, source, destination);
        } catch (IOException e) {
            throw new WatchingException(e.getMessage(), file, e);
        }
        getLog().info(file.getName() + " updated in the configuration directory");
        return false;
    }

    @Override
    public boolean fileDeleted(File file) throws WatchingException {
        File copied = ResourceCopy.computeRelativeFile(file, source, destination);
        if (copied.exists()) {
            copied.delete();
        }
        getLog().info(copied.getName() + " deleted");
        return false;
    }

}
