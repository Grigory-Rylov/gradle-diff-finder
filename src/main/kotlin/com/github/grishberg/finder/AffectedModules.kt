package com.github.grishberg.finder

import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import java.io.File
import java.util.*

class AffectedModules(
        private val targetBranch: String = "master",
        private val pathToGit: String = "git",
        private val cmdLineExecutor: CommandLineExecutor
) {
    constructor(
            targetBranch: String = "master",
            pathToGit: String = "git"
    ) : this(targetBranch, pathToGit, CommandLineExecutor())

    private val affectedModules = mutableSetOf<Project>()
    private var diffFiles: List<File> = listOf()
    private val projectsDiffFilesMap = mutableMapOf<Project, List<File>>()

    /**
     * Find all affected modules by changes from git or modules with dependencies
     * changed from git.
     */
    fun findChangedModules(project: Project): Set<Project> {
        projectsDiffFilesMap.clear()
        diffFiles = getAffectedFiles()

        project.rootProject.allprojects { currentModule ->
            if (currentModule == project.rootProject) {
                return@allprojects
            }
            val hasChangedDependency = checkChangedDependencies(currentModule)
            if (hasChangedDependency || moduleHasChangedFiles(diffFiles, currentModule)) {
                affectedModules.add(currentModule)
            }
        }

        return Collections.unmodifiableSet(affectedModules)
    }

    private fun moduleHasChangedFiles(diffFiles: List<File>, currentModule: Project): Boolean {
        val diff = findDiffForProject(diffFiles, currentModule.projectDir)
        return diff.isNotEmpty()
    }

    private fun checkChangedDependencies(module: Project): Boolean {
        var hasChanges = false
        module.configurations.forEach { conf ->
            for (dep in conf.allDependencies) {
                if (dep is DefaultProjectDependency) {
                    val currentModule = dep.dependencyProject
                    if (affectedModules.contains(currentModule)) {
                        affectedModules.add(currentModule)
                        return true
                    }

                    if (moduleHasChangedFiles(diffFiles, currentModule) || checkChangedDependencies(currentModule)) {
                        affectedModules.add(currentModule)
                        hasChanges = true
                    }
                }
            }
        }
        return hasChanges
    }

    private fun findDiffForProject(diffFiles: List<File>, projectDir: File): List<File> {
        val path = projectDir.canonicalPath

        return diffFiles.filter {
            it.canonicalPath.contains(path)
        }
    }

    private fun getAffectedFiles(): List<File> {
        val output = cmdLineExecutor.executeCommand(listOf(pathToGit, "diff", "--name-only", targetBranch))
        return output.split("\n").map {
            File(it)
        }
    }

}