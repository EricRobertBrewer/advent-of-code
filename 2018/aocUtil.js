#!/usr/local/bin node

const fs = require("fs");
const https = require("https");
const path = require("path");

const INPUT_DIR = "input";
const ENV_VAR = "AOC_SESSION";

module.exports.getOrDownloadInputFile = function (year, day, callback) {
    const yearDir = INPUT_DIR + path.sep + year;
    try {
        yearDirStat = fs.statSync(yearDir);
        if (!yearDirStat.isDirectory()) {
            fs.unlinkSync(yearDir);
            fs.mkdirSync(yearDir);
        }
    } catch (error) {
        if (error.code !== "ENOENT") {
            throw error;
        }
        fs.mkdirSync(yearDir);
    }

    const inputFile = yearDir + path.sep + day + ".txt";
    try {
        fd = fs.openSync(inputFile);
        callback(fd);
    } catch (error) {
        if (error.code !== "ENOENT") {
            throw error;
        }
        const token = process.env[ENV_VAR];
        if (token === undefined) {
            throw new Error("Set the environment variable `" + ENV_VAR + "` as your AoC session token " +
                    "(found in Dev Tools > Application > Cookies > 'session'): " +
                    "$ export " + ENV_VAR + "=<my_token>");
        }
        const options = {
            headers: {
                cookie: "session=" + token
            }
        };
        // https://nodejs.org/api/http.html#httpgetoptions-callback
        https.get("https://adventofcode.com/" + year + "/day/" + day + "/input", options, (res) => {
            res.setEncoding("utf8");
            let rawData = "";
            res.on("data", (chunk) => {
                rawData += chunk;
            });
            res.on("end", () => {
                fs.writeFileSync(inputFile, rawData);
                fd = fs.openSync(inputFile);
                callback(fd);
            });
        });
    }
};

module.exports.getExampleFile = function (year, day, callback) {
    fd = fs.openSync(INPUT_DIR + path.sep + year + path.sep + day + "-example.txt");
    callback(fd);
};
