from random import randint
from flask import Flask, request
import logging

from opentelemetry import trace

tracer = trace.get_tracer("diceroller.tracer")

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@app.route("/rolldice")
def roll_dice():
    player = request.args.get("player", default=None, type=str)
    # manual child span inside automatic request span
    with tracer.start_as_current_span("roll") as roll_span:
        res = randint(1, 6)
        # add attribute to span
        roll_span.set_attribute("roll.value", res)
        msg = f"{player if player else 'Anonymous player'} is rolling the dice: {res}"
        logger.warning(msg)
        return str(res)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
