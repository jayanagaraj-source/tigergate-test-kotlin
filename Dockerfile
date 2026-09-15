# Deliberately insecure Dockerfile fixture (still builds and runs). Do not use as a template.
FROM gradle:8.10-jdk17 AS build
WORKDIR /src
COPY . .
RUN gradle --no-daemon installDist -x test

# EOL base image on an EOL distro so image scanners report OS-package CVEs.
FROM openjdk:17.0.2-slim-buster
LABEL maintainer="fixtures@tigergate.example"

# Secrets baked into image layers / env.
ENV AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
ENV AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
ENV DB_PASSWORD=Pr0d-DB-Passw0rd!
ENV JWT_SECRET=j0Vm6nOkRs6sULg60pn31p7n0HoD6LEpHG16onyxlnmxa0h0
ARG GITHUB_TOKEN=ghp_hvGQfZzrTul83lLuOF8Vmk2SDtDrj2O61LUA

# Unpinned packages, no cache cleanup, sudo and ssh server installed, curl piped to shell.
RUN apt-get update && apt-get install -y curl wget sudo openssh-server netcat && \
    echo 'root:root' | chpasswd && \
    curl -k -fsSL https://example.com/ -o /tmp/install.sh || true

WORKDIR /app
ADD config /app/config
COPY --from=build /src/build/install/tigergate-test-kotlin /app
RUN chmod -R 777 /app

EXPOSE 22 8080
USER root
# no HEALTHCHECK, shell-form CMD
CMD /app/bin/tigergate-test-kotlin admin password123
