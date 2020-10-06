package il.ac.technion.cs.reactivize.gradle

import org.gradle.api.Task
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class ReactivizeGradlePluginTest {
    @Test
    fun `reactivize plugin add reactivize task to project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java-gradle-plugin")
        project.pluginManager.apply("kotlin")
        project.pluginManager.apply("il.ac.technion.cs.reactivize-gradle-plugin")


        println(project.tasks.map(Task::getName))
        val task = project.tasks.getByName("compileKotlin")
        assertTrue(task is AbstractCompile)
        val lastAction = task.actions[task.actions.size - 1]
        println(lastAction)
    }
}