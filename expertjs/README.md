# MicroExpert JavaScript

A JavaScript port of the MicroExpert rule-based expert system, originally written in Java as an applet. This version runs entirely in the browser without requiring a server.

## Features

- **Backward Chaining Inference Engine**: Implements the same backward chaining algorithm as the original Java version
- **Declarative Rule Language**: Supports the same `.exp` knowledge base syntax
- **Interactive Questioning**: Asks users questions dynamically to derive conclusions
- **Why Explanations**: Users can ask "Why?" to see the reasoning chain
- **No Server Required**: Runs entirely client-side in modern browsers
- **Modern UI**: Clean, responsive interface with real-time feedback

## Running the Expert System

Simply open `index.html` in a web browser. No server or build process required.

```bash
# On macOS/Linux
open index.html

# Or just double-click index.html in your file browser
```

## Knowledge Base Syntax

The system uses a declarative rule language with the following keywords:

### Basic Rules

```
IF condition
THEN conclusion
```

### Logical Operators

- `AND` - Add additional consequent
- `ANDIF` - Add additional antecedent (must also be true)
- `ANDNOT` - Add negated antecedent (must be false)
- `IFNOT` - Negated condition

### Special Keywords

- `THENHYP` - Hypothetical conclusion (exits if true)
- `ASK` - Directly ask the user a question
- `ASSERT` - Assert a fact as true without proving

### Example Rule

```
if animal is mammal
and animal eats meat
then animal is carnivore

if animal is carnivore
and animal has pointed teeth
and animal has retractable claws
and animal has forward pointing eyes
then animal is cat

if animal is cat
and animal has tawny color
and animal has black stripes
thenhyp animal is tiger
```

## API Usage

You can also use the expert system programmatically:

```javascript
// Create an expert system
const expert = createExpertSystem({
  verbose: true,
  maxDepth: 100
});

// Set up callbacks
expert.onAskQuestion = async (question, explanation) => {
  console.log('Question:', question);
  console.log('Why:', explanation);
  return confirm(question); // true/false
};

expert.onConclusion = (element) => {
  console.log('Conclusion:', element.text);
};

expert.onProgress = (message) => {
  console.log('Progress:', message);
};

// Compile and run
expert.compileKnowledgeBase(knowledgeBaseText);
await expert.run();
```

## Architecture

### Core Classes

- **ExpertSystem**: Main inference engine implementing backward chaining
- **Element**: Represents a condition or conclusion with truth value
- **Rule**: Contains antecedents (IF parts) and consequents (THEN parts)

### Truth Values

- `TRUE = -1` (binary ones)
- `FALSE = 0` (binary zeros)
- `UNKNOWN = 42` (special value)

### Inference Process

1. System starts with goal elements (conclusions to prove)
2. For each goal, searches for rules that conclude it
3. Verifies all antecedents of matching rules recursively
4. If no rule can prove a fact, asks the user
5. Tracks dependency chain for "Why" explanations

## Differences from Java Version

- **Async/Await**: Uses Promises instead of Java threads
- **DOM-based UI**: Modern web interface instead of Java AWT
- **No Applet**: Runs directly in browser without plugin
- **Same Algorithm**: Implements identical backward chaining logic

## Browser Compatibility

Works in all modern browsers that support ES6+:
- Chrome/Edge 88+
- Firefox 85+
- Safari 14+

## License

Port of the original MicroExpert system. Maintains compatibility with the original knowledge base format.
