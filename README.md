# Pipeprofen
_[pahy-pyoo-proh-fuhn, pahy-peh-proh-fen]_ ; **noun**
1. an intellij plugin ingesting selected editor lines,
piping to a custom bash command, and whose process
output replaces those lines.
2. alleviates headache symptoms

## usage
default keymap: <kbd>ctrl</kbd><kbd>alt</kbd><kbd>shift</kbd><kbd>x</kbd>

1. highlight lots of text
    1. note: works for multiple carets, too (latest jetbrains)
1. press keyboard shortcut
1. enter the command that bash runs

## internals
    for each selection block s
      create temp file f from s characters
      show prompt to get args
      pipe f as stdin to `bash -c ${args}`
      replace s with (stdout + stderr)
    remove selection

## demo
![nice](screencast-pipeprofen.gif)

# coc
Praise God, St. Benedict and the
[sqlite coc](/CODE_OF_CONDUCT.md)
`#usethatword` `#nationalism` :us: :us: :us:

# todo
1. option toggles keep/replace selected text
1. ingest `args` from clipboard, like so `$(bash -c args)`
1. history of previous args
1. customize `bash -c` in case bash is not present
1. test on windows

# thanks
Thank you for using my plugin, no bullshit.
Help me improve it by loggin' a bug.

Cheers!

> Copyright (c) 2018 Unbearable Professional
> 
> This is free and unencumbered software released into the public domain.
> 
> Anyone is free to copy, modify, publish, use, compile, sell, or
> distribute this software, either in source code form or as a compiled
> binary, for any purpose, commercial or non-commercial, and by any
> means.
> 
> In jurisdictions that recognize copyright laws, the author or authors
> of this software dedicate any and all copyright interest in the
> software to the public domain. We make this dedication for the benefit
> of the public at large and to the detriment of our heirs and
> successors. We intend this dedication to be an overt act of
> relinquishment in perpetuity of all present and future rights to this
> software under copyright law.
> 
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
> EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
> MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
> IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
> OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
> ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
> OTHER DEALINGS IN THE SOFTWARE.
> 
> For more information, please refer to http://unlicense.org/
