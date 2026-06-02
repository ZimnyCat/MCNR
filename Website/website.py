from flask import Flask, request
from flask import render_template
from mctools import RCONClient, PINGClient
from gson import json
from datetime import datetime
from dotenv import load_dotenv
import os
import re


load_dotenv()
password = os.getenv("RCON_PASSWORD")
app = Flask(__name__)
stats_cache = ["", 0]
status_cache = ["", 0]


@app.route("/")
def homepage():
    return render_template("index.html")


def sanitize(player):
    return re.sub(r'[^a-zA-Z0-9_]', '', player)[:16]


def get_joindate(player):
    try:
        rcon = RCONClient("localhost")
        if password is not None:
            rcon.login(password)
        response = rcon.command("joindate " + sanitize(player))
        rcon.stop()
        return json.loads(response.replace("\n\u001b[0m", ""))
    except:
        return {"error": "Сервер перезагружается или выключен"}


@app.route("/api/joindate")
def api_joindate():
    player = request.args.get("player")
    if player is None or player == "":
        return {"error": "Игрок не указан"}
    return get_joindate(player)


def get_stats():
    try:
        if datetime.now().timestamp() - stats_cache[1] > 10:
            rcon = RCONClient("localhost")
            if password is not None:
                rcon.login(password)
            response = rcon.command("stats")
            rcon.stop()
            stats_cache[0] = response.replace("\n\u001b[0m", "")
            stats_cache[1] = datetime.now().timestamp()
    except:
        None
    return json.loads(stats_cache[0])


@app.route("/api/stats")
def api_stats():
    return get_stats()


@app.route("/support")
def support():
    return render_template("support.html")


def get_time_diff(time):
    diff = datetime.now().timestamp() * 1000 - time

    if diff < 3600000:
        return f"({(int)(diff / 60000)} мин. назад)"
    if diff < 86400000:
        return f"({(int)(diff / 3600000)} ч. назад)"
    return f"({(int)(diff / 86400000)} д. назад)"


@app.route("/stats")
def stats():
    player = request.args.get("player")
    if player is None or player == "":
        return render_template("stats.html", data=get_stats())
    
    jd = get_joindate(player)
    if "error" in jd:
        return render_template("stats.html", data=get_stats(), playerdata=jd)
    jd["first"] = f"Первый заход: {datetime.fromtimestamp(jd['first'] / 1000).date()} {get_time_diff(jd['first'])}"
    if jd["last"] == "Online":
        jd["last"] = "Сейчас на сервере"
    else:
        jd["last"] = f"Последний заход: {datetime.fromtimestamp(jd['last'] / 1000).date()} {get_time_diff(jd['last'])}"
    jd["playtime"] = f"Время игры: {jd['playtime']} ч."
    return render_template("stats.html", data=get_stats(), playerdata=jd)


@app.route("/desc")
def desc():
    return render_template("desc.html")


@app.route("/api")
def api():
    return render_template("api.html")


def is_online(port):
    try:
        ping = PINGClient("localhost", port)
        ping.get_stats()["players"]["max"]
        return "ON"
    except:
        return "OFF"


@app.route("/status")
def status():
    if datetime.now().timestamp() - stats_cache[1] > 10:
        stats_cache[0] = {"proxy": is_online(25565), "auth": is_online(25566), "surv": is_online(25567)}
        stats_cache[1] = datetime.now().timestamp()
    return render_template("status.html", data=stats_cache[0])
    

if __name__ == "__main__":
    app.run(debug=False)