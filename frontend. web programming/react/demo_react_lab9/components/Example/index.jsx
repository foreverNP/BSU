import React from "react";
import "./styles.css";

/**
 * Since this component shows code we include the https://prismjs.com/
 * formatter. We invoke it by labelling code blocks with class="language-jsx"
 */
import Prism from "prismjs";
import "prismjs/components/prism-jsx.js";
import "../../node_modules/prismjs/themes/prism.css";



// React Components are subclass of React.Component.
class Example extends React.Component {
  constructor(props) {
    super(props); // Must run the constructor of React.Component first

    // Components have a special property named "state" that holds state. We can
    // initialize it here. We read the example model data into the state
    // variable 'name'.
    this.state = {
      name: window.lab9models.exampleModel().name,
      counter: 0,
      inputValue: "",
      buttonWasClicked: "",
      motto:" Первый React компонент",
    };

    // React events are called directly from DOM event handlers so we cannot
    // directly call the methods of this class. We generate new functions that
    // handle the event by just calling the method that handles the event.
    this.handleChangeBound = (event) => this.handleChange(event);
    this.handleChangeMotto = (event) => this.handleMotto(event);
    // Note: A commmon idiom in React code is to use JavaScript bind() to smash
    // the method to accomplish this passthrough to the method:
    //      this.handleChange = this.handleChange.bind(this);
  }

  // React components have several "lifecycle functions"
  // https://reactjs.org/docs/react-component.html that are used to inform the
  // Component of interesting events.

  // componentDidMount - Called when Component is activiated
  componentDidMount() {
    // To demonstate state updating we define a function that increments the
    // counter state and instruct the DOM to call it every 2 seconds.
    /* eslint-disable react/no-access-state-in-setstate */
    const counterIncrFunc = () => {
      this.setState({
        counter: this.state.counter + 1,
      });
    };
    this.timerID = setInterval(counterIncrFunc, 2 * 1000);

    // Trigger the code coloring
    Prism.highlightAll();
  }

  // componentWillUnmount - Called when Component is deactivated.
  componentWillUnmount() {
    // We need to tell the DOM to stop calling us otherwise React will complain
    // when we call setState on an unmounted component.
    clearInterval(this.timerID);
  }

  // Method called when the input box is typed into.
 handleMotto(event) {
    this.setState({ motto: event.target.value });
  }


  



  render() {
    return (
      <div className="container Example">
        <h1>Lab9 React.js Example</h1>

        <div className="motto-update">
          {
			  
            this.state.motto
            }
        </div>

       <div className="lab9-example-output">
          <label htmlFor="mottoId">Change the motto:</label>
          <input
            id="mottoId"
            type="text"
            value={this.state.motto}
            onChange={this.handleChangeMotto}
          />
       </div>

        <p>
          This view is an example of a &nbsp;
          <a
            href="https://react.dev/reference/react/Component"
            target="_blank"
            rel="noopener noreferrer"
          >
            React.js Component
          </a>
          &nbsp; named <span className="lab9-code-name">Example</span>. It is
          located in the file <code>components/Example/index.jsx</code>. It
          looks like a JavaScript class named Example that has a method named
          named <span className="lab9-code-name">render</span>, which appears
          to written in something that looks like HTML.
        </p>
        <p>
          It is actually written in a language named &nbsp;
          <a
            href="https://reactjs.org/docs/introducing-jsx.html"
            target="_blank"
            rel="noopener noreferrer"
          >
            JSX
          </a>
          &nbsp; that is run as a preprocessor to the HTML-like language to
          JavaScript. The generated JavaScipt is limited to calls to the
          React.js &nbsp;
          <a
            href="https://reactjs.org/docs/react-api.html#createelement"
            target="_blank"
            rel="noopener noreferrer"
          >
            createElement
          </a>
          &nbsp; function which allow us to write something that looks like HTML
          to describe what the component renders.
        </p>
        <p>
          Although JSX looks like HTML, it is not HTML. Some of the differences
          are necessary due to embeddding in JavaScript. For example, rather
          than <code>class=</code> we use <code>className=</code> (class is a
          JavaScript keyword). Although it is possible to interleave JavaScript
          and JSX code, care is needed since contents of JSX tags are processed
          into arguments to a function limiting what can be done as will be seen
          below.
        </p>

        <h3>Template substitution</h3>

        <p>
          JSX treats text inside of parentheses (e.g.{" "}
          <code>{"{JavaScriptExpression}"}</code>) as templates where the
          JavaScript expression is evaluated in the context of the current
          function and whose value replaces the template in the string. The
          expression can evaluate to a JavaScript string or value from a JSX
          expression. This feature allows component&apos;s specification to use
          templated HTML.
        </p>

        <p>
          The Example class constructor sets the object&apos;s property{" "}
          <code>state.name</code> (see the assignment to{" "}
          <code>this.state.name</code> in <code>Example.jsx</code>) from the
          model in the DOM which has a value of &ldquo; {this.state.name}
          &rdquo; so:
        </p>
        <pre className="lab9-example-code">
          <code className="language-jsx">
            {`<p>My name is "{this.state.name}".</p>`}
          </code>
        </pre>
        <p>should render as:</p>
        <p className="lab9-example-output">
          My name is &ldquo; {this.state.name} &rdquo;.
        </p>

        
       
      </div>
    );
  }
}

export default Example;
