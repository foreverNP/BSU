from random import randint
from flask import Flask, request
import logging
import sys

from opentelemetry import trace
from opentelemetry import metrics

# Acquire a tracer
tracer = trace.get_tracer("diceroller.tracer")

# Acquire a meter
meter = metrics.get_meter("diceroller.meter")

# Create a counter instrument to make measurements with
roll_counter = meter.create_counter(
    "dice.rolls",
    description="The number of rolls by roll value",
)

app = Flask(__name__)

logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s", handlers=[logging.StreamHandler(sys.stdout)])
logger = logging.getLogger(__name__)


@app.route("/rolldice")
def roll_dice():
    player = request.args.get("player", default=None, type=str)

    with tracer.start_as_current_span("roll") as roll_span:
        result = str(roll())
        roll_span.set_attribute("roll.value", result)
        # This adds 1 to the counter for the given roll value
        roll_counter.add(1, {"roll.value": result})

        if player:
            logger.info(f"Player '{player}' is rolling the dice: {result}")
        else:
            logger.info(f"Anonymous player is rolling the dice: {result}")

        logger.debug(f"Dice roll completed. Result: {result}")

        return result


@app.route("/health")
def health():
    logger.debug("Health check requested")
    return {"status": "healthy"}, 200


def roll():
    return randint(1, 6)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)
