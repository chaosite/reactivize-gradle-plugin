package il.ac.technion.cs.reactivize.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.AbstractCompile

open class ReactivizeGradlePluginConfiguration(var packagePrefixes: List<String> = listOf())

class ReactivizeGradlePlugin : Plugin<Project> {
    override fun apply(p: Project) {
        val extension = p.extensions.create<ReactivizeGradlePluginConfiguration>(
            "configuration",
            ReactivizeGradlePluginConfiguration::class.java
        )
        p.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.all { configureSourceSet(it, p, extension) }
    }

    private fun configureSourceSet(
        sourceSet: SourceSet,
        project: Project,
        extension: ReactivizeGradlePluginConfiguration
    ) {
        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            project.tasks.named(sourceSet.getCompileTaskName("kotlin"), AbstractCompile::class.java) { compileKotlin ->
                val action = project.objects.newInstance(ReactivizeAction::class.java, extension)
                action.addToTask(compileKotlin)
                action.inpath.from(compileKotlin.destinationDir)
            }
        }
    }
}