# todo - base version needs sourced from build script based on supported runtime.
# did not use latest due to WARNING:
#   > The requested image's platform (linux/arm64/v8) does not match the detected host platform (linux/amd64/v3) and no specific platform was requested
FROM mozilla/sbt:8u292_1.5.7

RUN curl -sfL "https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz" | gzip -d > /usr/sbin/coursier \
    && chmod +x /usr/sbin/coursier \
    && coursier setup -y

RUN coursier install --channel https://disneystreaming.github.io/coursier.json smithytranslate

ENV PATH="${PATH}:/root/.local/share/coursier/bin"

WORKDIR /app

ENTRYPOINT [ "smithytranslate" ]

CMD [ "smithytranslate" ]
