#
# SPDX-FileCopyrightText: 2023-2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
#
# SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
#

# Build webinterface
FROM eclipse-temurin:21.0.6_7-jdk AS build

COPY watermarker /app/watermarker
COPY webinterface /app/webinterface

RUN apt install findutils
WORKDIR /app/watermarker
RUN ./gradlew publishToMavenLocal

WORKDIR /app/webinterface
RUN ./gradlew clean zip

# Use unprivileged base image for final execution
FROM nginxinc/nginx-unprivileged:alpine
COPY --from=build /app/webinterface/build/libs/*.zip /usr/share/nginx/html/innamark-webinterface.zip

USER root
WORKDIR /usr/share/nginx/html
RUN apk --update add openssl unzip
RUN unzip -o innamark-webinterface.zip
RUN rm -rf innamark-webinterface.zip
RUN chown -R 101:101 /usr/share/nginx/html/
USER 101

CMD ["nginx", "-g", "daemon off;"]
