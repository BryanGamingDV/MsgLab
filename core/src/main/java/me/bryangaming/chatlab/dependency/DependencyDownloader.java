package me.bryangaming.chatlab.dependency;

import com.google.common.io.Files;
import team.unnamed.dependency.Dependency;
import team.unnamed.dependency.DependencyHandler;
import team.unnamed.dependency.MavenDependency;
import team.unnamed.dependency.exception.ErrorDetails;
import team.unnamed.dependency.load.JarLoader;
import team.unnamed.dependency.load.JarLoaderJdk8;
import team.unnamed.dependency.logging.LogStrategy;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// Credits: Fixed
public class DependencyDownloader {

    private static final int DEPENDENCIES_VERSION = 2;

    private final DependencyHandler handler;
    private final Set<Dependency> dependencies;
    private final List<Relocation> relocationList;

    private final File dependencyFolder;
    private final ClassLoader classLoader;
    private final Logger logger;

    public DependencyDownloader(ClassLoader classLoader, File dependencyFolder, Logger logger) {
        this.logger = logger;
        this.classLoader = classLoader;
        this.dependencyFolder = dependencyFolder;

        File dependencyVersionFile = new File(dependencyFolder, "dependency-version");
        try {
            if (dependencyVersionFile.exists()) {
                String line = Files.readFirstLine(dependencyVersionFile, Charset.defaultCharset());
                int versionFound = Integer.parseInt(line);

                if (versionFound != DEPENDENCIES_VERSION) {
                    logger.log(Level.INFO, "The version of dependencies found was " + versionFound + " this version of ChatLab uses " + DEPENDENCIES_VERSION);
                    logger.log(Level.INFO, "Deleting folder and redownloading!");
                    deleteFolder(dependencyFolder);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read dependency version", e);
        }

        handler = DependencyHandler
                .builder()
                .setClassLoader(classLoader)
                .setDependenciesFolder(dependencyFolder)
                .setLogStrategy(LogStrategy.getSilent())
                .setDeleteOnNonEqual(false)
                .build();

        dependencies = new HashSet<>();
        relocationList = new ArrayList<>();
        setDefaultDependencies();
    }

    private void deleteFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                deleteFolder(file);

                continue;
            }

            file.delete();
        }
    }

    public void addDependency(Dependency dependency) {
        dependencies.add(dependency);
    }

    public void addRelocation(Relocation relocation) {
        this.relocationList.add(relocation);
    }

    public void addRelocation(String pattern, String relocatedPattern) {
        addRelocation(Relocation.of(pattern, relocatedPattern));
    }

    public void downloadDependencies() {
        logger.log(Level.INFO, "Downloading dependencies");
        handler.setup(new MavenDependency(new String[]{MavenDependency.CENTRAL},
                        "me.lucko",
                        "jar-relocator",
                        "1.4",
                        "relocator.jar",
                        false),
                new MavenDependency(new String[]{MavenDependency.CENTRAL},
                        "org.ow2.asm",
                        "asm",
                        "7.1",
                        "asm.jar",
                        false),
                new MavenDependency(new String[]{MavenDependency.CENTRAL},
                        "org.ow2.asm",
                        "asm-commons",
                        "7.1",
                        "asm-commons.jar",
                        false));

        RelocationApplier applier = new RelocationApplier();
        applier.setRelocationList(relocationList);

        JarLoader loader = new JarLoaderJdk8((URLClassLoader) classLoader);
        ErrorDetails details = new ErrorDetails("relocate and load");

        List<Dependency> dependenciesToDownload = new ArrayList<>();
        List<Dependency> dependenciesToRelocate = new ArrayList<>();
        List<Dependency> readyDependencies = new ArrayList<>();

        for (Dependency dependency : dependencies) {
            boolean existsRelocated = existsRelocated(dependency);
            if (!exists(dependency) && !existsRelocated) {
                dependenciesToDownload.add(dependency);

                continue;
            }

            if (!existsRelocated) {
                dependenciesToRelocate.add(dependency);

                continue;
            }

            readyDependencies.add(dependency);
        }

        for (File file : handler.download(dependenciesToDownload)) {
            try {
                File relocatedFile = new File(file.getParentFile(), file.getName() + "-relocated.jar");
                applier.applyRelocations(file, relocatedFile);
                file.delete();

                loader.load(relocatedFile, details);
            } catch (IOException e) {
                details.add(e);
            }
        }

        for (Dependency dependency : dependenciesToRelocate) {
            try {
                File file = getDownloadedFile(dependency);
                File relocatedFile = new File(file.getParentFile(), file.getName() + "-relocated.jar");

                applier.applyRelocations(file, relocatedFile);
                file.delete();

                loader.load(relocatedFile, details);
            } catch (IOException e) {
                details.add(e);
            }
        }

        for (Dependency dependency : readyDependencies) {
            String artifactName = dependency.getArtifactName();

            File relocatedFile = new File(dependencyFolder, artifactName + "-relocated.jar");
            loader.load(relocatedFile, details);
        }

        if (details.errorCount() > 0) {
            logger.log(Level.SEVERE, "An error occurred while downloading dependencies!", details.toDownloadException());
        } else {
            logger.log(Level.INFO, "Dependencies downloaded successfully!");

            File dependencyVersionFile = new File(dependencyFolder, "dependency-version");
            try {
                if (!dependencyVersionFile.exists()) {
                    dependencyVersionFile.createNewFile();
                }
                Files.write(DEPENDENCIES_VERSION + "", dependencyVersionFile, Charset.defaultCharset());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An error occurred while downloading dependencies!", e);
            }
        }
    }

    private void setDefaultDependencies() {
        String[] commandflowRepo = new String[]{MavenDependency.CENTRAL, "https://repo.unnamed.team/repository/unnamed-releases/"};
        String[] mavenRepo = new String[]{MavenDependency.CENTRAL, "https://oss.sonatype.org/content/repositories/snapshots/"};

        dependencies.add(new MavenDependency(commandflowRepo,
                "me.fixeddev",
                "commandflow-universal",
                "0.4.5",
                "commandflow-universal",
                false));

        dependencies.add(new MavenDependency(commandflowRepo,
                "me.fixeddev",
                "commandflow-bukkit",
                "0.4.0",
                "commandflow-bukkit",
                false));

        dependencies.add(new MavenDependency(commandflowRepo,
                "net.kyori",
                "text-api",
                "3.0.0-stripped",
                "text-api",
                false));

        dependencies.add(new MavenDependency(commandflowRepo,
                "net.kyori",
                "text-serializer-plain",
                "3.0.0-stripped",
                "text-serializer-plain",
                false));

        dependencies.add(new MavenDependency(commandflowRepo,
                "net.kyori",
                "text-serializer-legacy",
                "3.0.0-stripped",
                "text-serializer-legacy",
                false));

        dependencies.add(new MavenDependency(commandflowRepo,
                "net.kyori",
                "text-serializer-gson",
                "3.0.0-stripped",
                "text-serializer-gson",
                false));

        dependencies.add(new MavenDependency(mavenRepo,
                "net.kyori",
                "adventure-api",
                "4.8.0",
                "adventure-api",
                false));



        String originalPackage = new String(new char[]{'m', 'e', '.'});
        originalPackage += "chatlab.";
        originalPackage += "dependency";

        addRelocation(originalPackage, "me.bryangaming.chatlab.dependency");
    }

    private boolean exists(Dependency dependency) {
        File dependencyFile = new File(dependencyFolder, dependency.getArtifactName());

        return dependencyFile.exists();
    }

    private boolean existsRelocated(Dependency dependency) {
        File dependencyFile = new File(dependencyFolder, dependency.getArtifactName() + "-relocated.jar");

        return dependencyFile.exists();
    }

    private File getDownloadedFile(Dependency dependency) {
        File dependencyFile = new File(dependencyFolder, dependency.getArtifactName());

        return dependencyFile;
    }
}
