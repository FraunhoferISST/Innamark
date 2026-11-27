/*
 * SPDX-FileCopyrightText: 2026 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e. V.
 *
 * SPDX-License-Identifier: LicenseRef-Fraunhofer-Gesellschaft
 */

config.module.rules.push({test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/, type: 'asset'});
config.module.rules.push({test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, type: 'asset'});
config.module.rules.push({test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, type: 'asset/resource'});
