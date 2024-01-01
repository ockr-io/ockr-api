# Changelog

## [0.3.0](https://github.com/ockr-io/ockr-api/compare/v0.2.4...v0.3.0) (2023-12-30)


### ⚠ BREAKING CHANGES

* introduce puzzle abstraction to forward knowledge about the initial total puzzle grid

### Features

* introduce puzzle abstraction to forward knowledge about the initial total puzzle grid ([80b2cf8](https://github.com/ockr-io/ockr-api/commit/80b2cf849eca1c622c2d8446fbf3a7d8a96a8e78))

## [0.2.4](https://github.com/ockr-io/ockr-api/compare/v0.2.3...v0.2.4) (2023-12-29)


### Bug Fixes

* calculate puzzle error does now respect false negatives ([fc57f91](https://github.com/ockr-io/ockr-api/commit/fc57f91db41973e5ccf80d5f6514b115c6fe08e8))
* keep minX and minY in mind, when slicing the puzzle pieces ([c9352fb](https://github.com/ockr-io/ockr-api/commit/c9352fb6c153f0aa3ddd23d4602748d4c6abb066))
* modelService was not available in compute method ([19d54fc](https://github.com/ockr-io/ockr-api/commit/19d54fcff0eaeb908121874f493c82b6f9cb92e1))

## [0.2.3](https://github.com/ockr-io/ockr-api/compare/v0.2.2...v0.2.3) (2023-12-26)


### Features

* render jpg from pdf to feed the ping pong algorithm with data ([4c49442](https://github.com/ockr-io/ockr-api/commit/4c4944284c028b3cc920fd593c3271fe8592ea2b))


### Bug Fixes

* add default array list to builder pattern to prevent null pointer issues ([2f841ba](https://github.com/ockr-io/ockr-api/commit/2f841ba21113f066dd8bb70fcd9fa5950824db3d))

## [0.2.2](https://github.com/ockr-io/ockr-api/compare/v0.2.1...v0.2.2) (2023-12-26)


### Bug Fixes

* add more tests fix full helper size ([ca1cbe4](https://github.com/ockr-io/ockr-api/commit/ca1cbe4282fc062a53d2c18dfb1564897003740c))

## [0.2.1](https://github.com/ockr-io/ockr-api/compare/v0.2.0...v0.2.1) (2023-12-26)


### Features

* add basic setup for puzzle ping pong algorithm ([72e17c4](https://github.com/ockr-io/ockr-api/commit/72e17c4e5f363cd924f11b5f43ed9fdce9c4670b))
* add new puzzle ping pong implementation ([273124e](https://github.com/ockr-io/ockr-api/commit/273124e20a4318bd3e8dec8ad4086a74fe8ea20a))


### Bug Fixes

* use levenshtein distance instead of hamming distance for error calculation ([7d5a0db](https://github.com/ockr-io/ockr-api/commit/7d5a0dba2cf5e98e5dedcb8f048b44a21cbeab5c))

## [0.2.0](https://github.com/ockr-io/ockr-api/compare/v0.1.0...v0.2.0) (2023-12-08)


### Features

* add default puzzle algorithm ([c8d7dfd](https://github.com/ockr-io/ockr-api/commit/c8d7dfd7d9e326899d1a26dad2de209ed5074253))
* add model inference methods ([72a4242](https://github.com/ockr-io/ockr-api/commit/72a4242c7dd7a137379bc5be5f43eb132854fcae))
* add rest assured api tests ([3e666a6](https://github.com/ockr-io/ockr-api/commit/3e666a68bd42e21719b45704557f8b5c605941b4))
* add service to extract text from pdfs ([e63ec5f](https://github.com/ockr-io/ockr-api/commit/e63ec5f3200b173fa812fab65be43f948b3ff89d))


### Bug Fixes

* bump version manually to fix the pipeline ([3a7d7d8](https://github.com/ockr-io/ockr-api/commit/3a7d7d8976cc56b5e80d9217a8094edff8b2f388))
* reset version to the old one in README and pom to make sure release-please is able to find them ([61e6741](https://github.com/ockr-io/ockr-api/commit/61e67415573254853812fcf5c067ff87761324fb))


### Miscellaneous Chores

* release 0.2.0 ([7d94e57](https://github.com/ockr-io/ockr-api/commit/7d94e57b486374d497cf6f78d8171c134a979af4))

## 0.1.0 (2023-12-03)


### Features

* add service and apis around the model entity ([1febcd3](https://github.com/ockr-io/ockr-api/commit/1febcd394f79935d10b8175bc9d73ac431fd9ffb))
