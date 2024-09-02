import React from "react";
import "./styles.css";

class Dances extends React.Component {
  constructor(props) {
    super(props);
    console.log(
      "window.lab9models.dancesModel()",
      window.lab9models.dancesModel()
    );
    this.state = { substr: "" };

    this.handleChangeSubstr = (event) => this.handleChange(event);
  }

  handleChange(event) {
    const substr = event.target.value;
    this.setState({ substr });

    const dan = window.lab9models.dancesModel();
    const result = dan.filter((dan) =>
      dan.toLowerCase().includes(substr.toLowerCase())
    );

    document.getElementById("IInfo").innerHTML = result;
  }

  render() {
    return (
      <div>
        <div className="state-search">{this.state.substr}</div>
        <div className="lab9-example-output">
          <span id="IInfo"></span>
        </div>
        <label htmlFor="substrId">Enter substring to search:</label>
        <input
          id="substrId"
          type="text"
          value={this.state.substr}
          onChange={this.handleChangeSubstr}
        />
      </div>
    );
  }
}

export default Dances;
