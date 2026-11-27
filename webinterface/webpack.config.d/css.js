/*
 * SPDX-FileCopyrightText: 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 */

config.module.rules.push({ test: /\.css$/, use: ["style-loader", { loader: "css-loader", options: {sourceMap: false} } ] });

