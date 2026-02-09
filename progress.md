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

### 8. AskMe buttons unresponsive on Java 17 (AWT 1.0 event model + deprecated enable/disable)
- **File:** `src/AskMe.java`
- **Severity:** Critical — True/False/Why/Stop buttons completely non-functional
- **Problem:** Two issues combined: (1) Button clicks were handled via the deprecated `handleEvent(Event)` with `Event.ACTION_EVENT` checks (AWT 1.0 model). On Java 17, these events no longer propagate reliably for Button components. (2) `yesButton.enable()` / `yesButton.disable()` are deprecated and don't reliably re-enable ActionEvent delivery on modern Java. Both issues together made the consultation UI completely unresponsive.
- **Fix:** Replaced `handleEvent()` with proper `ActionListener` registrations on all four buttons via a new `SymAction` inner class. Replaced all `enable()`/`disable()` calls with `setEnabled(true)`/`setEnabled(false)`.

### 9. Thread.stop() throws UnsupportedOperationException on Java 17
- **File:** `src/Infer.java:296-299`
- **Severity:** Critical — crashes the inference engine on exit/quit
- **Problem:** `doexit()` called `this.stop()` which throws `UnsupportedOperationException` on Java 9+. This made the Quit button and normal inference completion crash.
- **Fix:** Replaced with a `volatile boolean exitRequested` flag. Added flag checks in `run()`, `verify()`, and `askval()` loops so the thread exits gracefully.

### 10. URLData.activate() NPE when stream is null
- **File:** `src/URLData.java:97-99`
- **Severity:** High — crashes on startup if any .exp file can't be opened
- **Problem:** `theStream.close()` was called without null check. If `openURL()` failed (stream is null), `activate()` threw NullPointerException.
- **Fix:** Added `if (theStream != null)` guard before close.

### 11. file:/ URL path mangled by manual string manipulation (file://////path)
- **File:** `src/ExpertSystem.java`, `src/URLData.java`
- **Severity:** High — .exp knowledge base files failed to load
- **Problem:** `initializeApp()` hand-built `file:///` URLs, and `URLData.baseContext()` tried to normalize them by string manipulation. Java's `URL.toString()` normalizes `file:///path` to `file:/path`, so `baseContext()` would re-prepend `file:///` creating `file://////path`. The two methods fought each other producing wrong URLs.
- **Fix:** Replaced `initializeApp()` with `File.toURI().toURL()` which always produces the correct URL. Replaced `URLData`'s hand-built string concatenation with `new URL(context, url)` — the standard Java API for resolving relative URLs. Removed `baseContext()` entirely.

### 12. build.xml: ant compiled with Java 24, incompatible with Java 17 runtime
- **File:** `build.xml`
- **Severity:** High — JAR wouldn't run: UnsupportedClassVersionError
- **Problem:** Ant used its own Java 24 to compile, but the runtime `java` was Java 17. Class file version 68.0 (Java 24) can't run on Java 17.
- **Fix:** Added `source="17" target="17"` to the `<javac>` task. Also added `dir="${dist}"` to the `<java>` run task so .exp files are found.

## UI Modernization

### 13. Replaced null layout with responsive BorderLayout + CardLayout
- **Files:** `src/ExpertSystem.java`, `src/AppletFrame.java`
- **Problem:** Original null layout produced a fixed-size, non-resizable UI.
- **Changes:**
  - Main applet uses `BorderLayout`: title label (NORTH), center panel (CENTER), bottom toolbar (SOUTH).
  - Center panel uses `BorderLayout`: AskMe panel (NORTH), text area card panel (CENTER).
  - `sourceText` and `queryText` share a `CardLayout` panel, swapped on Consult/Edit toggle.
  - Text card panel has minimum size 700×300 and preferred width matching parent.
  - 20px left/right margin panels around text areas.
  - `AppletFrame` sets minimum size 700×400, default size 700×701, offset 50px from left/top.
  - Window close handled via `WindowListener` (replaces AWT 1.0 `WINDOW_DESTROY`).

### 14. Enlarged fonts and controls for readability
- **Files:** `src/ExpertSystem.java`, `src/AskMe.java`
- **Changes:**
  - `sourceText`: Monospaced 24pt (was 12pt).
  - `queryText`: Times 28pt (was 14pt).
  - Main title ("MicroExpert"): Helvetica 36pt (was 18pt).
  - AskMe panel text: Helvetica 18pt (was 12pt).
  - AskMe buttons: Helvetica Bold 18pt (was 12pt).
  - Increased internal padding (`ipady`/`ipadx`) and label preferred sizes.

### 15. Added 10px vertical spacing between AskMe panel components
- **File:** `src/AskMe.java`
- **Changes:** Added `gcon.insets = new Insets(10, 0, 10, 0)` to all seven `GridBagConstraints` blocks (titleLabel, label3D1, optionList, yesButton, noButton, whyButton, quitButton) for consistent 10px gaps above and below each component.

### 16. AskMe panel height too small — components half-obscured
- **File:** `src/AskMe.java`
- **Severity:** High — buttons and labels clipped/overlapping
- **Problem:** `preferredSize()` returned a hardcoded `Dimension(525, 90)`. After enlarging fonts to 18pt, adding `ipady=8` internal padding, and 10px top/bottom insets, the actual content needed far more than 90px. Since the AskMe panel sits in `BorderLayout.NORTH` (which uses preferred height), everything was squished into 90 pixels.
- **Fix:** Changed `preferredSize()` to delegate to `GridBagLayout.preferredLayoutSize(this)` so the height is calculated naturally from the actual contents. A minimum width of 525px is still enforced.

## Remaining Warnings (not fixed)
The 20 compiler warnings are all deprecated API usage standard for 1995-2005 era Java:
- `java.applet.Applet` (removed in modern Java)
- `new Integer()`, `new Float()`, `new Long()`, `new Double()` constructors
- `Component.show()`, `hide()`, `enable()`, `disable()`, `resize()`, `size()`
- `DataInputStream.readLine()`
- `Class.newInstance()`
- Raw type `Hashtable` and `Vector` without generics

These all compile and function correctly but would require a larger rewrite to modernize.

## Build
```
ant jar                    # builds dist/ExpertSystem.jar
ant run                    # runs standalone from dist/
```
