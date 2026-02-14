/**
 * MicroExpert JavaScript - A rule-based expert system
 * Port of Java MicroExpert applet to modern JavaScript
 * Can run without a server - pure client-side
 */

// Truth value constants (matching Java version)
const TRUTH = {
  UNKNOWN: 42,
  TRUE: -1,
  FALSE: 0
};

// Element types
const ELEMENT_TYPE = {
  ANT: 'ANT',  // Antecedent (IF part)
  CON: 'CON',  // Consequent (THEN part)
  ANY: 'ANY'   // Either part
};

// Rule types for keywords
/* these need to be sorted so longer versions appear before shorter to avoid incorrect parsing
const RULE_TYPES = {
  ANDIF: 'ANT',
  ANDNOT: 'ANT',
  ANDRUN: 'ANDRUN',
  IFNOT: 'ANT',
  THENHYP: 'CON',
  IF: 'ANT',
  THEN: 'CON',
  AND: 'CON',
  ASK: 'ASK',
  ASSERT: 'ASSERT',
  NOT: 'NOT'
};
*/

const RULE_TYPES = {
	WHENNOT: 'ANT|NOT',
	WHEN: 'ANT',
	THENRUNHYP: 'CON|HYP',
	THENRUN: 'CON',
	THENNOT: 'CON|NOT',
	THENHYP: 'CON|HYP',
	THENASKNOT: 'CON|NOT',
	THENASK: 'CON',
	THEN: 'CON',
	IFRUN: 'ANT',
	IFNOTRUN: 'ANT|NOT',
	IFNOT: 'ANT|NOT',
	IF: 'ANT',
	CONCLUDENOT: 'CON|NOT',
	CONCLUDE: 'CON',
	ASSERTNOT: 'CON|NOT',
	ASSERT: 'CON',
	ASKNOT: 'ANT|NOT',
	ASK: 'ANT',
	ANDWHEN: 'ANT',
	ANDTHENRUNHYP: 'CON|HYP',
	ANDTHENRUN: 'CON',
	ANDTHENNOT: 'CON|NOT',
	ANDTHENHYP: 'CON|HYP',
	ANDTHEN: 'CON',
	ANDRUN: 'ANT',
	ANDNOTRUN: 'ANT|NOT',
	ANDNOT: 'ANT|NOT',
	ANDIFRUN: 'ANT',
	ANDIF: 'ANT',
	AND: 'ANT'
};
/**
 * Represents a single element (condition or conclusion) in a rule
 */
class Element {
  constructor(type, text, ruleType = ELEMENT_TYPE.ANY) {
    this.type = type;           // ELEMENT_TYPE.ANT, CON, or ANY
    this.text = text;           // The text content
    this.ruleType = ruleType;   // The specific rule type (ANDIF, ANDNOT, etc.)
    this.truthValue = TRUTH.UNKNOWN;
    this.verifying = false;     // Circular logic detection flag
    this.asked = false;         // Track if we've already asked the user
  }

  toString() {
    return `${this.ruleType}: ${this.text} [${this.getTruthString()}]`;
  }

  getTruthString() {
    if (this.truthValue === TRUTH.TRUE) return 'TRUE';
    if (this.truthValue === TRUTH.FALSE) return 'FALSE';
    return 'UNKNOWN';
  }
}

/**
 * Represents a rule with antecedents (conditions) and consequences (conclusions)
 */
class Rule {
  constructor(text = '') {
    this.text = text;
    this.antecedents = [];  // IF parts
    this.consequents = [];  // THEN parts
  }

  addAntecedent(element) {
    this.antecedents.push(element);
  }

  addConsequent(element) {
    this.consequents.push(element);
  }

  toString() {
    let str = '';
    if (this.antecedents.length > 0) {
      str += 'IF ' + this.antecedents.map(a => a.text).join(', ');
    }
    if (this.consequents.length > 0) {
      str += ' THEN ' + this.consequents.map(c => c.text).join(', ');
    }
    return str;
  }
}

/**
 * The main Expert System class
 * Implements backward chaining inference engine
 */
class ExpertSystem {
  constructor(options = {}) {
    this.rules = [];
    this.facts = new Map();  // fact text -> truth value
    this.goalElements = [];  // Goals to prove
    this.conclusions = [];   // Proven conclusions
    this.dependencyStack = [];  // Track inference chain for "Why" explanations
    this.hypothesisReached = false;  // Track if a HYP conclusion has been reached

    // Callbacks for UI interaction
    this.onAskQuestion = null;      // Called when system needs to ask user a question
    this.onConclusion = null;       // Called when a conclusion is reached
    this.onProgress = null;         // Called for progress updates
    this.onExplanation = null;      // Called to show why a question is being asked
    this.onError = null;            // Called on errors
    this.onTrace = null;            // Called for trace output

    // Configuration
    this.verbose = options.verbose || false;
    this.maxDepth = options.maxDepth || 100;
  }

  /**
   * Parse and compile a knowledge base from text
   */
  compileKnowledgeBase(text) {
    this.rules = [];
    this.facts.clear();
    this.conclusions = [];
    this.goalElements = [];
    this.allConsequents = [];  // Track all consequents separately
    this.allAntecedents = [];  // Track all antecedents separately

    const lines = text.split('\n');
    let currentRule = null;
    let ruleText = '';

    for (let i = 0; i < lines.length; i++) {
      let line = lines[i].trim();

      // Skip empty lines and comments
      if (!line || line.startsWith('//') || line.startsWith('#')) {
        continue;
      }

      // Extract keyword if present
      let keyword = null;
      let content = line;

      // Check for rule keywords
      const keywords = Object.keys(RULE_TYPES);
      for (const kw of keywords) {
        if (line.toLowerCase().startsWith(kw.toLowerCase())) {
          keyword = kw.toUpperCase();
          content = line.substring(kw.length).trim();
          break;
        }
      }

      // For IFNOT and ANDNOT keywords, strip the "not " prefix from content
      // The negation is indicated by the ruleType, not the element text
      // Note: No longer needed since ruleType now contains NOT flag
      // if ((keyword === 'IFNOT' || keyword === 'ANDNOT') && content.toLowerCase().startsWith('not ')) {
      //   content = content.substring(4).trim();
      // }

      // At this point should have keyword, if any, set and content - line less keyword
      // If no keyword found, check if this is a continuation
      if (!keyword) {
        // If we have a current rule, this might be part of its text
        if (currentRule && currentRule.consequents.length > 0) {
          // This is a new rule starting without IF
          // Save previous rule and start new one
          if (currentRule) {
            this.rules.push(currentRule);
          }
          currentRule = null;
          ruleText = '';
        }
        keyword = 'IF';  // Default to IF for standalone lines, consider changing to ASSERT
        content = line; // already trimmed at start
      }

      // Determine rule type
      const ruleType = RULE_TYPES[keyword] || ELEMENT_TYPE.ANY; // would probably prefer an error if ruletype not found

      // Handle different keywords based on ruleType category
      const isAntecedent = ruleType.startsWith('ANT');
      const isConsequent = ruleType.startsWith('CON');

      if (isAntecedent) {
        // Start new rule or continue existing one
        if (keyword === 'IF' || keyword === 'IFNOT') {
          if (currentRule) {
            this.rules.push(currentRule);
          }
          currentRule = new Rule(content);
          ruleText = line;
        } else {
          if (!currentRule) {
            currentRule = new Rule();
          }
          ruleText += '\n' + line;
        }

        // Add antecedent
        const element = new Element(ELEMENT_TYPE.ANT, content, ruleType);
        currentRule.addAntecedent(element);
        this.allAntecedents.push(element);

      } else if (isConsequent) {
        // Consequents should be added to the existing rule, not create a new one
        // If there's no current rule, we have a syntax error in the knowledge base
        if (!currentRule) {
          this.handleError(`Syntax error: consequent keyword "${keyword}" found without antecedents`);
          continue;
        }
        ruleText += '\n' + line;

        const element = new Element(ELEMENT_TYPE.CON, content, ruleType);
        currentRule.addConsequent(element);
        this.allConsequents.push(element);

      } else if (keyword === 'ASK' || keyword === 'ASKNOT') {
        // ASK and ASKNOT are special directives - store as goals
        const element = new Element(ELEMENT_TYPE.ANY, content, ruleType);
        this.goalElements.push(element);

      } else if (keyword === 'ASSERT' || keyword === 'ASSERTNOT') {
        // ASSERT immediately sets a fact to true, ASSERTNOT sets to false
        const hasNot = ruleType.includes('NOT');
        this.facts.set(content.toLowerCase(), hasNot ? TRUTH.FALSE : TRUTH.TRUE);
      }
    }

    // Push the last rule
    if (currentRule) {
      this.rules.push(currentRule);
    }

    // All consequents are potential goals (backward chaining from conclusions)
    this.goalElements.push(...this.allConsequents);

    this.trace(`Compiled ${this.rules.length} rules`);
    this.trace(`Antecedents: ${this.allAntecedents.length}, Consequents: ${this.allConsequents.length}`);
    this.trace(`Goal elements: ${this.goalElements.length}`);
    if (this.verbose) {
      this.trace('--- All antecedents: ---');
      for (let i = 0; i < this.allAntecedents.length; i++) {
        this.trace(`  ${i + 1}. "${this.allAntecedents[i].text}" (${this.allAntecedents[i].ruleType})`);
      }
      this.trace('--- All consequents: ---');
      for (let i = 0; i < this.allConsequents.length; i++) {
        this.trace(`  ${i + 1}. "${this.allConsequents[i].text}" (${this.allConsequents[i].ruleType})`);
      }
    }
    currentRule = null;
    ruleText = '';
    return this;
  }

  /**
   * Load a knowledge base from a URL
   */
  async loadKnowledgeBase(url) {
    try {
      const response = await fetch(url);
      const text = await response.text();
      this.compileKnowledgeBase(text);
      return this;
    } catch (error) {
      this.handleError(`Failed to load knowledge base: ${error.message}`);
      throw error;
    }
  }

  /**
   * Main inference loop - proves all goal elements
   */
  async run() {
    this.log('Starting inference...');
    this.conclusions = [];
    this.displayedConclusions = new Set(); // Track which conclusions have been displayed
    this.hypothesisReached = false; // Reset hypothesis flag

    this.trace('=== Starting inference ===');
    this.trace(`Total goals to prove: ${this.goalElements.length}`);

    let passNumber = 1;
    let maxPasses = 10; // Prevent infinite loops
    let previousUnknownCount = -1;

    while (passNumber <= maxPasses) {
      this.trace(`=== Pass ${passNumber} ===`);
      const unknownGoals = [];

      for (let i = 0; i < this.goalElements.length; i++) {
        // Stop processing if a hypothesis has been reached
        if (this.hypothesisReached) {
          this.trace(`Hypothesis reached, stopping further goal processing`);
          break;
        }

        const goal = this.goalElements[i];
        const key = goal.text.toLowerCase();

        // Skip if already proven
        if (this.facts.has(key)) {
          const knownValue = this.facts.get(key);
          if (knownValue === TRUTH.TRUE || knownValue === TRUTH.FALSE) {
            this.trace(`--- Goal ${i + 1}: "${goal.text}" - already ${knownValue === TRUTH.TRUE ? 'TRUE' : 'FALSE'} ---`);
            continue;
          }
        }

        this.trace(`--- Goal ${i + 1}: "${goal.text}" (type: ${goal.ruleType}) ---`);
        this.dependencyStack = [];

        const result = await this.prove(goal);

        if (result === TRUTH.TRUE) {
          this.conclusions.push(goal);
          // Only notify if we haven't already displayed this conclusion
          const conclusionKey = goal.text.toLowerCase();
          if (!this.displayedConclusions.has(conclusionKey)) {
            this.displayedConclusions.add(conclusionKey);

            // Check if this is a hypothesis (HYP) conclusion
            const hasHyp = goal.ruleType && goal.ruleType.includes('HYP');
            if (hasHyp) {
              this.trace(`HYPOTHESIS: ${goal.text}`);
              this.hypothesisReached = true;
              // Create a modified element for notification with "Conclude: " prefix
              const hypElement = new Element(goal.type, `Conclude: ${goal.text}`, goal.ruleType);
              this.notifyConclusion(hypElement);
            } else {
              this.notifyConclusion(goal);
            }
          } else {
            this.trace(`Conclusion already displayed: ${goal.text}`);
          }
        } else if (result === TRUTH.UNKNOWN) {
          this.trace(`Goal "${goal.text}" could not be determined (UNKNOWN)`);
          unknownGoals.push(goal);
        }
      }

      // Break out of while loop if hypothesis was reached
      if (this.hypothesisReached) {
        this.trace(`Hypothesis conclusion reached, ending inference`);
        break;
      }

      // Check if we've made progress or if all goals are resolved
      if (unknownGoals.length === 0) {
        this.trace(`All goals resolved after pass ${passNumber}`);
        break;
      }

      // Check if we're stuck (same number of unknowns as before)
      if (unknownGoals.length === previousUnknownCount) {
        this.trace(`Stuck at ${unknownGoals.length} unknown goals, trying proactive questioning`);

        // Find the unknown consequent that appears most often as an antecedent
        const mostReferenced = this.findMostReferencedUnknown(unknownGoals);

        if (mostReferenced) {
          this.trace(`Asking about most referenced unknown: "${mostReferenced.text}" (appears ${mostReferenced.count} times as antecedent)`);

          // Create a temporary element to ask about
          const tempElement = new Element(ELEMENT_TYPE.ANT, mostReferenced.text, 'ASK');
          const answer = await this.askUser(tempElement);
          const answerKey = mostReferenced.text.toLowerCase();

          // Set the fact
          this.facts.set(answerKey, answer);
          this.trace(`User answered: "${mostReferenced.text}" is ${answer === TRUTH.TRUE ? 'TRUE' : 'FALSE'}`);

          // Continue to next pass to use this new information
          previousUnknownCount = unknownGoals.length;
          passNumber++;
          continue;
        } else {
          this.trace(`No more questions to ask, ending inference`);
          break;
        }
      }

      previousUnknownCount = unknownGoals.length;
      passNumber++;
    }

    this.log(`Inference complete. Found ${this.conclusions.length} conclusions.`);
    return this.conclusions;
  }

  /**
   * Find the unknown consequent that appears most frequently as an antecedent
   */
  findMostReferencedUnknown(unknownGoals) {
    const counts = new Map();

    // Count how many times each unknown goal appears as an antecedent
    for (const goal of unknownGoals) {
      const key = goal.text.toLowerCase();

      // Count occurrences in all antecedents
      const antecedentCount = this.allAntecedents.filter(a => a.text.toLowerCase() === key).length;

      if (antecedentCount > 0) {
        counts.set(key, {
          text: goal.text,
          count: antecedentCount,
          element: goal
        });
      }
    }

    // Find the one with the highest count
    let maxCount = 0;
    let mostReferenced = null;

    for (const [key, data] of counts.entries()) {
      this.trace(`Unknown "${data.text}" appears ${data.count} times as antecedent`);
      if (data.count > maxCount) {
        maxCount = data.count;
        mostReferenced = data;
      }
    }

    return mostReferenced;
  }

  /**
   * Prove a single element using backward chaining
   */
  async prove(element) {
    const key = element.text.toLowerCase();

    // Check circular logic
    if (element.verifying) {
      this.trace(`Circular logic detected for: ${element.text}`);
      this.log(`Circular logic detected for: ${element.text}`);
      return TRUTH.UNKNOWN;
    }

    // Check if already known
    if (this.facts.has(key)) {
      this.trace(`Already known: ${element.text} = ${this.facts.get(key) === TRUTH.TRUE ? 'TRUE' : 'FALSE'}`);
      return this.facts.get(key);
    }

    // Check cache on element itself
    if (element.truthValue !== TRUTH.UNKNOWN) {
      this.trace(`Using cached value: ${element.text} = ${element.truthValue === TRUTH.TRUE ? 'TRUE' : 'FALSE'}`);
      return element.truthValue;
    }

    // Add to dependency stack
    this.dependencyStack.push(element);

    // Try to find rules that conclude this element
    const matchingRules = this.rules.filter(rule =>
      rule.consequents.some(c => c.text.toLowerCase() === key)
    );

    if (matchingRules.length > 0) {
      this.trace(`Found ${matchingRules.length} rule(s) that conclude "${element.text}"`);
      // Try each rule
      for (const rule of matchingRules) {
        this.trace(`Trying rule: IF ${rule.antecedents.map(a => a.text).join(' AND ')} THEN ${rule.consequents.map(c => c.text).join(' AND ')}`);
        element.verifying = true;

        const verified = await this.verify(rule);
        element.verifying = false;

        if (verified === TRUTH.TRUE) {
          // All antecedents true, so evaluate consequents based on their ruleType
          this.trace(`Rule verified: evaluating consequents`);

          // Set each consequent based on CASE 3 and CASE 4
          for (const con of rule.consequents) {
            const hasNot = con.ruleType.includes('NOT');
            const hasHyp = con.ruleType.includes('HYP');

            // CASE 3: Consequent proposition is assigned true if all antecedent conditions are true AND keyword does NOT contain "NOT"
            // CASE 4: Consequent proposition is assigned false if all antecedent conditions are true AND keyword contains "NOT"
            const truthValue = hasNot ? TRUTH.FALSE : TRUTH.TRUE;
            const conKey = con.text.toLowerCase();

            this.trace(`Setting consequent: ${con.text} (${con.ruleType}) to ${truthValue === TRUTH.TRUE ? 'TRUE' : 'FALSE'}`);
            this.facts.set(conKey, truthValue);

            // If this consequent is TRUE and not already displayed, add it to conclusions
            if (truthValue === TRUTH.TRUE) {
              const conclusionKey = con.text.toLowerCase();
              if (!this.displayedConclusions.has(conclusionKey)) {
                this.displayedConclusions.add(conclusionKey);
                this.conclusions.push(con);

                // Check if this is a hypothesis (HYP) conclusion
                if (hasHyp) {
                  this.trace(`HYPOTHESIS: ${con.text}`);
                  this.hypothesisReached = true;
                  // Create a modified element for notification with "Conclude: " prefix
                  const hypElement = new Element(con.type, `Conclude: ${con.text}`, con.ruleType);
                  this.notifyConclusion(hypElement);
                } else {
                  this.trace(`CONCLUSION: ${con.text}`);
                  this.notifyConclusion(con);
                }
              } else {
                this.trace(`Conclusion already tracked: ${con.text}`);
              }
            }

            // Update element truthValue if this is the element we're proving
            if (con.text.toLowerCase() === key) {
              element.truthValue = truthValue;
            }
          }

          this.dependencyStack.pop();

          // Return the truth value of the specific element we were proving
          const elementResult = this.facts.get(key);
          this.trace(`Rule verified: "${element.text}" is ${elementResult === TRUTH.TRUE ? 'TRUE' : 'FALSE'}`);
          return elementResult;
        }
      }
    }

    // No rules could prove it - ask the user
    // ONLY ask if this is an antecedent (condition) that is NOT also a consequent
    // If something appears as a consequent, it should be derived from rules, not asked directly
    const isAntecedent = this.allAntecedents.some(a => a.text.toLowerCase() === key);
    const isConsequent = this.allConsequents.some(c => c.text.toLowerCase() === key);
    const canAsk = isAntecedent && !isConsequent;

    this.trace(`Can ask "${element.text}"? isAntecedent=${isAntecedent}, isConsequent=${isConsequent}, canAsk=${canAsk}`);
    this.trace(`  allAntecedents has "${key}": ${this.allAntecedents.filter(a => a.text.toLowerCase() === key).map(a => a.ruleType).join(', ')}`);
    this.trace(`  allConsequents has "${key}": ${this.allConsequents.filter(c => c.text.toLowerCase() === key).length > 0}`);

    if (!element.asked && canAsk) {
      element.asked = true;
      const answer = await this.askUser(element);

      this.dependencyStack.pop();
      this.facts.set(key, answer);
      element.truthValue = answer;
      return answer;
    }

    // This is a consequent we couldn't prove and shouldn't ask about directly
    this.dependencyStack.pop();
    return TRUTH.UNKNOWN;
  }

  /**
   * Verify that all antecedents of a rule are true
   */
  async verify(rule) {
    for (const ant of rule.antecedents) {
      this.trace(`Checking antecedent: ${ant.text} (${ant.ruleType})`);

      // Check if this antecedent's ruleType contains "NOT"
      const hasNot = ant.ruleType.includes('NOT');

      // Get the actual truth value of the proposition
      let result = await this.prove(ant);

      // CASE 1: Antecedent condition is true if proposition is true AND keyword does NOT contain "NOT"
      // CASE 2: Antecedent condition is true if proposition is false AND keyword contains "NOT"
      if (hasNot) {
        // For negated antecedents, condition is true when proposition is FALSE
        if (result === TRUTH.FALSE) {
          this.trace(`Antecedent verified: ${ant.text} is FALSE, and ruleType contains NOT (${ant.ruleType})`);
          continue; // This antecedent condition is satisfied
        } else if (result === TRUTH.TRUE) {
          this.trace(`Antecedent failed: ${ant.text} is TRUE, but ruleType contains NOT (${ant.ruleType})`);
          return TRUTH.FALSE;
        } else {
          this.trace(`Antecedent failed: ${ant.text} is UNKNOWN, cannot verify NOT condition`);
          return TRUTH.UNKNOWN;
        }
      } else {
        // For normal antecedents, condition is true when proposition is TRUE
        if (result === TRUTH.TRUE) {
          this.trace(`Antecedent verified: ${ant.text} is TRUE`);
          continue;
        } else if (result === TRUTH.FALSE) {
          this.trace(`Antecedent failed: ${ant.text} is FALSE  (${ant.ruleType})`);
          return TRUTH.FALSE;
        } else {
          this.trace(`Antecedent failed: ${ant.text} is ${result === TRUTH.FALSE ? 'FALSE' : 'UNKNOWN'}`);
          return TRUTH.UNKNOWN;
        }
      }
    }
    this.trace(`All antecedents verified for rule: ${rule.consequents.map(c => c.text).join(', ')}`);
    return TRUTH.TRUE;
  }

  /**
   * Ask the user a question
   */
  async askUser(element) {
    const question = element.text;

    if (this.onAskQuestion) {
      try {
        const answer = await this.onAskQuestion(question, this.getExplanation());
        return answer ? TRUTH.TRUE : TRUTH.FALSE;
      } catch (e) {
        this.handleError(`Error asking question: ${e.message}`);
        return TRUTH.UNKNOWN;
      }
    } else {
      // Default: return UNKNOWN if no callback
      this.log(`No question handler - cannot ask: ${question}`);
      return TRUTH.UNKNOWN;
    }
  }

  /**
   * Get explanation for why a question is being asked
   */
  trace(message) {
    if (this.onTrace) {
      this.onTrace(message);
    }
  }

  /**
   * Get explanation for why a question is being asked
   */
  getExplanation() {
    if (this.dependencyStack.length <= 1) {
      return 'I need this information to reach a conclusion.';
    }

    const stack = [...this.dependencyStack];
    let explanation = 'I am trying to determine if "' + stack[0].text + '" is true.\n\n';

    for (let i = 1; i < stack.length; i++) {
      explanation += `  → To know "${stack[i].text}", I need to check conditions.\n`;
    }

    explanation += '\nThis will help me reach a conclusion.';
    return explanation;
  }

  /**
   * Notify about a conclusion
   */
  notifyConclusion(element) {
    this.log(`Conclusion: ${element.text}`);
    if (this.onConclusion) {
      this.onConclusion(element);
    }
  }

  /**
   * Log message
   */
  log(message) {
    if (this.verbose && this.onProgress) {
      this.onProgress(message);
    }
  }

  /**
   * Handle errors
   */
  handleError(message) {
    if (this.onError) {
      this.onError(message);
    } else {
      console.error('ExpertSystem error:', message);
    }
  }

  /**
   * Reset the system
   */
  reset() {
    this.facts.clear();
    this.conclusions = [];
    for (const goal of this.goalElements) {
      goal.truthValue = TRUTH.UNKNOWN;
      goal.asked = false;
    }
  }

  /**
   * Get all known facts
   */
  getKnownFacts() {
    const facts = [];
    for (const [text, value] of this.facts.entries()) {
      if (value === TRUTH.TRUE) {
        facts.push({ text, value: 'TRUE' });
      }
    }
    return facts;
  }
}

/**
 * Convenience function to create a new expert system
 */
function createExpertSystem(options) {
  return new ExpertSystem(options);
}

// Export for use in browser or Node.js
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { ExpertSystem, createExpertSystem, TRUTH, ELEMENT_TYPE };
}
