FROM discoenv/openjdk-base:master

# Include CA certs so the UI server-side can make https calls to 3rd party services.
RUN apk add --no-cache ca-certificates java-cacerts

ENV CONF_TEMPLATE=/etc/iplant/de/de.properties.tmpl
ENV CONF_FILENAME=de.properties
ENV PROGRAM=java

ARG git_commit=unknown
ARG version="2.9.0"
ARG descriptive_version=unknown

LABEL org.cyverse.git-ref="$git_commit"
LABEL org.cyverse.version="$version"
LABEL org.cyverse.descriptive-version="$descriptive_version"

ADD target/de-copy.war /de.war
ADD de.properties.tmpl /etc/iplant/de/de.properties.tmpl
ADD de-application.yaml /etc/iplant/de/de-application.yaml

EXPOSE 8080

ENTRYPOINT ["run-service", "--exclude-config-arg", "-Dlogging.config=file:/etc/iplant/de/logging/de-ui.xml", "-jar", "de.war", "--spring.config.location=file:/etc/iplant/de/de-application.yaml"]
LABEL org.label-schema.vcs-ref="$git_commit"
LABEL org.label-schema.vcs-url="https://github.com/cyverse-de/ui"
LABEL org.label-schema.version="$descriptive_version"
