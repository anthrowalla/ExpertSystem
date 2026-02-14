# ExpertSystem

A rule-based expert system shell (circa 1995-2005) available in both Java and JavaScript implementations. The system provides a consultation interface where users can interact with knowledge bases defined in `.exp` rule files.

## Features

- **Multiple implementations**: Available as both a Java Applet/application and a modern JavaScript/browser version
- **Rule compiler**: Parses and compiles expert system rules from text files
- **Interactive consultation**: AskMe panel with True/False/Why/Stop buttons for user interaction
- **Multiple knowledge bases**: Includes built-in examples (Animal, Izzat, Navigate, vi) and supports custom URLs
- **Responsive UI**: Modern layout with resizable window and enlarged fonts for readability
- **Backward chaining**: Implements goal-directed inference engine
- **Hypothesis support**: Special `HYP` keyword allows marking final conclusions that end consultation
- **Proactive questioning**: When stuck, system identifies most relevant unknown facts to ask about

## JavaScript Version

The modern JavaScript implementation runs entirely in the browser with no server required:

```bash
# Simply open the HTML file in a browser
open expertjs/index.html
```

Or serve it via a local web server:

```bash
cd expertjs
python3 -m http.server 8000
# Then visit http://localhost:8000
```

### JavaScript Features

- **Pure client-side**: No server required, runs entirely in the browser
- **Modern async/await**: Clean asynchronous code for user interaction
- **Multi-pass inference**: Continues reasoning until all goals resolved or hypothesis reached
- **Duplicate prevention**: Tracks displayed conclusions to avoid redundant output
- **Enhanced tracing**: Optional detailed trace output for debugging knowledge bases
- **HYP support**: Hypothesis conclusions end consultation immediately
- **Smart questioning**: Identifies most relevant unknowns when inference stalls

### Building

Requires Java 17 or later and Apache Ant.

```bash
ant jar                    # builds dist/ExpertSystem.jar
ant run                    # runs standalone from dist/
ant clean                  # remove build and dist directories
```

### Running

After building, run the standalone application:

```bash
java -jar dist/ExpertSystem.jar
```

You can also run directly using Ant:

```bash
ant run
```

## Usage

1. Select a knowledge base from the dropdown menu (Animal, Izzat, Navigate, vi) or enter a custom URL
2. Click "Consult" to load the knowledge base
3. Answer questions using the True/False buttons in the AskMe panel
4. Click "Why" to see the current reasoning context
5. Click "Stop" to end the consultation
6. Use "Edit" to view and modify the source rules

## Knowledge Base Format

Knowledge bases are defined in `.exp` files with a simple rule syntax.

### Rule Keywords

Both Java and JavaScript versions support these rule keywords:

**Antecedents (IF conditions):**
- `IF` - Start a new rule
- `AND` - Additional condition
- `ANDNOT` - Negated condition
- `ASK` - Ask user directly

**Consequents (THEN conclusions):**
- `THEN` - Normal conclusion
- `THENNOT` - Negated conclusion
- `THENHYP` - Hypothesis conclusion (ends consultation)
- `CONCLUDE` - Final conclusion

**Special directives:**
- `ASSERT` - Set fact to true
- `ASSERTNOT` - Set fact to false

### Example Rule File

```
; Comments start with semicolon
IF animal has feathers
AND animal lays eggs
THEN animal is bird

IF animal is mammal
AND animal eats meat
THEN animal is carnivore

IF animal is mammal
AND animal has pointed teeth
AND animal has forward pointing eyes
THENHYP animal is tiger
```

### Advanced Features

**Hypothesis conclusions**: Rules ending with `THENHYP` mark final diagnoses:
```
IF animal is carnivore
AND animal has tawny color
AND animal has black stripes
THENHYP animal is tiger
```
When a HYP rule is proven, the system displays "Conclude: animal is tiger" and stops further inference.

**Negation**: Use `NOT` variants for false conditions:
```
IF animal flies
ANDNOT animal is bird
THEN animal is bat
```

Example knowledge bases are included in the `expertjs/kb/` and `resources/` directories.

## Recent Bug Fixes

This project has been extensively modernized and debugged. Major fixes include:

**JavaScript Version:**
- Implemented multi-pass inference with proactive questioning
- Added hypothesis (HYP) support for early termination
- Fixed duplicate conclusion display tracking
- Enhanced trace output for debugging knowledge bases
- Implemented smart selection of unknown facts to ask about

**Java Version:**
- Replaced deprecated `Hashtable.contains()` with `containsKey()`
- Fixed string comparisons using `.equals()` instead of `==`
- Replaced AWT 1.0 event model with proper `ActionListener` implementations
- Fixed `Thread.stop()` crashes by using a flag-based exit pattern
- Corrected URL handling for local `.exp` files
- Modernized UI with `BorderLayout` and `CardLayout` for responsive design
- Enlarged fonts and controls for better readability

See `progress.md` for a complete detailed log of all fixes.

## Project Structure

```
ExpertSystem/
├── expertjs/               # JavaScript/browser implementation
│   ├── expert.js          # Core inference engine (ES6 class)
│   ├── index.html          # Web interface
│   └── kb/                # Knowledge base files (.exp)
│       ├── animal.exp
│       └── ...
├── src/                    # Java source files
│   ├── ExpertSystem.java   # Main applet/application
│   ├── Infer.java          # Inference engine
│   ├── InferCompiler.java  # Rule compiler
│   ├── AskMe.java          # Consultation UI panel
│   └── ...
├── resources/              # Java knowledge base files (.exp)
├── bin/                    # Compiled Java classes
├── dist/                   # Built JAR file
├── build.xml               # Ant build configuration
├── README.md               # This file
└── progress.md             # Detailed bug fix log
```

## Requirements

**JavaScript Version:**
- Modern web browser (Chrome, Firefox, Safari, Edge)
- No build step required

**Java Version:**
- Java 17 or later
- Apache Ant (for building)

## License

Preserved from original circa 1995-2005 era codebase.

## Contributing

This is a legacy codebase that has been modernized to run on contemporary Java versions. Contributions are welcome, particularly for further modernization efforts while preserving the original functionality.
