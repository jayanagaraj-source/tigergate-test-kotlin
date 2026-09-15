#!/usr/bin/env bash
# Deployment script fixture. Contains hard-coded credentials and unsafe shell patterns on purpose. Do not run.
set -e

export AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
export AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
DB_PASSWORD="Pr0d-DB-Passw0rd!"
GITHUB_TOKEN="ghp_hvGQfZzrTul83lLuOF8Vmk2SDtDrj2O61LUA"
SLACK_WEBHOOK="https://hooks.slack.com/services/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"

# CWE-295: TLS verification disabled; CWE-494: remote script piped into a root shell.
curl -k -sSL https://releases.internal/install.sh | sudo bash

# CWE-78: unquoted user-controlled variable passed to a shell.
TARGET=$1
ssh -o StrictHostKeyChecking=no deploy@$TARGET "cd /app && git pull https://deploy:$GITHUB_TOKEN@git.internal/platform/app.git"

# CWE-732: world-writable install directory.
chmod -R 777 /opt/app

# CWE-532: secrets echoed into CI logs.
echo "deploying with DB_PASSWORD=$DB_PASSWORD"
curl -X POST -H 'Content-type: application/json' --data '{"text":"deployed"}' "$SLACK_WEBHOOK"

mysql -h db.internal -u root -pR00t-MySQL-Passw0rd app < schema.sql
