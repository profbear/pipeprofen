/*
 * Copyright (c) 2018 Unbearable Professional
 *
 * I dedicate any and all copyright interest in this software to the
 * public domain. I make this dedication for the benefit of the public at
 * large and to the detriment of my heirs and successors. I intend this
 * dedication to be an overt act of relinquishment in perpetuity of all
 * present and future rights to this software under copyright law.
 */

package pw.unbear.ijplug

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.NonEmptyInputValidator
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import java.nio.file.Files
import java.util.concurrent.TimeUnit

/** We have a piper down! I repeat, a piper is down! */
class PiperDown : AnAction() {
    // note how do we get this from the plugin definition?
    private val name = "pipeprofen"

    /** wait this long before abandoning the results of the piped command */
    private val duration = TimeUnit.SECONDS.toSeconds(21)

    /**
     * the executable which will have args appended, e.g.:
     *
     *   $ bash -c 'lastArg' < selected-text-block
     */
    private val process = listOf("bash", "-c")

    /** the last args the user used */
    private val lastArgs = Key.create<String>("args.last")

    data class ProcessPipes(val out: String, val err: String)

    /**
     * main functionality for the action.
     * we enter this fun upon menu selection or keyboard shortcut execution
     * but only after the `update` validates that the action is visible.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val document = editor.document
        val project = e.project!!
        val selectionModel = editor.selectionModel
        val starts = selectionModel.blockSelectionStarts
        val ends = selectionModel.blockSelectionEnds

        /* can't invoke `showInputDialog` without performing an `invokeLater` */
        ApplicationManager.getApplication().invokeLater({
            getArgs(project)?.let { args ->
                project.putUserData(lastArgs, args)
                starts.size.minus(1).downTo(0).map {
                    WriteCommandAction.runWriteCommandAction(project) {
                        val pipes = executeProcessWithArgs(args, document.getText(TextRange(starts[it], ends[it])))
                        document.replaceString(starts[it], ends[it], pipes.out + pipes.err)
                    }
                }
                val all = true
                selectionModel.removeSelection(all)
            }
        }, ModalityState.NON_MODAL)
    }

    /** determines visibility and availability of this action in the context menu and keymap */
    override fun update(e: AnActionEvent) {
        val editor = CommonDataKeys.EDITOR.getData(e.dataContext)
        e.presentation.isVisible = editor?.selectionModel?.hasSelection()!!
    }

    private fun getArgs(project: Project) = Messages.showInputDialog(
        project,
        process.joinToString(" ") + "\${this input box}",
        "Pipe selected text to",
        AllIcons.Actions.Run_anything,
        project.getUserData(lastArgs),
        NonEmptyInputValidator())

    private fun executeProcessWithArgs(args: String, content: String): ProcessPipes {
        val withArg = process.toMutableList()
        withArg.add(args)
        val pb = ProcessBuilder(*withArg.toTypedArray())

        // note is there temporary storage for us?
        val inp = Files.createTempFile("$name-inp", "txt").toFile()
        val out = Files.createTempFile("$name-out", "txt").toFile()
        val err = Files.createTempFile("$name-err", "txt").toFile()
        inp.writeText(content)
        pb.redirectInput(ProcessBuilder.Redirect.from(inp))
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(out))
        pb.redirectError(ProcessBuilder.Redirect.appendTo(err))

        try {
            pb.start().waitFor(duration, TimeUnit.SECONDS)
        } finally {
        }

        val pipes = ProcessPipes(out.readText(), err.readText())

        inp.delete()
        err.delete()
        out.delete()

        return pipes
    }
}
