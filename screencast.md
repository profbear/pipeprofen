# Pipeprofen
Why?
- eliminates the headache of having to tab to your console
- don't need scratch files to obtain output from processes
- never-leave-your-editor-swiss-army-knife

## screencast
this file exists to create a screencast of the plugin in action
sort of like a testing framework ... [that gives me an idea!](/README.md#todo)

## explanation
_a quick note on the examples we'll run here shortly_

```markdown
    bash args: copied-to-clipboard  <- these are copied into the plugin's dialog
    ```process-output-language      <- this is the process output language
    this is the input file          <- stdin piped to the bash process 
    
    highlight this text 
    then invoke the plugins         <- open the dialog with ctrl-alt-shift-x
    
    note: multi carets are each separately piped into the process
    ```
```

# run commands without input
_get the current date_

bash args: `date`
```
```

# run text as stdin to any program
_pipe selected text lines in the editor to stdin_

bash args: `node`
```yaml
console.info(module)
// errors
console.error('trololol')
```

# fix whitespace
_i use this one regularly_

bash args: `fold -w 60 -s`
```
This section covers the basics of working with IntelliJ Platform. I hate the way you type long lines all the time. It will familiarize you with the working environment, project structure, and frequently used API components.
```

# hit a json api and make it pretty
_demonstrate ability to use pipes in the bash args_

bash args: `xargs -I {} curl -sk {} | jq '[.results[]|.name]'`
```json
https://swapi.co/api/starships/
```

# when using many carets
_each line separately pipes stdin to xxd_

bash args: `xxd`
```
Lorem
ipsum
dolor
sit
amet,
consectetur
adipisicing
elit.
```

# pipes work like the shell args
_more piping examples_

bash args: `sort | uniq`
bash args: `sort | tac`
```kotlin
import java.awt.Graphics2D
import java.awt.Graphics
import com.intellij.util.ui.UIUtil.isValidFont
import java.awt.Graphics
import com.util.xmlb.XmlSerializerUtil
import com.intellij.openapi.util.Pair
import bun.schwing.SwingUtilities2
import com.intellij.openapi.util.Pair
import bun.schwing.SwingUtilities2
import com.intellij.openapi.util.IconLoader

class NoClass {
    private final val noStyle
    protected val what
    public val ever
    private val whatEver
    // todo improve readability
    protected val hatNever
    private val wutSliver
}
```

# install npm packages 
_shows how to ignore console error output_

bash args: `npm install debug lodash 2>/dev/null`
```
```

# ❤️ npm
_run any of the zillions of npm packages what have a CLI_

- bash args: `npm install -g marked remark-lint 2>/dev/null`
- bash args: `marked`
```html
# lol
_that's rad_
```
equivalent to `echo \# lol | marked >somefile` but isn't this easier?

# run npm installed packages
_create scripts inside your editor_

bash args: `node`
```log
const namespace = 'namespace:ijplugins:are:unbearable'
const d = require('debug')
const _ = require('lodash')
const logger = d(namespace)
d.enable(`${namespace}`)

// just prints
logger('pipeprofen!')
```

# math
_the possibilities are endless_

bash args: `wcalc`
```
1776-2018
```

# dec to hex
bash args: `wcalc -h`
```
3203398350
```

# man page search
_intellij is better at searching text than the terminal's `man`_

this example shows that the selected text is piped first through bash
then proceeds through the pipes.

todo: should do something cleverer to automate `xargs -I{} bash -c '{}'`

bash args: `xargs -I{} bash -c '{}' | sed 's/ - /#/' | cut -d'#' -f2 | sort`
```markdown
## printf
man -k printf 

## grep
man -k grep 

## git
man -k git 
```

