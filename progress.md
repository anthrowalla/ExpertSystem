# ExpertSystem Bug Fix Progress

## Project Overview
Java rule-based expert system (circa 1995-2005). 11 source files in `src/`, no package declarations, builds with Ant. Runs as both an Applet and standalone application via `AppletFrame`.

## Bugs Found and Fixed

### 1. PropHash.contains() should be containsKey()
- **File:** `src/Infer.java:21`
- **Severity:** High — breaks core inference logic
- **Problem:** `Hashtable.contains()` searches values, not keys. The lookup by key `s.p` always failed, creating duplicate `Str` entries instead of reusing existing ones. The inference engine would treat the same property name as separate properties.
- **Fix:** Changed `contains(s.p)` to `containsKey(s.p)`.

### 2. String comparison with == instead of .equals()
- **File:** `src/StringStream.java:78`
- **Severity:** High — breaks rule compiler input parsing
- **Problem:** `while (line == "")` compares object references, not string content. Blank lines were never skipped by `sfgets(trim, noblanks)`, potentially feeding empty lines to the rule compiler.
- **Fix:** Changed to `while (line != null && line.equals(""))`.

### 3. Deprecated 4-arg String constructor corrupts data
- **File:** `src/URLData.java:77`
- **Severity:** Medium — data corruption for non-ASCII input
- **Problem:** `new String(cbuf,0,0,hm)` uses the deprecated `String(byte[],hibyte,offset,length)` constructor which mangles non-ASCII characters.
- **Fix:** Changed to `new String(cbuf,0,hm)`.

### 4. URL selection dropdown did nothing for built-in choices
- **File:** `src/ExpertSystem.java:107-141`
- **Severity:** High — UI completely broken for 4 of 5 menu items
- **Problem:** Selecting Animal, Izzat, Navigate, or vi from the dropdown set `aURL` but never called `loadURL()` or updated `sourceText`. Only the "Your Choice" branch loaded and displayed content.
- **Fix:** Added `sourceText.setText(loadURL(aURL))` and early return for pre-loaded URL selections.

### 5. Format string / argument mismatch in newstate()
- **File:** `src/InferCompiler.java:163`
- **Severity:** Medium — garbled error message, potential crash
- **Problem:** `printfld("%s: bad new val: %d\n", newState)` had two format specifiers but only one argument. The `progname` string was missing.
- **Fix:** Added `progname` as the second argument.

### 6. String.trim() return value discarded in savestr()
- **File:** `src/InferCompiler.java:224`
- **Severity:** Medium — leading/trailing whitespace not removed from rule text
- **Problem:** `s.trim()` was called without assigning the result. Java strings are immutable, so `trim()` returns a new string and the original is unchanged.
- **Fix:** Changed to `s = s.trim()`.

### 7. Thread.sleep() called on uninitialized instance variable
- **File:** `src/ExpertSystem.java:135,148`
- **Severity:** Low — works by accident since sleep() is static
- **Problem:** `ourThread.sleep(5)` references an uninitialized `Thread` field. Java allows static method calls on null references, but the code is misleading and fragile.
- **Fix:** Changed to `Thread.sleep(5)`.

## Remaining Warnings (not fixed)
The 20 compiler warnings are all deprecated API usage standard for 1995-2005 era Java:
- `java.applet.Applet` (removed in modern Java)
- `new Integer()`, `new Float()`, `new Long()`, `new Double()` constructors
- `Component.show()`, `hide()`, `enable()`, `disable()`, `resize()`, `size()`
- `handleEvent(Event)` (old AWT event model)
- `DataInputStream.readLine()`
- `Class.newInstance()`
- Raw type `Hashtable` and `Vector` without generics

These all compile and function correctly but would require a larger rewrite to modernize.

## Build
```
javac src/*.java -d bin    # compiles with 20 deprecation warnings, 0 errors
ant jar                    # builds dist/ExpertSystem.jar
ant run                    # runs standalone
```
