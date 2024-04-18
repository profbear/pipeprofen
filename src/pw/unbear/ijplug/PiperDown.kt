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
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages.showInputDialog
import com.intellij.openapi.ui.NonEmptyInputValidator
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import java.io.BufferedReader
import java.io.InputStreamReader
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

    private val inputDialogTitle = "Pipe selected text to"

    private val inputDialogMsg = process.joinToString(" ") + " \${this input box}"

    /** the last args the user used */
    private val lastArgs = Key.create<String>("args.last")

    data class ProcessPipes(val out: String, val err: String)

    /** determines visibility and availability of this action in the context menu and keymap */
    override fun update(event: AnActionEvent) {
        val editor = /* early out if the editor is not in focus */
            CommonDataKeys.EDITOR.getData(event.dataContext) ?: return
        event.presentation.isVisible = editor.selectionModel.hasSelection()
    }

    /**
     * main functionality for the action:
     *
     * - always enters this fun upon keyboard shortcut execution.
     * - conditionally, we enter this fun upon menu selection, but
     *   after the `update` validates that the action is visible.
     */
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document
        val selectionModel = editor.selectionModel
        val starts = selectionModel.blockSelectionStarts
        val ends = selectionModel.blockSelectionEnds

        /* can't invoke `showInputDialog` without a `getApplication().invokeLater` */
        getApplication().invokeLater({
            /* allow the user to cancel */
            getArgs(project)?.let { args ->

                project.putUserData(lastArgs, args)

                /* appetizer */
                starts.size.minus(1).downTo(0).map {
                    /* can't modify the document strings without a runWriteCommandAction */
                    runWriteCommandAction(project) {
                        val process = processFactory(args)
                        val input = document.getText(TextRange(starts[it], ends[it]))
                        val pipes = pipeInputToProcess(input, process)

                        /* enchilada */
                        document.replaceString(starts[it], ends[it], pipes.out + pipes.err)
                    }
                }

                val all = true
                selectionModel.removeSelection(all)
            }
        }, ModalityState.defaultModalityState())
    }

    /** show input dialog to obtain user's args */
    private fun getArgs(project: Project) = showInputDialog(
        project,
        inputDialogMsg,
        inputDialogTitle,
        AllIcons.Toolwindows.ToolWindowRun,
        project.getUserData(lastArgs),
        NonEmptyInputValidator())

    private fun processFactory(args: String): Process? {
        val withArg = process.toMutableList()
        withArg.add(args)
        return ProcessBuilder(*withArg.toTypedArray()).start()
    }

    private fun pipeInputToProcess(input: String, process: Process?): ProcessPipes {
        val out = BufferedReader(InputStreamReader(process?.inputStream))
        val err = BufferedReader(InputStreamReader(process?.errorStream))
        val inp = process?.outputStream

        return try {
            inp?.write(input.toByteArray())
            inp?.flush()
            inp?.close()
            process?.waitFor(duration, TimeUnit.SECONDS)
            ProcessPipes(
                out.readText().replace("\r\n", "\n"),
                err.readText().replace("\r\n", "\n"))
        } catch (e: Exception) {
            ProcessPipes("", "")
        }
    }
}
