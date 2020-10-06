package il.ac.technion.cs.reactivize.gradle

import il.ac.technion.cs.reactivize.ReactivizeCompileSpec
import il.ac.technion.cs.reactivize.ReactivizePostCompiler
import org.gradle.api.Action
import org.gradle.api.Describable
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.compile.AbstractCompile
import java.io.File
import javax.inject.Inject

open class ReactivizeAction @Inject constructor(
    objectFactory: ObjectFactory,
    val configuration: ReactivizeGradlePluginConfiguration
) : Action<Task>, Describable {
    val inpath: ConfigurableFileCollection = objectFactory.fileCollection()

    override fun execute(t: Task) {
        if (!t.didWork)
            return

        // ReactivizePostCompiler assumes that Soot has not been initialized yet. But Soot uses singletons extensively,
        // and the Gradle daemon might have already initialized Soot. To solve that, we reset it here.
        soot.G.reset()

        val spec = createSpec(t as AbstractCompile)
        val compiler = ReactivizePostCompiler()
        println(spec.workingDir)
        println(spec.destinationDir)
        println(spec.applicationClassNames)
        println(spec.tempDir)

        compiler.execute(spec)
    }

    fun addToTask(task: Task) {
        task.doLast("reactivize", this)
    }

    private fun makeClassnames(compile: AbstractCompile): Iterable<String> =
        compile.destinationDir.walkTopDown().filter { it.extension == "class" }.map {
            it.relativeTo(compile.destinationDir).path.replace(Regex("[.]class$"), "").replace(File.separator, ".")
        }.toList()

    private fun createSpec(compile: AbstractCompile): ReactivizeCompileSpec = ReactivizeCompileSpec(
        destinationDir = compile.destinationDir,
        workingDir = compile.destinationDir,
        tempDir = compile.temporaryDir,
        compileClasspath = compile.classpath.filter { it.exists() }.files,
        inpath = inpath,
        applicationClassNames = makeClassnames(compile),
        applicationClassPackagePrefixes = configuration.packagePrefixes
    )

    override fun getDisplayName(): String = "reactivize"
}