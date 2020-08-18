package il.ac.technion.cs.reactivize.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.AbstractCompile

class ReactivizeGradlePlugin : Plugin<Project> {
    lateinit var project: Project

    override fun apply(p: Project) {
        project = p
        p.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.all { configureSourceSet(it) }
    }

    private fun configureSourceSet(sourceSet: SourceSet) {
        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            project.tasks.named(sourceSet.getCompileTaskName("kotlin"), AbstractCompile::class.java) { compileKotlin ->
                val action = project.objects.newInstance(ReactivizeAction::class.java)
                action.addToTask(compileKotlin)
                action.inpath.from(compileKotlin.destinationDir)
                println(compileKotlin.source.files)
            }
        }
    }
}