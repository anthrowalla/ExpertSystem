# ExpertSystem

A rule-based expert system shell (circa 1995-2005) that runs as both a Java Applet and a standalone application. The system provides a consultation interface where users can interact with knowledge bases defined in `.exp` rule files.

## Features

- **Dual-mode execution**: Runs as both a Java Applet and standalone application via `AppletFrame`
- **Rule compiler**: Parses and compiles expert system rules from text files
- **Interactive consultation**: AskMe panel with True/False/Why/Stop buttons for user interaction
- **Multiple knowledge bases**: Includes built-in examples (Animal, Izzat, Navigate, vi) and supports custom URLs
- **Responsive UI**: Modern layout with resizable window and enlarged fonts for readability

## Building

Requires Java 17 or later and Apache Ant.

```bash
ant jar                    # builds dist/ExpertSystem.jar
ant run                    # runs standalone from dist/
ant clean                  # remove build and dist directories
```

## Running

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

Knowledge bases are defined in `.exp` files with a simple rule syntax:

```
; Comments start with semicolon
goal: is_animal
ask: "Does it have wings?"
   yes -> has_wings
   no -> no_wings
```

Example knowledge bases are included in the `resources/` directory.

## Recent Bug Fixes

This project has been extensively modernized and debugged to run on Java 17+. Major fixes include:

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
├── src/                    # Java source files
│   ├── ExpertSystem.java   # Main applet/application
│   ├── Infer.java          # Inference engine
│   ├── InferCompiler.java  # Rule compiler
│   ├── AskMe.java          # Consultation UI panel
│   └── ...
├── resources/              # Knowledge base files (.exp)
├── bin/                    # Compiled classes
├── dist/                   # Built JAR file
├── build.xml               # Ant build configuration
└── progress.md             # Detailed bug fix log
```

## Requirements

- Java 17 or later
- Apache Ant (for building)

## License

Preserved from original circa 1995-2005 era codebase.

## Contributing

This is a legacy codebase that has been modernized to run on contemporary Java versions. Contributions are welcome, particularly for further modernization efforts while preserving the original functionality.
